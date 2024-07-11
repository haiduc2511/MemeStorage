package com.example.memestorage.Repositories;

import static android.content.ContentValues.TAG;

import android.app.Application;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.memestorage.Activities.MainActivity;
import com.example.memestorage.FirebaseHelper;
import com.example.memestorage.Model.ImageModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.List;
import java.util.Objects;

public class ImageRepo {
    private static final String COLLECTION_NAME = "images";
    private static final String USER_COLLECTION_NAME = "users";
    private String myUserId = Objects.requireNonNull(FirebaseHelper.getInstance().getAuth().getCurrentUser()).getUid();
    private final FirebaseFirestore db = FirebaseHelper.getInstance().getDb();
    private DocumentReference myImagesRef = db.collection(USER_COLLECTION_NAME).document(myUserId);



    public ImageRepo() {
    }


    public void addImageFirebase(ImageModel imageModel) {
        String id = db.collection(USER_COLLECTION_NAME).document(myUserId).collection(COLLECTION_NAME).document().getId(); // Generate a new ID
        imageModel.iId = id;
        myImagesRef.collection(COLLECTION_NAME).document(id).set(imageModel);
    }

    // Read all my Images
    public void getMyImagesFirebase(OnCompleteListener<QuerySnapshot> onCompleteListener) {
        myImagesRef.collection(COLLECTION_NAME).get().addOnCompleteListener(onCompleteListener);
    }

    // Update an Image
    public void updateImageFirebase(String id, ImageModel imageModel, OnCompleteListener<Void> onCompleteListener) {
        myImagesRef.collection(COLLECTION_NAME).document(id).set(imageModel).addOnCompleteListener(onCompleteListener);
    }

    // Delete an Image
    public void deleteImageFirebase(String id, OnCompleteListener<Void> onCompleteListener) {
        myImagesRef.collection(COLLECTION_NAME).document(id).delete().addOnCompleteListener(onCompleteListener);
    }

    public void uploadImagesFirebaseStorage(List<Uri> imageUris) {
        if (!imageUris.isEmpty()) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference("uploads");

            for (Uri imageUri : imageUris) {
                StorageReference fileReference = storageReference.child("" + System.currentTimeMillis());

                fileReference.putFile(imageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri downloadUri) {
                                        ImageModel image = new ImageModel();
                                        image.imageName = fileReference.getName();
                                        image.userId = myUserId;
                                        image.imageURL = downloadUri.toString();
                                        addImageFirebase(image);
                                        Log.d(TAG, "Upload images successful. Download URL: " + downloadUri.toString());
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "Upload images failed: " + e.getMessage());
                            }
                        });
            }
        } else {
            Log.d(TAG, "No files selected");
        }
    }
}
