package com.aichathon.aickathon2018;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.aichathon.aickathon2018.com.aickathon.aickathon2018.model.ClothList;
import com.aichathon.aickathon2018.com.aickathon.aickathon2018.model.DataPasser;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

public class ResultPage extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result);
        ArrayList<ClothList> clothList = new ArrayList<>(DataPasser.getCl());
        DataPasser.setCl(null);
        ClothAdapter adapter = new ClothAdapter(getApplicationContext(), clothList);
        ListView listView = findViewById(R.id.result_list);
        listView.setAdapter(adapter);
    }
}
