package com.example.memestorage.activities;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.memestorage.authentication.StartActivity;
import com.example.memestorage.models.CategoryModel;
import com.example.memestorage.models.ImageCategoryModel;
import com.example.memestorage.utils.ImageItemTouchHelper;
import com.example.memestorage.utils.FirebaseHelper;
import com.example.memestorage.adapters.ImageAdapter;
import com.example.memestorage.models.ImageModel;
import com.example.memestorage.utils.SharedPrefManager;
import com.example.memestorage.viewmodels.CategoryViewModel;
import com.example.memestorage.viewmodels.ImageCategoryViewModel;
import com.example.memestorage.viewmodels.ImageViewModel;
import com.example.memestorage.databinding.ActivityMainBinding;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    static final int PICK_IMAGES_REQUEST = 1;
    static boolean isHeightWrapContent = false;
    private static final int REQUEST_CODE = 1;
    ImageViewModel imageViewModel;
    CategoryViewModel categoryViewModel;
    ImageCategoryViewModel imageCategoryViewModel;
    MainCategoryAdapter categoryAdapter;
    ImageAdapter imageAdapter;
    int numberOfTimesSearched = 0;
    OnCategorySearchChosen onCategorySearchChosen = new OnCategorySearchChosen() {
        @Override
        public void OnCategorySearchChosen(Set<String> categoryIdSet) {
            numberOfTimesSearched++;
            List<String> categoryIdList = new ArrayList<>(categoryIdSet);
            imageAdapter.setImageModels(new ArrayList<>());
            if (categoryIdSet.size() != 0) {
                getImageCategoriesByCategoryIdList(categoryIdList);
            } else {
                tryRetrieveImagesByRxJava();
            }
        }
    };

    SharedPrefManager sharedPrefManager;
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
        sharedPrefManager = new SharedPrefManager(this);
        initUI();
    }

//    private void hideSystemUI() {
//        View decorView = getWindow().getDecorView();
//        int uiOptions = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_FULLSCREEN;
//        decorView.setSystemUiVisibility(uiOptions);
//    }
//
//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        if (hasFocus) {
//            hideSystemUI();
//        }
//    }
    private void getImageCategoriesByCategoryIdList(List<String> categoryIdList) {
        imageCategoryViewModel.getImageCategoriesByCategoryIdFirebase(categoryIdList.get(0), new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                categoryIdList.remove(0);
//                Log.d("Get ImageCategoryModel", task.getResult().toObjects(ImageCategoryModel.class).toString());
                getImagesByImageCategoryList(task.getResult().toObjects(ImageCategoryModel.class), categoryIdList);
            }
        });
    }
    private void getImagesByImageCategoryList(List<ImageCategoryModel> imageCategoryList, List<String> categoryIdList) {
        imageViewModel.getMyImagesByListImageCategoryFirebase(imageCategoryList, new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        int thisSearchNumber = numberOfTimesSearched;
                        filterImageWithOtherCategories(document.toObject(ImageModel.class), new ArrayList<>(categoryIdList), thisSearchNumber);
                    } else {
                        Log.d("Firestore", "No such document");
                    }
                } else {
                    Log.d("Firestore", "get failed with ", task.getException());
                }
            }
        });
    }

    private void initUI() {
        initButtons();

        initCategories();

        initImages();

//        retrieveImages();
//        hideSystemUI();
    }

    private void initImages() {
        binding.rvImages.setLayoutManager(new GridLayoutManager(this, 3));
        imageAdapter = new ImageAdapter(new ArrayList<>(), MainActivity.this, getSupportFragmentManager());
        binding.rvImages.setAdapter(imageAdapter);
        ImageItemTouchHelper imageItemTouchHelper = new ImageItemTouchHelper(imageAdapter, imageViewModel);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(imageItemTouchHelper);
        itemTouchHelper.attachToRecyclerView(binding.rvImages);
        tryRetrieveImagesByRxJava();
    }

    private void initButtons() {
        binding.btChooseImage.setOnClickListener(v -> openFileChooser());

        binding.tvSeeMore.setOnClickListener(v -> {
            ViewGroup.LayoutParams params = binding.rvCategories.getLayoutParams();

            if (isHeightWrapContent) {
                params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics());
            } else {
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            }

            binding.rvCategories.setLayoutParams(params);
            isHeightWrapContent = !isHeightWrapContent;

            if (binding.tvSeeMore.getText().equals("See more")) {
                binding.tvSeeMore.setText("See less");
            } else {
                binding.tvSeeMore.setText("See more");
            }
        });

        binding.btSetting.setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingActivity.class);
            startActivity(intent);
        });

        binding.btGoToAddCategory.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddCategoryActivity.class);
            startActivity(intent);
        });
    }

    private void initCategories() {
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getApplicationContext());
        layoutManager.setFlexDirection(FlexDirection.ROW);
        binding.rvCategories.setLayoutManager(layoutManager);
        categoryAdapter = new MainCategoryAdapter(new ArrayList<>(), onCategorySearchChosen);
        binding.rvCategories.setAdapter(categoryAdapter);
        categoryViewModel.getCategoriesFirebase(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                categoryViewModel.getCategories().observe(MainActivity.this, categories -> {
                    categoryAdapter.setCategoryModels(categories);
                });
            }
        });

    }


    private void filterImageWithOtherCategories(ImageModel imageModel, List<String> categories, int thisSeachNumber) {
        if (categories.isEmpty()) {
            if (thisSeachNumber == numberOfTimesSearched) {
                imageAdapter.addImage(imageModel);
            }
        } else {
            Log.d("Filtering", imageModel.iId + "\n" + categories.get(0));
            imageCategoryViewModel.getImageCategoriesByImageIdAndCategoryIdFirebase(imageModel.iId, categories.get(0), new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        if (!task.getResult().toObjects(ImageCategoryModel.class).isEmpty()) {
                            categories.remove(0);
                            filterImageWithOtherCategories(imageModel, categories, thisSeachNumber);
                        }
                    }

                }
            });
        }
    }

