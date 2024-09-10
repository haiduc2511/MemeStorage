package com.example.memestorage.activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.memestorage.R;
import com.example.memestorage.authentication.StartActivity;
import com.example.memestorage.databinding.ActivitySettingBinding;
import com.example.memestorage.utils.FirebaseHelper;
import com.example.memestorage.utils.SharedPrefManager;
import com.example.memestorage.viewmodels.CategoryViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
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
        initPowerModeToggleGroup();
        binding.btNumberOfImages.setText(sharedPrefManager.getNumberOfImages());
        binding.btNumberOfColumn.setText(sharedPrefManager.getNumberOfColumn());

        binding.btNumberOfImages.setOnClickListener(v -> {
            NumberPicker numberPicker = new NumberPicker(this);
            numberPicker.setMinValue(1);
            numberPicker.setMaxValue(100);
            numberPicker.setValue(Integer.parseInt(sharedPrefManager.getNumberOfImages()));

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(numberPicker);
            builder.setPositiveButton("OK", (dialog, which) -> {
                String selectedNumber = String.valueOf(numberPicker.getValue());
                if (isNumberLessThan300(selectedNumber)) {
                    sharedPrefManager.saveNumberOfImages(selectedNumber);
                } else {
                    Toast.makeText(this, "Number of images not appropriate", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("Cancel", null);
            builder.show();
        });

        binding.btNumberOfColumn.setOnClickListener(v -> {
            NumberPicker numberPicker = new NumberPicker(this);
            numberPicker.setMinValue(1);
            numberPicker.setMaxValue(20);
            numberPicker.setValue(Integer.parseInt(sharedPrefManager.getNumberOfImages()));

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(numberPicker);
            builder.setPositiveButton("OK", (dialog, which) -> {
                String selectedNumber = String.valueOf(numberPicker.getValue());
                if (isNumberLessThan300(selectedNumber)) {
                    sharedPrefManager.saveNumberOfColumn(selectedNumber);
                } else {
                    Toast.makeText(this, "Number of columns not appropriate", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("Cancel", null);
            builder.show();
        });

        binding.btLogOut.setOnClickListener(v -> {
            logOut();
        });

    }

    @Override
    protected void onDestroy() {
//        saveSettings();
        super.onDestroy();
    }

    private void initPowerModeToggleGroup() {
        MaterialButtonToggleGroup toggleGroup = findViewById(R.id.tg_power_mode);

        toggleGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (isChecked) {
                    if (checkedId == binding.btnLow.getId()) {
                        updateButtonChose(binding.btnLow);
                        sharedPrefManager.savePowerMode("low");
                    } else if (checkedId == binding.btnMedium.getId()) {
                        updateButtonChose(binding.btnMedium);
                        sharedPrefManager.savePowerMode("medium");
                    } else if (checkedId == binding.btnHigh.getId()) {
                        updateButtonChose(binding.btnHigh);
                        sharedPrefManager.savePowerMode("high");
                    }
                    binding.btNumberOfImages.setText(sharedPrefManager.getNumberOfImages());
                    binding.btNumberOfColumn.setText(sharedPrefManager.getNumberOfColumn());
                }
            }
        });
    }
    private void updateButtonChose(MaterialButton button) {
        final int selectedColor = ContextCompat.getColor(this, R.color.colorAccent);
        final int unselectedColor = ContextCompat.getColor(this, R.color.colorPrimary);
        final int selectedTextColor = ContextCompat.getColor(this, R.color.black);
        final int unselectedTextColor = ContextCompat.getColor(this, R.color.white);

        binding.btnLow.setBackgroundTintList(ColorStateList.valueOf(unselectedColor));
        binding.btnLow.setTextColor(unselectedTextColor);
        binding.btnMedium.setBackgroundTintList(ColorStateList.valueOf(unselectedColor));
        binding.btnMedium.setTextColor(unselectedTextColor);
        binding.btnHigh.setBackgroundTintList(ColorStateList.valueOf(unselectedColor));
        binding.btnHigh.setTextColor(unselectedTextColor);
        button.setBackgroundTintList(ColorStateList.valueOf(selectedColor));
        button.setTextColor(selectedTextColor);
    }
    private void logOut() {
        CategoryViewModel.resetInstance();
        mAuth.signOut();
        Intent intent = new Intent(this, StartActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void saveNumberOfImage(String numberOfImage) {
        if (numberOfImage.equals("")) {
            return;
        }
        if (isNumberLessThan300(numberOfImage)) {
            sharedPrefManager.saveNumberOfImages(numberOfImage);
        } else {
            Toast.makeText(this, "Number of images not appropriate", Toast.LENGTH_SHORT).show();
        }
    }
    private void saveNumberOfColumn(String numberOfColumn) {
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