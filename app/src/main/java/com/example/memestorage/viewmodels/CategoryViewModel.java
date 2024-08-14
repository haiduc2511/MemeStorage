package com.example.memestorage.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.example.memestorage.models.CategoryModel;
import com.example.memestorage.repositories.CategoryRepo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CategoryViewModel {
    private final CategoryRepo categoryRepo;
    private static MutableLiveData<List<CategoryModel>> categories = new MutableLiveData<>(new ArrayList<>());
    private static CategoryViewModel categoryViewModel;

    private CategoryViewModel() {
        categoryRepo = new CategoryRepo();
    }

    public static CategoryViewModel newInstance() {
        if (categoryViewModel == null) {
            categoryViewModel = new CategoryViewModel();
            categoryViewModel.getCategoriesFirebase(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        List<CategoryModel> categoryList = task.getResult().toObjects(CategoryModel.class);
                        categories.postValue(categoryList);
                    } else {
                        categories.postValue(new ArrayList<>());
                    }
                }
            });
        }
        return categoryViewModel;
    }


    public LiveData<List<CategoryModel>> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryModel> categories) {
        CategoryViewModel.categories.setValue(categories);
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
