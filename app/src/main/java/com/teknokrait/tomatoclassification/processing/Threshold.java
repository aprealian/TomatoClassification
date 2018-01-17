package com.teknokrait.tomatoclassification.processing;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

/**
 * Created by Aprilian Nur Wakhid Daini on 12/24/2017.
 *
 * https://github.com/yusufshakeel/Java-Image-Processing-Project/blob/master/src/imageFX/Threshold.java
 *
 */

public class Threshold {

    public static Bitmap threshold(Bitmap src){
        int width = src.getWidth();
        int height = src.getHeight();
        // create output bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
        // color information
        int A, R, G, B;
        int pixel;
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                // get pixel color
                pixel = src.getPixel(x, y);
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);
                int gray = (int) (0.2989 * R + 0.5870 * G + 0.1140 * B);
                // use 128 as threshold, above -> white, below -> black
                if (gray > 170) {
                    gray = 255;
                }
                else{
                    gray = 0;
                }
                // set new pixel color to output bitmap
                bmOut.setPixel(x, y, Color.argb(A, gray, gray, gray));
            }
        }
        return bmOut;
    }






    public static Bitmap thresholdGreen(Bitmap src){
        int width = src.getWidth();
        int height = src.getHeight();
        // create output bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
        // color information
        int A, R, G, B;
        int pixel;
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                // get pixel color
                pixel = src.getPixel(x, y);
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);
                int gray = (int) (0.2989 * R + 0.5870 * G + 0.1140 * B);
                // use 128 as threshold, above -> white, below -> black

                /*if (G > 180) {
                    gray = 255;
                    bmOut.setPixel(x, y, Color.argb(A, R, G, B));
                }
                else{
                    gray = 0;
                    bmOut.setPixel(x, y, Color.argb(A, gray, gray, gray));
                }
                */

                int a = Color.alpha(pixel);
                int r = Color.red(pixel);
                int g = Color.green(pixel);
                int b = Color.blue(pixel);

                float[] hsv = new float[3];
                Color.RGBToHSV(r, g, b, hsv);

                //Log.e("cek warna ke-"+y, hsv[0]+" "+hsv[1]+" "+hsv[2]);

                //if (hsv[0] > 60 && hsv[1] > 0.75 && hsv[2] > 0.50) { //OK
                if (hsv[0] > 60 && hsv[1] > 0.80 && hsv[2] > 0.60) {
                    //gray = 255;
                    //bmOut.setPixel(x, y, Color.argb(A, R, G, B));
                    gray = 0;
                    bmOut.setPixel(x, y, Color.argb(A, gray, gray, gray));
                }
                else{
                    gray = 255;
                    bmOut.setPixel(x, y, Color.argb(A, gray, gray, gray));
                }
                // set new pixel color to output bitmap
                //bmOut.setPixel(x, y, Color.argb(A, gray, gray, gray));
            }
        }
        return bmOut;
    }


}

