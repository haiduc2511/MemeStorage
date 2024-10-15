package com.example.memestorage.viewmodels;

import static android.content.ContentValues.TAG;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.cloudinary.android.callback.UploadCallback;
import com.cloudinary.utils.ObjectUtils;
import com.example.memestorage.activities.MainActivity;
import com.example.memestorage.models.ImageCategoryModel;
import com.example.memestorage.models.ImageModel;
import com.example.memestorage.repositories.ImageRepo;
import com.example.memestorage.utils.CloudinaryHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ImageViewModel extends AndroidViewModel {
    private final ImageRepo imageRepo = new ImageRepo();
    private List<ImageModel> images = new ArrayList<>();

    public ImageViewModel(@NonNull Application application) {
        super(application);
    }

    public void setImages(List<ImageModel> images) {
        this.images = images;
    }

    public void addImages(List<ImageModel> images) {
        this.images.addAll(images);
    }

    public void addImageFirst(ImageModel imageModel) {
        this.images.add(0, imageModel);
    }

    public List<ImageModel> getImages() {
        return images;
    }

    public ImageModel addImageFirebase(ImageModel imageModel) {
        return imageRepo.addImageFirebase(imageModel);
    }

    public void getMyImagesFirebase(int limit, OnCompleteListener<QuerySnapshot> onCompleteListener) {
        imageRepo.getMyImagesFirebase(limit, onCompleteListener);
    }

    public void getMoreMyImagesFirebase(int limit, DocumentSnapshot lastDocument, OnCompleteListener<QuerySnapshot> onCompleteListener) {
        imageRepo.getMoreMyImagesFirebase(limit, lastDocument, onCompleteListener);
    }

    public void getMyImagesByIdFirebase(String iId, OnCompleteListener<DocumentSnapshot> onCompleteListener) {
        imageRepo.getMyImagesByIdFirebase(iId,onCompleteListener);
    }

    public void getMyImagesByListImageCategoryFirebase(List<ImageCategoryModel> imageCategories, OnCompleteListener<DocumentSnapshot> onCompleteListener) {
        List<String> imageIds = new ArrayList<>();
        for (ImageCategoryModel imageCategory : imageCategories) {
            imageIds.add(imageCategory.imageId);
        }
        imageRepo.getMyImagesByListIdFirebase(imageIds,onCompleteListener);
    }

    public void updateImageFirebase(String id, ImageModel imageModel, OnCompleteListener<Void> onCompleteListener) {
        imageRepo.updateImageFirebase(id, imageModel, onCompleteListener);
    }

    public void deleteImageFirebase(String id, OnCompleteListener<Void> onCompleteListener) {
        imageRepo.deleteImageFirebase(id, onCompleteListener);
    }
//    public void uploadImagesFirebaseStorage(List<Uri> imageUris, MainActivity.UploadImageListener onSuccessUploadingImages) {
//        imageRepo.uploadImagesFirebaseStorage(imageUris,getApplication().getContentResolver() , onSuccessUploadingImages);
//    }
    public void uploadImagesCloudinary(List<Uri> imageUris, UploadCallback uploadCallback) {
        imageRepo.uploadImagesCloudinary(imageUris, getApplication().getContentResolver(), uploadCallback);
    }

    public void uploadImageCloudinary(Uri imageUri, UploadCallback uploadCallback) {
        imageRepo.uploadImageCloudinary(imageUri, getApplication().getContentResolver(), uploadCallback);
    }

    public void uploadReplaceImageCloudinary(Uri imageUri, ContentResolver contentResolver, ImageModel imageModel, UploadCallback uploadCallback) {
        imageRepo.uploadReplaceImageCloudinary(imageUri, contentResolver, imageModel, uploadCallback);
    }

    public void deleteImageFirebaseStorage(String url) {
        imageRepo.deleteImageFirebaseStorage(url);
    }

    public void deleteImageCloudinary(ImageModel imageModel) {
        imageRepo.deleteImageCloudinary(imageModel);
    }
}