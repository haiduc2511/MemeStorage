package com.example.memestorage.activities;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.example.memestorage.adapters.AddCategoryCategoryAdapter;
import com.example.memestorage.adapters.MainCategoryAdapter;
import com.example.memestorage.broadcastreceiver.InternetBroadcastReceiver;
import com.example.memestorage.broadcastreceiver.NetworkStatusManager;
import com.example.memestorage.databinding.ActivityAddCategoryBinding;
import com.example.memestorage.utils.CategoryItemTouchHelper;
import com.example.memestorage.viewmodels.CategoryViewModel;
import com.example.memestorage.models.CategoryModel;
import com.example.memestorage.R;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AddCategoryActivity extends AppCompatActivity {

    ActivityAddCategoryBinding binding;
    CategoryViewModel categoryViewModel;
    AddCategoryCategoryAdapter categoryAdapter;
    NetworkStatusManager networkStatusManager = NetworkStatusManager.getInstance();
    InternetBroadcastReceiver internetBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityAddCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        categoryViewModel = CategoryViewModel.newInstance();
        initInternetBroadcastReceiver();
        initUI();
    }

    private void initUI() {
        binding.btAddCategory.setOnClickListener(v -> {
            addNewCategory();
            binding.etCategoryName.setText("");
        });
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getApplicationContext());
        layoutManager.setFlexDirection(FlexDirection.ROW);
        binding.rvCategories.setLayoutManager(layoutManager);
        categoryAdapter = new AddCategoryCategoryAdapter(new ArrayList<>(), this);
        binding.rvCategories.setAdapter(categoryAdapter);
        CategoryItemTouchHelper categoryItemTouchHelper = new CategoryItemTouchHelper(categoryAdapter, categoryViewModel);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(categoryItemTouchHelper);
        itemTouchHelper.attachToRecyclerView(binding.rvCategories);
        categoryViewModel.addCategoryObserver(categoryAdapter);
        categoryAdapter.setCategoryModels(categoryViewModel.getCategories());
//        getCategoriesFromFirebase();

    }
    private void initInternetBroadcastReceiver() {
        internetBroadcastReceiver = new InternetBroadcastReceiver(new InternetBroadcastReceiver.NetworkChangeListener() {
            @Override
            public void onNetworkChange(boolean isConnected) {
                if (isConnected) {
                    networkStatusManager.setConnected(true);
                } else {
                    networkStatusManager.setConnected(false);
                }
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(internetBroadcastReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(internetBroadcastReceiver);
    }

    private void getCategoriesFromFirebase() {
        categoryViewModel.getCategoriesFirebase(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                categoryViewModel.setCategories(task.getResult().toObjects(CategoryModel.class));
            }
        });

    }

    private void addNewCategory() {
        String categoryName = binding.etCategoryName.getText().toString();
        CategoryModel categoryModel = new CategoryModel();
        categoryModel.categoryName = categoryName;
        categoryViewModel.addCategoryFirebase(categoryModel, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                categoryViewModel.getCategoriesFirebase(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        categoryViewModel.setCategories(task.getResult().toObjects(CategoryModel.class));
//                        categoryAdapter.setCategoryModels(categoryViewModel.getCategories());
                    }
                });
                Toast.makeText(AddCategoryActivity.this, "Add Category  " + categoryModel.categoryName + " successfully", Toast.LENGTH_SHORT).show();
            }
        });

    }
}