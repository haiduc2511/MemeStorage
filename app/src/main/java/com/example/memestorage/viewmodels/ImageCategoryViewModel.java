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

    public void setImages(List<ImageCategoryModel> imageCategories) {
        this.imageCategories = imageCategories;
    }

    public List<ImageCategoryModel> getImages() {
        return imageCategories;
    }

    public void addImageCategoryFirebase(ImageCategoryModel imageCategoryModel, OnCompleteListener<Void> onCompleteListener) {
        imageCategoryRepo.addImageCategoryFirebase(imageCategoryModel, onCompleteListener);
    }

    public void getImageCategoriesFirebase(OnCompleteListener<QuerySnapshot> onCompleteListener) {
        imageCategoryRepo.getImageCategoriesFirebase(onCompleteListener);
    }

    public void updateImageCategoryFirebase(String id, ImageCategoryModel imageCategoryModel, OnCompleteListener<Void> onCompleteListener) {
        imageCategoryRepo.updateImageCategoryFirebase(id, imageCategoryModel, onCompleteListener);
    }

    public void deleteImageCategoryFirebase(String id, OnCompleteListener<Void> onCompleteListener) {
        imageCategoryRepo.deleteImageCategoryFirebase(id, onCompleteListener);
    }
}