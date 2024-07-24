package com.example.memestorage.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memestorage.R;
import com.example.memestorage.databinding.ItemCategoryBinding;
import com.example.memestorage.models.CategoryModel;
import com.example.memestorage.models.ImageCategoryModel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AddCategoryCategoryAdapter extends RecyclerView.Adapter<AddCategoryCategoryAdapter.AddCategoryCategoryViewHolder> {
    private List<CategoryModel> categoryModels;
    public AddCategoryCategoryAdapter(List<CategoryModel> categoryModels) {
        this.categoryModels = categoryModels;
    }

    @NonNull
    @Override
    public AddCategoryCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCategoryBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_category, parent, false);
        return new AddCategoryCategoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AddCategoryCategoryViewHolder holder, int position) {
        CategoryModel categoryModel = categoryModels.get(position);
        holder.bind(categoryModel);
    }

    @Override
    public int getItemCount() {
        return categoryModels.size();
    }

    public class AddCategoryCategoryViewHolder extends RecyclerView.ViewHolder {
        ItemCategoryBinding binding;

        public AddCategoryCategoryViewHolder(@NonNull ItemCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }

        public void bind(CategoryModel categoryModel) {
            binding.setCategoryModel(categoryModel);
        }

    }
}