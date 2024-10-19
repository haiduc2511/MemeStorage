package com.example.memestorage.adapters;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.cloudinary.Transformation;
import com.cloudinary.android.MediaManager;
import com.example.memestorage.fragments.ImageFragment;
import com.example.memestorage.R;
import com.example.memestorage.databinding.ItemImageBinding;
import com.example.memestorage.models.ImageModel;
import com.example.memestorage.utils.SharedPrefManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private List<ImageModel> imageModels = new ArrayList<>();
    private Context context;
    private FragmentManager fragmentManager;
    private Set<ImageModel> imageModelsDownloaded = new HashSet<>(); //Forgot to add ImageModelsShared
    private Set<ImageModel> imageModelsShared = new HashSet<>(); //Forgot to add ImageModelsShared
    private int numberOfColumn;
    private SharedPrefManager sharedPrefManager;

    public ImageAdapter(Context context, FragmentManager fragmentManager, int numberOfColumn) {
        this.numberOfColumn = numberOfColumn;
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.sharedPrefManager = new SharedPrefManager(context);
    }
    public ImageModel getImageAt(int position) {
        return imageModels.get(position);
    }

    public int getNumberOfColumn() {
        return numberOfColumn;
    }

    public void setNumberOfColumn(int numberOfColumn) {
        this.numberOfColumn = numberOfColumn;
    }

    public Context getContext() {
        return context;
    }
    public void deleteImage(int position) {
        imageModels.remove(position);
        notifyItemRemoved(position);
    }

    public void addImage(ImageModel imageModel) {
        imageModels.add(imageModel);
        notifyItemInserted(imageModels.size() - 1);
    }

    public void addImageFirst(ImageModel imageModel) {
        imageModels.add(0, imageModel);
        notifyItemInserted(0);
    }

    public void setImageModels(List<ImageModel> imageModels) {
        this.imageModels = imageModels;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemImageBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_image, parent, false);
        return new ImageViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        ImageModel data = imageModels.get(position);
        holder.binding.ivImage.setImageResource(R.drawable.ic_loading2);

