package com.example.memestorage.fragments;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ablanco.zoomy.Zoomy;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.cloudinary.Transformation;
import com.cloudinary.android.MediaManager;
import com.example.memestorage.R;
import com.example.memestorage.adapters.CategoryAdapter;
import com.example.memestorage.databinding.FragmentDoubleCheckAISuggestionsBinding;
import com.example.memestorage.databinding.FragmentImageBinding;
import com.example.memestorage.models.CategoryModel;
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

public class DoubleCheckAISuggestionsFragment extends Fragment {

    private static final String ARG_IMAGE_PRELOAD = "preload bitmap image";
    private static final String ARG_IMAGE_CATEGORY_SUGGESTION_LIST = "image category suggestion list";
    private static final String ARG_IMAGE_MODEL = "image model";
    private static final String ARG_RESPONSE_TEXT = "response text";

    private Bitmap imageBitmapPreload;
    private List<ImageCategoryModel> imageCategoryModelList;
    FragmentDoubleCheckAISuggestionsBinding binding;
    CategoryViewModel categoryViewModel;
    CategoryAdapter categoryAdapter;
    ImageCategoryViewModel imageCategoryViewModel;
    ImageModel imageModel;
    String responseText;

    public DoubleCheckAISuggestionsFragment() {
        // Required empty public constructor
    }

    public static DoubleCheckAISuggestionsFragment newInstance(Bitmap imageBitmapPreload, List<ImageCategoryModel> imageCategoryModelList, ImageModel imageModel, String responseText) {
        DoubleCheckAISuggestionsFragment fragment = new DoubleCheckAISuggestionsFragment();
        Bundle args = new Bundle();
        ArrayList<ImageCategoryModel> imageCategoryModelArrayList = new ArrayList<>(imageCategoryModelList);
        args.putParcelableArrayList(ARG_IMAGE_CATEGORY_SUGGESTION_LIST, imageCategoryModelArrayList);
        args.putParcelable(ARG_IMAGE_PRELOAD, imageBitmapPreload);
        args.putParcelable(ARG_IMAGE_MODEL, imageModel);
        args.putString(ARG_RESPONSE_TEXT, responseText);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            imageBitmapPreload = getArguments().getParcelable(ARG_IMAGE_PRELOAD);
            imageModel = getArguments().getParcelable(ARG_IMAGE_MODEL);
            responseText = getArguments().getString(ARG_RESPONSE_TEXT);
            imageCategoryModelList = getArguments().getParcelableArrayList(ARG_IMAGE_CATEGORY_SUGGESTION_LIST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDoubleCheckAISuggestionsBinding.inflate(inflater, container, false);
        imageCategoryViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()).create(ImageCategoryViewModel.class);

        initUI();
        return binding.getRoot();
    }

    private void initUI() {
//        binding.ivImage.setImageBitmap(BitmapPlaceholderUtil.getBitmap());
//        Glide.with(this).asBitmap().load(imageUriPreload).into(binding.ivImage);

        binding.flOutside.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager().popBackStack();
        });
        binding.cvInside.setOnClickListener(v -> {

        });
        initCategories();
        initTextView();

        setImage();
    }
    @Override
    public void onPause() {

        updateImageCategories();

        super.onPause();
    }
    private void updateImageCategories() {


        for (String selectedCategory : categoryAdapter.getSelectedCategories()) {
            ImageCategoryModel imageCategoryModel = new ImageCategoryModel();
            imageCategoryModel.imageId = imageModel.iId;
            imageCategoryModel.categoryId = selectedCategory;
            imageCategoryViewModel.addImageCategoryFirebase(imageCategoryModel, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d("Add ImageCategory", imageCategoryModel.imageId + " " + imageCategoryModel.categoryId);
                }
            });
            Log.d("Add Failed ImageCategory", imageCategoryModel.imageId + " " + imageCategoryModel.categoryId);

        };
    }

    private void initCategories() {
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(requireContext().getApplicationContext());
        layoutManager.setFlexDirection(FlexDirection.ROW);
        binding.rvCategories.setLayoutManager(layoutManager);
        categoryViewModel = CategoryViewModel.newInstance();
        categoryAdapter = new CategoryAdapter(new ArrayList<>());
        binding.rvCategories.setAdapter(categoryAdapter);
        categoryViewModel.addCategoryObserver(categoryAdapter);

        categoryAdapter.setImageCategoryModels(imageCategoryModelList);


    }

    private void initTextView() {
        binding.tvCategorySuggested.setText(responseText);
    }


    private void setImage() {
        binding.ivImage.setImageBitmap(imageBitmapPreload);
        Zoomy.Builder builder = new Zoomy.Builder(requireActivity()).target(binding.ivImage).enableImmersiveMode(false);
        builder.register();

    }
}