package com.example.memestorage.activities;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.memestorage.authentication.StartActivity;
import com.example.memestorage.FirebaseHelper;
import com.example.memestorage.ImageAdapter;
import com.example.memestorage.models.ImageModel;
import com.example.memestorage.viewmodels.ImageViewModel;
import com.example.memestorage.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    static final int PICK_IMAGES_REQUEST = 1;
    private static final int REQUEST_CODE = 1;
    ImageViewModel imageViewModel;
    FirebaseAuth mAuth = FirebaseHelper.getInstance().getAuth();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        imageViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(ImageViewModel.class);
        checkPermissions();
        binding.btChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        binding.fabLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(this, StartActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        binding.btGoToAddCategory.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddCategoryActivity.class);
            startActivity(intent);
        });
        binding.btGoToAddCategory.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ImageAdapter imageAdapter = new ImageAdapter(imageViewModel.getImages(), MainActivity.this);
                binding.rvImages.setAdapter(imageAdapter);
                return false;
            }
        });

        binding.rvImages.setLayoutManager(new GridLayoutManager(this, 3));
        imageViewModel.getMyImagesFirebase(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    imageViewModel.setImages(task.getResult().toObjects(ImageModel.class));
                    for (ImageModel imageModel : imageViewModel.getImages()) {
                        Log.d(TAG, imageModel.toString());
                    }
                    ImageAdapter imageAdapter = new ImageAdapter(imageViewModel.getImages(), MainActivity.this);
                    binding.rvImages.setAdapter(imageAdapter);
                } else {
                    Log.w(TAG, "Error getting my imageModel", task.getException());
                }
            }
        });

    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Pictures"), PICK_IMAGES_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGES_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                List<Uri> uriList = new ArrayList<>();
                if (data.getClipData() != null) { // Multiple images selected
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        Log.d("URI", imageUri.toString());
                        uriList.add(imageUri);
                    }
                } else if (data.getData() != null) { // Single image selected
                    Uri imageUri = data.getData();
                    uriList.add(imageUri);
                }
                imageViewModel.uploadImagesFirebaseStorage(uriList);
            }
        }
    }
}