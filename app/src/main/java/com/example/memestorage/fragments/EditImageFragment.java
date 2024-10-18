package com.example.memestorage.fragments;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.cloudinary.Transformation;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.memestorage.R;
import com.example.memestorage.activities.MainActivity;
import com.example.memestorage.adapters.ImageAdapter;
import com.example.memestorage.databinding.FragmentEditImageBinding;
import com.example.memestorage.models.ImageCategoryModel;
import com.example.memestorage.models.ImageModel;
import com.example.memestorage.utils.AIImageCategoryResponseListener;
import com.example.memestorage.utils.FirebaseHelper;
import com.example.memestorage.utils.ImageItemTouchHelper;
import com.example.memestorage.utils.ImageUploadListener;
import com.example.memestorage.viewmodels.ImageCategoryViewModel;
import com.example.memestorage.viewmodels.ImageViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class EditImageFragment extends Fragment {
    FragmentEditImageBinding binding;
    ImageViewModel imageViewModel;
    ImageCategoryViewModel imageCategoryViewModel;
    private static final String ARG_IMAGE = "image";
    ImageItemTouchHelper.ImageEditListener imageEditListener;
    ImageUploadListener imageUploadListener;
    private ImageModel imageModel;
    String myUserId = Objects.requireNonNull(FirebaseHelper.getInstance().getAuth().getCurrentUser()).getUid();


    public void setImageUploadListener(ImageUploadListener imageUploadListener) {
        this.imageUploadListener = imageUploadListener;
    }

    public void setImageEditListener(ImageItemTouchHelper.ImageEditListener imageEditListener) {
        this.imageEditListener = imageEditListener;
    }

    public EditImageFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static EditImageFragment newInstance(ImageModel imageModel) {
        EditImageFragment fragment = new EditImageFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_IMAGE, imageModel);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            imageModel = getArguments().getParcelable(ARG_IMAGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEditImageBinding.inflate(getLayoutInflater(), container, false);
        imageViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()).create(ImageViewModel.class);
        imageCategoryViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()).create(ImageCategoryViewModel.class);

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


        initUI();
        setImage(url);
