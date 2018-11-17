package com.aichathon.aickathon2018.com.aickathon.aickathon2018.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Person {

    @SerializedName("photos")
    @Expose
    private List<Photo> photos = null;
    @SerializedName("name")
    @Expose
    private String name = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }
    @SerializedName("top_colors")
    @Expose
    private List<String> topColors = null;
    @SerializedName("top_colors_hex")
    @Expose
    private List<String> topColorsHex = null;
    @SerializedName("top_garments")
    @Expose
    private List<String> topGarments = null;
    @SerializedName("top_styles")
    @Expose
    private List<String> topStyles = null;
    @SerializedName("user_id")
    @Expose
    private Integer userId;

    public List<String> getTopColors() {
        return topColors;
    }

    public void setTopColors(List<String> topColors) {
        this.topColors = topColors;
    }

    public List<String> getTopColorsHex() {
        return topColorsHex;
    }

    public void setTopColorsHex(List<String> topColorsHex) {
        this.topColorsHex = topColorsHex;
    }

    public List<String> getTopGarments() {
        return topGarments;
    }

    public void setTopGarments(List<String> topGarments) {
        this.topGarments = topGarments;
    }

    public List<String> getTopStyles() {
        return topStyles;
    }

    public void setTopStyles(List<String> topStyles) {
        this.topStyles = topStyles;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

}