package com.aichathon.aickathon2018.com.aickathon.aickathon2018.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Garment implements Parcelable {

    @SerializedName("bounding_box")
    @Expose
    private BoundingBox boundingBox;
    @SerializedName("confidence")
    @Expose
    private Double confidence;
    @SerializedName("name")
    @Expose
    private String name;

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(BoundingBox boundingBox) {
        this.boundingBox = boundingBox;
    }

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
        dest.writeParcelable(boundingBox,flags);
        dest.writeDouble(confidence);
        dest.writeString(name);
    }
    public Garment(Parcel in){
        boundingBox = in.readParcelable(BoundingBox.class.getClassLoader());
        confidence = in.readDouble();
        name = in.readString();
    }
    public static final Parcelable.Creator<Garment> CREATOR = new Parcelable.Creator<Garment>(){

        @Override
        public Garment createFromParcel(Parcel parcel) {
            return new Garment(parcel);
        }

        @Override
        public Garment[] newArray(int size) {
            return new Garment[0];
        }
    };
}