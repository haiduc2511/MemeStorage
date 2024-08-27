package com.example.memestorage.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memestorage.R;
import com.example.memestorage.databinding.ItemSuggestedCategoryBinding;
import com.example.memestorage.fragments.CategorySuggestFragment;
import com.example.memestorage.models.CategoryModel;
import com.example.memestorage.models.ImageModel;

import java.util.ArrayList;
import java.util.List;

public class SuggestCategoryAdapter extends RecyclerView.Adapter<SuggestCategoryAdapter.SuggestCategoryViewHolder> {
    private List<CategoryModel> categoryModels = new ArrayList<>();
    private CategorySuggestFragment.OnSuggestedCategoryClickListener categoryClickListener;
    public SuggestCategoryAdapter(CategorySuggestFragment.OnSuggestedCategoryClickListener categoryClickListener) {
        this.categoryClickListener = categoryClickListener;
    }
    public void setCategoryModels(List<CategoryModel> categoryModels) {
        this.categoryModels = categoryModels;
        notifyDataSetChanged();
    }
    public void addCategoryModel(CategoryModel categoryModel) {
        categoryModels.add(categoryModel);
        notifyItemInserted(categoryModels.size() - 1);
    }
    @NonNull
    @Override
    public SuggestCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSuggestedCategoryBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_suggested_category, parent, false);
        return new SuggestCategoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestCategoryViewHolder holder, int position) {
        CategoryModel categoryModel = categoryModels.get(position);
        holder.bind(categoryModel);
        holder.itemView.setOnClickListener(v -> {
            categoryClickListener.onSuggestedCategoryClick(categoryModel);
        });
    }

    @Override
    public int getItemCount() {
        return categoryModels.size();
    }
    public class SuggestCategoryViewHolder extends RecyclerView.ViewHolder {
        ItemSuggestedCategoryBinding binding;

        public SuggestCategoryViewHolder(@NonNull ItemSuggestedCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }

        public void bind(CategoryModel categoryModel) {
            binding.setCategoryModel(categoryModel);
        }

    }
}