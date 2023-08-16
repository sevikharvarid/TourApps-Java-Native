package com.example.exploreasy;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class PlaceModel implements Parcelable {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getImage1() {
        return image1;
    }

    public void setImage1(String image1) {
        this.image1 = image1;
    }

    public String getImage2() {
        return image2;
    }

    public void setImage2(String image2) {
        this.image2 = image2;
    }

    public String getImage3() {
        return image3;
    }

    public void setImage3(String image3) {
        this.image3 = image3;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    private String name;
    private double rating;
    private String address;
    private String review;
    private String image1;
    private String image2;
    private String image3;
    private double latitude;
    private double longitude;

    public PlaceModel(String name, double rating, String address, String review, String image1, String image2, String image3,double latitude,double longitude) {
        this.name = name;
        this.rating = rating;
        this.address = address;
        this.review = review;
        this.image1 = image1;
        this.image2 = image2;
        this.image3 = image3;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeDouble(rating);
        dest.writeString(address);
        dest.writeString(review);
        dest.writeString(image1);
        dest.writeString(image2);
        dest.writeString(image3);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }

    public static final Parcelable.Creator<PlaceModel> CREATOR = new Parcelable.Creator<PlaceModel>() {
        public PlaceModel createFromParcel(Parcel in) {
            return new PlaceModel(in);
        }

        public PlaceModel[] newArray(int size) {
            return new PlaceModel[size];
        }
    };

    private PlaceModel(Parcel in) {
        name = in.readString();
        rating = in.readDouble();
        address = in.readString();
        review = in.readString();
        image1 = in.readString();
        image2 = in.readString();
        image3 = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
    }


}
