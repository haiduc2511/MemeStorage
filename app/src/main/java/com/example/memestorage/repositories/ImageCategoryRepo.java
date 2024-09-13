package com.example.memestorage.repositories;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.memestorage.models.ImageModel;
import com.example.memestorage.utils.FirebaseHelper;
import com.example.memestorage.models.ImageCategoryModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Objects;

public class ImageCategoryRepo {
    private static final String IMAGE_CATEGORY_COLLECTION_NAME = "imageCategories";
    private String myUserId = Objects.requireNonNull(FirebaseHelper.getInstance().getAuth().getCurrentUser()).getUid();
    private static final String USER_COLLECTION_NAME = "users";
    private final FirebaseFirestore db = FirebaseHelper.getInstance().getDb();
    private DocumentReference myImageCategoriesRef = db.collection(USER_COLLECTION_NAME).document(myUserId);


    public ImageCategoryRepo() {

    }


    public void getImageCategoryByIdFirebase(String icId, OnCompleteListener<QuerySnapshot> onCompleteListener) {
        myImageCategoriesRef.collection(IMAGE_CATEGORY_COLLECTION_NAME)
                .whereEqualTo("icId", icId)
                .get()
                .addOnCompleteListener(onCompleteListener);
    }
    public void addImageCategoryFirebase(ImageCategoryModel imageCategoryModel, OnCompleteListener<Void> onCompleteListener) {
        String id = myImageCategoriesRef.collection(IMAGE_CATEGORY_COLLECTION_NAME).document().getId(); // Generate a new ID
        imageCategoryModel.icId = id;
        myImageCategoriesRef.collection(IMAGE_CATEGORY_COLLECTION_NAME).document(id).set(imageCategoryModel).addOnCompleteListener(onCompleteListener);
    }


    // Read all categories
    public void getImageCategoriesFirebase(OnCompleteListener<QuerySnapshot> onCompleteListener) {
        myImageCategoriesRef.collection(IMAGE_CATEGORY_COLLECTION_NAME).get().addOnCompleteListener(onCompleteListener);
    }

    public void getImageCategoriesByImageIdFirebase(ImageModel imageModel, OnCompleteListener<QuerySnapshot> onCompleteListener) {
        myImageCategoriesRef.collection(IMAGE_CATEGORY_COLLECTION_NAME)
                .whereEqualTo("imageId", imageModel.iId).get().addOnCompleteListener(onCompleteListener);
    }
    public void getImageCategoriesByCategoryIdFirebase(String categoryId, OnCompleteListener<QuerySnapshot> onCompleteListener) {
        myImageCategoriesRef.collection(IMAGE_CATEGORY_COLLECTION_NAME)
                .whereEqualTo("categoryId", categoryId).get().addOnCompleteListener(onCompleteListener);
    }
    public void getImageCategoriesByImageIdAndCategoryIdFirebase(String imageId, String categoryId, OnCompleteListener<QuerySnapshot> onCompleteListener) {
        myImageCategoriesRef.collection(IMAGE_CATEGORY_COLLECTION_NAME)
                .whereEqualTo("imageId", imageId)
                .whereEqualTo("categoryId", categoryId)
                .get().addOnCompleteListener(onCompleteListener);
    }

    // Update a category
    public void updateImageCategoryFirebase(String id, ImageCategoryModel imageCategoryModel, OnCompleteListener<Void> onCompleteListener) {
        myImageCategoriesRef.collection(IMAGE_CATEGORY_COLLECTION_NAME).document(id).set(imageCategoryModel).addOnCompleteListener(onCompleteListener);
    }

    // Delete a category
    public void deleteImageCategoryFirebase(String id, OnCompleteListener<Void> onCompleteListener) {
        myImageCategoriesRef.collection(IMAGE_CATEGORY_COLLECTION_NAME).document(id).delete().addOnCompleteListener(onCompleteListener);
    }

    public void deleteImageCategoryByCategoryIdFirebase(String categoryId) {
        myImageCategoriesRef.collection(IMAGE_CATEGORY_COLLECTION_NAME).whereEqualTo("categoryId", categoryId)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Log.d("ImageCategory deletion by categoryId", categoryId);
                        if (task.isSuccessful()) {
                            List<ImageCategoryModel> imageCategoryModelList = task.getResult().toObjects(ImageCategoryModel.class);
                            for (ImageCategoryModel imageCategoryModel : imageCategoryModelList) {
                                deleteImageCategoryFirebase(imageCategoryModel.icId, new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Log.d("ImageCategory deletion", imageCategoryModel.toString());
                                    }
                                });
                            }
                        }
                    }
                });
    }

    public void deleteImageCategoryByImageIdFirebase(String imageId) {
        myImageCategoriesRef.collection(IMAGE_CATEGORY_COLLECTION_NAME).whereEqualTo("imageId", imageId)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Log.d("ImageCategory deletion by imageId", imageId);
                        if (task.isSuccessful()) {
                            List<ImageCategoryModel> imageCategoryModelList = task.getResult().toObjects(ImageCategoryModel.class);
                            for (ImageCategoryModel imageCategoryModel : imageCategoryModelList) {
                                deleteImageCategoryFirebase(imageCategoryModel.icId, new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Log.d("ImageCategory deletion", imageCategoryModel.toString());
                                    }
                                });
                            }
                        }
                    }
                });
    }
}