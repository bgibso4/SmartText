package com.gmf.dev;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class BitmapTypeConverter {
    public static String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream byteStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, byteStream);
        byte [] b=byteStream.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public static Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }
}
