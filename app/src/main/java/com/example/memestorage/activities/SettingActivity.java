package com.example.memestorage.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.memestorage.R;
import com.example.memestorage.authentication.StartActivity;
import com.example.memestorage.databinding.ActivitySettingBinding;
import com.example.memestorage.utils.FirebaseHelper;
import com.example.memestorage.utils.SharedPrefManager;
import com.example.memestorage.viewmodels.CategoryViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class SettingActivity extends AppCompatActivity {
    ActivitySettingBinding binding;
    FirebaseAuth mAuth = FirebaseHelper.getInstance().getAuth();
    SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sharedPrefManager = new SharedPrefManager(this);
        initUI();
    }

    private void initUI() {
        binding.etLimitNumberOfImage.setHint("Number of images: " + sharedPrefManager.getNumberOfImages());
        binding.etCompressPercentage.setHint("Compress %: " + sharedPrefManager.getCompressPercentage());
        binding.etNumberOfColumn.setHint("Number of column %: " + sharedPrefManager.getNumberOfColumn());
        binding.btLogOut.setOnClickListener(v -> {
            logOut();
        });

        binding.btSaveSetting.setOnClickListener(v -> {
            saveSettings();
        });
    }

    @Override
    protected void onDestroy() {
//        saveSettings();
        super.onDestroy();
    }

    private void logOut() {
        CategoryViewModel.resetInstance();
        mAuth.signOut();
        Intent intent = new Intent(this, StartActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void saveSettings() {
        saveNumberOfImage();
        saveCompressPercentage();
        saveNumberOfColumn();
    }

    private void saveNumberOfImage() {
        String numberOfImage = binding.etLimitNumberOfImage.getText().toString();
        if (numberOfImage.equals("")) {
            return;
        }
        if (isNumberLessThan300(numberOfImage)) {
            sharedPrefManager.saveNumberOfImages(numberOfImage);
        } else {
            Toast.makeText(this, "Number of images not appropriate", Toast.LENGTH_SHORT).show();
        }
    }
    private void saveCompressPercentage() {
        String compressPercentage = binding.etCompressPercentage.getText().toString();
        if (compressPercentage.equals("")) {
            return;
        }
        if (isNumberLessThan100(compressPercentage)) {
            sharedPrefManager.saveCompressPercentage(compressPercentage);
        } else {
            Toast.makeText(this, "Compress percentage not appropriate", Toast.LENGTH_SHORT).show();
        }
    }
    private void saveNumberOfColumn() {
        String numberOfColumn = binding.etNumberOfColumn.getText().toString();
        if (numberOfColumn.equals("")) {
            return;
        }
        if (isNumberLessThan20(numberOfColumn)) {
            sharedPrefManager.saveNumberOfColumn(numberOfColumn);
        } else {
            Toast.makeText(this, "Number of column not appropriate", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isNumberLessThan20(String str) {
        try {
            double num = Double.parseDouble(str);
            return num < 20 && num > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    private boolean isNumberLessThan100(String str) {
        try {
            double num = Integer.parseInt(str);
            return num < 100 && num > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    private boolean isNumberLessThan300(String str) {
        try {
            double num = Double.parseDouble(str);
            return num < 300 && num > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}