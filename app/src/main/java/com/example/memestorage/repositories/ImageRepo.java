package com.example.memestorage.repositories;

import static android.content.ContentValues.TAG;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.memestorage.utils.FirebaseHelper;
import com.example.memestorage.activities.MainActivity;
import com.example.memestorage.models.ImageModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class ImageRepo {
    private static final String IMAGE_COLLECTION_NAME = "images";
    private static final String USER_COLLECTION_NAME = "users";
    private String myUserId = Objects.requireNonNull(FirebaseHelper.getInstance().getAuth().getCurrentUser()).getUid();
    private final FirebaseFirestore db = FirebaseHelper.getInstance().getDb();
    private final FirebaseStorage storage = FirebaseHelper.getInstance().getStorage();

    private DocumentReference myImagesRef = db.collection(USER_COLLECTION_NAME).document(myUserId);



    public ImageRepo() {
    }


    public void addImageFirebase(ImageModel imageModel) {
        String id = db.collection(USER_COLLECTION_NAME).document(myUserId).collection(IMAGE_COLLECTION_NAME).document().getId(); // Generate a new ID
        imageModel.iId = id;
        myImagesRef.collection(IMAGE_COLLECTION_NAME).document(id).set(imageModel);
    }

    // Read all my Images
    public void getMyImagesFirebase(OnCompleteListener<QuerySnapshot> onCompleteListener) {
        myImagesRef.collection(IMAGE_COLLECTION_NAME).get().addOnCompleteListener(onCompleteListener);
    }

    // Update an Image
    public void updateImageFirebase(String id, ImageModel imageModel, OnCompleteListener<Void> onCompleteListener) {
        myImagesRef.collection(IMAGE_COLLECTION_NAME).document(id).set(imageModel).addOnCompleteListener(onCompleteListener);
    }

    // Delete an Image
    public void deleteImageFirebase(String id, OnCompleteListener<Void> onCompleteListener) {
        myImagesRef.collection(IMAGE_COLLECTION_NAME).document(id).delete().addOnCompleteListener(onCompleteListener);
    }

    public void uploadImagesFirebaseStorage(List<Uri> imageUris, ContentResolver contentResolver, MainActivity.OnSuccessUploadingImages onSuccessUploadingImages) {
        if (!imageUris.isEmpty()) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference("uploads");

            for (Uri imageUri : imageUris) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri);

                    // Tạo ByteArrayOutputStream
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    // Nén Bitmap thành JPEG với chất lượng 50% (chất lượng có thể thay đổi từ 0 đến 100)
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);

                    // Chuyển ByteArrayOutputStream thành byte array
                    byte[] data = baos.toByteArray();

                    StorageReference fileReference = storageReference.child("" + System.currentTimeMillis());

                    fileReference.putBytes(data)
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
                                            onSuccessUploadingImages.OnSuccessUploadingImages();
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
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Log.d(TAG, "No files selected");
        }
    }

    public void deleteImageFirebaseStorage(String imageUrl) {
        StorageReference storageRef = storage.getReferenceFromUrl(imageUrl);

        storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                Log.d("Delete Image Storage", "Image in Storage deleted successfully");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // An error occurred
                Log.d("Delete Image Storage", "Failed to delete Storage image: " + exception.getMessage());
            }
        });
    }
}
