package com.example.memestorage.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memestorage.R;
import com.example.memestorage.adapters.ImageAdapter;
import com.example.memestorage.fragments.EditImageFragment;
import com.example.memestorage.fragments.ImageFragment;
import com.example.memestorage.viewmodels.ImageCategoryViewModel;
import com.example.memestorage.viewmodels.ImageViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


public class ImageItemTouchHelper extends ItemTouchHelper.SimpleCallback {

    private ImageAdapter adapter;
    private ImageViewModel imageViewModel;
    private ImageCategoryViewModel imageCategoryViewModel;
    private FragmentManager fragmentManager;

    public ImageItemTouchHelper(ImageAdapter adapter, ImageViewModel imageViewModel, ImageCategoryViewModel imageCategoryViewModel, FragmentManager fragmentManager) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.adapter = adapter;
        this.imageViewModel = imageViewModel;
        this.imageCategoryViewModel = imageCategoryViewModel;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
        final int position = viewHolder.getBindingAdapterPosition();
        if (direction == ItemTouchHelper.LEFT) {
            showDeleteDialog(position);
        } else {
            showEditFragment(position, viewHolder);
        }


        adapter.notifyItemChanged(viewHolder.getBindingAdapterPosition());
    }

    private void showDeleteDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(adapter.getContext());
        builder.setTitle("Delete Image");
        builder.setMessage("Are you sure you want to delete " +
                " ?");
        builder.setPositiveButton("Confirm",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String imageId = imageViewModel.getImages().get(position).iId;
                        imageCategoryViewModel.deleteImageCategoryByImageIdFirebase(imageId);
                        imageViewModel.deleteImageFirebase(imageId, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    imageViewModel.deleteImageCloudinary(imageViewModel.getImages().get(position));
//                                    imageViewModel.deleteImageFirebaseStorage(imageViewModel.getImages().get(position).imageURL);
                                    Log.d("Delete Image Firestore", "Image " + imageViewModel.getImages().get(position).imageName + " in Firestore deleted successfully");
                                    Toast.makeText(adapter.getContext(),
                                            "Deleted image "
                                            , Toast.LENGTH_SHORT).show();
                                    imageViewModel.getImages().remove(position);
                                    adapter.deleteImage(position);
                                } else {
                                    Log.d("Delete Image Firestore", "Image " + imageViewModel.getImages().get(position).imageName + " failed");
                                }
                            }
                        });
                    }
                });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }
    public interface ImageEditListener {
        public void onImageEdited();
    }
    private void showEditFragment(int position, RecyclerView.ViewHolder viewHolder) {
        EditImageFragment fragment = EditImageFragment.newInstance(imageViewModel.getImages().get(position));
        fragment.setImageEditListener(new ImageEditListener() {
            @Override
            public void onImageEdited() {
                adapter.notifyItemChanged(position);
//                adapter.notifyDataSetChanged();
            }
        });
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_image, fragment)
                .addToBackStack(null)
                .commit();

    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        Drawable icon;
        //ColorDrawable background;
        GradientDrawable background;

        View itemView = viewHolder.itemView;
        int backgroundCornerOffset = 20;

//        float density = itemView.getContext().getResources().getDisplayMetrics().density;
//        float cornerRadius = 20 * density;

        if (dX > 0) {
            icon = ContextCompat.getDrawable(adapter.getContext(), R.drawable.ic_baseline_delete);
//            background = new ColorDrawable(ContextCompat.getColor(adapter.getContext(), R.color.green));
        } else {
            icon = ContextCompat.getDrawable(adapter.getContext(), R.drawable.ic_baseline_edit);
//            background = new ColorDrawable(Color.RED);
        }

        assert icon != null;
        int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconBottom = iconTop + icon.getIntrinsicHeight();

        float maxSwipeDistance = itemView.getWidth();
        if (dX > maxSwipeDistance) dX = maxSwipeDistance;
        if (dX < -maxSwipeDistance) dX = -maxSwipeDistance;

        if (dX > 0) { // Swiping to the right
            icon = ContextCompat.getDrawable(adapter.getContext(), R.drawable.ic_baseline_edit);
            background = new GradientDrawable();
            background.setCornerRadius(40); // Set corner radius in dp
            background.setColor(Color.GREEN);
        } else if (dX < 0) { // Swiping to the left
            icon = ContextCompat.getDrawable(adapter.getContext(), R.drawable.ic_baseline_delete);
            background = new GradientDrawable();
            background.setCornerRadius(40); // Set corner radius in dp
            background.setColor(Color.RED);
        } else { // view is unSwiped
            background = new GradientDrawable();
            background.setColor(Color.TRANSPARENT);
        }

        if (dX > 0) { // Swiping to the right
            int iconLeft = itemView.getLeft() ;
            int iconRight = itemView.getLeft()  + icon.getIntrinsicWidth();
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            background.setBounds(itemView.getLeft(), itemView.getTop(),
                    itemView.getLeft() + ((int) dX) + backgroundCornerOffset, itemView.getBottom());
        } else if (dX < 0) { // Swiping to the left
            int iconLeft = itemView.getRight() - icon.getIntrinsicWidth();
            int iconRight = itemView.getRight() ;
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                    itemView.getTop(), itemView.getRight(), itemView.getBottom());
        } else { // view is unSwiped
            background.setBounds(0, 0, 0, 0);
        }

        background.draw(c);
        icon.draw(c);
    }


}

