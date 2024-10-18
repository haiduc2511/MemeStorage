package com.example.memestorage.utils;

import com.example.memestorage.models.ImageCategoryModel;

import java.util.List;

public interface AIImageCategoryResponseListener {
    public void onReceiveAIImageCategorySuggestions(List<ImageCategoryModel> imageCategoryModelList, String responseText);
}
