package com.aichathon.aickathon2018.com.aickathon.aickathon2018.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ClothList implements Parcelable {

    @SerializedName("colors")
    @Expose
    private List<Color> colors = null;
    @SerializedName("garments")
    @Expose
    private List<Garment> garments = null;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("photo_data")
    @Expose
    private String photoData;
    @SerializedName("photo_id")
    @Expose
    private Integer photoId;
    @SerializedName("price")
    @Expose
    private Double price;
    @SerializedName("store_id")
    @Expose
    private Integer storeId;
    @SerializedName("store_name")
    @Expose
    private String storeName;
    @SerializedName("styles")
    @Expose
    private List<Style> styles = null;

    public List<Color> getColors() {
        return colors;
    }

    public void setColors(List<Color> colors) {
        this.colors = colors;
    }

    public List<Garment> getGarments() {
        return garments;
    }

    public void setGarments(List<Garment> garments) {
        this.garments = garments;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoData() {
        return photoData;
    }

    public void setPhotoData(String photoData) {
        this.photoData = photoData;
    }

    public Integer getPhotoId() {
        return photoId;
    }

    public void setPhotoId(Integer photoId) {
        this.photoId = photoId;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public List<Style> getStyles() {
        return styles;
    }

    public void setStyles(List<Style> styles) {
        this.styles = styles;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(colors);
        dest.writeList(garments);
        dest.writeList(styles);
        dest.writeString(location);
        dest.writeString(name);
        dest.writeString(photoData);
        dest.writeInt(photoId);
        dest.writeDouble(price);
        dest.writeInt(storeId);
        dest.writeString(storeName);
    }
    public ClothList(Parcel in){
        in.readList(colors,Color.class.getClassLoader());
        in.readList(garments,Garment.class.getClassLoader());
        in.readList(styles,Style.class.getClassLoader());
        location = in.readString();
        name = in.readString();
        photoData = in.readString();
        photoId = in.readInt();
        price = in.readDouble();
        storeId = in.readInt();
        storeName = in.readString();
    }
    public static final Parcelable.Creator<ClothList> CREATOR = new Parcelable.Creator<ClothList>(){

        @Override
        public ClothList createFromParcel(Parcel parcel) {
            return new ClothList(parcel);
        }

        @Override
        public ClothList[] newArray(int size) {
            return new ClothList[0];
        }
    };

}