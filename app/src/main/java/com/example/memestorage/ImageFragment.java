package com.example.memestorage;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.memestorage.adapters.CategoryAdapter;
import com.example.memestorage.databinding.FragmentImageBinding;
import com.example.memestorage.models.CategoryModel;
import com.example.memestorage.models.ImageModel;
import com.example.memestorage.viewmodels.CategoryViewModel;
import com.example.memestorage.viewmodels.ImageViewModel;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ImageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImageFragment extends Fragment {

    private static final String ARG_IMAGE = "image";
    FragmentImageBinding binding;
    private ImageModel imageModel;
    ImageViewModel imageViewModel;
    CategoryViewModel categoryViewModel;
    CategoryAdapter categoryAdapter;

    public static ImageFragment newInstance(ImageModel imageModel) {
        ImageFragment fragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_IMAGE, imageModel);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            imageModel = getArguments().getParcelable(ARG_IMAGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentImageBinding.inflate(inflater, container, false);

        binding.flOutside.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager().popBackStack();
        });
        binding.cvInside.setOnClickListener(v -> {

        });

        categoryViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()).create(CategoryViewModel.class);
        categoryViewModel.getCategoriesFirebase(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                categoryViewModel.setCategories(task.getResult().toObjects(CategoryModel.class));
                categoryAdapter = new CategoryAdapter(categoryViewModel.getCategories());

                FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(requireContext().getApplicationContext());
                layoutManager.setFlexDirection(FlexDirection.ROW);
                binding.rvCategories.setLayoutManager(layoutManager);
                binding.rvCategories.setAdapter(categoryAdapter);
            }
        });


        imageViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()).create(ImageViewModel.class);
        binding.btSaveImage.setOnClickListener(v -> {
            imageModel.imageName = binding.etImageName.getText().toString();
            imageViewModel.updateImageFirebase(imageModel.iId, imageModel, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(requireContext().getApplicationContext(), "Image updated", Toast.LENGTH_SHORT).show();
                }
            });
        });
        binding.setImageModel(imageModel);

        Glide.with(this).load(imageModel.imageURL).into(binding.imageViewDetail);

        return binding.getRoot();
    }
}