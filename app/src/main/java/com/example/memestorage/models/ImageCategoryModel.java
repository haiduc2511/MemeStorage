package com.example.memestorage.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class ImageCategoryModel implements Parcelable {

    @NonNull
    public String icId;
    public String imageId;
    public String categoryId;
    public ImageCategoryModel() {
    }

    protected ImageCategoryModel(Parcel in) {
        icId = in.readString();
        imageId = in.readString();
        categoryId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(icId);
        dest.writeString(imageId);
        dest.writeString(categoryId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ImageCategoryModel> CREATOR = new Creator<ImageCategoryModel>() {
        @Override
        public ImageCategoryModel createFromParcel(Parcel in) {
            return new ImageCategoryModel(in);
        }

        @Override
        public ImageCategoryModel[] newArray(int size) {
            return new ImageCategoryModel[size];
        }
    };

    @Override
    public String toString() {
        return "ImageCategoryModel{" +
                "icId='" + icId + '\'' +
                ", imageId='" + imageId + '\'' +
                ", categoryId='" + categoryId + '\'' +
                '}';
    }
}