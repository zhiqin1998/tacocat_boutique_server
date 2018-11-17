package com.aichathon.aickathon2018.com.aickathon.aickathon2018.model;


import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Photo {

    @SerializedName("colors")
    @Expose
    private List<Color> colors = null;
    @SerializedName("garments")
    @Expose
    private List<Garment> garments = null;
    @SerializedName("photo_id")
    @Expose
    private Integer photoId;
    @SerializedName("styles")
    @Expose
    private List<Style> styles = null;
    @SerializedName("photo_data")
    @Expose
    private String photoData = null;

    public String getPhotoData() {
        return photoData;
    }

    public void setPhotoData(String photoData) {
        this.photoData = photoData;
    }

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

    public Integer getPhotoId() {
        return photoId;
    }

    public void setPhotoId(Integer photoId) {
        this.photoId = photoId;
    }

    public List<Style> getStyles() {
        return styles;
    }

    public void setStyles(List<Style> styles) {
        this.styles = styles;
    }

}
