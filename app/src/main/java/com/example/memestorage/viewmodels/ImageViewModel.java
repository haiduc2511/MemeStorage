package com.example.memestorage.viewmodels;

import static android.content.ContentValues.TAG;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.memestorage.activities.MainActivity;
import com.example.memestorage.models.ImageCategoryModel;
import com.example.memestorage.models.ImageModel;
import com.example.memestorage.repositories.ImageRepo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ImageViewModel extends AndroidViewModel {
    private final ImageRepo imageRepo = new ImageRepo();
    private List<ImageModel> images = new ArrayList<>();

    public ImageViewModel(@NonNull Application application) {
        super(application);
    }

    public void setImages(List<ImageModel> images) {
        this.images = images;
    }

    public List<ImageModel> getImages() {
        return images;
    }

    public void addImageFirebase(ImageModel imageModel) {
        imageRepo.addImageFirebase(imageModel);
    }

    public void getMyImagesFirebase(OnCompleteListener<QuerySnapshot> onCompleteListener) {
        imageRepo.getMyImagesFirebase(onCompleteListener);
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
    public void uploadImagesFirebaseStorage(List<Uri> imageUris, MainActivity.OnSuccessUploadingImages onSuccessUploadingImages) {
        imageRepo.uploadImagesFirebaseStorage(imageUris,getApplication().getContentResolver() , onSuccessUploadingImages);
    }

    public void deleteImageFirebaseStorage(String url) {
        imageRepo.deleteImageFirebaseStorage(url);
    }
}