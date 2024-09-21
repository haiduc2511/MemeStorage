package com.example.memestorage.activities;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.cloudinary.Transformation;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.memestorage.BuildConfig;
import com.example.memestorage.R;
import com.example.memestorage.adapters.CategoryAdapter;
import com.example.memestorage.adapters.MainCategoryAdapter;
import com.example.memestorage.authentication.StartActivity;
import com.example.memestorage.broadcastreceiver.InternetBroadcastReceiver;
import com.example.memestorage.broadcastreceiver.NetworkStatusManager;
import com.example.memestorage.models.CategoryModel;
import com.example.memestorage.models.ImageCategoryModel;
import com.example.memestorage.utils.CloudinaryHelper;
import com.example.memestorage.utils.ImageItemTouchHelper;
import com.example.memestorage.utils.FirebaseHelper;
import com.example.memestorage.adapters.ImageAdapter;
import com.example.memestorage.models.ImageModel;
import com.example.memestorage.utils.MediaManagerState;
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

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import gun0912.tedimagepicker.builder.TedImagePicker;
import gun0912.tedimagepicker.builder.listener.OnMultiSelectedListener;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.functions.Predicate;
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
    boolean isFirstInternetConnectionCheck = true;
    boolean searchingByCategory = false; //for lazy loading (retrieveMoreImages..)
    NetworkStatusManager networkStatusManager = NetworkStatusManager.getInstance();
    CategorySearchListener onCategorySearchChosen = new CategorySearchListener() {
        @Override
        public void onCategorySearchChosen(Set<String> categoryIdSet) {
            numberOfTimesSearched++;
            List<String> categoryIdList = new ArrayList<>(categoryIdSet);
            imageAdapter.setImageModels(new ArrayList<>());
            if (!categoryIdSet.isEmpty()) {
                getImageCategoriesByCategoryIdList(categoryIdList);
                searchingByCategory = true;
            } else {
                imageAdapter.setImageModels(new ArrayList<>());
                retrieveImagesByRxJava();
                searchingByCategory = false;
            }
        }
    };
    InternetBroadcastReceiver internetBroadcastReceiver;
    SharedPrefManager sharedPrefManager;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    Observable<Long> networkObservable;
    DocumentSnapshot lastVisible;
    String myUserId = Objects.requireNonNull(FirebaseHelper.getInstance().getAuth().getCurrentUser()).getUid();
    private Map<String, Pair<Integer, NotificationCompat.Builder>> notificationMap = new HashMap<>();
//    NotificationCompat.Builder notificationBuilder;
    NotificationManager notificationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        checkPermissions();
        sharedPrefManager = new SharedPrefManager(this);
        initViewModel();
        initUI();
        initCloudinary();
