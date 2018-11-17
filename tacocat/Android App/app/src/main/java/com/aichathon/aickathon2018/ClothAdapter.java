package com.aichathon.aickathon2018;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aichathon.aickathon2018.com.aickathon.aickathon2018.model.ClothList;
import com.aichathon.aickathon2018.com.aickathon.aickathon2018.model.Color;
import com.aichathon.aickathon2018.com.aickathon.aickathon2018.model.Garment;
import com.aichathon.aickathon2018.com.aickathon.aickathon2018.model.Photo;
import com.aichathon.aickathon2018.com.aickathon.aickathon2018.model.Style;

import java.util.ArrayList;
import java.util.List;

public class ClothAdapter extends ArrayAdapter<ClothList> {

    public ClothAdapter(Context context, ArrayList<ClothList> cloths) {
        super(context, 0, cloths);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ClothList cl = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.cloth_list, parent, false);
        }
        // Lookup view for data population
        ImageView image = convertView.findViewById(R.id.cloth_img);
        String base64 = cl.getPhotoData();
        base64 = base64.substring(2,base64.length()-1);
        image.setImageBitmap(decode(base64));
        TextView name = convertView.findViewById(R.id.cloth_name);
        name.setText(cl.getName());
        TextView price = convertView.findViewById(R.id.cloth_price);
        price.setText(String.format("RM %.2f",cl.getPrice()));
        TextView storeName = convertView.findViewById(R.id.shop_name);
        storeName.setText(cl.getStoreName());
        TextView storeLocation = convertView.findViewById(R.id.shop_location);
        storeLocation.setText(cl.getLocation());
        // Return the completed view to render on screen
        return convertView;
    }
    public Bitmap decode(String encodedImage){
        byte[] decodedString = android.util.Base64.decode(encodedImage, android.util.Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;

    }
}
