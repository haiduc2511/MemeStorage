package com.example.memestorage.utils;

import android.content.Context;
import android.widget.Toast;

import com.example.memestorage.viewmodels.CategoryViewModel;

public class CategoryUtil {
    public static boolean catetogyIsAcceptable(String categoryName, Context context) {
        if (categoryName.isEmpty()) {
            Toast.makeText(context, "You must enter category's name first", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (categoryName.length() > 50) {
            Toast.makeText(context, "Category's name's too long", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (CategoryViewModel.newInstance().getCategories().size() > 30) {
            Toast.makeText(context, "Too many categories", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (CategoryViewModel.newInstance().getCategoryIdAndNameHashMap().containsKey(categoryName)) {
            Toast.makeText(context, "Category already existed", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
