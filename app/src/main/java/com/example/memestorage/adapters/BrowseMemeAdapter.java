package com.example.memestorage.adapters;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
        import android.util.DisplayMetrics;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;

        import androidx.annotation.NonNull;
        import androidx.recyclerview.widget.RecyclerView;

        import com.bumptech.glide.Glide;

        import java.util.ArrayList;
        import java.util.List;

        import android.view.LayoutInflater;
        import android.view.ViewGroup;
        import androidx.annotation.NonNull;
        import androidx.recyclerview.widget.RecyclerView;
        import com.bumptech.glide.Glide;
import com.example.memestorage.databinding.ItemMemeBrowsedBinding;
import com.example.memestorage.models.MemeBrowsedModel;

import java.util.ArrayList;
        import java.util.List;

public class BrowseMemeAdapter extends RecyclerView.Adapter<BrowseMemeAdapter.MemeViewHolder> {

    private final List<MemeBrowsedModel> memeBrowsedModels = new ArrayList<>();
    private Context context;

    public BrowseMemeAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public MemeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemMemeBrowsedBinding binding = ItemMemeBrowsedBinding.inflate(inflater, parent, false);
        return new MemeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MemeViewHolder holder, int position) {
        MemeBrowsedModel memeBrowsedModel = memeBrowsedModels.get(position);

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int screenHeight = displayMetrics.heightPixels;
        int imageHeight = screenHeight / 6; // 1/6 of the screen height

        ViewGroup.LayoutParams layoutParams = holder.binding.ivMemeBrowsed.getLayoutParams();
        layoutParams.height = imageHeight;
        holder.binding.ivMemeBrowsed.setLayoutParams(layoutParams);

        holder.bind(memeBrowsedModel);
    }

    @Override
    public int getItemCount() {
        return memeBrowsedModels.size();
    }

    public void addMemeBrowsed(MemeBrowsedModel memeBrowsedModel) {
        memeBrowsedModels.add(memeBrowsedModel);
        notifyItemInserted(memeBrowsedModels.size() - 1);
    }
    public class MemeViewHolder extends RecyclerView.ViewHolder {

        private final ItemMemeBrowsedBinding binding;

        public MemeViewHolder(ItemMemeBrowsedBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(MemeBrowsedModel memeBrowsedModel) {
            Glide.with(binding.ivMemeBrowsed.getContext())
                    .load(memeBrowsedModel.getUrl())
                    .into(binding.ivMemeBrowsed);
        }
    }
}