//        openUCrop(url);

        return binding.getRoot();
    }

    private void initUI() {
        binding.flOutside.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager().popBackStack();
        });
        binding.cvInside.setOnClickListener(v -> {

        });
    }

    private void setImage(String url) {
        Glide.with(this).asBitmap().load(url)
//                .placeholder(new BitmapDrawable(getResources(), imageBitmapPreload)).into(binding.ivImage);
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        binding.ivImage.setImageBitmap(resource);
                        openUCrop(bitmapToFileUri(resource));
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void openUCrop(Uri uri) {
        Uri sourceUri = uri;
        Uri destinationUri = Uri.fromFile(new File(requireActivity().getCacheDir(), "cropped_image.jpg" + System.currentTimeMillis()));
        UCrop.of(sourceUri, destinationUri)
                .start(getContext(), EditImageFragment.this);  // 'this' refers to Activity or Fragment

    }



    private Uri bitmapToFileUri(Bitmap bitmap) {

        try {
            File file = new File(requireActivity().getCacheDir(), "temp_image2.jpg"); // Use your desired file name
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out); // Compress bitmap to file
            out.flush();
            out.close();
            return Uri.fromFile(file); // Return the Uri of the saved file
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    private void applyDarkenEffect() {
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0); // Make it grayscale
        colorMatrix.setScale(0.5f, 0.5f, 0.5f, 1.0f); // Reduce brightness

        binding.ivImage.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
    }

    // Remove the darkening effect from the ImageView
    private void removeDarkenEffect() {
        binding.ivImage.clearColorFilter();
    }

        @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            Uri resultUri = UCrop.getOutput(data);
            binding.ivImage.setImageURI(resultUri);
            binding.btReplaceOldImageAfterEditing.setOnClickListener(v -> {
                imageViewModel.uploadReplaceImageCloudinary(resultUri, getActivity().getContentResolver(), imageModel, new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        Log.d("Replace progress", "Replace progress starts");
                        binding.pbImageEditLoading.setProgress(0);
                        binding.pbImageEditLoading.setVisibility(View.VISIBLE);
                        applyDarkenEffect();
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {
                        Log.d("Replace progress", "Replace progress: " + requestId + " - " + bytes + "/" + totalBytes);
                        int progress = (int) ((bytes * 100) / totalBytes);
                        binding.pbImageEditLoading.setProgress(progress, true);
                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        Log.d("Replace progress", "Replace progress successful");
                        binding.pbImageEditLoading.setProgress(100);
                        removeDarkenEffect();

                        String imageUrl = (String) resultData.get("secure_url");
                        String imageName = (String) resultData.get("public_id");
                        imageModel.imageURL = imageUrl;
                        imageModel.imageName = imageName;
                        imageViewModel.updateImageFirebase(imageModel.iId, imageModel, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d("Edit image", "Success editing " + imageModel.toString());
                                binding.pbImageEditLoading.setVisibility(View.GONE);
                            }
                        });
                        imageEditListener.onImageEdited();
//                        getActivity().getSupportFragmentManager().popBackStack();

                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Log.d("Replace progress", "Replace progress failed");
                        binding.pbImageEditLoading.setVisibility(View.GONE);
                        removeDarkenEffect();

                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {

                    }
                });
            });

            binding.btAddNewImageAfterEditing.setOnClickListener(v -> {
                List<Uri> uriList = new ArrayList<>();
                uriList.add(resultUri);
                imageViewModel.uploadImagesCloudinary(uriList, new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {

                        binding.pbImageEditLoading.setProgress(0);
                        Log.d("Add new image progress", "Add new image progress starts");
                        binding.pbImageEditLoading.setVisibility(View.VISIBLE);
                        applyDarkenEffect();
                        binding.flOutside.setOnClickListener(v -> {
                        });

                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {
                        Log.d("Add new image progress", "Add new image progress: " + requestId + " - " + bytes + "/" + totalBytes);
                        int progress = (int) ((bytes * 100) / totalBytes);
                        binding.pbImageEditLoading.setProgress(progress, true);

                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        String imageUrl = (String) resultData.get("secure_url");
                        Log.d("Add new image progress", "Add new image progress successfull Image URL: " + imageUrl);
                        binding.pbImageEditLoading.setProgress(100);
                        removeDarkenEffect();
                        binding.pbImageEditLoading.setVisibility(View.GONE);



                        ImageModel imageModel = new ImageModel();
                        String extractedPublicId = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
                        String extractedPublicIdWithoutExtension = extractedPublicId.substring(0, extractedPublicId.lastIndexOf("."));
                        imageModel.iId = extractedPublicIdWithoutExtension;
                        imageModel.imageName = (String) resultData.get("public_id");
                        imageModel.userId = myUserId;
                        imageModel.imageURL = imageUrl;
                        imageModel = imageViewModel.addImageFirebase(imageModel);
                        Log.d("Upload cloudinary", "Upload images successful. Download URL: " + imageModel.toString() + " \n" +  imageUrl.toString());


                        imageUploadListener.onSuccessUploadingImages(imageModel);

                        String url = MediaManager.get().url()
                                .transformation(new Transformation().quality("auto").chain().fetchFormat("auto"))
                                .generate(imageModel.imageName);
                        ImageModel finalImageModel = imageModel;
                        Glide.with(EditImageFragment.this).asBitmap().load(url)
                                .into(new CustomTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {
                                        imageCategoryViewModel.getAICategoriesSuggestions(bitmap, finalImageModel, new AIImageCategoryResponseListener() {
                                            @Override
                                            public void onReceiveAIImageCategorySuggestions(List<ImageCategoryModel> imageCategoryModelList, String responseText) {

                                            }
                                        });
                                        Log.d("Image size before giving to Gemini", String.valueOf(bitmap.getAllocationByteCount()));
                                        binding.flOutside.setOnClickListener(v -> {
                                            getActivity().getSupportFragmentManager().popBackStack();
                                        });
                                    }

                                    @Override
                                    public void onLoadCleared(@Nullable Drawable placeholder) {

                                    }
                                });
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Log.d("Replace progress", "Replace progress failed");
                        binding.pbImageEditLoading.setVisibility(View.GONE);
                        removeDarkenEffect();

                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {

                    }
                });
            });
        } else if (resultCode == UCrop.RESULT_ERROR) {
            Throwable cropError = UCrop.getError(data);
            cropError.printStackTrace();
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }


}