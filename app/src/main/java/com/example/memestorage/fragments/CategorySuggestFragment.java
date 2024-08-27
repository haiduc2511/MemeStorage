package com.example.memestorage.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.memestorage.R;
import com.example.memestorage.adapters.CategoryAdapter;
import com.example.memestorage.adapters.SuggestCategoryAdapter;
import com.example.memestorage.databinding.FragmentCategorySuggestBinding;
import com.example.memestorage.databinding.FragmentImageBinding;
import com.example.memestorage.models.CategoryModel;
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

public class CategorySuggestFragment extends Fragment {
    private static final String ARG_IMAGE = "image";
    FragmentCategorySuggestBinding binding;
    CategoryViewModel categoryViewModel;
    SuggestCategoryAdapter suggestCategoryAdapter;
    public static CategorySuggestFragment newInstance() {
        CategorySuggestFragment fragment = new CategorySuggestFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

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
        suggestCategoryAdapter = new SuggestCategoryAdapter();
        binding.rvCategories.setAdapter(suggestCategoryAdapter);
     }

    private void retrieveData() {
        categoryViewModel.getSuggestedCategoriesFirebase(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    suggestCategoryAdapter.setCategoryModels(task.getResult().toObjects(CategoryModel.class));
                }
            }
        });
    }
}