//        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
//        int screenHeight = displayMetrics.heightPixels;
//        int imageHeight = screenHeight / numberOfColumn / 2; // 1/6 of the screen height
//
//        ViewGroup.LayoutParams layoutParams = holder.binding.ivImage.getLayoutParams();
//        layoutParams.height = imageHeight;
//        holder.binding.ivImage.setLayoutParams(layoutParams);

        int columnCount = numberOfColumn; // Số cột (3 hoặc 4 tùy bạn)
        int screenWidth = holder.itemView.getContext().getResources().getDisplayMetrics().widthPixels;
        int itemWidth = screenWidth / columnCount;

        // Đặt chiều rộng cho item
        ViewGroup.LayoutParams layoutParams = holder.binding.ivImage.getLayoutParams();
        layoutParams.width = itemWidth;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        holder.binding.ivImage.setLayoutParams(layoutParams);


        holder.incrementTimeBound();
        holder.bind(data);
    }

    @Override
    public int getItemCount() {
        return imageModels.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        ItemImageBinding binding;
        int timeBound = 0;

        public ImageViewHolder(@NonNull ItemImageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.ivDownload.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    ImageModel image = imageModels.get(position);
                    downloadImageLikeTinCoder(image.imageURL, image.iId);
                    binding.ivDownload.setImageResource(R.drawable.ic_download_done);
                    imageModelsDownloaded.add(image);
                }
            });

            binding.ivShare.setOnClickListener(v -> {
                int position = getAdapterPosition();
                ImageModel image = imageModels.get(position);
                binding.ivShare.setImageResource(R.drawable.ic_loading3);
                Glide.with(context).asBitmap().load(imageModels.get(position).imageURL)
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                binding.ivShare.setImageResource(R.drawable.ic_download_done);
                                shareImageToOtherApps(resource);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {

                            }
                        });
                imageModelsShared.add(image);
            });
        }

        public void incrementTimeBound() {
            timeBound++;
        }

        public void bind(ImageModel imageModel) {
            // Load image using Glide or Picasso
            String url = "";
            if (imageModel.iId.length() > 36) {
                 url = MediaManager.get().url()
                        .transformation(new Transformation()
                                .quality("auto")
                                .width(Integer.parseInt(sharedPrefManager.getFetchQuality())))
                        .generate(imageModel.imageName);
                 Log.d("URL CLOUDINARY", url);
            } else {
                url = imageModel.imageURL;
                Log.d("URL FireStore", url);
            }

            loadImageWithGlide(url);

            binding.setImageModel(imageModel);
            if (sharedPrefManager.getIfDownloadImageEasyModeOn().equals("true")) {
                binding.ivDownload.setVisibility(View.VISIBLE);
            } else {
                binding.ivDownload.setVisibility(View.GONE);
            }

            if (sharedPrefManager.getIfShareImageEasyModeOn().equals("true")) {
                binding.ivShare.setVisibility(View.VISIBLE);
            } else {
                binding.ivShare.setVisibility(View.GONE);
            }

            if (imageModelsDownloaded.contains(imageModel)) {
                binding.ivDownload.setImageResource(R.drawable.ic_download_done);
            } else {
                binding.ivDownload.setImageResource(R.drawable.ic_download);
            }

            if (imageModelsShared.contains(imageModel)) {
                binding.ivShare.setImageResource(R.drawable.ic_download_done);
            } else {
                binding.ivShare.setImageResource(R.drawable.ic_share);
            }
        }

        public void loadImageWithGlide(String url) {
            int thisTimeBound = timeBound;
            binding.ivImage.setImageResource(R.drawable.ic_loading2);
            Glide.with(context).asBitmap().load(url)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(Glide.with(context).asBitmap().load(url))
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            binding.ivImage.setImageBitmap(resource);
                            binding.ivImage.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    int position = getAdapterPosition();
                                    if (position != RecyclerView.NO_POSITION) {
                                        ImageModel image = imageModels.get(position);
                                        ImageFragment fragment = ImageFragment.newInstance(image, resource);
                                        fragmentManager.beginTransaction()
                                                .replace(R.id.fragment_image, fragment)
                                                .addToBackStack(null)
                                                .commit();
                                    }
                                }
                            });
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            binding.ivImage.setImageDrawable(placeholder);

                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            super.onLoadFailed(errorDrawable);
                            Log.d("thisTimeBound", thisTimeBound + " " + timeBound);
                            if (thisTimeBound == timeBound) {
                                binding.ivImage.setImageResource(R.drawable.ic_reload_image2);
                                binding.ivImage.setOnClickListener(v -> {
                                    loadImageWithGlide(url);
                                    notifyItemChanged(getAdapterPosition());
                                });
                            }
                        }
                    });


        }
    }
    private void downloadImageLikeTinCoder(String imageUrl, String iId) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(imageUrl));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setTitle("Download from meme storage");
        request.setDescription("Downloading...");

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        String directoryPath = Environment.DIRECTORY_PICTURES;
        String filePath =   "MemeStorage/" + System.currentTimeMillis() + "and" + iId + ".jpg";

        request.setDestinationInExternalPublicDir(directoryPath, filePath);

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        if (downloadManager != null) {
            downloadManager.enqueue(request);
        }
    }

    private void shareImageToOtherApps(Bitmap bitmap) {
        Uri imageUri = saveImageToCache(bitmap);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        context.startActivity(Intent.createChooser(shareIntent, "Share Image via"));
    }

    private Uri saveImageToCache(Bitmap bitmap) {
        File cachePath = new File(context.getCacheDir(), "images");
        cachePath.mkdirs(); // Create directory if needed
        File file = new File(cachePath, "image.png");
        try (FileOutputStream stream = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
    }
}
