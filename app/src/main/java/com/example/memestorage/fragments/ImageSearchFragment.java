package com.example.memestorage.fragments;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ablanco.zoomy.Zoomy;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.cloudinary.Transformation;
import com.cloudinary.android.MediaManager;
import com.example.memestorage.R;
import com.example.memestorage.adapters.CategoryAdapter;
import com.example.memestorage.adapterver2.CommentAdapter;
import com.example.memestorage.databinding.FragmentImageSearchBinding;
import com.example.memestorage.models.ImageCategoryModel;
import com.example.memestorage.models.ImageModel;
import com.example.memestorage.test.model.MediaCommentModel;
import com.example.memestorage.test.model.MediaLikeModel;
import com.example.memestorage.test.model.MediaSaveModel;
import com.example.memestorage.test.model.MyLikedMediaModel;
import com.example.memestorage.test.model.MySavedMediaModel;
import com.example.memestorage.test.model.UserFriendCompatibilityActionModel;
import com.example.memestorage.test.model.UserFriendCompatibilityModel;
import com.example.memestorage.test.viewmodel.MediaCommentViewModel;
import com.example.memestorage.test.viewmodel.MediaLikeViewModel;
import com.example.memestorage.test.viewmodel.MediaSaveViewModel;
import com.example.memestorage.test.viewmodel.MyLikedMediaViewModel;
import com.example.memestorage.test.viewmodel.MySavedMediaViewModel;
import com.example.memestorage.test.viewmodel.UserFriendCompatibilityActionViewModel;
import com.example.memestorage.test.viewmodel.UserFriendCompatibilityViewModel;
import com.example.memestorage.utils.FirebaseHelper;
import com.example.memestorage.viewmodels.CategoryViewModel;
import com.example.memestorage.viewmodels.ImageCategoryViewModel;
import com.example.memestorage.viewmodels.ImageViewModel;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ImageSearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImageSearchFragment extends Fragment {
    public interface OnImageCategoryRetrieved {
        public void OnImageCategoryRetrieved();
    }

    private static final String ARG_IMAGE = "image";
    private static final String ARG_PRELOADED_IMAGE = "preload image";
    private Bitmap imageBitmapPreload;
    FragmentImageSearchBinding binding;
    private ImageModel imageModel;
    ImageViewModel imageViewModel;
    MediaCommentViewModel commentViewModel;
    MediaLikeViewModel likeViewModel;
    MediaSaveViewModel saveViewModel;
    MyLikedMediaViewModel myLikeViewModel;
    MySavedMediaViewModel mySaveViewModel;
    CategoryViewModel categoryViewModel;
    CategoryAdapter categoryAdapter;
    CommentAdapter commentAdapter;
    ImageCategoryViewModel imageCategoryViewModel;
    private String myUserId = FirebaseHelper.getInstance().getAuth().getCurrentUser().getUid();
    UserFriendCompatibilityActionViewModel userFriendCompatibilityActionViewModel;
    UserFriendCompatibilityViewModel userFriendCompatibilityViewModel;

    public ImageSearchFragment(Bitmap imageBitmapPreload) {
        this.imageBitmapPreload = imageBitmapPreload;
    }

    public static ImageSearchFragment newInstance(ImageModel imageModel, Bitmap imageBitmapPreload) {
        ImageSearchFragment fragment = new ImageSearchFragment(imageBitmapPreload);
        Bundle args = new Bundle();
        args.putParcelable(ARG_IMAGE, imageModel);
//        args.putParcelable(ARG_PRELOADED_IMAGE, imageBitmapPreload);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            imageModel = getArguments().getParcelable(ARG_IMAGE);
//            imageBitmapPreload = getArguments().getParcelable(ARG_PRELOADED_IMAGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentImageSearchBinding.inflate(inflater, container, false);
        initUI();

        retrieveData();


        return binding.getRoot();
    }
    private void retrieveData() {
        categoryViewModel = CategoryViewModel.newInstance();
        imageCategoryViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()).create(ImageCategoryViewModel.class);
        imageCategoryViewModel.getImageCategoriesByImageIdFirebase(imageModel, new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                imageCategoryViewModel.setImageCategories(task.getResult().toObjects(ImageCategoryModel.class));
                categoryAdapter.setImageCategoryModels(imageCategoryViewModel.getImageCategories());
            }
        });

    }


    private void initUI() {
//        binding.ivImage.setImageBitmap(BitmapPlaceholderUtil.getBitmap());
//        Glide.with(this).asBitmap().load(imageUriPreload).into(binding.ivImage);

        binding.flOutside.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager().popBackStack();
        });
        binding.cvInside.setOnClickListener(v -> {

        });

        imageViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()).create(ImageViewModel.class);
        commentViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()).create(MediaCommentViewModel.class);
        likeViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()).create(MediaLikeViewModel.class);
        saveViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()).create(MediaSaveViewModel.class);
        myLikeViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()).create(MyLikedMediaViewModel.class);
        mySaveViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()).create(MySavedMediaViewModel.class);
        userFriendCompatibilityActionViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()).create(UserFriendCompatibilityActionViewModel.class);
        userFriendCompatibilityViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()).create(UserFriendCompatibilityViewModel.class);

        initButtons();
        initCategories();
        initComments();

        setImage();
    }
    private void initCategories() {
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(requireContext().getApplicationContext());
        layoutManager.setFlexDirection(FlexDirection.ROW);
        binding.rvCategories.setLayoutManager(layoutManager);
        categoryViewModel = CategoryViewModel.newInstance();
        categoryAdapter = new CategoryAdapter(new ArrayList<>());
        binding.rvCategories.setAdapter(categoryAdapter);
        categoryViewModel.addCategoryObserver(categoryAdapter);

    }

    private void initComments() {
        binding.rvComments.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        Log.d("TAG", "initComments: " + imageModel.toString());
        commentViewModel.getMediaCommentsByMediaId(imageModel.iId, new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                commentViewModel.setMediaComments(task.getResult().toObjects(MediaCommentModel.class));
                commentAdapter = new CommentAdapter(commentViewModel.getMediaComments());
                binding.rvComments.setAdapter(commentAdapter);
            }
        });


    }
    private void initButtons() {
//        binding.ivDownloadImage.setOnClickListener(v -> {
//            downloadImageLikeTinCoder(imageModel.imageURL, imageModel.iId);
//            binding.ivDownloadImage.setImageResource(R.drawable.ic_download_done);
//        });
//        Toast.makeText(requireContext(), "ready to tai anh", Toast.LENGTH_SHORT).show();
//        binding.ivDownloadImage.setOnClickListener(v -> {
//            Toast.makeText(requireContext(), "hello tai anh ", Toast.LENGTH_SHORT).show();
//            downloadImageLikeBuiQuangHuy(resource, imageModel.iId);
//            binding.ivDownloadImage.setImageResource(R.drawable.ic_download_done);
//        })

        binding.ivDownloadImage.setOnClickListener(v -> {
            Log.d("ImageSearchFragment", "ivDownloadImage clicked");
//            downloadImageLikeBuiQuangHuy(imageBitmapPreload, imageModel.iId);
            downloadImageLikeTinCoder(imageModel);
            binding.ivDownloadImage.setImageResource(R.drawable.ic_download_done);
            Log.d("ImageSearchFragment", "Download initiated and icon changed");
        });

        binding.btnSendComment.setOnClickListener(v -> {
            Log.d("ImageSearchFragment", "btnSendComment clicked");
            String commentText = binding.etComment.getText().toString();
            Log.d("ImageSearchFragment", "Comment text: " + commentText);
            MediaCommentModel mediaComment = new MediaCommentModel(
                    "",
                    commentText,
                    myUserId,
                    imageModel.iId
                    );
            commentViewModel.addMediaCommentFirebase(mediaComment, task -> {
                if (task.isSuccessful()) {
                    Log.d("ImageSearchFragment", "Add MediaComment successful: " + mediaComment.getMcId());
                } else {
                    Log.e("ImageSearchFragment", "Add MediaComment failed: " + mediaComment.getMcId(), task.getException());
                }
            });
            binding.etComment.setText("");

        }
        );

        binding.ivLikeImage.setOnClickListener(v -> {
            Log.d("ImageSearchFragment", "ivLikeImage clicked");
            MediaLikeModel mediaLike = new MediaLikeModel(
                    "",
                    myUserId,
                    imageModel.iId
            );
            likeViewModel.addMediaLikeFirebase(mediaLike, task -> {
                if (task.isSuccessful()) {
                    Log.d("ImageSearchFragment", "Add MediaLike successful: " + mediaLike.getMlId());
                } else {
                    Log.e("ImageSearchFragment", "Add MediaLike failed: " + mediaLike.getMlId(), task.getException());
                }
            });
            Log.d("ImageSearchFragment", "Like action performed (commented out in original code)");
            MyLikedMediaModel myMediaLike = new MyLikedMediaModel(
                    "",
                    imageModel.iId,
                    ((int) System.currentTimeMillis())
            );
            myLikeViewModel.addMyLikedMediaFirebase(myMediaLike, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d("Add MySavedMedia", mediaLike.getMlId());
                }
            });

            UserFriendCompatibilityActionModel userFriendCompatibilityActionModel = new UserFriendCompatibilityActionModel(
                    "",
                    imageModel.iId,
                    "like",
                    myUserId
            );
            userFriendCompatibilityActionViewModel.addUfcaFirebase(userFriendCompatibilityActionModel, task -> {
                if (task.isSuccessful()) {
                    Log.d(ImageSearchFragment.class.getSimpleName(), "Add UFCA for save action successful for media ID: " + userFriendCompatibilityActionModel.getUfcaId());
                } else {
                    Log.e(ImageSearchFragment.class.getSimpleName(), "Add UFCA for save action failed for media ID: " + userFriendCompatibilityActionModel.getUfcaId(), task.getException());
                }
            });
            userFriendCompatibilityViewModel.checkAndCreateOrUpdateUserFriendCompatibility(imageModel.userId, myUserId, 3, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d(ImageSearchFragment.class.getSimpleName(), "Check and create or update UserFriendCompatibility successful");
                }
            });

        });

        binding.ivSaveImage.setOnClickListener(v -> {
            Log.d(ImageSearchFragment.class.getSimpleName(), "ivSaveImage clicked");
            MediaSaveModel mediaSave = new MediaSaveModel(
                    "",
                    myUserId,
                    imageModel.iId
            );
            Log.d(ImageSearchFragment.class.getSimpleName(), "MediaSaveModel created: " + mediaSave.toString());
            saveViewModel.addMediaSaveFirebase(mediaSave, task -> {
                if (task.isSuccessful()) {
                    Log.d(ImageSearchFragment.class.getSimpleName(), "Add MediaSave successful: " + mediaSave.getMsId());
                } else {
                    Log.e(ImageSearchFragment.class.getSimpleName(), "Add MediaSave failed: " + mediaSave.getMsId(), task.getException());
                }
            });
            MySavedMediaModel myMediaSave = new MySavedMediaModel(
                    "",
                    imageModel.iId,
                    ((int) System.currentTimeMillis())
            );
            mySaveViewModel.addMySavedMediaFirebase(myMediaSave, task -> {
                if (task.isSuccessful()) {
                    Log.d(ImageSearchFragment.class.getSimpleName(), "Add MySavedMedia successful for media ID: " + myMediaSave.getMediaId());
                } else {
                    Log.e(ImageSearchFragment.class.getSimpleName(), "Add MySavedMedia failed for media ID: " + myMediaSave.getMediaId(), task.getException());
                }
            });

            UserFriendCompatibilityActionModel userFriendCompatibilityActionModel = new UserFriendCompatibilityActionModel(
                    "",
                    imageModel.iId,
                    "save",
                    myUserId
            );
            userFriendCompatibilityActionViewModel.addUfcaFirebase(userFriendCompatibilityActionModel, task -> {
                if (task.isSuccessful()) {
                    Log.d(ImageSearchFragment.class.getSimpleName(), "Add UFCA for save action successful for media ID: " + userFriendCompatibilityActionModel.getUfcaId());
                } else {
                    Log.e(ImageSearchFragment.class.getSimpleName(), "Add UFCA for save action failed for media ID: " + userFriendCompatibilityActionModel.getUfcaId(), task.getException());
                }
            });
            userFriendCompatibilityViewModel.checkAndCreateOrUpdateUserFriendCompatibility(imageModel.userId, myUserId, 3, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d(ImageSearchFragment.class.getSimpleName(), "Check and create or update UserFriendCompatibility successful");
                }
            });
        });

        binding.ivShareImage.setOnClickListener(v -> {
            binding.ivShareImage.setImageResource(R.drawable.ic_loading3);
            String url = "";
            if (imageModel.iId.length() > 36) {
                url = MediaManager.get().url()
                        .transformation(new Transformation().quality("auto").chain().fetchFormat("auto"))
                        .generate(imageModel.imageName);
                Log.d("ImageSearchFragment", "ivShareImage URL CLOUDINARY: " + url);
            } else {
                url = imageModel.imageURL;
                Log.d("ImageSearchFragment", "ivShareImage URL FireStore: " + url);
            }
            Log.d("ImageSearchFragment", "Sharing image with URL: " + url);
            Glide.with(requireContext()).asBitmap().load(url)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        Log.d("ImageSearchFragment", "Share image resource ready");
                        binding.ivShareImage.setImageResource(R.drawable.ic_download_done);
                        shareImageToOtherApps(resource);
                        Log.d("ImageSearchFragment", "Image shared and icon changed");

                    }
                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        Log.d("ImageSearchFragment", "Share image load cleared");
                    }
                });
            Log.d("ImageSearchFragment", "Glide request for sharing image sent");
        });
    }

    @Override
    public void onPause() {

        updateImageCategories(categoryAdapter.getSelectedCategories(), imageCategoryViewModel.getImageCategories());

        super.onPause();
    }

    private void setImage() {
        binding.ivImage.setImageBitmap(imageBitmapPreload);
        Zoomy.Builder builder = new Zoomy.Builder(requireActivity()).target(binding.ivImage).enableImmersiveMode(false);
        builder.register();
        binding.setImageModel(imageModel);
        String url = "";
        if (imageModel.iId.length() > 36) {
            url = MediaManager.get().url()
                    .transformation(new Transformation().quality("auto").chain().fetchFormat("auto"))
                    .generate(imageModel.imageName);
            Log.d("URL CLOUDINARY", url);
        } else {
            url = imageModel.imageURL;
            Log.d("URL FireStore", url);
        }
        Log.d("Fetch Full Image in Fragment", url);
        Glide.with(this).asBitmap().load(url)
//                .placeholder(new BitmapDrawable(getResources(), imageBitmapPreload)).into(binding.ivImage);
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        binding.ivImage.setImageBitmap(resource);
//                        binding.ivEditImage.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
////                                Uri sourceUri = bitmapToFileUri(resource);
//
////                                Uri destinationUri = Uri.fromFile(new File(requireActivity().getCacheDir(), "cropped_image.jpg" + System.currentTimeMillis()));
////                                UCrop.of(sourceUri, destinationUri)
////                                        .start(getContext(), ImageFragment.this);  // 'this' refers to Activity or Fragment
//
//                            }
//                        });
//                        binding.ivEditImage.setOnLongClickListener(new View.OnLongClickListener() {
//                            @Override
//                            public boolean onLongClick(View v) {
//                                return false;
//                            }
//                        });
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });

