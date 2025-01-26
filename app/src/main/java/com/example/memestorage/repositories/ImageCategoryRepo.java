package com.example.memestorage.repositories;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.memestorage.activities.MainActivity;
import com.example.memestorage.models.ImageModel;
import com.example.memestorage.utils.AIImageCategoryResponseListener;
import com.example.memestorage.utils.FirebaseHelper;
import com.example.memestorage.models.ImageCategoryModel;
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
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ImageCategoryRepo {
    private static final String IMAGE_CATEGORY_COLLECTION_NAME = "imageCategories";
    private String myUserId = Objects.requireNonNull(FirebaseHelper.getInstance().getAuth().getCurrentUser()).getUid();
    private static final String USER_COLLECTION_NAME = "users";
    private final FirebaseFirestore db = FirebaseHelper.getInstance().getDb();
    private DocumentReference myImageCategoriesRef = db.collection(USER_COLLECTION_NAME).document(myUserId);

    private static final int MAX_RETRIES = 5;
    private static final long BASE_DELAY_MS = 10000L; // Start with 10 seconds delay

    public ImageCategoryRepo() {

    }


    public void getImageCategoryByIdFirebase(String icId, OnCompleteListener<QuerySnapshot> onCompleteListener) {
        myImageCategoriesRef.collection(IMAGE_CATEGORY_COLLECTION_NAME)
                .whereEqualTo("icId", icId)
                .get()
                .addOnCompleteListener(onCompleteListener);
    }
    public void addImageCategoryFirebase(ImageCategoryModel imageCategoryModel, OnCompleteListener<Void> onCompleteListener) {
        String id = myImageCategoriesRef.collection(IMAGE_CATEGORY_COLLECTION_NAME).document().getId(); // Generate a new ID
        imageCategoryModel.icId = id;
        myImageCategoriesRef.collection(IMAGE_CATEGORY_COLLECTION_NAME).document(id).set(imageCategoryModel).addOnCompleteListener(onCompleteListener);
    }


    // Read all categories
    public void getImageCategoriesFirebase(OnCompleteListener<QuerySnapshot> onCompleteListener) {
        myImageCategoriesRef.collection(IMAGE_CATEGORY_COLLECTION_NAME).get().addOnCompleteListener(onCompleteListener);
    }

    public void getImageCategoriesByImageIdFirebase(ImageModel imageModel, OnCompleteListener<QuerySnapshot> onCompleteListener) {
        myImageCategoriesRef.collection(IMAGE_CATEGORY_COLLECTION_NAME)
                .whereEqualTo("imageId", imageModel.iId).get().addOnCompleteListener(onCompleteListener);
    }
    public void getImageCategoriesByCategoryIdFirebase(String categoryId, OnCompleteListener<QuerySnapshot> onCompleteListener) {
        myImageCategoriesRef.collection(IMAGE_CATEGORY_COLLECTION_NAME)
                .whereEqualTo("categoryId", categoryId).get().addOnCompleteListener(onCompleteListener);
    }
    public void getImageCategoriesByImageIdAndCategoryIdFirebase(String imageId, String categoryId, OnCompleteListener<QuerySnapshot> onCompleteListener) {
        myImageCategoriesRef.collection(IMAGE_CATEGORY_COLLECTION_NAME)
                .whereEqualTo("imageId", imageId)
                .whereEqualTo("categoryId", categoryId)
                .get().addOnCompleteListener(onCompleteListener);
    }

    // Update a category
    public void updateImageCategoryFirebase(String id, ImageCategoryModel imageCategoryModel, OnCompleteListener<Void> onCompleteListener) {
        myImageCategoriesRef.collection(IMAGE_CATEGORY_COLLECTION_NAME).document(id).set(imageCategoryModel).addOnCompleteListener(onCompleteListener);
    }

    // Delete a category
    public void deleteImageCategoryFirebase(String id, OnCompleteListener<Void> onCompleteListener) {
        myImageCategoriesRef.collection(IMAGE_CATEGORY_COLLECTION_NAME).document(id).delete().addOnCompleteListener(onCompleteListener);
    }

    public void deleteImageCategoryByCategoryIdFirebase(String categoryId) {
        myImageCategoriesRef.collection(IMAGE_CATEGORY_COLLECTION_NAME).whereEqualTo("categoryId", categoryId)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Log.d("ImageCategory deletion by categoryId", categoryId);
                        if (task.isSuccessful()) {
                            List<ImageCategoryModel> imageCategoryModelList = task.getResult().toObjects(ImageCategoryModel.class);
                            for (ImageCategoryModel imageCategoryModel : imageCategoryModelList) {
                                deleteImageCategoryFirebase(imageCategoryModel.icId, new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Log.d("ImageCategory deletion", imageCategoryModel.toString());
                                    }
                                });
                            }
                        }
                    }
                });
    }

    public void deleteImageCategoryByImageIdFirebase(String imageId) {
        myImageCategoriesRef.collection(IMAGE_CATEGORY_COLLECTION_NAME).whereEqualTo("imageId", imageId)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Log.d("ImageCategory deletion by imageId", imageId);
                        if (task.isSuccessful()) {
                            List<ImageCategoryModel> imageCategoryModelList = task.getResult().toObjects(ImageCategoryModel.class);
                            for (ImageCategoryModel imageCategoryModel : imageCategoryModelList) {
                                deleteImageCategoryFirebase(imageCategoryModel.icId, new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Log.d("ImageCategory deletion", imageCategoryModel.toString());
                                    }
                                });
                            }
                        }
                    }
                });
    }

    public void getAICategoriesSuggestions(Bitmap bitmap, ImageModel imageModel, int retryCount, AIImageCategoryResponseListener responseListener, boolean doubleCheckAISuggestions){
        if (retryCount > MAX_RETRIES) {
            Log.d("AI Google response", "Max retries reached. Giving up.");
            return;
        }
        Log.d("AI Google response", "This is the " + retryCount + " time");

        SafetySetting harassmentSafety = new SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.NONE);
        SafetySetting hateSpeechSafety  = new SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.NONE);
        GenerativeModel generativeModel = new GenerativeModel("gemini-1.5-flash",
//                "AIzaSyC7hv2O0EH2A-L8Hvo0jNYmYGiIfkcbjYY",
//                "AIzaSyBTLd3CSEZFQ41oTviQVIPGlAoc6JojAFQ",
                "AIzaSyA3ZIHa-S1bMu6HWXTp63yI1NvMz-m3cbU",
                null,
                Arrays.asList(harassmentSafety, hateSpeechSafety));

        GenerativeModelFutures model = GenerativeModelFutures.from(generativeModel);
        String categoryNames = CategoryViewModel.getStringListOfCategoryNames();
        String text = "i want to categorize this picture, 1 picture can have many categories," +
                " only choose the categories below, if not, return 'There are no categories that fit this image', and give me no duplicates\n" +
                categoryNames + "\n" +
                "with each category separated by a comma";
        Log.d("Gemini Text Sent", text);
        Content content = new Content.Builder()
                .addText(text)
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
                    if (doubleCheckAISuggestions) {
                        responseListener.onReceiveAIImageCategorySuggestions(ImageCategoryUtil
                                .stringToImageCategoryList(responseText, imageModel), responseText);
                    } else {
                    addImageCategoryAfterGettingAISuggestion(ImageCategoryUtil
                            .stringToImageCategoryList(responseText, imageModel));
                    }
                } else {
                    Log.d("AI Google response", "responseText null");
                }

            }
            @Override
            public void onFailure(Throwable t) {
                if (t.getMessage().contains("RESOURCE_EXHAUSTED")
                        || t.getMessage().contains("SAFETY")
                        || t.getMessage().contains("Quota")
                        || t.getMessage().contains("UNAVAILABLE")) {
                    // Exponential backoff retry mechanism
                    long delay = (long) (BASE_DELAY_MS * Math.pow(2, retryCount)); // Exponential backoff
                    Log.d("AI Google Failed response", t.getMessage());
                    Log.d("AI Google Failed response", "Retrying in " + delay + "ms...");
                    new Handler(Looper.getMainLooper()).postDelayed(() ->
                            getAICategoriesSuggestions(bitmap, imageModel, retryCount + 1, responseListener, doubleCheckAISuggestions), delay);
                } else {
                    t.printStackTrace();
                }
            }
        }, executor);
    }

    private void addImageCategoryAfterGettingAISuggestion(List<ImageCategoryModel> imageCategoryModels) {
        for (ImageCategoryModel imageCategoryModel : imageCategoryModels) {
            addImageCategoryFirebase(imageCategoryModel, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d("Adding imagecategories by AI's API", imageCategoryModel.toString());
                    } else {
                        Log.d("Adding imagecategories by AI's API", "Failed");
                    }
                }
            });
        }

    }

}