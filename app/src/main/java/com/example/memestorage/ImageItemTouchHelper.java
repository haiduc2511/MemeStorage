package com.example.memestorage;

import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.example.memestorage.viewmodels.ImageViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


public class ImageItemTouchHelper extends ItemTouchHelper.SimpleCallback {

    private ImageAdapter adapter;
    private ImageViewModel imageViewModel;

    public ImageItemTouchHelper(ImageAdapter adapter, ImageViewModel imageViewModel) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.adapter = adapter;
        this.imageViewModel = imageViewModel;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
        final int position = viewHolder.getAdapterPosition();
        if (direction == ItemTouchHelper.LEFT) {
            AlertDialog.Builder builder = new AlertDialog.Builder(adapter.getContext());
            builder.setTitle("Delete Player");
            builder.setMessage("Are you sure you want to delete " +
                     " ?");
            builder.setPositiveButton("Confirm",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            imageViewModel.deleteImageFirebase(imageViewModel.getImages().get(position).iId, new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.d("Delete Image Firestore", "Image " + imageViewModel.getImages().get(position).imageName + " in Firestore deleted successfully");
                                }
                            });
                            imageViewModel.deleteImageFirebaseStorage(imageViewModel.getImages().get(position).imageURL);
                            Toast.makeText(adapter.getContext(),
                                    "deleted player "
                                    , Toast.LENGTH_SHORT).show();
                        }
                    });
            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            Toast.makeText(adapter.getContext(), "Méo cho edit tên heh (chủ yếu do lười :v) " + position, Toast.LENGTH_SHORT).show();
        }
        adapter.notifyItemChanged(viewHolder.getAdapterPosition());
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
            icon = ContextCompat.getDrawable(adapter.getContext(), R.drawable.ic_baseline_edit);
//            background = new ColorDrawable(ContextCompat.getColor(adapter.getContext(), R.color.green));
        } else {
            icon = ContextCompat.getDrawable(adapter.getContext(), R.drawable.ic_baseline_delete);
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
            background.setColor(ContextCompat.getColor(adapter.getContext(), R.color.green));
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
            int iconLeft = itemView.getLeft() + iconMargin;
            int iconRight = itemView.getLeft() + iconMargin + icon.getIntrinsicWidth();
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            background.setBounds(itemView.getLeft(), itemView.getTop(),
                    itemView.getLeft() + ((int) dX) + backgroundCornerOffset, itemView.getBottom());
        } else if (dX < 0) { // Swiping to the left
            int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
            int iconRight = itemView.getRight() - iconMargin;
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

