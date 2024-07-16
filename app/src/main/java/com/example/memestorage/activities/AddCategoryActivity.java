package com.example.memestorage.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.memestorage.databinding.ActivityAddCategoryBinding;
import com.example.memestorage.viewmodels.CategoryViewModel;
import com.example.memestorage.models.CategoryModel;
import com.example.memestorage.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class AddCategoryActivity extends AppCompatActivity {

    ActivityAddCategoryBinding binding;
    CategoryViewModel categoryViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityAddCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        categoryViewModel = CategoryViewModel.newInstance(getApplication());

        initUI();
    }

    private void initUI() {
        binding.btAddCategory.setOnClickListener(v -> {
            String categoryName = binding.etCategoryName.getText().toString();
            CategoryModel categoryModel = new CategoryModel();
            categoryModel.categoryName = categoryName;
            categoryViewModel.addCategoryFirebase(categoryModel, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(AddCategoryActivity.this, "Add Category  " + categoryModel.categoryName + " successfully", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}