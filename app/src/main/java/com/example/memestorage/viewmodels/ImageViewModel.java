package com.example.memestorage.viewmodels;

import static android.content.ContentValues.TAG;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.memestorage.models.ImageModel;
import com.example.memestorage.repositories.ImageRepo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ImageViewModel extends AndroidViewModel {
    private final ImageRepo imageRepo = new ImageRepo();
    private List<ImageModel> images = new ArrayList<>();

    public ImageViewModel(@NonNull Application application) {
        super(application);
    }

    public List<ImageModel> getImages() {
        return images;
    }

    public void addImageFirebase(ImageModel imageModel) {
        imageRepo.addImageFirebase(imageModel);
    }

    public void getMyImagesFirebase() {
        imageRepo.getMyImagesFirebase(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    images = task.getResult().toObjects(ImageModel.class);
                    for (ImageModel imageModel : images) {
                        Log.d(TAG, imageModel.toString());
                    }
                } else {
                    Log.w(TAG, "Error getting my imageModel", task.getException());
                }
            }
        });
    }

    public void updateImageFirebase(String id, ImageModel imageModel, OnCompleteListener<Void> onCompleteListener) {
        imageRepo.updateImageFirebase(id, imageModel, onCompleteListener);
    }

    public void deleteImageFirebase(String id, OnCompleteListener<Void> onCompleteListener) {
        imageRepo.deleteImageFirebase(id, onCompleteListener);
    }
    public void uploadImagesFirebaseStorage(List<Uri> imageUris) {
        imageRepo.uploadImagesFirebaseStorage(imageUris);
    }
}