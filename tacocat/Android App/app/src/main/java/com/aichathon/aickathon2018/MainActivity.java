package com.aichathon.aickathon2018;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class MainActivity extends AppCompatActivity {

    private Button button2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button2 = findViewById(R.id.button2);

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Dashboard.class);
                startActivity(intent);
            }
        });
//        try {
//            InputStream inputStream = this.getAssets().open("user1.json");
//            Reader reader = new InputStreamReader(inputStream, "UTF-8");
//            User user1 = new Gson().fromJson(reader, User.class);
//            Log.d("JSON", user1.top_colors[0]);
//        } catch (IOException e){
//            e.printStackTrace();
//        }
    }
}
