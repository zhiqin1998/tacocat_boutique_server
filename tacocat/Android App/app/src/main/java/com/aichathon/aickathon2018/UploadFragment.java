package com.aichathon.aickathon2018;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.aichathon.aickathon2018.com.aickathon.aickathon2018.model.ClothList;
import com.aichathon.aickathon2018.com.aickathon.aickathon2018.model.Person;
import com.aichathon.aickathon2018.com.aickathon.aickathon2018.model.Photo;
import com.aichathon.aickathon2018.com.aickathon.aickathon2018.remote.APIUtils;
import com.aichathon.aickathon2018.com.aickathon.aickathon2018.remote.FileService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class UploadFragment extends android.app.Fragment {
    FileService fileService;
    Button btnChooseFile;
    Button btnUpload;
    Button btnShowResult;
    String imagePath;
    Uri imageUri;

    private static final int IMAGE_REQUEST = 1;
    private String currentImagePath = null;
    private Button upload_button;
    private Button capture_button;
    private ProgressBar progressBar;

    public UploadFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_album, container, false);
        fileService = APIUtils.getFileService();
        progressBar = view.findViewById(R.id.progressBar2);
        upload_button = (Button) view.findViewById(R.id.upload_button);
        upload_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,0);

            }
        });

        //Camera
        capture_button = (Button) view.findViewById(R.id.capture_button);
        capture_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage(v);
            }
        });
        refresh();
        return view;
    }

    public void post(){
        Bitmap bm = null;
        try {
            bm = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,80, stream);
        String data = android.util.Base64.encodeToString(stream.toByteArray(),android.util.Base64.DEFAULT);
        Call<Person> call = fileService.newphoto(0,data);
        progressBar.setVisibility(View.VISIBLE);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        call.enqueue(new Callback<Person>() {
            @Override
            public void onResponse(Call<Person> call, Response<Person> response) {
                progressBar.setVisibility(View.GONE);
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Log.i("success",response.code()+" "+response.message());
                Toast.makeText(getActivity(), "Successfully Uploaded", Toast.LENGTH_SHORT).show();
                refresh();
            }

            @Override
            public void onFailure(Call<Person> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Log.i("failed",t.getMessage()+" "+t.getCause());
                Toast.makeText(getActivity(), "Upload Failed", Toast.LENGTH_SHORT).show();
            }
        });

    }


    public void refresh(){
        Call<Person> call = fileService.getuser(0);
        progressBar.setVisibility(View.VISIBLE);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        call.enqueue(new Callback<Person>() {
            @Override
            public void onResponse(Call<Person> call, Response<Person> response) {
                progressBar.setVisibility(View.GONE);
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Log.i("success",response.code()+" "+response.message());
                Person person = response.body();
                //returned param
                List<Photo> photos = person.getPhotos();
                if (photos!=null){
                    ArrayList<Photo> photoArrayList = new ArrayList<>(photos);
                    PhotoAdapter adapter = new PhotoAdapter(getActivity().getApplicationContext() , photoArrayList);
                    ListView listView = (ListView) getActivity().findViewById(R.id.photo_list);
                    listView.setAdapter(adapter);
                }
//
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
    public void captureImage(View view){

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(cameraIntent.resolveActivity(getActivity().getPackageManager())!=null){
            File imageFile = null;
            try{
                imageFile = getImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(imageFile!=null){
                imageUri = FileProvider.getUriForFile(getActivity(),"com.example.android.fileprovider", imageFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                startActivityForResult(cameraIntent, IMAGE_REQUEST);
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==IMAGE_REQUEST){
            if (resultCode==RESULT_OK){
                post();
            }
        }
        else if (requestCode == 0){
            if(resultCode==RESULT_OK){
                imageUri=data.getData();
                post();
            }
        }
        else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    private File getImageFile() throws IOException{
        String timestamp = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z").format(new Date());
        String imageName = "jpg" + timestamp +"_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(imageName, ".jpg",storageDir);
        currentImagePath = imageFile.getAbsolutePath();
        return imageFile;
    }


}
