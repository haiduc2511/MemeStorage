package com.example.memestorage.fragments;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
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
import com.example.memestorage.adapters.ImageAdapter;
import com.example.memestorage.databinding.FragmentEditImageBinding;
import com.example.memestorage.models.ImageModel;
import com.example.memestorage.utils.ImageItemTouchHelper;
import com.example.memestorage.viewmodels.ImageCategoryViewModel;
import com.example.memestorage.viewmodels.ImageViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

public class EditImageFragment extends Fragment {
    FragmentEditImageBinding binding;
    ImageViewModel imageViewModel;
    private static final String ARG_IMAGE = "image";
    ImageItemTouchHelper.ImageEditListener imageEditListener;
    private ImageModel imageModel;

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
        openUCrop(url);

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
//        Glide.with(this).asBitmap().load(url)
////                .placeholder(new BitmapDrawable(getResources(), imageBitmapPreload)).into(binding.ivImage);
//                .into(new CustomTarget<Bitmap>() {
//                    @Override
//                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                        binding.ivImage.setImageBitmap(resource);
//                    }
//
//                    @Override
//                    public void onLoadCleared(@Nullable Drawable placeholder) {
//
//                    }
//                });

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void openUCrop(String url) {
        Uri sourceUri = Uri.parse(url);

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

        @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            Uri resultUri = UCrop.getOutput(data);
            binding.ivImage.setImageURI(resultUri);
            binding.btReplaceOldImageAfterEditing.setOnClickListener(v -> {
                imageViewModel.uploadReplaceImageCloudinary(resultUri, imageModel, new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {

                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {
                        Log.d(TAG, "Replace progress: " + requestId + " - " + bytes + "/" + totalBytes);
                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        Log.d(TAG, "Replace progress successful");


                        String imageUrl = (String) resultData.get("secure_url");
                        String imageName = (String) resultData.get("public_id");
                        imageModel.imageURL = imageUrl;
                        imageModel.imageName = imageName;
                        imageViewModel.updateImageFirebase(imageModel.iId, imageModel, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d("Edit image", "Success editing " + imageModel.toString());
                            }
                        });
                        imageEditListener.onImageEdited();
                        getActivity().getSupportFragmentManager().popBackStack();

                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Log.d(TAG, "Replace progress failed");

                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {

                    }
                });
            });

            binding.btAddNewImageAfterEditing.setOnClickListener(v -> {

            });
        } else if (resultCode == UCrop.RESULT_ERROR) {
            Throwable cropError = UCrop.getError(data);
            cropError.printStackTrace();
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }


}