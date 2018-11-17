package com.aichathon.aickathon2018.com.aickathon.aickathon2018.remote;

public class APIUtils {
    private APIUtils(){

    }

    public static final String API_URL = "http://192.168.43.61:5000/";

    public static FileService getFileService(){
        return RetrofitClient.getClient(API_URL).create(FileService.class);
    }
}