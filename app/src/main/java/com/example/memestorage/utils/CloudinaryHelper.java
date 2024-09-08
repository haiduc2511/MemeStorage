package com.example.memestorage.utils;

import android.content.Context;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.memestorage.BuildConfig;
import com.example.memestorage.R;

public class CloudinaryHelper {
    private static Cloudinary cloudinaryInstance = new Cloudinary(ObjectUtils.asMap(
            "cloud_name", BuildConfig.CLOUD_NAME,
            "api_key", BuildConfig.API_KEY,
            "api_secret", BuildConfig.API_SECRET));

    // Hàm khởi tạo private để ngăn chặn việc tạo đối tượng mới từ bên ngoài
    private CloudinaryHelper() {
    }

    // Phương thức để lấy đối tượng Cloudinary duy nhất
    public static Cloudinary getInstance() {
        return cloudinaryInstance;
    }
}
