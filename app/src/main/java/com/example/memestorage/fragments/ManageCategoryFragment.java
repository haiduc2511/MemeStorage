package com.example.memestorage.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.memestorage.R;
import com.example.memestorage.adapters.AddCategoryCategoryAdapter;
import com.example.memestorage.broadcastreceiver.InternetBroadcastReceiver;
import com.example.memestorage.broadcastreceiver.NetworkStatusManager;
import com.example.memestorage.databinding.FragmentManageCategoryBinding;
import com.example.memestorage.models.CategoryModel;
import com.example.memestorage.utils.CategoryItemTouchHelper;
import com.example.memestorage.utils.CategoryUtil;
import com.example.memestorage.viewmodels.CategoryViewModel;
import com.example.memestorage.viewmodels.ImageCategoryViewModel;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ManageCategoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ManageCategoryFragment extends Fragment {
    FragmentManageCategoryBinding binding;
    CategoryViewModel categoryViewModel;
    ImageCategoryViewModel imageCategoryViewModel;
    AddCategoryCategoryAdapter categoryAdapter;

    public ManageCategoryFragment() {
    }

    public static ManageCategoryFragment newInstance() {
        ManageCategoryFragment fragment = new ManageCategoryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentManageCategoryBinding.inflate(getLayoutInflater(), container, false);
        categoryViewModel = CategoryViewModel.newInstance();
        imageCategoryViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()).create(ImageCategoryViewModel.class);
        initUI();

        return binding.getRoot();
    }

    private void initUI() {
        binding.btAddCategory.setOnClickListener(v -> {
            String categoryName = binding.etCategoryName.getText().toString();
            if (CategoryUtil.catetogyIsAcceptable(categoryName, requireContext())) {
                addNewCategory();
            }
        });

        initCategories();
        binding.btAddCategory.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                openCategorySuggestFragment();
                return false;
            }
        });

    }

    private void initCategories() {
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(requireContext().getApplicationContext());
        layoutManager.setFlexDirection(FlexDirection.ROW);
        binding.rvCategories.setLayoutManager(layoutManager);

        categoryAdapter = new AddCategoryCategoryAdapter(requireContext());
        binding.rvCategories.setAdapter(categoryAdapter);

        CategoryItemTouchHelper categoryItemTouchHelper = new CategoryItemTouchHelper(categoryAdapter, categoryViewModel, imageCategoryViewModel);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(categoryItemTouchHelper);
        itemTouchHelper.attachToRecyclerView(binding.rvCategories);

        categoryViewModel.addCategoryObserver(categoryAdapter);

        categoryAdapter.setCategoryModels(categoryViewModel.getCategories());
        categoryViewModel.getCategoriesFirebase(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().toObjects(CategoryModel.class).isEmpty()) {
                        openCategorySuggestFragment();
                    }
                }
            }
        });

    }
    private void openCategorySuggestFragment() {
        CategorySuggestFragment fragment = CategorySuggestFragment.newInstance();
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_add_category, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void addNewCategory() {
        String categoryName = binding.etCategoryName.getText().toString();
        CategoryModel categoryModel = new CategoryModel();
        categoryModel.categoryName = categoryName;
        categoryViewModel.addCategoryFirebase(categoryModel, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("Add category model", categoryModel.toString());
            }
        });
        binding.etCategoryName.setText("");

    }

}