package com.example.memestorage.adapters;

import android.content.Context;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AddCategoryCategoryAdapter extends RecyclerView.Adapter<AddCategoryCategoryAdapter.AddCategoryCategoryViewHolder> implements CategoryObserver {
    private List<CategoryModel> categoryModels = new ArrayList<>();
    private Context context;
    public AddCategoryCategoryAdapter(Context context) {
        this.context = context;
    }

    public List<CategoryModel> getCategoryModels() {
        return categoryModels;
    }

    public Context getContext() {
        return context;
    }

    public void setCategoryModels(List<CategoryModel> categoryModels) {
        this.categoryModels = categoryModels;
        notifyDataSetChanged();
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