//    private void retrieveImages() {
//        String numberOfImages = "100";
//        if (sharedPrefManager.contains("Number of images")) {
//            numberOfImages = sharedPrefManager.getData("Number of images");
//        }
//        imageViewModel.getMyImagesFirebase(Integer.parseInt(numberOfImages), new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    imageViewModel.setImages(task.getResult().toObjects(ImageModel.class));
//                    for (ImageModel imageModel : imageViewModel.getImages()) {
//                        Log.d(TAG, imageModel.toString());
//                    }
//                    imageAdapter.setImageModels(imageViewModel.getImages());
//                } else {
//                    Log.w(TAG, "Error getting my imageModel", task.getException());
//                }
//            }
//        });
//    }

    private void tryRetrieveImagesByRxJava() {
        String numberOfImages;
        if (sharedPrefManager.contains("Number of images")) {
            numberOfImages = sharedPrefManager.getData("Number of images");
        } else {
            numberOfImages = "100";
        }


        Single<List<ImageModel>> observable = Single.<List<ImageModel>>create(emitter -> {
                    imageViewModel.getMyImagesFirebase(Integer.parseInt(numberOfImages), new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                List<ImageModel> images = task.getResult().toObjects(ImageModel.class);
                                emitter.onSuccess(images);
                            } else {
                                emitter.onError(task.getException());
                            }
                        }
                    });
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        observable.subscribe(new SingleObserver<List<ImageModel>>() {
            @Override
            public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull List<ImageModel> imageModels) {
                imageViewModel.setImages(imageModels);
                for (ImageModel imageModel : imageModels) {
                    Log.d("receiving Images", imageModel.imageURL);
                    imageAdapter.addImage(imageModel);
                }
                //imageAdapter.setImageModels(imageViewModel.getImages());
            }

            @Override
            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                Log.d("error rxjava", e.getMessage());
                Log.w(TAG, "Error getting my imageModel", e);
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
                        tryRetrieveImagesByRxJava();
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