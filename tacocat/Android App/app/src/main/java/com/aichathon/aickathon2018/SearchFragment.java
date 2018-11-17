package com.aichathon.aickathon2018;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.aichathon.aickathon2018.com.aickathon.aickathon2018.model.ClothList;
import com.aichathon.aickathon2018.com.aickathon.aickathon2018.model.ClothList_;
import com.aichathon.aickathon2018.com.aickathon.aickathon2018.model.DataPasser;
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
public class SearchFragment extends android.app.Fragment {

    private FileService fileService;
    private ImageView cameraVector;
    private ImageView uploadVector;
    Uri imageUri;
    private static final int IMAGE_REQUEST = 1;
    private String currentImagePath = null;
    private ProgressBar progressBar = null;


    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        // Inflate the layout for this fragment
        fileService = APIUtils.getFileService();
        progressBar = (ProgressBar)view.findViewById(R.id.progressBar1);
        uploadVector = (ImageView) view.findViewById(R.id.uploadVector);
        uploadVector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,0);

            }
        });

        //Camera
        cameraVector = (ImageView) view.findViewById(R.id.cameraVector);
        cameraVector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage(v);
            }
        });
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
        bm.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        String data = android.util.Base64.encodeToString(stream.toByteArray(),android.util.Base64.DEFAULT);
        Call<ClothList_> call = fileService.suggestwithphoto(0,data);
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
                            Toast.makeText(getActivity(), "Upload Successful", Toast.LENGTH_SHORT).show();
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
                Log.i("failed",t.getMessage()+" "+t.getCause());
                progressBar.setVisibility(View.GONE);
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Toast.makeText(getActivity(), "Upload Failed", Toast.LENGTH_SHORT).show();
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

    private File getImageFile() throws IOException{
        String timestamp = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z").format(new Date());
        String imageName = "jpg" + timestamp +"_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(imageName, ".jpg",storageDir);
        currentImagePath = imageFile.getAbsolutePath();
        return imageFile;
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

}
