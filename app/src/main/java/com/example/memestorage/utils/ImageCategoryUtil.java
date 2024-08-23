package com.example.memestorage.utils;

import com.example.memestorage.models.ImageCategoryModel;
import com.example.memestorage.models.ImageModel;
import com.example.memestorage.viewmodels.CategoryViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ImageCategoryUtil {
    public static List<ImageCategoryModel> stringToImageCategoryList(String response, ImageModel imageModel) {
        List<ImageCategoryModel> imageCategoryModels = new ArrayList<>();
        String[] arrayCategoryNames = response.split(",\\s*");
        Map<String, String> mapCategories = CategoryViewModel.newInstance().getCategoryIdAndNameHashMap();
        for (String categoryName : arrayCategoryNames) {
            if (mapCategories.containsKey(categoryName)) {
                ImageCategoryModel imageCategoryModel = new ImageCategoryModel();
                imageCategoryModel.imageId = imageModel.iId;
                imageCategoryModel.categoryId = mapCategories.get(categoryName);
                imageCategoryModels.add(imageCategoryModel);
            }
        }
        return imageCategoryModels;
    }
}
