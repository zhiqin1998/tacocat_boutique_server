package com.aichathon.aickathon2018;

import java.util.List;

public class User {
    public String name;
    public List<Photo> photo;
    public String[] top_colors;
    public String[] top_colors_hex;
    public String[] top_garment;
    public String[] top_styles;
}

class Photo {
    public List<Colors> colors;
    public List<Garments> garments;
    public String photo_data;
    public String photo_id;
    public List<Style> style;
}

class Colors {
    public String category;
    public String color_name;
    public String hex;
    public String ratio;
}

class Garments {
    public List<Bounding_box> bounding_box;
    public String confidence;
    public String name;
}

class Bounding_box {
    public String h;
    public String w;
    public String x;
    public String y;
}

class Style {
    public String confidence;
    public String name;
}

