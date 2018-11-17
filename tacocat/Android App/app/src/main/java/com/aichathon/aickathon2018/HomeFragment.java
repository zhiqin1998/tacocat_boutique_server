package com.aichathon.aickathon2018;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aichathon.aickathon2018.com.aickathon.aickathon2018.model.ClothList;
import com.aichathon.aickathon2018.com.aickathon.aickathon2018.model.ClothList_;
import com.aichathon.aickathon2018.com.aickathon.aickathon2018.model.Color;
import com.aichathon.aickathon2018.com.aickathon.aickathon2018.model.DataPasser;
import com.aichathon.aickathon2018.com.aickathon.aickathon2018.model.Person;
import com.aichathon.aickathon2018.com.aickathon.aickathon2018.model.Photo;
import com.aichathon.aickathon2018.com.aickathon.aickathon2018.remote.APIUtils;
import com.aichathon.aickathon2018.com.aickathon.aickathon2018.remote.FileService;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends android.app.Fragment {
    FileService fileService;
    private ProgressBar progressBar;

    private TextView profile_name;
    private TextView your_colour;
    private TextView your_style;
    private User user;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        fileService = APIUtils.getFileService();
        progressBar = view.findViewById(R.id.progressBar3);
        Button btn = view.findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<ClothList_> call = fileService.suggestwithoutphoto(0);
                progressBar.setVisibility(View.VISIBLE);
                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                call.enqueue(new Callback<ClothList_>() {
                    @Override
                    public void onResponse(Call<ClothList_> call, Response<ClothList_> response) {
                        Log.i("success",response.code()+" "+response.message());
                        progressBar.setVisibility(View.GONE);
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        ClothList_ cl = response.body();
                        if (cl==null){
                            Toast.makeText(getActivity(), "No Garment Detected", Toast.LENGTH_LONG).show();
                        }else {
                            List<ClothList> clothList = cl.getClothList();
                            if (clothList != null) {
                                if (!clothList.isEmpty()) {
                                    Toast.makeText(getActivity(), "Load Successful", Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(getActivity(), ResultPage.class);
                                    DataPasser.setCl(clothList);
                                    startActivity(i);
                                } else {
                                    Toast.makeText(getActivity(), "No Suggested Clothing", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(getActivity(), "Unknown Error", Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ClothList_> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        Log.i("failed",t.getMessage()+" "+t.getCause());
                        Toast.makeText(getActivity(), "Failed!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        refresh();
//        try {
//            InputStream inputStream = getActivity().getAssets().open("user1.json");
//            Reader reader = new InputStreamReader(inputStream, "UTF-8");
//            user = new Gson().fromJson(reader, User.class);
//            Log.d("JSON", user.top_colors[0]);
//        } catch (IOException e){
//            e.printStackTrace();
//        }

//        //Name
//        profile_name = (TextView) view.findViewById(R.id.profile_name);
//        profile_name.setText(user.name);
//
//        //Top 3 colors
//        String[] colours = user.top_colors;
//        String your_colours = "Your colours:\n";
//        for(String s: colours){
//            your_colours += (s + "\n");
//        }
//        your_colour = (TextView) view.findViewById(R.id.your_colour);
//        your_colour.setText(your_colours);
//
//        //Top 3 style
//        String[] styles = user.top_styles;
//        String your_styles = "Your styles:\n";
//        for(String s: styles){
//            your_styles += (s + "\n");
//        }
//        your_style = (TextView) view.findViewById(R.id.your_style);
//        your_style.setText(your_styles);

        return view;
    }
    public void refresh(){
        Call<Person> call = fileService.getuser(0);
        progressBar.setVisibility(View.VISIBLE);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        call.enqueue(new Callback<Person>() {
            @Override
            public void onResponse(Call<Person> call, Response<Person> response) {

                Log.i("success",response.code()+" "+response.message());
                progressBar.setVisibility(View.GONE);
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Person person = response.body();
                //returned param
                TextView name = (TextView) getActivity().findViewById(R.id.profile_name);
                name.setText(person.getName());
                TextView[] color = {getActivity().findViewById(R.id.textView),getActivity().findViewById(R.id.textView8),getActivity().findViewById(R.id.textView9),getActivity().findViewById(R.id.textView10),getActivity().findViewById(R.id.textView11)};
                List<String> topColors = person.getTopColors();
                for (int i = 0; i < topColors.size(); i++) {
                    String c = topColors.get(i);
                    color[i].setText(c);
                }
                TextView[] style = {getActivity().findViewById(R.id.textView12),getActivity().findViewById(R.id.textView13),getActivity().findViewById(R.id.textView14),getActivity().findViewById(R.id.textView15),getActivity().findViewById(R.id.textView16)};
                List<String> topStyles = person.getTopStyles();
                for (int i = 0; i < topStyles.size(); i++) {
                    String s = topStyles.get(i);
                    style[i].setText(s);
                }
//                Intent i = new Intent();
//                i.putStringArrayListExtra("colorList",(ArrayList<String>) colorList);
//                i.putStringArrayListExtra("styleList",(ArrayList<String>) styleList);
//                i.putParcelableArrayListExtra("clothList", (ArrayList<ClothList>)cloth_list);

            }

            @Override
            public void onFailure(Call<Person> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Log.i("failed",t.getMessage()+" "+t.getCause());
                Toast.makeText(getActivity(), "Error Occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
