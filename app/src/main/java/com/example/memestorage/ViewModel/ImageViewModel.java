package com.example.memestorage.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.memestorage.FirebaseHelper;
import com.example.memestorage.Model.ImageModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class ImageViewModel extends AndroidViewModel {
    private static final String COLLECTION_NAME = "images";
    private static final String USER_COLLECTION_NAME = "users";
    private String myUserId;
    private final FirebaseFirestore db = FirebaseHelper.getInstance().getDb();
    private CollectionReference myImagesRef = db.collection(USER_COLLECTION_NAME).document(myUserId).collection(USER_COLLECTION_NAME);



    public ImageViewModel(@NonNull Application application, String myUserId) {
        super(application);
        this.myUserId = myUserId;
    }


    public void addImageFirebase(ImageModel imageModel, OnCompleteListener<Void> onCompleteListener) {
        String id = db.collection(USER_COLLECTION_NAME).document(myUserId).collection(COLLECTION_NAME).document().getId(); // Generate a new ID
        imageModel.iId = id;
        myImagesRef.document(id).set(imageModel).addOnCompleteListener(onCompleteListener);
    }

    // Read all my Images
    public void getMyImagesFirebase(OnCompleteListener<QuerySnapshot> onCompleteListener) {
        myImagesRef.get().addOnCompleteListener(onCompleteListener);
    }

    // Update an Image
    public void updateImageFirebase(String id, ImageModel imageModel, OnCompleteListener<Void> onCompleteListener) {
        myImagesRef.document(id).set(imageModel).addOnCompleteListener(onCompleteListener);
    }

    // Delete an Image
    public void deleteImageFirebase(String id, OnCompleteListener<Void> onCompleteListener) {
        myImagesRef.document(id).delete().addOnCompleteListener(onCompleteListener);
    }

}