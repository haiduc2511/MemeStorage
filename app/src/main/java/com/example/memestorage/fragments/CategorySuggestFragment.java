package com.example.memestorage.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.memestorage.R;
import com.example.memestorage.adapters.CategoryAdapter;
import com.example.memestorage.databinding.FragmentCategorySuggestBinding;
import com.example.memestorage.databinding.FragmentImageBinding;
import com.example.memestorage.models.ImageModel;
import com.example.memestorage.viewmodels.CategoryViewModel;
import com.example.memestorage.viewmodels.ImageCategoryViewModel;
import com.example.memestorage.viewmodels.ImageViewModel;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;

import java.util.ArrayList;

public class CategorySuggestFragment extends Fragment {
    private static final String ARG_IMAGE = "image";
    FragmentCategorySuggestBinding binding;
    private ImageModel imageModel;
    ImageViewModel imageViewModel;
    CategoryViewModel categoryViewModel;
    CategoryAdapter categoryAdapter;
    ImageCategoryViewModel imageCategoryViewModel;

    public static CategorySuggestFragment newInstance() {
        CategorySuggestFragment fragment = new CategorySuggestFragment();
//        Bundle args = new Bundle();
//        args.putParcelable(ARG_IMAGE, imageModel);
//        fragment.setArguments(args);
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
        binding = FragmentCategorySuggestBinding.inflate(inflater, container, false);
        initUI();

        retrieveData();

        return binding.getRoot();
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
     }

    private void retrieveData() {

    }
}