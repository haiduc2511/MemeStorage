package com.example.memestorage.activities;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.RecoverableSecurityException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.cloudinary.Transformation;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.memestorage.BuildConfig;
import com.example.memestorage.R;
import com.example.memestorage.adapters.MainCategoryAdapter;
import com.example.memestorage.broadcastreceiver.InternetBroadcastReceiver;
import com.example.memestorage.broadcastreceiver.NetworkStatusManager;
import com.example.memestorage.customview.SafeFlexboxLayoutManager;
import com.example.memestorage.fragments.AccountFragment;
import com.example.memestorage.fragments.DoubleCheckAISuggestionsFragment;
import com.example.memestorage.fragments.ImageFragment;
import com.example.memestorage.fragments.MainFragment;
import com.example.memestorage.fragments.ManageCategoryFragment;
import com.example.memestorage.models.CategoryModel;
import com.example.memestorage.models.ImageCategoryModel;
import com.example.memestorage.utils.AIImageCategoryResponseListener;
import com.example.memestorage.utils.ImageItemTouchHelper;
import com.example.memestorage.utils.FirebaseHelper;
import com.example.memestorage.adapters.ImageAdapter;
import com.example.memestorage.models.ImageModel;
import com.example.memestorage.utils.ImageUploadListener;
import com.example.memestorage.utils.MediaManagerState;
import com.example.memestorage.utils.SharedPrefManager;
import com.example.memestorage.viewmodels.CategoryViewModel;
import com.example.memestorage.viewmodels.ImageCategoryViewModel;
import com.example.memestorage.viewmodels.ImageViewModel;
import com.example.memestorage.databinding.ActivityMainBinding;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

