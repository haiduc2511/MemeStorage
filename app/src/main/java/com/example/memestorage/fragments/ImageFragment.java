package com.example.memestorage.fragments;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ablanco.zoomy.Zoomy;
import com.bumptech.glide.Glide;
import com.cloudinary.Transformation;
import com.cloudinary.android.MediaManager;
import com.example.memestorage.R;
import com.example.memestorage.adapters.CategoryAdapter;
import com.example.memestorage.adapters.MainCategoryAdapter;
import com.example.memestorage.databinding.FragmentImageBinding;
import com.example.memestorage.models.ImageCategoryModel;
import com.example.memestorage.models.ImageModel;
import com.example.memestorage.viewmodels.CategoryViewModel;
import com.example.memestorage.viewmodels.ImageCategoryViewModel;
import com.example.memestorage.viewmodels.ImageViewModel;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ImageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImageFragment extends Fragment {
    public interface OnImageCategoryRetrieved {
        public void OnImageCategoryRetrieved();
    }

    private static final String ARG_IMAGE = "image";
    private static final String ARG_PRELOADED_IMAGE = "preload image";
    private Bitmap imageBitmapPreload;
    FragmentImageBinding binding;
    private ImageModel imageModel;
    ImageViewModel imageViewModel;
    CategoryViewModel categoryViewModel;
    CategoryAdapter categoryAdapter;
    ImageCategoryViewModel imageCategoryViewModel;

    public static ImageFragment newInstance(ImageModel imageModel, Bitmap imageBitmapPreload) {
        ImageFragment fragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_IMAGE, imageModel);
        args.putParcelable(ARG_PRELOADED_IMAGE, imageBitmapPreload);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            imageModel = getArguments().getParcelable(ARG_IMAGE);
            imageBitmapPreload = getArguments().getParcelable(ARG_PRELOADED_IMAGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentImageBinding.inflate(inflater, container, false);
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

        binding.flOutside.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager().popBackStack();
        });
        binding.cvInside.setOnClickListener(v -> {

        });

        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(requireContext().getApplicationContext());
        layoutManager.setFlexDirection(FlexDirection.ROW);
        binding.rvCategories.setLayoutManager(layoutManager);
        categoryViewModel = CategoryViewModel.newInstance();
        categoryAdapter = new CategoryAdapter(new ArrayList<>());
        binding.rvCategories.setAdapter(categoryAdapter);
        categoryViewModel.addCategoryObserver(categoryAdapter);

        imageViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()).create(ImageViewModel.class);
        setImage();
    }

    @Override
    public void onPause() {

        updateImageCategories(categoryAdapter.getSelectedCategories(), imageCategoryViewModel.getImageCategories());

        super.onPause();
    }

    private void setImage() {
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
        Glide.with(this).load(url)
                .placeholder(new BitmapDrawable(getResources(), imageBitmapPreload))
                .into(binding.ivImage);

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

    }
}