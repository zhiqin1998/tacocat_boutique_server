package com.aichathon.aickathon2018.com.aickathon.aickathon2018.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BoundingBox implements Parcelable {

    @SerializedName("h")
    @Expose
    private Integer h;
    @SerializedName("w")
    @Expose
    private Integer w;
    @SerializedName("x")
    @Expose
    private Integer x;
    @SerializedName("y")
    @Expose
    private Integer y;

    public Integer getH() {
        return h;
    }

    public void setH(Integer h) {
        this.h = h;
    }

    public Integer getW() {
        return w;
    }

    public void setW(Integer w) {
        this.w = w;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(h);
        dest.writeInt(w);
        dest.writeInt(x);
        dest.writeInt(y);
    }
    public BoundingBox(Parcel in){
        h = in.readInt();
        w = in.readInt();
        x = in.readInt();
        y = in.readInt();

    }
    public static final Parcelable.Creator<BoundingBox> CREATOR = new Parcelable.Creator<BoundingBox>(){

        @Override
        public BoundingBox createFromParcel(Parcel parcel) {
            return new BoundingBox(parcel);
        }

        @Override
        public BoundingBox[] newArray(int size) {
            return new BoundingBox[0];
        }
    };
}