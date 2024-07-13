package com.example.memestorage;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.memestorage.databinding.FragmentImageBinding;
import com.example.memestorage.models.ImageModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ImageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImageFragment extends Fragment {

    private static final String ARG_IMAGE = "image";
    FragmentImageBinding binding;
    private ImageModel imageModel;

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

        binding.setImageModel(imageModel);

        Glide.with(this).load(imageModel.imageURL).into(binding.imageViewDetail);

        return binding.getRoot();
    }
}