//        initInternetBroadcastReceiver();
        createNotificationChannel();
    }
    private void initCloudinary() {
        if (!MediaManagerState.isInitialized()) {
            Map<String, Object> config = new HashMap<>();
            config.put("cloud_name", BuildConfig.CLOUD_NAME);
            config.put("api_key", BuildConfig.API_KEY);
            config.put("api_secret", BuildConfig.API_SECRET);
//        config.put("secure", true);

            MediaManager.init(this, config);
            MediaManagerState.initialize();
        }
    }
    private void initViewModel() {
        imageViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(ImageViewModel.class);
        categoryViewModel = CategoryViewModel.newInstance();
        imageCategoryViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(ImageCategoryViewModel.class);

    }
    private void initInternetBroadcastReceiver() {
        internetBroadcastReceiver = new InternetBroadcastReceiver(new InternetBroadcastReceiver.NetworkChangeListener() {
            @Override
            public void onNetworkChange(boolean isConnected) {
//                binding.linearLayout.setVisibility(View.VISIBLE);
                if (isFirstInternetConnectionCheck) {
                    if (!isConnected) {
                        showNetworkStatusBar("Không có kết nối mạng", R.color.red);
                        isFirstInternetConnectionCheck = false;
                        networkStatusManager.setConnected(false);
                    }
                } else {
                    if (isConnected) {
                        networkStatusManager.setConnected(true);
                        showNetworkStatusBar("Đã kết nối lại mạng", R.color.green);
                    } else {
                        networkStatusManager.setConnected(false);
                        showNetworkStatusBar("Không có kết nối mạng", R.color.red);
                    }
                }

            }
        });
    }
    private void showNetworkStatusBar(String message, int colorResource) {
        binding.linearLayout.setBackgroundColor(getResources().getColor(colorResource));
        binding.tvNetworkStatus.setText(message);
        networkObservable = Observable.interval(3, TimeUnit.SECONDS)
                .takeWhile(new Predicate<Long>() {
                    @Override
                    public boolean test(Long aLong) throws Throwable {
                        return !networkStatusManager.isConnected();
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        networkObservable.subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull Long aLong) {
                        if (!networkStatusManager.isConnected()) {
                            Toast.makeText(MainActivity.this, "Vẫn chưa có kết nối mạng", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        binding.tvNetworkStatus.setText("");
                        Toast.makeText(MainActivity.this, "Đã có kết nối mạng", Toast.LENGTH_SHORT).show();
                        binding.linearLayout.setBackgroundColor(Color.TRANSPARENT);
//                        binding.linearLayout.setVisibility(View.INVISIBLE);
                    }
                });
    }

    private void resetIfNumberOfColumnChanges() {
        int numberOfColumn = Integer.parseInt(sharedPrefManager.getNumberOfColumn());
        if (numberOfColumn != imageAdapter.getNumberOfColumn()) {
            binding.rvImages.setLayoutManager(new GridLayoutManager(this, numberOfColumn));
            imageAdapter.setNumberOfColumn(numberOfColumn);
            imageAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        resetIfNumberOfColumnChanges();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(internetBroadcastReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(internetBroadcastReceiver);
        isFirstInternetConnectionCheck = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        compositeDisposable.clear();
        binding.tvNetworkStatus.setText("");
        binding.linearLayout.setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initInternetBroadcastReceiver();
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

        initSwipeLayout();

        initImages();

//        retrieveImages();
//        hideSystemUI();
    }
    private void initSwipeLayout() {
        binding.slRvImages.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onCategorySearchChosen.onCategorySearchChosen(categoryAdapter.getSelectedCategories());
                if(binding.slRvImages.isRefreshing()) {
                    binding.slRvImages.setRefreshing(false);
                }
            }
        });
    }

    private void initImages() {
        binding.rvImages.setLayoutManager(new GridLayoutManager(this, Integer.parseInt(sharedPrefManager.getNumberOfColumn())));
        imageAdapter = new ImageAdapter(MainActivity.this, getSupportFragmentManager(), Integer.parseInt(sharedPrefManager.getNumberOfColumn()));
        binding.rvImages.setAdapter(imageAdapter);
        ImageItemTouchHelper imageItemTouchHelper = new ImageItemTouchHelper(imageAdapter, imageViewModel, imageCategoryViewModel);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(imageItemTouchHelper);
        itemTouchHelper.attachToRecyclerView(binding.rvImages);
        binding.rvImages.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (!recyclerView.canScrollVertically(1)) {
                    retrieveMoreImagesByRxJava();
                }
            }
        });
        retrieveImagesByRxJava();
    }

    private void initButtons() {
//        binding.btChooseImage.setOnClickListener(v -> openFileChooser());
        binding.btChooseImage.setOnClickListener(v -> chooseImageFromGalleryWithTedImagePicker());

        binding.tvSeeMore.setOnClickListener(v -> {
            Log.d("Kiem tra HashMap", notificationMap.toString());
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

        binding.btBrowseMemes.setOnClickListener(v -> {
            Intent intent = new Intent(this, BrowseMemeActivity.class);
            startActivity(intent);
        });
    }

    private void initCategories() {
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getApplicationContext());
        layoutManager.setFlexDirection(FlexDirection.ROW);
        binding.rvCategories.setLayoutManager(layoutManager);
        categoryAdapter = new MainCategoryAdapter(new ArrayList<>(), onCategorySearchChosen);
        binding.rvCategories.setAdapter(categoryAdapter);
        categoryViewModel.addCategoryObserver(categoryAdapter);
        categoryViewModel.getCategoriesFirebase(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                categoryViewModel.setCategories(task.getResult().toObjects(CategoryModel.class));
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

    private void retrieveImagesByRxJava() {
        String numberOfImages;
        final int thisSearchNumber = numberOfTimesSearched;
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
                                imageViewModel.setImages(images);
                                emitter.onSuccess(images);
                                if (!images.isEmpty()) {
                                    lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
                                }
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
                if (imageModels.isEmpty()) {
                    binding.getRoot().setBackgroundResource(R.drawable.background3);
                } else {
                    binding.getRoot().setBackgroundResource(R.drawable.background);
                }
                for (ImageModel imageModel : imageModels) {
                    Log.d("receiving Images", imageModel.imageURL);
                    if (thisSearchNumber == numberOfTimesSearched) {
                        imageAdapter.addImage(imageModel);
                    }
                }
            }

            @Override
            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                Log.d("error rxjava", e.getMessage());
                Log.w(TAG, "Error getting my imageModel", e);
            }
        });
    }

    private void retrieveMoreImagesByRxJava() {
        if (searchingByCategory) {
            return;
        }
        String numberOfImages;
        if (sharedPrefManager.contains("Number of images")) {
            numberOfImages = sharedPrefManager.getData("Number of images");
        } else {
            numberOfImages = "40";
        }
        Single<List<ImageModel>> observable = Single.<List<ImageModel>>create(emitter -> {

                    if (lastVisible != null) {
                        imageViewModel.getMoreMyImagesFirebase(Integer.parseInt(numberOfImages), lastVisible, new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    List<ImageModel> images = task.getResult().toObjects(ImageModel.class);
                                    imageViewModel.addImages(images);
                                    emitter.onSuccess(images);
                                    if (!images.isEmpty()) {
                                        lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
                                    }
                                } else {
                                    emitter.onError(task.getException());
                                }

                            }
                        });
                    }

                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        observable.subscribe(new SingleObserver<List<ImageModel>>() {
            @Override
            public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull List<ImageModel> imageModels) {
                for (ImageModel imageModel : imageModels) {
                    Log.d("receiving Images", imageModel.imageURL);
                    imageAdapter.addImage(imageModel);
                }
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
                uploadImagesToCloudinary(uriList);
            }
        }
    }
    private void chooseImageFromGalleryWithTedImagePicker() {
        TedImagePicker.with(this)
                .startMultiImage(new OnMultiSelectedListener() {
                    @Override
                    public void onSelected(@NotNull List<? extends Uri> uriList) {
                        List<Uri> uriListConverted = new ArrayList<>(uriList);
                        uploadImagesToCloudinary(uriListConverted);
                    }
                });
    }
    private void uploadImagesToCloudinary(List<Uri> uriList) {
        imageViewModel.uploadImagesCloudinary(uriList, new UploadCallback() {
            @Override
            public void onStart(String requestId) {
                int notificationId = (int) System.currentTimeMillis() + (new Random().nextInt(50));
                initializeNotification(notificationId, "Uploading Image " + notificationId, requestId);
                Log.d(TAG, "Upload started for request: " + requestId + " " + notificationId);

            }
            // Notification hay bi "Upload progress for request" trong khi da upload success roi, day chi la cach tam thoi (neu onProgress de trong thi khong van de gi ca)
            @Override
            public void onProgress(String requestId, long bytes, long totalBytes) {
                int progress = (int) ((bytes * 100) / totalBytes);
                Log.d(TAG, "Upload progress for request: " + requestId + " - " + bytes + "/" + totalBytes);
                if (progress % 30 == 0) {
                    updateNotification(requestId, progress);
                }
            }

            @Override
            public void onSuccess(String requestId, Map resultData) {
                String imageUrl = (String) resultData.get("secure_url");
                Log.d(TAG, "Upload successful. Image URL: " + imageUrl);

                ImageModel imageModel = new ImageModel();
                String extractedPublicId = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
                imageModel.iId = extractedPublicId;
                imageModel.imageName = (String) resultData.get("public_id");
                imageModel.userId = myUserId;
                imageModel.imageURL = imageUrl;
                imageModel = imageViewModel.addImageFirebase(imageModel);
                Log.d("Upload cloudinary", "Upload images successful. Download URL: " + imageModel.toString() + " \n" +  imageUrl.toString());

                imageViewModel.addImageFirst(imageModel);
                imageAdapter.addImageFirst(imageModel);
                binding.rvImages.scrollToPosition(0);

                String url = MediaManager.get().url()
                        .transformation(new Transformation().quality("auto").chain().fetchFormat("auto"))
                        .generate(imageModel.imageName);
                ImageModel finalImageModel = imageModel;
                Glide.with(MainActivity.this).asBitmap().load(url)
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {
                                imageCategoryViewModel.getAICategoriesSuggestions(bitmap, finalImageModel, 0);
                                Log.d("Image size before giving to Gemini", String.valueOf(bitmap.getAllocationByteCount()));
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {

                            }
                        });
                successNotification(requestId, "Upload Complete " + requestId);

                notificationMap.remove(requestId);
            }

            @Override
            public void onError(String requestId, ErrorInfo error) {
                // Handle failure: update the notification to show error
                errorNotification(requestId, "Upload Failed: " + error.getDescription() + ", please don't exit app while uploading");

                // Remove the entry from the map
                notificationMap.remove(requestId);

            }

            @Override
            public void onReschedule(String requestId, ErrorInfo error) {

            }
        });
    }
    private void initializeNotification(int notificationId, String title, String requestId) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "upload_channel")
                .setContentTitle(title)
                .setContentText("Upload in progress")
                .setSmallIcon(R.drawable.ic_upload)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .setProgress(100, 0, false);

        notificationManager.notify(notificationId, notificationBuilder.build());
        notificationMap.put(requestId, new Pair<>(notificationId, notificationBuilder));  // Store requestId and notificationId
    }
    private void updateNotification(String requestId, int progress) {
        Log.d("Upload notification update", progress + " " + requestId);
        Pair<Integer, NotificationCompat.Builder> notificationData = notificationMap.get(requestId);
        if (notificationData != null) {
            int notificationId = notificationData.first;
            NotificationCompat.Builder builder = notificationData.second;

            // Update progress on the same builder
            builder.setProgress(100, progress, false);
            if (progress < 90) {
                notificationManager.notify(notificationId, builder.build());
            }

            // Notify the system to update the notification
        }

    }
    private void successNotification(String requestId, String title) {
        Log.d("Upload notification success", title + " " + requestId);
        Log.d("HashMap Notification", notificationMap.toString());

        Pair<Integer, NotificationCompat.Builder> notificationData = notificationMap.get(requestId);
        if (notificationData != null) {
            int notificationId = notificationData.first;
            NotificationCompat.Builder builder = notificationData.second;
            notificationMap.remove(requestId);

            builder.setContentTitle(title)
                    .setContentText("Upload Complete")
                    .setProgress(0, 0, false)
                    .setOngoing(false)
                    .setAutoCancel(true);

            notificationManager.notify(notificationId, builder.build());

            // Remove the requestId from the map
        }

    }
    private static final String CHANNEL_ID = "upload_channel";

    private void createNotificationChannel() {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelName = "Upload Progress";
            String channelDescription = "Notification for tracking file uploads";
            int importance = NotificationManager.IMPORTANCE_LOW;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, importance);
            channel.setDescription(channelDescription);

            // Register the channel with the system
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void errorNotification(String requestId, String errorMessage) {
        Pair<Integer, NotificationCompat.Builder> notificationData = notificationMap.get(requestId);
        if (notificationData != null) {
            int notificationId = notificationData.first;
            NotificationCompat.Builder builder = notificationData.second;

            builder.setContentTitle("Upload Failed")
                    .setContentText(errorMessage)
                    .setProgress(0, 0, false)
                    .setOngoing(false);

            notificationManager.notify(notificationId, builder.build());

            // Remove the requestId from the map
            notificationMap.remove(requestId);
        }
    }


    public interface UploadImageListener {
        public void onSuccessUploadingImages(ImageModel imageModel);
        public void onSuccessGetAICategoriesSuggestion(List<ImageCategoryModel> imageCategoryModels);
    }

    public interface CategorySearchListener {
        public void onCategorySearchChosen(Set<String> categories);
    }
}