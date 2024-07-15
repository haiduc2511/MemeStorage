package com.example.memestorage.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.memestorage.models.CategoryModel;
import com.example.memestorage.repositories.CategoryRepo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CategoryViewModel extends AndroidViewModel {
    private final CategoryRepo categoryRepo;
    private List<CategoryModel> categories = new ArrayList<>();

    public CategoryViewModel(@NonNull Application application) {
        super(application);
        categoryRepo = new CategoryRepo();
    }

    public List<CategoryModel> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryModel> categories) {
        this.categories = categories;
    }

    public void addCategoryFirebase(CategoryModel categoryModel, OnCompleteListener<Void> onCompleteListener) {
        categoryRepo.addCategoryFirebase(categoryModel, onCompleteListener);
    }

    public void getCategoriesFirebase(OnCompleteListener<QuerySnapshot> onCompleteListener) {
        categoryRepo.getCategoriesFirebase(onCompleteListener);
    }

    public void updateCategoryFirebase(String id, CategoryModel categoryModel, OnCompleteListener<Void> onCompleteListener) {
        categoryRepo.updateCategoryFirebase(id, categoryModel, onCompleteListener);
    }

    public void deleteCategoryFirebase(String id, OnCompleteListener<Void> onCompleteListener) {
        categoryRepo.deleteCategoryFirebase(id, onCompleteListener);
    }
}
