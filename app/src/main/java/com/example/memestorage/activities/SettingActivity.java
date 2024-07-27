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
import com.google.firebase.auth.FirebaseAuth;

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
        binding.btLogOut.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(this, StartActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        binding.btSaveSetting.setOnClickListener(v -> {
            String numberOfImage = binding.etLimitNumberOfImage.getText().toString();
            if (isNumberLessThan300(numberOfImage)) {
                sharedPrefManager.saveData("Number of images", numberOfImage);
            } else {
                Toast.makeText(this, "Number of images not appropriate", Toast.LENGTH_SHORT).show();
            }
            String compressPercentage = binding.etCompressPercentage.getText().toString();
            if (isNumberLessThan100(compressPercentage)) {
                sharedPrefManager.saveData("Compress percentage", compressPercentage);
            } else {
                Toast.makeText(this, "Percentage not appropriate", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean isNumberLessThan100(String str) {
        try {
            // Parse the string to a double
            double num = Double.parseDouble(str);
            // Check if the number is less than 100
            return num < 100 && num > 0;
        } catch (NumberFormatException e) {
            // If parsing fails, the string is not a number
            return false;
        }
    }
    public boolean isNumberLessThan300(String str) {
        try {
            // Parse the string to a double
            double num = Double.parseDouble(str);
            // Check if the number is less than 100
            return num < 300 && num > 0;
        } catch (NumberFormatException e) {
            // If parsing fails, the string is not a number
            return false;
        }
    }
}