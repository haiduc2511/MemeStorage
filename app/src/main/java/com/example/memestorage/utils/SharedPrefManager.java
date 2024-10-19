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
        this.saveData("Number of columns", numberOfColumn);
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
        if (!contains("Number of columns")) {
            return "3";
        }
        return this.getData("Number of columns");
    }

    public String getPowerMode() {
        if (!contains("Power mode")) {
            return "high";
        }
        return this.getData("Power mode");
    }
    public void savePowerMode(String powerMode) {
        this.saveData("Power mode", powerMode);
        if (powerMode.equals("low")) {
            saveNumberOfImages("10");
            saveFetchQuality("200");
        }
        if (powerMode.equals("medium")) {
            saveNumberOfImages("20");
            saveFetchQuality("250");
        }
        if (powerMode.equals("high")) {
            saveNumberOfImages("30");
            saveFetchQuality("300");
        }
    }
    public String getFetchQuality() {
        if (!contains("Fetch Quality")) {
            return "300";
        }
        return this.getData("Fetch Quality");
    }
    public void saveFetchQuality(String fetchQuality) {
        this.saveData("Fetch Quality", fetchQuality);
    }

    public String getIfDeleteImageGalleryAfterUpload() {
        if (!contains("If Delete Image Gallery After Upload")) {
            return "false";
        }
        return this.getData("If Delete Image Gallery After Upload");
    }
    public void saveIfDeleteImageGalleryAfterUpload(String ifDeleteImageGalleryAfterUpload) {
        this.saveData("If Delete Image Gallery After Upload", ifDeleteImageGalleryAfterUpload);
    }

    public String getIfDoubleCheckAISuggestions() {
        if (!contains("If Double Check AI's Suggestions")) {
            return "false";
        }
        return this.getData("If Double Check AI's Suggestions");
    }
    public void saveIfDoubleCheckAISuggestions(String ifDoubleCheckAISuggestions) {
        this.saveData("If Double Check AI's Suggestions", ifDoubleCheckAISuggestions);
    }

    public String getIfDeleteImageEasyModeOn() {
        if (!contains("IfDeleteImageEasyModeOn")) {
            return "false";
        }
        return this.getData("IfDeleteImageEasyModeOn");
    }
    public void saveIfDeleteImageEasyModeOn(String ifDeleteImageEasyModeOn) {
        this.saveData("IfDeleteImageEasyModeOn", ifDeleteImageEasyModeOn);
    }

    public String getIfDownloadImageEasyModeOn() {
        if (!contains("IfDownloadImageEasyModeOn")) {
            return "false";
        }
        return this.getData("IfDownloadImageEasyModeOn");
    }
    public void saveIfDownloadImageEasyModeOn(String ifDownloadImageEasyModeOn) {
        this.saveData("IfDownloadImageEasyModeOn", ifDownloadImageEasyModeOn);
    }

    public String getIfShareImageEasyModeOn() {
        if (!contains("ifShareImageEasyModeOn")) {
            return "false";
        }
        return this.getData("ifShareImageEasyModeOn");
    }
    public void saveIfShareImageEasyModeOn (String ifShareImageEasyModeOn) {
        this.saveData("ifShareImageEasyModeOn", ifShareImageEasyModeOn);
    }
}
