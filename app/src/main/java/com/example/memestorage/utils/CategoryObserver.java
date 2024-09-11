package com.example.memestorage.utils;

import com.example.memestorage.models.CategoryModel;

import java.util.List;

public interface CategoryObserver {
    public void notifyAdapter(List<CategoryModel> categoryModels);
    public void notifyCategoryInserted(CategoryModel categoryModel);
    public void notifyCategoryDeleted(int position);
    public void notifyCategoryUpdated(int position);
}
