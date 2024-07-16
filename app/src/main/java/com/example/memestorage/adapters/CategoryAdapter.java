package com.example.memestorage.adapters;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memestorage.R;
import com.example.memestorage.databinding.ItemCategoryBinding;
import com.example.memestorage.models.CategoryModel;
import com.example.memestorage.models.ImageCategoryModel;
import com.example.memestorage.models.ImageModel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private List<CategoryModel> categoryModels;
    private List<ImageCategoryModel> imageCategoryModels;
    private Set<String> selectedCategories;

    public CategoryAdapter(List<CategoryModel> categoryModels, List<ImageCategoryModel> imageCategoryModels) {
        this.categoryModels = categoryModels;
        this.imageCategoryModels = imageCategoryModels;
        selectedCategories = new HashSet<>();
        for (ImageCategoryModel imageCategoryModel : imageCategoryModels) {
            selectedCategories.add(imageCategoryModel.categoryId);
        }
    }

    public Set<String> getSelectedCategories() {
        return selectedCategories;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCategoryBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_category, parent, false);
        return new CategoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        CategoryModel categoryModel = categoryModels.get(position);
        if (selectedCategories.contains(categoryModel.cId)) {
            holder.binding.clLayout.setBackgroundColor(Color.GREEN);
        };
        holder.bind(categoryModel);
        holder.binding.clLayout.setOnClickListener(v -> {
            holder.toggleSelection(holder, categoryModel.cId);
        });
    }

    @Override
    public int getItemCount() {
        return categoryModels.size();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        ItemCategoryBinding binding;

        public CategoryViewHolder(@NonNull ItemCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }

        public void bind(CategoryModel categoryModel) {
            binding.setCategoryModel(categoryModel);
        }

        public void toggleSelection(@NonNull CategoryViewHolder holder, String categoryId) {
            if (selectedCategories.contains(categoryId)) {
                holder.binding.clLayout.setBackgroundColor(Color.WHITE);
                selectedCategories.remove(categoryId);
            } else {
                holder.binding.clLayout.setBackgroundColor(Color.GREEN);
                selectedCategories.add(categoryId);
            }
        }
    }
}