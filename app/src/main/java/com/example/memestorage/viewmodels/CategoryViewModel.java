package com.example.memestorage.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memestorage.models.CategoryModel;
import com.example.memestorage.repositories.CategoryRepo;
import com.example.memestorage.utils.CategoryObserver;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CategoryViewModel {
    private final CategoryRepo categoryRepo;
    private static List<CategoryModel> categories = new ArrayList<>();
    private static CategoryViewModel categoryViewModel;
    private List<CategoryObserver> categoryObservers = new ArrayList<>();

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
                        categories = task.getResult().toObjects(CategoryModel.class);
                    }
                }
            });
        }
        return categoryViewModel;
    }
    public static void resetInstance() {
        categoryViewModel = null;
    }



    public List<CategoryModel> getCategories() {
        return categories;
    }
    public void addCategoryObserver(CategoryObserver categoryObserver) {
        categoryObservers.add(categoryObserver);
        categoryObserver.notifyAdapter(categories);
    }

    public void setCategories(List<CategoryModel> categories) {
        CategoryViewModel.categories = categories;
        for (CategoryObserver categoryObserver : categoryObservers) {
            categoryObserver.notifyAdapter(categories);
        }
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
