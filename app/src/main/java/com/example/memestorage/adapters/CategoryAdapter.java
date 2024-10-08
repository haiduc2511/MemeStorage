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
import com.example.memestorage.utils.CategoryObserver;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> implements CategoryObserver {
    private List<CategoryModel> categoryModels;
    private Set<String> selectedCategories = new HashSet<>();

    public CategoryAdapter(List<CategoryModel> categoryModels) {
        this.categoryModels = categoryModels;
    }
    public void setCategoryModels(List<CategoryModel> categoryModels) {
        this.categoryModels = categoryModels;
        notifyDataSetChanged();
    }
    public void setImageCategoryModels(List<ImageCategoryModel> imageCategoryModels) {
        for (ImageCategoryModel imageCategoryModel : imageCategoryModels) {
            selectedCategories.add(imageCategoryModel.categoryId);
        }
        for (int i = 0; i < categoryModels.size(); i++) {
            if (selectedCategories.contains(categoryModels.get(i).cId)) {
                notifyItemChanged(i);
            }
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
        } else {
            holder.binding.clLayout.setBackgroundColor(Color.BLACK);
        }
        holder.bind(categoryModel);
        holder.binding.clLayout.setOnClickListener(v -> {
            holder.toggleSelection(holder, categoryModel.cId);
        });
    }

    @Override
    public int getItemCount() {
        return categoryModels.size();
    }
    @Override
    public void notifyAdapter(List<CategoryModel> categoryModels) {
        this.setCategoryModels(categoryModels);
    }
    @Override
    public void notifyCategoryInserted(CategoryModel categoryModel) {
        notifyItemInserted(categoryModels.size() - 1);
    }

    @Override
    public void notifyCategoryDeleted(int position) {
        notifyItemRemoved(position);
    }

    @Override
    public void notifyCategoryUpdated(int position) {
        notifyItemChanged(position);
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
                holder.binding.clLayout.setBackgroundColor(Color.BLACK);
                selectedCategories.remove(categoryId);
            } else {
                holder.binding.clLayout.setBackgroundColor(Color.GREEN);
                selectedCategories.add(categoryId);
            }
        }
    }
}