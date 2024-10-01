package com.example.memestorage.fragments;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.memestorage.R;
import com.example.memestorage.activities.MainActivity;
import com.example.memestorage.adapters.ImageAdapter;
import com.example.memestorage.adapters.MainCategoryAdapter;
import com.example.memestorage.customview.SafeFlexboxLayoutManager;
import com.example.memestorage.databinding.FragmentMainBinding;
import com.example.memestorage.models.CategoryModel;
import com.example.memestorage.models.ImageCategoryModel;
import com.example.memestorage.models.ImageModel;
import com.example.memestorage.utils.ImageItemTouchHelper;
import com.example.memestorage.utils.SharedPrefManager;
import com.example.memestorage.viewmodels.CategoryViewModel;
import com.example.memestorage.viewmodels.ImageCategoryViewModel;
import com.example.memestorage.viewmodels.ImageViewModel;
import com.google.android.flexbox.FlexDirection;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainFragment extends Fragment {
    FragmentMainBinding binding;
    ImageViewModel imageViewModel;
    CategoryViewModel categoryViewModel;
    static boolean showingAllCategories = false;
    ImageCategoryViewModel imageCategoryViewModel;
    MainCategoryAdapter categoryAdapter;
    ImageAdapter imageAdapter;
    int numberOfTimesSearched = 0;
    DocumentSnapshot lastVisible;
    MainActivity.CategorySearchListener onCategorySearchChosen = new MainActivity.CategorySearchListener() {
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
    boolean searchingByCategory = false; //for lazy loading (retrieveMoreImages..)
    SharedPrefManager sharedPrefManager;


    public MainFragment() {
    }

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        initViewModel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(getLayoutInflater(), container, false);
        sharedPrefManager = new SharedPrefManager(requireContext());
        initUI();

        return binding.getRoot();
    }
    private void initViewModel() {
        imageViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()).create(ImageViewModel.class);
        categoryViewModel = CategoryViewModel.newInstance();
        imageCategoryViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()).create(ImageCategoryViewModel.class);
    }

//    private void resetIfNumberOfColumnChanges() {
//        int numberOfColumn = Integer.parseInt(sharedPrefManager.getNumberOfColumn());
//        if (numberOfColumn != imageAdapter.getNumberOfColumn()) {
//            binding.rvImages.setLayoutManager(new GridLayoutManager(requireContext(), numberOfColumn));
//            imageAdapter.setNumberOfColumn(numberOfColumn);
//            imageAdapter.notifyDataSetChanged();
//        }
//    }
    private void getImageCategoriesByCategoryIdList(List<String> categoryIdList) {
        imageCategoryViewModel.getImageCategoriesByCategoryIdFirebase(categoryIdList.get(0), new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                categoryIdList.remove(0);
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
        initCategories();

        initSwipeLayout();

        initImages();

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
        binding.rvImages.setLayoutManager(new GridLayoutManager(requireContext(), Integer.parseInt(sharedPrefManager.getNumberOfColumn())));
        imageAdapter = new ImageAdapter(requireContext(), requireActivity().getSupportFragmentManager(), Integer.parseInt(sharedPrefManager.getNumberOfColumn()));
        binding.rvImages.setAdapter(imageAdapter);
        binding.rvImages.setFadingEdgeLength(100);
        binding.rvImages.setHorizontalFadingEdgeEnabled(true);
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

    private void initCategories() {
        LinearLayoutManager firstLayoutManager = new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false);
        binding.rvCategories.setLayoutManager(firstLayoutManager);
        categoryAdapter = new MainCategoryAdapter(onCategorySearchChosen);

        binding.rvCategories.setAdapter(categoryAdapter);
        categoryViewModel.addCategoryObserver(categoryAdapter);

        binding.btExpand.setOnClickListener(v -> {
//            ViewGroup.LayoutParams params = binding.rvCategories.getLayoutParams();

            if (showingAllCategories) {
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false);
                binding.rvCategories.setLayoutManager(linearLayoutManager);
                binding.rvCategories.setPadding(0, 0, 50, 0);
                binding.btExpand.setBackgroundResource(R.drawable.ic_expand_big);
            } else {
                SafeFlexboxLayoutManager flexboxLayoutManager = new SafeFlexboxLayoutManager(requireActivity().getApplicationContext(), FlexDirection.ROW);
                binding.rvCategories.setLayoutManager(flexboxLayoutManager);
                binding.rvCategories.setPadding(0, 0, 0, 0);
                binding.btExpand.setBackgroundResource(R.drawable.ic_compact_big);
            }
            binding.rvCategories.setAdapter(categoryAdapter);

            showingAllCategories = !showingAllCategories;
//
//            if (binding.tvSeeMore.getText().equals("See more")) {
//                binding.tvSeeMore.setText("See less");
//            } else {
//                binding.tvSeeMore.setText("See more");
//            }
        });

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




}