//        DisplayMetrics displayMetrics = requireActivity().getApplicationContext().getResources().getDisplayMetrics();
//        int screenHeight = displayMetrics.heightPixels;
//        int imageHeight = screenHeight / 8; // 1/6 of the screen height
//
//        ViewGroup.LayoutParams layoutParams = binding.ivImage.getLayoutParams();
//        if (layoutParams.height > screenHeight / 4) {
//            layoutParams.height = imageHeight;
//            binding.ivImage.setLayoutParams(layoutParams);
//        }

    }


    private void downloadImageLikeTinCoder(ImageModel imageModel) {
        String imageUrl = imageModel.imageURL;
        String iId = imageModel.iId;
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(imageUrl));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setTitle("Download from meme storage");
        request.setDescription("Downloading...");

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        String directoryPath = Environment.DIRECTORY_PICTURES;
        String filePath = "MemeStorage/" + System.currentTimeMillis() + "and" + iId + ".jpg";

        request.setDestinationInExternalPublicDir(directoryPath, filePath);

        DownloadManager downloadManager = (DownloadManager) requireContext().getSystemService(Context.DOWNLOAD_SERVICE);
        if (downloadManager != null) {
            downloadManager.enqueue(request);
        }
    }
//
//    private void downloadImageLikeBuiQuangHuy(Bitmap finalBitmap, String iId) {
//        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
//
//        File myDir = new File(Environment.DIRECTORY_PICTURES, "MemeStorage");
//
//        if (!myDir.exists()) {
//            if (!myDir.mkdirs()) {
//                Log.e("Path Download folder", "Failed to create MemeStorage directory.");
//                return;
//            }
//        }
//
//        String fileName = "Meme_" + System.currentTimeMillis() + "_and_" + iId + ".jpg";
//        File file = new File(myDir, fileName);
//
//        if (file.exists()) file.delete();
//
//        Log.i("Path Download image", file.getAbsolutePath());
//
//        try {
//            FileOutputStream out = new FileOutputStream(file);
//            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
//            out.flush();
//            out.close();
//
//            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//            Uri contentUri = Uri.fromFile(file);
//            mediaScanIntent.setData(contentUri);
//            requireContext().sendBroadcast(mediaScanIntent);
//
//            Toast.makeText(requireContext(), "Image saved to Pictures/MemeStorage", Toast.LENGTH_SHORT).show();
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.e("Path Download image", "Error saving image: " + e.getMessage());
//        }
//    }

    private void shareImageToOtherApps(Bitmap bitmap) {
        Single.<Uri>create(emitter -> {
                    Uri imageUri = saveImageToCache(bitmap);
                    emitter.onSuccess(imageUri);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Uri>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull Uri imageUri) {
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("image/*");
                        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                        requireActivity().startActivity(Intent.createChooser(shareIntent, "Share Image via"));

                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {

                    }
                });
    }

    private Uri saveImageToCache(Bitmap bitmap) {
        File cachePath = new File(requireContext().getCacheDir(), "images");
        cachePath.mkdirs(); // Create directory if needed
        File file = new File(cachePath, "image.png");
        try (FileOutputStream stream = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + ".fileprovider", file);
    }


    private void updateImageCategories(Set<String> selectedCategories, List<ImageCategoryModel> imageCategoryModels) {
        List<String> unTouchedImageCategories = new ArrayList<>();
        for (ImageCategoryModel imageCategoryModel : imageCategoryModels) {
            if (!selectedCategories.contains(imageCategoryModel.categoryId)) {
                imageCategoryViewModel.deleteImageCategoryFirebase(imageCategoryModel.icId, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("Delete ImageCategory", imageCategoryModel.icId);
                    }
                });
            } else {
                selectedCategories.remove(imageCategoryModel.categoryId);
                unTouchedImageCategories.add(imageCategoryModel.categoryId);
            }
        }

        for (String selectedCategory : selectedCategories) {
            ImageCategoryModel imageCategoryModel = new ImageCategoryModel();
            imageCategoryModel.imageId = imageModel.iId;;
            imageCategoryModel.categoryId = selectedCategory;
            imageCategoryViewModel.addImageCategoryFirebase(imageCategoryModel, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d("Add ImageCategory", imageCategoryModel.imageId + " " + imageCategoryModel.categoryId);
                }
            });
            Log.d("Add Failed ImageCategory", imageCategoryModel.imageId + " " + imageCategoryModel.categoryId);

        };

        for (String unTouchedImageCategory : unTouchedImageCategories) {
            selectedCategories.add(unTouchedImageCategory);
        }
        //TODO: cái đoạn cuối này để làm gì nhỉ??

    }

}