package com.teknokrait.tomatoclassification.processing;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

/**
 * Created by Aprilian Nur Wakhid Daini on 1/15/2018.
 *
 * https://github.com/yusufshakeel/Java-Image-Processing-Project/blob/master/src/imageFX/morph/Erosion.java
 *
 */

public class Erosion {

    /**
     * This method will perform erosion operation on the binary image img.
     * A binary image has two types of pixels - Black and White.
     * WHITE pixel has the ARGB value (255,255,255,255)
     * BLACK pixel has the ARGB value (255,0,0,0)
     *
     * For erosion we generally consider foreground pixel. So, erodeForegroundPixel = true
     *
     * @param img The image on which erosion operation is performed
     * @param erodeForegroundPixel If set to TRUE will perform erosion on WHITE pixels else on BLACK pixels.
     */
    public static Bitmap binaryImage(Bitmap img, boolean erodeForegroundPixel){
        /**
         * Dimension of the image img.
         */

        Log.e("cek erosi : ","start");

        int width = img.getWidth();
        int height = img.getHeight();

        /**
         * This will hold the erosion result which will be copied to image img.
         */
        int output[] = new int[width * height];

        /**
         * If erosion is to be performed on BLACK pixels then
         * targetValue = 0
         * else
         * targetValue = 255;  //for WHITE pixels
         */
        int targetValue = (erodeForegroundPixel == true)?0:255;

        /**
         * If the target pixel value is WHITE (255) then the reverse pixel value will
         * be BLACK (0) and vice-versa.
         */
        int reverseValue = (targetValue == 255)?0:255;

        //perform erosion
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                //For BLACK pixel RGB all are set to 0 and for WHITE pixel all are set to 255.
                if(Color.green(img.getPixel(x, y)) == targetValue){
                    /**
                     * We are using a 3x3 kernel
                     * [1, 1, 1
                     *  1, 1, 1
                     *  1, 1, 1]
                     */
                    boolean flag = false;   //this will be set if a pixel of reverse value is found in the mask
                    for(int ty = y - 20; ty <= y + 20 && flag == false; ty++){
                        for(int tx = x - 20; tx <= x + 20 && flag == false; tx++){
                            if(ty >= 0 && ty < height && tx >= 0 && tx < width){
                                //origin of the mask is on the image pixels
                                if(Color.green(img.getPixel(tx, ty)) != targetValue){
                                    flag = true;
                                    output[x+y*width] = reverseValue;
                                }
                            }
                        }
                    }
                    if(flag == false){
                        //all pixels inside the mask [i.e., kernel] were of targetValue
                        output[x+y*width] = targetValue;
                    }
                }else{
                    output[x+y*width] = reverseValue;
                }
            }
        }

        /**
         * Save the erosion value in image img.
         */
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                int v = output[x+y*width];

                img.setPixel(x, y, Color.argb(255, v, v, v));
                //img.setPixel(x, y, 255, v, v, v);
            }
        }

        Log.e("cek erosi : ","finish");

        return img;
    }



}
