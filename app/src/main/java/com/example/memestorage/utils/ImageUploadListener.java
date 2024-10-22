package com.example.memestorage.utils;

import android.graphics.Bitmap;

import com.example.memestorage.models.ImageCategoryModel;
import com.example.memestorage.models.ImageModel;

import java.util.List;

public interface ImageUploadListener {
    public void onSuccessUploadingImages(ImageModel imageModel);
    public void onSuccessGettingAISuggestions(Bitmap image, List<ImageCategoryModel> imageCategoryModelList, ImageModel imageModel, String responseText);
}
