package com.example.memestorage.repositories;

import com.example.memestorage.utils.FirebaseHelper;
import com.example.memestorage.models.CategoryModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class CategoryRepo {
    private static final String CATEGORY_COLLECTION_NAME = "categories";
    private String myUserId = Objects.requireNonNull(FirebaseHelper.getInstance().getAuth().getCurrentUser()).getUid();
    private static final String USER_COLLECTION_NAME = "users";
    private final FirebaseFirestore db = FirebaseHelper.getInstance().getDb();
    private DocumentReference myCategoriesRef = db.collection(USER_COLLECTION_NAME).document(myUserId);


    public CategoryRepo() {

    }


    public void getCategoryByIdFirebase(String cId, OnCompleteListener<QuerySnapshot> onCompleteListener) {
        myCategoriesRef.collection(CATEGORY_COLLECTION_NAME)
                .whereEqualTo("cId", cId)
                .get()
                .addOnCompleteListener(onCompleteListener);
    }
    public void addCategoryFirebase(CategoryModel categoryModel, OnCompleteListener<Void> onCompleteListener) {
        String id = myCategoriesRef.collection(CATEGORY_COLLECTION_NAME).document().getId(); // Generate a new ID
        categoryModel.cId = id;
        myCategoriesRef.collection(CATEGORY_COLLECTION_NAME).document(id).set(categoryModel).addOnCompleteListener(onCompleteListener);
    }


    // Read all categories
    public void getCategoriesFirebase(OnCompleteListener<QuerySnapshot> onCompleteListener) {
        myCategoriesRef.collection(CATEGORY_COLLECTION_NAME).get().addOnCompleteListener(onCompleteListener);
    }


    // Update a category
    public void updateCategoryFirebase(String id, CategoryModel categoryModel, OnCompleteListener<Void> onCompleteListener) {
        myCategoriesRef.collection(CATEGORY_COLLECTION_NAME).document(id).set(categoryModel).addOnCompleteListener(onCompleteListener);
    }

    // Delete a category
    public void deleteCategoryFirebase(String id, OnCompleteListener<Void> onCompleteListener) {
        myCategoriesRef.collection(CATEGORY_COLLECTION_NAME).document(id).delete().addOnCompleteListener(onCompleteListener);
    }

}
