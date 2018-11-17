package com.aichathon.aickathon2018.com.aickathon.aickathon2018.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Style implements Parcelable {

    @SerializedName("confidence")
    @Expose
    private Double confidence;
    @SerializedName("name")
    @Expose
    private String name;

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeDouble(confidence);
    }
    public Style(Parcel in){
        name = in.readString();
        confidence = in.readDouble();
    }
    public static final Parcelable.Creator<Style> CREATOR = new Parcelable.Creator<Style>(){

        @Override
        public Style createFromParcel(Parcel parcel) {
            return new Style(parcel);
        }

        @Override
        public Style[] newArray(int size) {
            return new Style[0];
        }
    };
}