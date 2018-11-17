package com.aichathon.aickathon2018.com.aickathon.aickathon2018.model;

import java.util.List;

public class DataPasser {
    static List<ClothList> cl = null;

    public static List<ClothList> getCl() {
        return cl;
    }

    public static void setCl(List<ClothList> cl) {
        DataPasser.cl = cl;
    }
}
