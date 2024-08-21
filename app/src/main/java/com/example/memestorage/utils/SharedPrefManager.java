package com.example.memestorage.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {
    private static final String PREF_NAME = "Preferences";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SharedPrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveData(String key, String value) {
        editor.putString(key, value);
        editor.apply();
    }

    public String getData(String key) {
        return sharedPreferences.getString(key, "");
    }

    public boolean contains(String key) {
        return sharedPreferences.contains(key);
    }

    public void saveNumberOfImages(String numberOfImage) {
        this.saveData("Number of images", numberOfImage);
    }

    public void saveCompressPercentage(String compressPercentage) {
        this.saveData("Compress percentage", compressPercentage);
    }

    public void saveNumberOfColumn(String numberOfColumn) {
        this.saveData("Number of column", numberOfColumn);
    }
    public String getNumberOfImages() {
        if (!contains("Number of images")) {
            return "50";
        }
        return this.getData("Number of images");
    }

    public String getCompressPercentage() {
        if (!contains("Compress percentage")) {
            return "99";
        }
        return this.getData("Compress percentage");
    }

    public String getNumberOfColumn() {
        if (!contains("Number of column")) {
            return "3";
        }
        return this.getData("Number of column");
    }




}
