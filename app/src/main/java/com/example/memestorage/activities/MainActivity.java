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
import androidx.recyclerview.widget.ItemTouchHelper;

import com.example.memestorage.adapters.CategoryAdapter;
import com.example.memestorage.adapters.MainCategoryAdapter;
import com.example.memestorage.models.CategoryModel;
import com.example.memestorage.models.ImageCategoryModel;
import com.example.memestorage.utils.ImageItemTouchHelper;
import com.example.memestorage.utils.FirebaseHelper;
import com.example.memestorage.adapters.ImageAdapter;
import com.example.memestorage.models.ImageModel;
import com.example.memestorage.viewmodels.CategoryViewModel;
import com.example.memestorage.viewmodels.ImageCategoryViewModel;
import com.example.memestorage.viewmodels.ImageViewModel;
import com.example.memestorage.databinding.ActivityMainBinding;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    static final int PICK_IMAGES_REQUEST = 1;
    private static final int REQUEST_CODE = 1;
    ImageViewModel imageViewModel;
    CategoryViewModel categoryViewModel;
    ImageCategoryViewModel imageCategoryViewModel;
    MainCategoryAdapter categoryAdapter;
    FirebaseAuth mAuth = FirebaseHelper.getInstance().getAuth();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        imageViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(ImageViewModel.class);
        categoryViewModel = CategoryViewModel.newInstance(getApplication());
        imageCategoryViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(ImageCategoryViewModel.class);
        checkPermissions();

        initUI();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        initUI();
    }

    private void initUI() {
        binding.btChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

//        binding.fabLogout.setOnClickListener(v -> {
//            mAuth.signOut();
//            Intent intent = new Intent(this, StartActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(intent);
//            finish();
//        });

        initCategories();


        binding.btGoToAddCategory.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddCategoryActivity.class);
            startActivity(intent);
        });


        binding.rvImages.setLayoutManager(new GridLayoutManager(this, 3));
        retrieveImages();
    }

    private void initCategories() {
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getApplicationContext());
        layoutManager.setFlexDirection(FlexDirection.ROW);
        binding.rvCategories.setLayoutManager(layoutManager);
        categoryViewModel.getCategoriesFirebase(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                categoryAdapter = new MainCategoryAdapter(categoryViewModel.getCategories(), new OnCategorySearchChosen() {
                    @Override
                    public void OnCategorySearchChosen(Set<String> categoryIds) {
                        List<String> categoryIdList = new ArrayList<>(categoryIds);
                        if (categoryIds.size() != 0) {
                            imageCategoryViewModel.getImageCategoriesByCategoryIdFirebase(categoryIdList.get(0), new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    categoryIdList.remove(0);
                                    Log.d("Get ImageCategoryModel", task.getResult().toObjects(ImageCategoryModel.class).toString());
                                    List<String> endResultList =
                                            filterFirstListWithOtherCategories(task.getResult().toObjects(ImageCategoryModel.class),
                                                    categoryIdList);
                                }
                            });
                        }
                    }
                });
                binding.rvCategories.setAdapter(categoryAdapter);
            }
        });

    }

    private List<String> filterFirstListWithOtherCategories(List<ImageCategoryModel> firstList
                                                                , List<String> otherCategories) {
        List<String> endResultImageIdList = new ArrayList<>();
        for (int i = 0; i < firstList.size(); i++) {
            endResultImageIdList.add(firstList.get(i).imageId);
        }
        for (int i = 0; i < otherCategories.size(); i++) {
            List<String> filteredOnceImageIdList = new ArrayList<>();
            Log.d("Check category", otherCategories.get(i));
            for (int j = 0; j < firstList.size(); j++) {
                Log.d("Check image", firstList.get(j).imageId);
                imageCategoryViewModel.getImageCategoriesByImageIdAndCategoryIdFirebase(firstList.get(j).imageId
                        , otherCategories.get(i), new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.getResult().toObjects(ImageModel.class).size() != 0) {
                            filteredOnceImageIdList.add(task.getResult().toObjects(ImageCategoryModel.class).get(0).imageId);
                            Log.d("Add image ", task.getResult().toObjects(ImageCategoryModel.class).get(0).imageId);
                        }
                    }
                });
                endResultImageIdList = filteredOnceImageIdList;
            }
        }
        Log.d("I found it, the end ImageIdList", endResultImageIdList.toString());
        return endResultImageIdList;
    }

    private void retrieveImages() {
        imageViewModel.getMyImagesFirebase(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    imageViewModel.setImages(task.getResult().toObjects(ImageModel.class));
                    for (ImageModel imageModel : imageViewModel.getImages()) {
                        Log.d(TAG, imageModel.toString());
                    }
                    ImageAdapter imageAdapter = new ImageAdapter(imageViewModel.getImages(), MainActivity.this, getSupportFragmentManager());
                    binding.rvImages.setAdapter(imageAdapter);
                    ImageItemTouchHelper imageItemTouchHelper = new ImageItemTouchHelper(imageAdapter, imageViewModel);
                    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(imageItemTouchHelper);
                    itemTouchHelper.attachToRecyclerView(binding.rvImages);
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
                imageViewModel.uploadImagesFirebaseStorage(uriList, new OnSuccessUploadingImages() {
                    @Override
                    public void OnSuccessUploadingImages() {
                        retrieveImages();
                    }
                });
            }
        }
    }

    public interface OnSuccessUploadingImages {
        public void OnSuccessUploadingImages();
    }

    public interface OnCategorySearchChosen {
        public void OnCategorySearchChosen(Set<String> categories);
    }
}