import gun0912.tedimagepicker.builder.TedImagePicker;
import gun0912.tedimagepicker.builder.listener.OnMultiSelectedListener;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Predicate;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    static final int PICK_IMAGES_REQUEST = 1;
    private static final int REQUEST_CODE = 1;
    ImageViewModel imageViewModel;
    ImageCategoryViewModel imageCategoryViewModel;
    ImageAdapter imageAdapter;
    boolean isFirstInternetConnectionCheck = true;
    NetworkStatusManager networkStatusManager = NetworkStatusManager.getInstance();
    InternetBroadcastReceiver internetBroadcastReceiver;
    SharedPrefManager sharedPrefManager;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    Observable<Long> networkObservable;
    String myUserId = Objects.requireNonNull(FirebaseHelper.getInstance().getAuth().getCurrentUser()).getUid();
    private Map<String, Pair<Integer, NotificationCompat.Builder>> notificationMap = new HashMap<>();
    NotificationManager notificationManager;
    private ImageUploadListener imageUploadListener;
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
        initBottomNavigation();
        initCloudinary();
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
        imageCategoryViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(ImageCategoryViewModel.class);
    }
    private void initBottomNavigation() {
        MainFragment mainFragment = MainFragment.newInstance();
        imageUploadListener = mainFragment;
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_main, mainFragment).commit();
        binding.bnMain.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                if (R.id.nav_category == id) {
                    fragmentTransaction
                            .hide(mainFragment)
                            .replace(R.id.fragment_container, ManageCategoryFragment.newInstance())
                            .commit();
                }
                if (R.id.nav_profile == id) {
                    fragmentTransaction
                            .hide(mainFragment)
                            .replace(R.id.fragment_container, AccountFragment.newInstance())
                            .commit();
                }
                if (R.id.nav_home == id) {
                    Fragment otherFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                    if (otherFragment != null) {
                        fragmentTransaction
                                .remove(otherFragment);
                    }
                    fragmentTransaction
                            .show(mainFragment)
                            .commit();
                }
                if (R.id.nav_search == id) {
                    Toast.makeText(MainActivity.this, "Chưa phát triển tính năng này hêh", Toast.LENGTH_SHORT).show();
                    return false;
                }
                return true;
            }

        });
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
        networkObservable = Observable.interval(5, TimeUnit.SECONDS)
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
//
//    private void resetIfNumberOfColumnChanges() {
//        int numberOfColumn = Integer.parseInt(sharedPrefManager.getNumberOfColumn());
//        if (numberOfColumn != imageAdapter.getNumberOfColumn()) {
//            binding.rvImages.setLayoutManager(new GridLayoutManager(this, numberOfColumn));
//            imageAdapter.setNumberOfColumn(numberOfColumn);
//            imageAdapter.notifyDataSetChanged();
//        }
//    }

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


    private void initUI() {
        initButtons();
    }


    private void initButtons() {
        binding.btChooseImage.setOnClickListener(v -> chooseImageFromGalleryWithTedImagePicker());
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
//    private void openFileChooser() {
//        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//        intent.setType("image/*");
//        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//        startActivityForResult(Intent.createChooser(intent, "Select Pictures"), PICK_IMAGES_REQUEST);
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        if (requestCode == PICK_IMAGES_REQUEST && resultCode == RESULT_OK) {
//            if (data != null) {
//                List<Uri> uriList = new ArrayList<>();
//                if (data.getClipData() != null) { // Multiple images selected
//                    int count = data.getClipData().getItemCount();
//                    for (int i = 0; i < count; i++) {
//                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
//                        Log.d("URI", imageUri.toString());
//                        uriList.add(imageUri);
//                    }
//                } else if (data.getData() != null) { // Single image selected
//                    Uri imageUri = data.getData();
//                    uriList.add(imageUri);
//                }
//                uploadImagesToCloudinary(uriList);
//            }
//        }
        if (requestCode == DELETE_PERMISSION_REQUEST) {
            if (resultCode == RESULT_OK) {
                // Permission was granted, perform the action you want to take
                Log.d("PermissionDelete3", "Permission granted. Proceeding with deletion.");
                String uriString = uriStackToDelete.pop().toString();
                Log.d("Delete image gallery", "pop uri " + uriString);

                if (!uriString.isEmpty()) {
                    Uri imageUri = Uri.parse(uriString); // Parse the string back into a URI
                    Log.d("Delete image gallery", "delete again uri " + uriString + "\n" + uriStackToDelete.toString());
                    deleteImageFromUri(imageUri); // Delete the image using the URI
                }
            } else {
                // Permission was denied, handle the case appropriately
                Log.d("Permission delete", "Permission denied.");
                Toast.makeText(this, "Permission denied to delete this image.", Toast.LENGTH_SHORT).show();
            }
        }


    }
    private void chooseImageFromGalleryWithTedImagePicker() {
        TedImagePicker.with(this)
                .startMultiImage(new OnMultiSelectedListener() {
                    @Override
                    public void onSelected(@NotNull List<? extends Uri> uriList) {
                        List<Uri> uriListConverted = new ArrayList<>(uriList);
                        for (Uri uri: uriListConverted) {
                            uploadImageToCloudinary(uri);
                        }
                    }
                });
    }
//    private void uploadImagesToCloudinary(List<Uri> uriList) {
//        imageViewModel.uploadImagesCloudinary(uriList, new UploadCallback() {
//            @Override
//            public void onStart(String requestId) {
//                int notificationId = (int) System.currentTimeMillis() + (new Random().nextInt(50));
//                initializeNotification(notificationId, "Uploading Image " + notificationId, requestId);
//                Log.d(TAG, "Upload started for request: " + requestId + " " + notificationId);
//
//            }
//            // Notification hay bi "Upload progress for request" trong khi da upload success roi, day chi la cach tam thoi (neu onProgress de trong thi khong van de gi ca)
//            @Override
//            public void onProgress(String requestId, long bytes, long totalBytes) {
//                int progress = (int) ((bytes * 100) / totalBytes);
//                Log.d(TAG, "Upload progress for request: " + requestId + " - " + bytes + "/" + totalBytes);
////                if (progress % 30 == 0) {
////                    updateNotification(requestId, progress);
////                }
//            }
//
//            @Override
//            public void onSuccess(String requestId, Map resultData) {
//                String imageUrl = (String) resultData.get("secure_url");
//                Log.d(TAG, "Upload successful. Image URL: " + imageUrl);
//
//                ImageModel imageModel = new ImageModel();
//                String extractedPublicId = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
//                String extractedPublicIdWithoutExtension = extractedPublicId.substring(0, extractedPublicId.lastIndexOf("."));
//                imageModel.iId = extractedPublicIdWithoutExtension;
//                imageModel.imageName = (String) resultData.get("public_id");
//                imageModel.userId = myUserId;
//                imageModel.imageURL = imageUrl;
//                imageModel = imageViewModel.addImageFirebase(imageModel);
//                Log.d("Upload cloudinary", "Upload images successful. Download URL: " + imageModel.toString() + " \n" +  imageUrl.toString());
//
//                imageUploadListener.onSuccessUploadingImages(imageModel);
//
//                String url = MediaManager.get().url()
//                        .transformation(new Transformation().quality("auto").chain().fetchFormat("auto"))
//                        .generate(imageModel.imageName);
//                ImageModel finalImageModel = imageModel;
//                Glide.with(MainActivity.this).asBitmap().load(url)
//                        .into(new CustomTarget<Bitmap>() {
//                            @Override
//                            public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {
//                                imageCategoryViewModel.getAICategoriesSuggestions(bitmap, finalImageModel, 0);
//                                //TODO: Suggestion advisory by user fragment before adding
//                                Log.d("Image size before giving to Gemini", String.valueOf(bitmap.getAllocationByteCount()));
//                            }
//
//                            @Override
//                            public void onLoadCleared(@Nullable Drawable placeholder) {
//
//                            }
//                        });
//                successNotification(requestId, "Upload Complete " + requestId);
//
//                notificationMap.remove(requestId);
//            }
//
//            @Override
//            public void onError(String requestId, ErrorInfo error) {
//                // Handle failure: update the notification to show error
//                errorNotification(requestId, "Upload Failed: " + error.getDescription() + ", please don't exit app while uploading");
//
//                // Remove the entry from the map
//                notificationMap.remove(requestId);
//
//            }
//
//            @Override
//            public void onReschedule(String requestId, ErrorInfo error) {
//
//            }
//        });
//    }


    private void uploadImageToCloudinary(Uri uri) {
        imageViewModel.uploadImageCloudinary(uri, new UploadCallback() {
            @Override
            public void onStart(String requestId) {
                int notificationId = (int) System.currentTimeMillis() + (new Random().nextInt(50));
                initializeNotification(notificationId, "Uploading Image " + notificationId, requestId);
                Log.d(TAG, "Upload started for request: " + requestId + " " + notificationId);

            }
            // Notification hay bi "Upload progress for request" trong khi da upload success roi, day chi la cach tam thoi (neu onProgress de trong thi khong van de gi ca)
            @Override
            public void onProgress(String requestId, long bytes, long totalBytes) {
//                int progress = (int) ((bytes * 100) / totalBytes);
//                Log.d(TAG, "Upload progress for request: " + requestId + " - " + bytes + "/" + totalBytes);
//                if (totalBytes > 1000000) {
//                    if (progress % 10 == 0) {
//                        updateNotification(requestId, progress);
//                    }
//                }
            }

            @Override
            public void onSuccess(String requestId, Map resultData) {
                String imageUrl = (String) resultData.get("secure_url");
                Log.d(TAG, "Upload successful. Image URL: " + imageUrl);

                ImageModel imageModel = new ImageModel();
                String extractedPublicId = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
                String extractedPublicIdWithoutExtension = extractedPublicId.substring(0, extractedPublicId.lastIndexOf("."));
                imageModel.iId = extractedPublicIdWithoutExtension;
                imageModel.imageName = (String) resultData.get("public_id");
                imageModel.userId = myUserId;
                imageModel.imageURL = imageUrl;
                imageModel = imageViewModel.addImageFirebase(imageModel);
                //TODO: remember to add callback for gemini suggestions
                Log.d("Upload cloudinary", "Upload images successful. Download URL: " + imageModel.toString() + " \n" +  imageUrl.toString());

                imageUploadListener.onSuccessUploadingImages(imageModel);

                String url = MediaManager.get().url()
                        .transformation(new Transformation()
                                .quality("auto")
                                .width(Integer.parseInt(sharedPrefManager.getFetchQuality()))
                                .chain()
                                .fetchFormat("auto"))
                        .generate(imageModel.imageName);
                ImageModel finalImageModel = imageModel;
                Glide.with(MainActivity.this).asBitmap().load(url)
                        .error(Glide.with(MainActivity.this).asBitmap())
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {
                                imageCategoryViewModel.getAICategoriesSuggestions(bitmap, finalImageModel, new AIImageCategoryResponseListener() {
                                    @Override
                                    public void onReceiveAIImageCategorySuggestions(List<ImageCategoryModel> imageCategoryModelList, String responseText) {
                                        openDoubleCheckAISuggestionsFragment(bitmap, imageCategoryModelList, finalImageModel, responseText);
                                    }
                                });
                                //TODO: Suggestion advisory by user fragment before adding
                                Log.d("Image size before giving to Gemini", String.valueOf(bitmap.getAllocationByteCount()));
                            }
//
//                            @Override
//                            public void onLoadFailed(@Nullable Drawable errorDrawable) {
//                                Glide.with(MainActivity.this)
//                                        .asBitmap()
//                                        .load(url)  // Retry loading the same URL
//                                        .into(this);  // Use the same CustomTarget for the reload
//                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {

                            }
                        });
                successNotification(requestId, "Upload Complete " + requestId);

                if (sharedPrefManager.getIfDeleteImageGalleryAfterUpload().equals("true")) {
                    deleteImageFromUri(uri);
                }
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

    private void openDoubleCheckAISuggestionsFragment(Bitmap image, List<ImageCategoryModel> imageCategoryModelList, ImageModel imageModel, String responseText) {
        DoubleCheckAISuggestionsFragment fragment = DoubleCheckAISuggestionsFragment.newInstance(image, imageCategoryModelList, imageModel, responseText);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container_double_check_ai_suggestions, fragment)
                .addToBackStack(imageModel.iId)
                .commitAllowingStateLoss();

    }

    private static final int DELETE_PERMISSION_REQUEST = 1001;
    private Stack<Uri> uriStackToDelete = new Stack<>();

    private void deleteImageFromUri(Uri uri) {
        // Check if the Android version is 10+ (Scoped Storage)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Log.d("Delete image gallery", "add uri " + uri.toString());

            try {
                int deletedRows = getContentResolver().delete(uri, null, null);
                if (deletedRows > 0) {
                    Log.d("Delete image gallery", uri + " Success");
                    Toast.makeText(this, "Image deleted in phone's gallery", Toast.LENGTH_SHORT).show(); // On Android 11+, this moves the file to trash
                } else {
                    Log.d("Delete image gallery", uri + " Failed");
                    Toast.makeText(this, "Failed to delete file", Toast.LENGTH_SHORT).show();
                }
            } catch (RecoverableSecurityException e) {
                // Handle the exception by requesting user permission
                try {
                    uriStackToDelete.add(uri);
                    startIntentSenderForResult(e.getUserAction().getActionIntent().getIntentSender(),
                            DELETE_PERMISSION_REQUEST, null, 0, 0, 0);
                } catch (IntentSender.SendIntentException ex) {
                    ex.printStackTrace();
                }
            } catch (SecurityException e) {
//                startIntentSenderForResult(e.getUserAction().getActionIntent().getIntentSender(),
//                        DELETE_PERMISSION_REQUEST, null, 0, 0, 0);
                Log.e("Delete image gallery", "Error: " + e.getMessage());
                Toast.makeText(this, "Permission denied security haha", Toast.LENGTH_SHORT).show();
            }
        } else {
            // For older Android versions, you can still use File.delete()
            File file = new File(Objects.requireNonNull(uri.getPath()));
            if (file.exists()) {
                if (file.delete()) {
                    Log.d("Delete image gallery", uri + " Success");
                    Toast.makeText(this, "File deleted", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("Delete image gallery", uri + " Failed");
                    Toast.makeText(this, "Failed to delete file", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show();
            }
        }
    }


//    private void deleteImageFromUri(Uri uri) {
//        File file = new File(Objects.requireNonNull(uri.getPath()));
//        if (file.exists()) {
//            if (file.delete()) {
//                Log.d("Delete image gallery", uri + " Success");
//                Toast.makeText(this, "File deleted", Toast.LENGTH_SHORT).show();
//            } else {
//                Log.d("Delete image gallery", uri + " Failed");
//                Toast.makeText(this, "Failed to delete file", Toast.LENGTH_SHORT).show();
//            }
//        } else {
//            Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show();
//        }
//    }

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

    public interface CategorySearchListener {
        public void onCategorySearchChosen(Set<String> categories);
    }
}