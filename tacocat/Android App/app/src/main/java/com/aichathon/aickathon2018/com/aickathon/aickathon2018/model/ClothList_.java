package com.aichathon.aickathon2018.com.aickathon.aickathon2018.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ClothList_ {

    @SerializedName("cloth_list")
    @Expose
    private List<ClothList> clothList = null;

    public List<ClothList> getClothList() {
        return clothList;
    }

    public void setClothList(List<ClothList> clothList) {
        this.clothList = clothList;
    }

}
