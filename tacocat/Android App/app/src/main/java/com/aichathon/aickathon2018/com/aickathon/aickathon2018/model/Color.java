package com.aichathon.aickathon2018.com.aickathon.aickathon2018.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Color implements Parcelable {

    @SerializedName("category")
    @Expose
    private String category;
    @SerializedName("color_name")
    @Expose
    private String colorName;
    @SerializedName("hex")
    @Expose
    private String hex;
    @SerializedName("ratio")
    @Expose
    private Double ratio;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getColorName() {
        return colorName;
    }

    public void setColorName(String colorName) {
        this.colorName = colorName;
    }

    public String getHex() {
        return hex;
    }

    public void setHex(String hex) {
        this.hex = hex;
    }

    public Double getRatio() {
        return ratio;
    }

    public void setRatio(Double ratio) {
        this.ratio = ratio;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(category);
        dest.writeString(colorName);
        dest.writeString(hex);
        dest.writeDouble(ratio);
    }
    public Color(Parcel in){
        category = in.readString();
        colorName = in.readString();
        hex = in.readString();
        ratio = in.readDouble();
    }
    public static final Parcelable.Creator<Color> CREATOR = new Parcelable.Creator<Color>(){

        @Override
        public Color createFromParcel(Parcel parcel) {
            return new Color(parcel);
        }

        @Override
        public Color[] newArray(int size) {
            return new Color[0];
        }
    };
}