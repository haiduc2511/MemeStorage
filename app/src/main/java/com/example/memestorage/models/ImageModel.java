package com.example.memestorage.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class ImageModel implements Parcelable {

    @NonNull
    public String iId;
    public String userId;
    public String imageName;
    public String imageURL;

    public ImageModel() {
    }

    protected ImageModel(Parcel in) {
        iId = in.readString();
        userId = in.readString();
        imageName = in.readString();
        imageURL = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(iId);
        dest.writeString(userId);
        dest.writeString(imageName);
        dest.writeString(imageURL);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ImageModel> CREATOR = new Creator<ImageModel>() {
        @Override
        public ImageModel createFromParcel(Parcel in) {
            return new ImageModel(in);
        }

        @Override
        public ImageModel[] newArray(int size) {
            return new ImageModel[size];
        }
    };

    @Override
    public String toString() {
        return "ImageModel{" +
                "iId='" + iId + '\'' +
                ", userId='" + userId + '\'' +
                ", imageName='" + imageName + '\'' +
                ", imageURL='" + imageURL + '\'' +
                '}';
    }
}