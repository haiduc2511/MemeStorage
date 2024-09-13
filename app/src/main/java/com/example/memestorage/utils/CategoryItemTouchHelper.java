package com.example.memestorage.utils;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memestorage.R;
import com.example.memestorage.adapters.AddCategoryCategoryAdapter;
import com.example.memestorage.adapters.CategoryAdapter;
import com.example.memestorage.adapters.ImageAdapter;
import com.example.memestorage.models.CategoryModel;
import com.example.memestorage.viewmodels.CategoryViewModel;
import com.example.memestorage.viewmodels.ImageCategoryViewModel;
import com.example.memestorage.viewmodels.ImageViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class CategoryItemTouchHelper extends ItemTouchHelper.SimpleCallback {

    private AddCategoryCategoryAdapter adapter;
    private CategoryViewModel categoryViewModel;
    private ImageCategoryViewModel imageCategoryViewModel;
    public CategoryItemTouchHelper(AddCategoryCategoryAdapter adapter, CategoryViewModel categoryViewModel, ImageCategoryViewModel imageCategoryViewModel) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.adapter = adapter;
        this.categoryViewModel = categoryViewModel;
        this.imageCategoryViewModel = imageCategoryViewModel;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
        final int position = viewHolder.getAdapterPosition();
        if (direction == ItemTouchHelper.LEFT) {
            showDeleteDialog(viewHolder, position);
        } else {
            showEditDialog(viewHolder, position);
        }
        adapter.notifyItemChanged(viewHolder.getAdapterPosition());
    }


    private void showEditDialog(@NonNull final RecyclerView.ViewHolder viewHolder, int position) {
        CategoryModel categoryModel = adapter.getCategoryModels().get(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(adapter.getContext());
        builder.setTitle("Edit Item Name");

        final EditText input = new EditText(adapter.getContext());
        input.setText(categoryModel.categoryName);
        input.setSelection(input.getText().length());
        input.requestFocus();
        builder.setView(input);

        input.postDelayed(() -> {
            InputMethodManager imm = (InputMethodManager) adapter.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
            }
        }, 200);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newCategoryName = input.getText().toString().trim();
                if (CategoryUtil.catetogyIsAcceptable(newCategoryName, adapter.getContext())) {
                    Toast.makeText(adapter.getContext(),
                            "Edited category " +
                                    adapter.getCategoryModels().get(position).categoryName +
                                    " to " +
                                    newCategoryName
                            , Toast.LENGTH_SHORT).show();
                    categoryModel.categoryName = newCategoryName;
                    categoryViewModel.updateCategoryFirebase(categoryModel.cId, categoryModel);
//                    adapter.notifyItemChanged(position);

                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void showDeleteDialog(@NonNull final RecyclerView.ViewHolder viewHolder, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(adapter.getContext());
        builder.setTitle("Delete Category");
        builder.setMessage("Are you sure you want to delete " +
                adapter.getCategoryModels().get(position).categoryName +
                " ?");
        builder.setPositiveButton("Confirm",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(adapter.getContext(),
                                "deleted category " +
                                        adapter.getCategoryModels().get(position).categoryName
                                , Toast.LENGTH_SHORT).show();
                        String categoryId = categoryViewModel.getCategories().get(position).cId;
                        imageCategoryViewModel.deleteImageCategoryByCategoryIdFirebase(categoryId);
                        categoryViewModel.deleteCategoryFirebase(categoryId);
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
