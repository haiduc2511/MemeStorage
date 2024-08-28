package com.example.memestorage.utils;

public class MediaManagerState {

    private static boolean isMediaManagerInitialized = false;
    public static boolean isInitialized() {
        return isMediaManagerInitialized;
    }
    public static void initialize() {
        isMediaManagerInitialized = true;
    }
}
