package com.example.memestorage.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.example.memestorage.R;
import com.example.memestorage.authentication.StartActivity;
import com.example.memestorage.databinding.FragmentAccountBinding;
import com.example.memestorage.models.CategoryModel;
import com.example.memestorage.models.ImageModel;
import com.example.memestorage.utils.FirebaseHelper;
import com.example.memestorage.utils.SharedPrefManager;
import com.example.memestorage.viewmodels.CategoryViewModel;
import com.example.memestorage.viewmodels.ImageCategoryViewModel;
import com.example.memestorage.viewmodels.ImageViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;


public class AccountFragment extends Fragment {
    FragmentAccountBinding binding;
    FirebaseAuth mAuth = FirebaseHelper.getInstance().getAuth();
    private GoogleSignInClient mGoogleSignInClient;
    SharedPrefManager sharedPrefManager;
    public AccountFragment() {
        // Required empty public constructor
    }
    public static AccountFragment newInstance() {
        AccountFragment fragment = new AccountFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        sharedPrefManager = new SharedPrefManager(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAccountBinding.inflate(getLayoutInflater(), container, false);
        initUI();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))  // Use your web client ID
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        return binding.getRoot();
    }
    private void logOut() {
        CategoryViewModel.resetInstance();
        mAuth.signOut();
        if (GoogleSignIn.getLastSignedInAccount(getContext()) != null) {
            mGoogleSignInClient.signOut().addOnCompleteListener(getActivity(), task -> {
                if (task.isSuccessful()) {
                    // Successfully signed out
                    goBackToStartActivity();
                } else {
                    // Sign out failed, show error message
                    Log.e("LogOutActivity", "Sign-out failed", task.getException());
                    Toast.makeText(getContext(), "Failed to sign out", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            goBackToStartActivity();
        }
    }
    private void goBackToStartActivity() {
        Intent intent = new Intent(requireActivity(), StartActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
    private void initUI() {
        initPowerModeToggleGroup();
        initButtonFetchQuality();
        initButtonNumberOfColumn();
        initButtonDeleteAccount();
        initButtonLogOut();
        initButtonNumberOfImages();
        initSwitchDeleteImageAfterUploading();
        initSwitchDoubleCheckAISuggestions();
        initSwitchDeleteImageEasyMode();
        initSwitchDownloadImageEasyMode();
        initSwitchShareImageEasyMode();
        initCustomBatteryMode();


        binding.tvSettingActivityName.setText(mAuth.getUid().toString());
        binding.tvSettingActivityName.setTextSize(10);



//        binding.fabBack.setOnClickListener(v -> {
//            getOnBackPressedDispatcher().onBackPressed();
//        });
    }
    private void initCustomBatteryMode() {
        binding.textView2.setVisibility(View.GONE);
        binding.textView4.setVisibility(View.GONE);
        binding.btFetchQuality.setVisibility(View.GONE);
        binding.btNumberOfImages.setVisibility(View.GONE);

        binding.tvCustomBatteryMode.setOnClickListener(v -> {
            if (binding.tvCustomBatteryMode.getText().equals("Custom battery mode ▲")) {
                binding.tvCustomBatteryMode.setText("Custom battery mode ▼");
                binding.textView2.setVisibility(View.VISIBLE);
                binding.textView4.setVisibility(View.VISIBLE);
                binding.btFetchQuality.setVisibility(View.VISIBLE);
                binding.btNumberOfImages.setVisibility(View.VISIBLE);
            } else {
                binding.tvCustomBatteryMode.setText("Custom battery mode ▲");
                binding.textView2.setVisibility(View.GONE);
                binding.textView4.setVisibility(View.GONE);
                binding.btFetchQuality.setVisibility(View.GONE);
                binding.btNumberOfImages.setVisibility(View.GONE);
            }
        });
    }

    private void initSwitchDoubleCheckAISuggestions() {
        if (sharedPrefManager.getIfDoubleCheckAISuggestions().equals("true")) {
            binding.swDoubleCheckAiSuggestions.setChecked(true);
        } else {
            binding.swDoubleCheckAiSuggestions.setChecked(false);
        }
        binding.swDoubleCheckAiSuggestions.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sharedPrefManager.saveIfDoubleCheckAISuggestions("true");
                } else {
                    sharedPrefManager.saveIfDoubleCheckAISuggestions("false");
                }
            }
        });
    }

    private void initSwitchDeleteImageAfterUploading() {
        if (sharedPrefManager.getIfDeleteImageGalleryAfterUpload().equals("true")) {
            binding.swDeleteImageAfterUpload.setChecked(true);
        } else {
            binding.swDeleteImageAfterUpload.setChecked(false);
        }
        binding.swDeleteImageAfterUpload.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sharedPrefManager.saveIfDeleteImageGalleryAfterUpload("true");
                } else {
                    sharedPrefManager.saveIfDeleteImageGalleryAfterUpload("false");
                }
            }
        });
    }

    private void initSwitchDeleteImageEasyMode() {
        if (sharedPrefManager.getIfDeleteImageEasyModeOn().equals("true")) {
            binding.swDeleteEasy.setChecked(true);
        } else {
            binding.swDeleteEasy.setChecked(false);
        }
        binding.swDeleteEasy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sharedPrefManager.saveIfDeleteImageEasyModeOn("true");
                } else {
                    sharedPrefManager.saveIfDeleteImageEasyModeOn("false");
                }
            }
        });
    }

    private void initSwitchDownloadImageEasyMode() {
        if (sharedPrefManager.getIfDownloadImageEasyModeOn().equals("true")) {
            binding.swDownloadEasy.setChecked(true);
        } else {
            binding.swDownloadEasy.setChecked(false);
        }
        binding.swDownloadEasy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sharedPrefManager.saveIfDownloadImageEasyModeOn("true");
                } else {
                    sharedPrefManager.saveIfDownloadImageEasyModeOn("false");
                }
            }
        });
    }

    private void initSwitchShareImageEasyMode() {
        if (sharedPrefManager.getIfShareImageEasyModeOn().equals("true")) {
            binding.swShareEasy.setChecked(true);
        } else {
            binding.swShareEasy.setChecked(false);
        }
        binding.swShareEasy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sharedPrefManager.saveIfShareImageEasyModeOn("true");
                } else {
                    sharedPrefManager.saveIfShareImageEasyModeOn("false");
                }
            }
        });
    }



    private void initButtonNumberOfColumn() {
        binding.btNumberOfColumn.setText(sharedPrefManager.getNumberOfColumn());

        binding.btNumberOfColumn.setOnClickListener(v -> {
            NumberPicker numberPicker = new NumberPicker(requireContext());
            numberPicker.setMinValue(1);
            numberPicker.setMaxValue(20);
            numberPicker.setValue(Integer.parseInt(sharedPrefManager.getNumberOfColumn()));

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setView(numberPicker);
            builder.setPositiveButton("OK", (dialog, which) -> {
                String selectedNumber = String.valueOf(numberPicker.getValue());
                if (isNumberLessThan20(selectedNumber)) {
                    binding.btNumberOfColumn.setText(selectedNumber);
                    sharedPrefManager.saveNumberOfColumn(selectedNumber);
                } else {
                    Toast.makeText(requireContext(), "Number of columns not appropriate", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("Cancel", null);
            builder.show();
        });

    }
    private void initButtonLogOut() {
        binding.btLogOut.setOnClickListener(v -> {
            logOut();
        });

    }
    private void initButtonDeleteAccount() {
        binding.btDeleteAccount.setOnClickListener(v -> {
            showConfirmDeleteAccountDialog();
        });

    }
    private void initButtonNumberOfImages() {
        binding.btNumberOfImages.setText(sharedPrefManager.getNumberOfImages());

        binding.btNumberOfImages.setOnClickListener(v -> {
            NumberPicker numberPicker = new NumberPicker(requireContext());
            numberPicker.setMinValue(1);
            numberPicker.setMaxValue(30);
            numberPicker.setValue(Integer.parseInt(sharedPrefManager.getNumberOfImages()));

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setView(numberPicker);
            builder.setPositiveButton("OK", (dialog, which) -> {
                String selectedNumber = String.valueOf(numberPicker.getValue());
                if (isNumberLessThan100(selectedNumber)) {
                    binding.btNumberOfImages.setText(selectedNumber);
                    sharedPrefManager.saveNumberOfImages(selectedNumber);
                } else {
                    Toast.makeText(requireContext(), "Number of images not appropriate", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("Cancel", null);
            builder.show();
        });

    }
    private void initButtonFetchQuality() {
        binding.btFetchQuality.setText(sharedPrefManager.getFetchQuality());

        binding.btFetchQuality.setOnClickListener(v -> {
            NumberPicker numberPicker = new NumberPicker(requireContext());
            numberPicker.setMinValue(1);
            numberPicker.setMaxValue(80);
            NumberPicker.Formatter formatter = new NumberPicker.Formatter() {
                @Override
                public String format(int value) {
                    int temp = value * 5;
                    return "" + temp;
                }
            };
            numberPicker.setFormatter(formatter);
            numberPicker.setValue(Integer.parseInt(sharedPrefManager.getFetchQuality()) / 5);
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setView(numberPicker);
            builder.setPositiveButton("OK", (dialog, which) -> {
                String selectedNumber = String.valueOf(numberPicker.getValue() * 5);
                if (isNumberLessThan500(selectedNumber)) {
                    binding.btFetchQuality.setText(selectedNumber);
                    sharedPrefManager.saveFetchQuality(selectedNumber);
                } else {
                    Toast.makeText(requireContext(), "Fetch Quality not appropriate", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("Cancel", null);
            builder.show();
        });

    }

    private void showConfirmDeleteAccountDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Delete Account");
        builder.setMessage("Are you sure you want to delete this account ?");
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteAccount();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void initPowerModeToggleGroup() {
        if (sharedPrefManager.getPowerMode().equals("low")) {
            updateButtonChose(binding.btnLow);
        }
        if (sharedPrefManager.getPowerMode().equals("medium")) {
            updateButtonChose(binding.btnMedium);
        }
        if (sharedPrefManager.getPowerMode().equals("high")) {
            updateButtonChose(binding.btnHigh);
        }

        binding.tgPowerMode.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
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
                    binding.btFetchQuality.setText(sharedPrefManager.getFetchQuality());
                }
            }
        });
    }
    private void updateButtonChose(MaterialButton button) {
        final int selectedColor = ContextCompat.getColor(requireContext(), R.color.colorAccent);
        final int unselectedColor = ContextCompat.getColor(requireContext(), R.color.colorPrimary);
        final int selectedTextColor = ContextCompat.getColor(requireContext(), R.color.black);
        final int unselectedTextColor = ContextCompat.getColor(requireContext(), R.color.white);

        binding.btnLow.setBackgroundTintList(ColorStateList.valueOf(unselectedColor));
        binding.btnLow.setTextColor(unselectedTextColor);
        binding.btnMedium.setBackgroundTintList(ColorStateList.valueOf(unselectedColor));
        binding.btnMedium.setTextColor(unselectedTextColor);
        binding.btnHigh.setBackgroundTintList(ColorStateList.valueOf(unselectedColor));
        binding.btnHigh.setTextColor(unselectedTextColor);
        button.setBackgroundTintList(ColorStateList.valueOf(selectedColor));
        button.setTextColor(selectedTextColor);
    }

    private void deleteAccount() {
        ImageCategoryViewModel imageCategoryViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()).create(ImageCategoryViewModel.class);

        ImageViewModel imageViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()).create(ImageViewModel.class);
        imageViewModel.getMyImagesFirebase(1000, new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<ImageModel> allImageModels = task.getResult().toObjects(ImageModel.class);
                Log.d("Delete all imageModels", allImageModels.toString());
                for (ImageModel imageModel : allImageModels) {
                    imageViewModel.deleteImageFirebase(imageModel.iId, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (imageModel.iId.length() > 36) {
                                imageViewModel.deleteImageCloudinary(imageModel);
                                Log.d("Delete imageModel CLOUDINARY", imageModel.toString());
                            } else {
                                imageViewModel.deleteImageFirebaseStorage(imageModel.imageURL);
                                Log.d("Delete imageModel FireStorage", imageModel.toString());
                            }
                            imageCategoryViewModel.deleteImageCategoryByImageIdFirebase(imageModel.iId);
                        }
                    });
                }

            }
        });
        CategoryViewModel.newInstance().getCategoriesFirebase(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<CategoryModel> allCategoryModels = task.getResult().toObjects(CategoryModel.class);
                for (CategoryModel categoryModel : allCategoryModels) {
                    CategoryViewModel.newInstance().deleteCategoryFirebase(categoryModel.cId);
                }
            }
        });
    }
//
//    private void saveNumberOfImage(String numberOfImage) {
//        if (numberOfImage.equals("")) {
//            return;
//        }
//        if (isNumberLessThan500(numberOfImage)) {
//            sharedPrefManager.saveNumberOfImages(numberOfImage);
//        } else {
//            Toast.makeText(this, "Number of images not appropriate", Toast.LENGTH_SHORT).show();
//        }
//    }
//    private void saveNumberOfColumn(String numberOfColumn) {
//        if (numberOfColumn.equals("")) {
//            return;
//        }
//        if (isNumberLessThan20(numberOfColumn)) {
//            sharedPrefManager.saveNumberOfColumn(numberOfColumn);
//        } else {
//            Toast.makeText(this, "Number of column not appropriate", Toast.LENGTH_SHORT).show();
//        }
//    }

    private boolean isNumberLessThan20(String str) {
        try {
            int num = Integer.parseInt(str);
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
    private boolean isNumberLessThan500(String str) {
        try {
            double num = Double.parseDouble(str);
            return num < 500 && num > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}