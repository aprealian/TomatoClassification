package com.teknokrait.tomatoclassification.view.scan;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.teknokrait.tomatoclassification.R;
import com.teknokrait.tomatoclassification.model.Status;
import com.teknokrait.tomatoclassification.processing.Threshold;
import com.teknokrait.tomatoclassification.util.GalleryCameraInvoker;
import com.teknokrait.tomatoclassification.view.trainning.TrainingResultActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class TestActivity extends AppCompatActivity implements
        GalleryCameraInvoker.CallbackWithProcessing {

    private static final int REQ_CODE_CAMERA = 100;
    private static final int REQ_CODE_GALLERY = 101;

    private ImageView photoImageView;
    private Bitmap bitmap;


    private GalleryCameraInvoker invoker;
    private File changedPhotoFile;
    private boolean isPickingPhoto;
    private ImageView beforeImageView, afterImageView, greyscaleImageView;
    private Button processButton, saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        initView();
        initExtra();
        initControl();
    }

    private void initControl() {
        processButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPickingPhoto = false;
                onChangePhoto();
            }
        });
    }

    private void initExtra() {
        Intent intent = getIntent();
        bitmap = (Bitmap) intent.getParcelableExtra("BitmapImage");

        if (bitmap != null){
            //mainLinearLayout.setVisibility(View.VISIBLE);
            /*setHistogram();
            classfication(colorsBF, colorsEQ);*/
            photoImageView.setImageBitmap(Threshold.thresholdGreen(bitmap));
        }
    }

    private void initView() {
        processButton = (Button) findViewById(R.id.process_button);
        photoImageView = (ImageView) findViewById(R.id.photo_imageView);
        if (bitmap != null) photoImageView.setImageBitmap(bitmap);
    }















    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (invoker != null) {
            invoker.onActivityResult(requestCode, resultCode, data);
        }
        if ((requestCode == REQ_CODE_CAMERA || requestCode == REQ_CODE_GALLERY) && requestCode == Activity.RESULT_CANCELED) {
            isPickingPhoto = false;
        }
    }

    protected void onChangePhoto() {
        invoker = new GalleryCameraInvoker() {
            @Override
            protected int maxImageWidth() {
                return getApplication().getResources().getDimensionPixelSize(R.dimen.max_create_place_image_width);
            }

            @Override
            protected File onProcessingImageFromCamera(String path) {
                File realFile = super.onProcessingImageFromCamera(path);

                Bitmap bitmap = null;
                int targetW = getResources().getDimensionPixelSize(R.dimen.max_create_place_image_width);

                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(realFile.getPath(), bmOptions);
                int photoW = bmOptions.outWidth;
                int scaleFactor = photoW / targetW;

                bmOptions.inJustDecodeBounds = false;
                bmOptions.inSampleSize = scaleFactor;
                bmOptions.inPurgeable = true;

                bitmap = BitmapFactory.decodeFile(realFile.getPath(), bmOptions);

                ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, imageStream);
                bitmap.recycle();

                String localPath = realFile.getPath();
                String thumbPath = localPath.replace("JPEG", "THUMB");
                File pictureFile = new File(thumbPath);

                try {
                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    fos.write(imageStream.toByteArray());
                    fos.close();

                    return realFile;
                } catch (FileNotFoundException e) {

                } catch (IOException e) {

                } finally {
                    System.gc();
                }
                return null;
            }

            @Override
            protected File onProcessingImageFromGallery(InputStream inputStream) throws IOException {
                File realFile = super.onProcessingImageFromGallery(inputStream);

                int targetW = getResources().getDimensionPixelSize(R.dimen.create_place_photo_thumb_width);

                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(realFile.getPath(), bmOptions);
                int photoW = bmOptions.outWidth;
                int photoH = bmOptions.outHeight;

                int targetH = (photoH * targetW) / photoW;

                int scaleFactor = photoW / targetW;

                bmOptions.inJustDecodeBounds = false;
                bmOptions.inSampleSize = scaleFactor;
                bmOptions.inPurgeable = true;

                Bitmap bitmap = BitmapFactory.decodeFile(realFile.getPath(), bmOptions);

                ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, imageStream);
                bitmap.recycle();

                String localPath = realFile.getPath();
                String thumbPath = localPath.replace("JPEG", "THUMB");
                File pictureFile = new File(thumbPath);

                try {
                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    fos.write(imageStream.toByteArray());
                    fos.close();
                    return realFile;

                } catch (FileNotFoundException e) {

                } catch (IOException e) {


                } finally {
                    System.gc();
                }
                return null;
            }

            @Override
            protected void onShowOptionList(Context context) {
                super.onShowOptionList(context);
                isPickingPhoto = true;
            }
        };
        invoker.invokeGalleryAndCamera(this, this, REQ_CODE_CAMERA, REQ_CODE_GALLERY, true);
    }

    @Override
    public void onBitmapResult(File file) {
        changedPhotoFile = file;
        isPickingPhoto = false;
        invoker = null;
        if (changedPhotoFile != null && changedPhotoFile.exists()) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(changedPhotoFile.getAbsolutePath(), options);
            if(bitmap != null) {
                this.bitmap = bitmap;
                Log.e("cek gallery","OK");
                photoImageView.setImageBitmap(Threshold.thresholdGreen(bitmap));
                //progressBar.setVisibility(View.GONE);
                //mainLinearLayout.setVisibility(View.VISIBLE);
                //saveButton.setVisibility(View.VISIBLE);
            }
        } else {
            Log.e("cek gallery","NO");
        }
    }

    @Override
    public void onCancelGalleryCameraInvoker() {
        isPickingPhoto = false;
        //progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onProcessing() {

    }



}
