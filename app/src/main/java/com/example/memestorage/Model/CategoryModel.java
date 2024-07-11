package com.example.memestorage.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class CategoryModel implements Parcelable {

    @NonNull
    public String cId;
    public String categoryName;
    public CategoryModel() {
    }

    protected CategoryModel(Parcel in) {
        cId = in.readString();
        categoryName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(cId);
        dest.writeString(categoryName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CategoryModel> CREATOR = new Creator<CategoryModel>() {
        @Override
        public CategoryModel createFromParcel(Parcel in) {
            return new CategoryModel(in);
        }

        @Override
        public CategoryModel[] newArray(int size) {
            return new CategoryModel[size];
        }
    };

    @Override
    public String toString() {
        return "CategoryModel{" +
                "cId='" + cId + '\'' +
                ", categoryName='" + categoryName + '\'' +
                '}';
    }
}