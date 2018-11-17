package com.aichathon.aickathon2018;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aichathon.aickathon2018.com.aickathon.aickathon2018.model.Color;
import com.aichathon.aickathon2018.com.aickathon.aickathon2018.model.Garment;
import com.aichathon.aickathon2018.com.aickathon.aickathon2018.model.Photo;
import com.aichathon.aickathon2018.com.aickathon.aickathon2018.model.Style;

import java.util.ArrayList;
import java.util.List;

public class PhotoAdapter extends ArrayAdapter<Photo> {

    public PhotoAdapter(Context context, ArrayList<Photo> photos) {
        super(context, 0, photos);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Photo photo = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.photo_list, parent, false);
        }
        // Lookup view for data population
        TextView color = (TextView) convertView.findViewById(R.id.color);
        TextView garment = (TextView) convertView.findViewById(R.id.garment);
        TextView style = (TextView) convertView.findViewById(R.id.style);
        ImageView image = (ImageView) convertView.findViewById(R.id.imageView) ;
        String base64 = photo.getPhotoData();
        base64 = base64.substring(2,base64.length()-1);
        image.setImageBitmap(decode(base64));
        // Populate the data into the template view using the data object
        StringBuilder cstr = new StringBuilder();
        List<Color> colors = photo.getColors();
        for (int i = 0; i < colors.size(); i++) {
            Color c = colors.get(i);
            if (i==colors.size()-1){
                cstr.append(c.getColorName());
            }
            else {
                cstr.append(c.getColorName()).append(", ");
            }
        }
        StringBuilder gstr = new StringBuilder();
        List<Garment> garments = photo.getGarments();
        for (int i = 0; i < garments.size(); i++) {
            Garment g = garments.get(i);
            if (i==garments.size()-1){
                gstr.append(g.getName());
            }
            else {
                gstr.append(g.getName()).append(", ");
            }
        }
        StringBuilder sstr = new StringBuilder();
        List<Style> styles = photo.getStyles();
        for (int i = 0; i < styles.size(); i++) {
            Style s = styles.get(i);
            if (i==styles.size()-1){
                sstr.append(s.getName());
            }
            else {
                sstr.append(s.getName()).append(", ");
            }
        }
        color.setText(cstr.toString());
        garment.setText(gstr.toString());
        style.setText(sstr.toString());
        // Return the completed view to render on screen
        return convertView;
    }
    public Bitmap decode(String encodedImage){
        byte[] decodedString = android.util.Base64.decode(encodedImage, android.util.Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;

    }
}
