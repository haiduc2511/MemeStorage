package com.example.memestorage.adapters;

import android.app.DownloadManager;
import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.memestorage.fragments.ImageFragment;
import com.example.memestorage.R;
import com.example.memestorage.databinding.ItemImageBinding;
import com.example.memestorage.models.ImageModel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private List<ImageModel> imageModels;
    private Context context;
    private FragmentManager fragmentManager;
    private Set<ImageModel> imageModelsDownloaded;

    public ImageAdapter(List<ImageModel> dataList, Context context, FragmentManager fragmentManager) {
        this.imageModels = dataList;
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.imageModelsDownloaded = new HashSet<>();
    }

    public Context getContext() {
        return context;
    }

    public void addImage(ImageModel imageModel) {
        imageModels.add(imageModel);
        notifyItemInserted(imageModels.size() - 1);
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

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int screenHeight = displayMetrics.heightPixels;
        int imageHeight = screenHeight / 6; // 1/6 of the screen height

        ViewGroup.LayoutParams layoutParams = holder.binding.ivImage.getLayoutParams();
        layoutParams.height = imageHeight;
        holder.binding.ivImage.setLayoutParams(layoutParams);

        holder.bind(data);
    }

    @Override
    public int getItemCount() {
        return imageModels.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        ItemImageBinding binding;

        public ImageViewHolder(@NonNull ItemImageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.ivDownload.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    ImageModel image = imageModels.get(position);
                    downloadImageLikeTinCoder(image.imageURL, image.imageName);
                    binding.ivDownload.setImageResource(R.drawable.ic_download_done);
                    imageModelsDownloaded.add(image);
                }
            });

            binding.ivImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        ImageModel image = imageModels.get(position);
                        ImageFragment fragment = ImageFragment.newInstance(image);
                        fragmentManager.beginTransaction()
                                .replace(R.id.fragment_container, fragment)
                                .addToBackStack(null)
                                .commit();
                    }
                }
            });
        }

        public void bind(ImageModel imageModel) {
            // Load image using Glide or Picasso
            Glide.with(itemView.getContext()).load(imageModel.imageURL).into(binding.ivImage);
            binding.setImageModel(imageModel);
            if (imageModelsDownloaded.contains(imageModel)) {
                binding.ivDownload.setImageResource(R.drawable.ic_download_done);
            } else {
                binding.ivDownload.setImageResource(R.drawable.ic_download);
            }
        }
    }
    private void downloadImageLikeTinCoder(String imageUrl, String imageName) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(imageUrl));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setTitle("Download meme");
        request.setDescription("Downloading meme mtfk...");

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        String directoryPath = Environment.DIRECTORY_PICTURES + "/MemeStorage";
        String filePath = System.currentTimeMillis() + "and" + imageName;

        request.setDestinationInExternalPublicDir(directoryPath, filePath);

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        if (downloadManager != null) {
            downloadManager.enqueue(request);
        }
    }
}
