package com.example.memestorage.repositories;

import static android.content.ContentValues.TAG;


import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.cloudinary.api.ApiResponse;
import com.cloudinary.utils.ObjectUtils;
import com.example.memestorage.R;
import com.example.memestorage.utils.CloudinaryHelper;
import com.example.memestorage.utils.FirebaseHelper;
import com.example.memestorage.activities.MainActivity;
import com.example.memestorage.models.ImageModel;
import com.example.memestorage.utils.ImageCategoryUtil;
import com.example.memestorage.viewmodels.CategoryViewModel;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.BlockThreshold;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.ai.client.generativeai.type.HarmCategory;
import com.google.ai.client.generativeai.type.SafetySetting;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.CompletableEmitter;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.core.CompletableOnSubscribe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ImageRepo {
    private static final String IMAGE_COLLECTION_NAME = "images";
    private static final String USER_COLLECTION_NAME = "users";
    private String myUserId = Objects.requireNonNull(FirebaseHelper.getInstance().getAuth().getCurrentUser()).getUid();
    private final FirebaseFirestore db = FirebaseHelper.getInstance().getDb();
    private final FirebaseStorage storage = FirebaseHelper.getInstance().getStorage();

    private DocumentReference myImagesRef = db.collection(USER_COLLECTION_NAME).document(myUserId);
    private static final int MAX_RETRIES = 5;
    private static final long BASE_DELAY_MS = 6000L; // Start with 6 second delay



    public ImageRepo() {
    }


    public ImageModel addImageFirebase(ImageModel imageModel) {
//        String id = db.collection(USER_COLLECTION_NAME).document(myUserId).collection(IMAGE_COLLECTION_NAME).document().getId(); // Generate a new ID
//        imageModel.iId = imageModel.imageName + id;
        myImagesRef.collection(IMAGE_COLLECTION_NAME).document(imageModel.iId).set(imageModel);
        return imageModel;
    }

    // Read all my Images
    public void getMyImagesFirebase(int limit, OnCompleteListener<QuerySnapshot> onCompleteListener) {
        myImagesRef.collection(IMAGE_COLLECTION_NAME)
                .orderBy("iId", Query.Direction.DESCENDING).limit(limit).get().addOnCompleteListener(onCompleteListener);
    }

    public void getMoreMyImagesFirebase(int limit, DocumentSnapshot lastDocument, OnCompleteListener<QuerySnapshot> onCompleteListener) {
        myImagesRef.collection(IMAGE_COLLECTION_NAME)
                .orderBy("iId", Query.Direction.DESCENDING).startAfter(lastDocument).limit(limit).get().addOnCompleteListener(onCompleteListener);
    }

    public void getMyImagesByIdFirebase(String iId, OnCompleteListener<DocumentSnapshot> onCompleteListener) {
        myImagesRef.collection(IMAGE_COLLECTION_NAME).document(iId).get().addOnCompleteListener(onCompleteListener);
    }
    public void getMyImagesByListIdFirebase(List<String> imageIds, OnCompleteListener<DocumentSnapshot> onCompleteListener) {
        for (String imageId : imageIds) {
            myImagesRef.collection(IMAGE_COLLECTION_NAME).document(imageId).get().addOnCompleteListener(onCompleteListener);
        }
    }


    // Update an Image
    public void updateImageFirebase(String id, ImageModel imageModel, OnCompleteListener<Void> onCompleteListener) {
        myImagesRef.collection(IMAGE_COLLECTION_NAME).document(id).set(imageModel).addOnCompleteListener(onCompleteListener);
    }

    // Delete an Image
    public void deleteImageFirebase(String id, OnCompleteListener<Void> onCompleteListener) {
        myImagesRef.collection(IMAGE_COLLECTION_NAME).document(id).delete().addOnCompleteListener(onCompleteListener);
    }

//    public void uploadImagesFirebaseStorage(List<Uri> imageUris, ContentResolver contentResolver, MainActivity.UploadImageListener uploadImageListener) {
//        if (!imageUris.isEmpty()) {
//            StorageReference storageReference = FirebaseStorage.getInstance().getReference("uploads");
//
//            for (Uri imageUri : imageUris) {
//                try {
//                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri);
//
//                    // Tạo ByteArrayOutputStream
//                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//
//                    // Nén Bitmap thành JPEG với chất lượng 50% (chất lượng có thể thay đổi từ 0 đến 100)
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
//
//                    // Chuyển ByteArrayOutputStream thành byte array
//                    byte[] data = baos.toByteArray();
//
//                    StorageReference fileReference = storageReference.child("" + System.currentTimeMillis());
//
//                    fileReference.putBytes(data)
//                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                                @Override
//                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                        @Override
//                                        public void onSuccess(Uri downloadUri) {
//                                            ImageModel imageModel = new ImageModel();
//                                            imageModel.imageName = fileReference.getName();
//                                            imageModel.userId = myUserId;
//                                            imageModel.imageURL = downloadUri.toString();
//                                            imageModel = addImageFirebase(imageModel);
//                                            Log.d(TAG, "Upload images successful. Download URL: " + downloadUri.toString());
//                                            uploadImageListener.onSuccessUploadingImages(imageModel);
//                                            getAICategoriesSuggestions(bitmap, imageModel, uploadImageListener);
//                                        }
//                                    });
//                                }
//                            })
//                            .addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Log.d(TAG, "Upload images failed: " + e.getMessage());
//                                }
//                            });
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        } else {
//            Log.d(TAG, "No files selected");
//        }
//
//    }

    public void uploadImagesCloudinary(List<Uri> imageUris, ContentResolver contentResolver, MainActivity.UploadImageListener uploadImageListener, Context context) {
        if (!imageUris.isEmpty()) {
            Observable.fromIterable(imageUris)
                    .map(new Function<Uri, byte[]>() {
                        @Override
                        public byte[] apply(Uri imageUri) throws Throwable {
                            Log.d("Check xem den doan mapping rxjava chua", "check");
                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri);

                                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                                bitmap.compress(Bitmap.CompressFormat.JPEG, 99, baos);

                                byte[] data = baos.toByteArray();

                                return data;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            return new byte[0];
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<byte[]>() {
                        @Override
                        public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

                        }

                        @Override
                        public void onNext(byte @io.reactivex.rxjava3.annotations.NonNull [] data) {
                            String imageId = System.currentTimeMillis() + myUserId;

                            Map<String, Object> options = new HashMap<>();
                            options.put("format", "jpg");
                            options.put("folder", "meme_storage/images");
                            options.put("public_id", imageId);

                            MediaManager.get().upload(data)
                                    .unsigned("your_unsigned_preset")
                                    .options(options)
                                    .callback(new UploadCallback() {
                                        @Override
                                        public void onStart(String requestId) {
                                            // Upload started
                                            Log.d(TAG, "Upload started for request: " + requestId);
                                        }

                                        @Override
                                        public void onProgress(String requestId, long bytes, long totalBytes) {
                                            // Upload progress
                                            Log.d(TAG, "Upload progress for request: " + requestId + " - " + bytes + "/" + totalBytes);
                                        }

                                        @Override
                                        public void onSuccess(String requestId, Map resultData) {
                                            // Upload success
                                            String imageUrl = (String) resultData.get("secure_url");
                                            Log.d(TAG, "Upload successful. Image URL: " + imageUrl);

                                            ImageModel imageModel = new ImageModel();
                                            imageModel.iId = imageId;
                                            imageModel.imageName = (String) resultData.get("public_id");
                                            imageModel.userId = myUserId;
                                            imageModel.imageURL = imageUrl;
                                            imageModel = addImageFirebase(imageModel);
                                            Log.d("Upload cloudinary", "Upload images successful. Download URL: " + imageModel.toString() + " \n" +  imageUrl.toString());
                                            uploadImageListener.onSuccessUploadingImages(imageModel);
                                            String url = MediaManager.get().url()
//                                                    .transformation(new Transformation()
//                                                            .quality("1")
//                                                            .width(400))
                                                    .generate(imageModel.imageName);

                                            ImageModel finalImageModel = imageModel;
                                            Glide.with(context).asBitmap().load(url).listener(new RequestListener<Bitmap>() {
                                                        @Override
                                                        public boolean onLoadFailed(@Nullable GlideException e, @Nullable Object model, @NonNull Target<Bitmap> target, boolean isFirstResource) {
                                                            e.printStackTrace();
                                                            Log.d("Check Image before giving to Gemini", model.toString());
                                                            return false;
                                                        }

                                                        @Override
                                                        public boolean onResourceReady(@NonNull Bitmap resource, @NonNull Object model, Target<Bitmap> target, @NonNull DataSource dataSource, boolean isFirstResource) {
                                                            return false;
                                                        }
                                                    })
                                                    .into(new CustomTarget<Bitmap>() {
                                                @Override
                                                public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {
                                                    getAICategoriesSuggestions(bitmap, finalImageModel, uploadImageListener, 0);
                                                    Log.d("Image size before giving to Gemini", String.valueOf(bitmap.getAllocationByteCount()));
                                                }

                                                @Override
                                                public void onLoadCleared(@Nullable Drawable placeholder) {

                                                }
                                            });

                                        }

                                        @Override
                                        public void onError(String requestId, ErrorInfo error) {
                                            // Upload error
                                            Log.d(TAG, "Upload failed: " + error.getDescription());
                                        }

                                        @Override
                                        public void onReschedule(String requestId, ErrorInfo error) {
                                            // Upload rescheduled
                                            Log.d(TAG, "Upload rescheduled: " + error.getDescription());
                                        }
                                    }).dispatch();
                        }

                        @Override
                        public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            Log.d(TAG, "No files selected");
        }
    }

    public void getAICategoriesSuggestions(Bitmap bitmap, ImageModel imageModel, MainActivity.UploadImageListener uploadImageListener, int retryCount){
        if (retryCount > MAX_RETRIES) {
            Log.d("AI Google response", "Max retries reached. Giving up.");
            return;
        }
        Log.d("AI Google response", "This is the " + retryCount + " time");

        SafetySetting harassmentSafety = new SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.NONE);
        SafetySetting hateSpeechSafety  = new SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.NONE);
        GenerativeModel generativeModel = new GenerativeModel("gemini-1.5-flash",
                "AIzaSyA1nRy25ETbj9swvGC83kdchtB-9rG3Fks",
                null,
                Arrays.asList(harassmentSafety, hateSpeechSafety));

        GenerativeModelFutures model = GenerativeModelFutures.from(generativeModel);
        String categoryNames = CategoryViewModel.getStringListOfCategoryNames();
        Content content = new Content.Builder()
                .addText("i want to categorize this picture, 1 picture can have many categories," +
                        " only choose the categories below, if not, leave it blank, and give me no duplicates\n" +
                        categoryNames + "\n" +
                        "with each category separated by a comma")
                .addImage(bitmap)
                .build();
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        Executor executor = Executors.newSingleThreadExecutor();
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse response) {
                String responseText = response.getText();
                if (responseText != null) {
                    Log.d("Gemini response", imageModel.imageURL + "\n" + responseText);
                    uploadImageListener.onSuccessGetAICategoriesSuggestion(ImageCategoryUtil
                            .stringToImageCategoryList(responseText, imageModel));
                } else {
                    Log.d("AI Google response", "responseText null");
                }

            }
            @Override
            public void onFailure(Throwable t) {
                if (t.getMessage().contains("RESOURCE_EXHAUSTED")) {
                    // Exponential backoff retry mechanism
                    long delay = (long) (BASE_DELAY_MS * Math.pow(3, retryCount)); // Exponential backoff
                    Log.d("AI Google Failed response", "Resource exhausted. Retrying in " + delay + "ms...");
                    new Handler(Looper.getMainLooper()).postDelayed(() ->
                            getAICategoriesSuggestions(bitmap, imageModel, uploadImageListener, retryCount + 1), delay);
                } else {
                    t.printStackTrace();
                }
            }
        }, executor);
    }

    public void deleteImageFirebaseStorage(String imageUrl) {
        StorageReference storageRef = storage.getReferenceFromUrl(imageUrl);

        storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                Log.d("Delete Image Storage", "Image in Storage deleted successfully");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // An error occurred
                Log.d("Delete Image Storage", "Failed to delete Storage image: " + exception.getMessage());
            }
        });
    }

    public void deleteImageCloudinary(ImageModel imageModel) {
        Completable.create(emitter -> {
            try {
                Map<String, Object> deleteParams = ObjectUtils.asMap("invalidate", true );
                CloudinaryHelper.getInstance().uploader().destroy(imageModel.imageName, deleteParams);
                emitter.onComplete();
            } catch (IOException e) {
                emitter.onError(e);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

            }

            @Override
            public void onComplete() {
                Log.d("Delete Image Cloudinary", "Successful");

            }

            @Override
            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                Log.d("Delete Image Cloudinary", "Failed");
                e.printStackTrace();
            }
        });

    }
}
