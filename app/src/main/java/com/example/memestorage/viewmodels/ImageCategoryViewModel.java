package com.example.memestorage.viewmodels;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.memestorage.models.ImageCategoryModel;
import com.example.memestorage.models.ImageModel;
import com.example.memestorage.repositories.ImageCategoryRepo;
import com.example.memestorage.repositories.ImageRepo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ImageCategoryViewModel extends AndroidViewModel {
    private final ImageCategoryRepo imageCategoryRepo = new ImageCategoryRepo();
    private List<ImageCategoryModel> imageCategories = new ArrayList<>();

    public ImageCategoryViewModel(@NonNull Application application) {
        super(application);
    }

    public void setImageCategories(List<ImageCategoryModel> imageCategories) {
        this.imageCategories = imageCategories;
    }

    public List<ImageCategoryModel> getImageCategories() {
        return imageCategories;
    }

    public void addImageCategoryFirebase(ImageCategoryModel imageCategoryModel, OnCompleteListener<Void> onCompleteListener) {
        imageCategoryRepo.addImageCategoryFirebase(imageCategoryModel, onCompleteListener);
    }

    public void getImageCategoriesFirebase(OnCompleteListener<QuerySnapshot> onCompleteListener) {
        imageCategoryRepo.getImageCategoriesFirebase(onCompleteListener);
    }

    public void getImageCategoriesByImageIdFirebase(ImageModel imageModel, OnCompleteListener<QuerySnapshot> onCompleteListener) {
        imageCategoryRepo.getImageCategoriesByImageIdFirebase(imageModel, onCompleteListener);
    }

    public void getImageCategoriesByCategoryIdFirebase(String categoryId, OnCompleteListener<QuerySnapshot> onCompleteListener) {
        imageCategoryRepo.getImageCategoriesByCategoryIdFirebase(categoryId, onCompleteListener);
    }

    public void getImageCategoriesByImageIdAndCategoryIdFirebase(String imageId, String categoryId, OnCompleteListener<QuerySnapshot> onCompleteListener) {
        imageCategoryRepo.getImageCategoriesByImageIdAndCategoryIdFirebase(imageId, categoryId, onCompleteListener);
    }

    public void updateImageCategoryFirebase(String id, ImageCategoryModel imageCategoryModel, OnCompleteListener<Void> onCompleteListener) {
        imageCategoryRepo.updateImageCategoryFirebase(id, imageCategoryModel, onCompleteListener);
    }

    public void deleteImageCategoryFirebase(String id, OnCompleteListener<Void> onCompleteListener) {
        imageCategoryRepo.deleteImageCategoryFirebase(id, onCompleteListener);
    }
    public void deleteImageCategoryByCategoryIdFirebase(String categoryId) {
        imageCategoryRepo.deleteImageCategoryByCategoryIdFirebase(categoryId);
    }
    public void deleteImageCategoryByImageIdFirebase(String imageId) {
        imageCategoryRepo.deleteImageCategoryByImageIdFirebase(imageId);
    }
}