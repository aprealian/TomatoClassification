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
import com.teknokrait.tomatoclassification.processing.Erosion;
import com.teknokrait.tomatoclassification.processing.Threshold;
import com.teknokrait.tomatoclassification.util.GalleryCameraInvoker;
import com.teknokrait.tomatoclassification.view.trainning.TrainingResultActivity;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

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

    private ImageView photoImageView, erotionImageView, dilationImageView;
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
        //initExtra();
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

    private void initExtra(Bitmap bitmap) {
        //Intent intent = getIntent();
        //bitmap = (Bitmap) intent.getParcelableExtra("BitmapImage");

        if (bitmap != null){

            Bitmap thresholdGreenBitmap = Threshold.thresholdGreen(bitmap);

            photoImageView.setImageBitmap(thresholdGreenBitmap);
            Bitmap erotionBitmap =  Bitmap.createBitmap(thresholdGreenBitmap);
            erotionImageView.setImageBitmap(Erosion.binaryImage(erotionBitmap, true));
            Bitmap dilationBitmap =  Bitmap.createBitmap(erotionBitmap);
            dilationImageView.setImageBitmap(Erosion.binaryImage(dilationBitmap, false));


            /*Mat img = new Mat(thresholdGreenBitmap.getWidth(), thresholdGreenBitmap.getHeight(),CvType.CV_8U);
            Utils.bitmapToMat(thresholdGreenBitmap, img);

            Mat threeChannel = new Mat();

            Imgproc.cvtColor(img, threeChannel, Imgproc.COLOR_RGB2GRAY);
            Imgproc.threshold(threeChannel, threeChannel, 100, 255, Imgproc.THRESH_BINARY);


            Mat fg = new Mat(img.size(),CvType.CV_8U);
            Imgproc.erode(threeChannel,fg,new Mat());

            Mat bg = new Mat(img.size(),CvType.CV_8U);
            Imgproc.dilate(threeChannel,bg,new Mat());
            Imgproc.threshold(bg,bg,1, 128,Imgproc.THRESH_BINARY_INV);

            Bitmap newBitmap = Bitmap.createBitmap(threeChannel.width(), threeChannel.height(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(threeChannel, newBitmap);

            photoImageView.setImageBitmap(newBitmap);*/


            /*Mat originalPicMat = new Mat(thresholdGreenBitmap.getWidth(), thresholdGreenBitmap.getHeight(), CvType.CV_8U);
            Utils.bitmapToMat(thresholdGreenBitmap, originalPicMat);
            Mat foregroundMat = new Mat(originalPicMat.size(), CvType.CV_8U);
            Imgproc.dilate(originalPicMat, foregroundMat, new Mat(), new Point(-1, -1), 3);

            Bitmap foregroundBmp = Bitmap.createBitmap(foregroundMat.cols(), foregroundMat.rows(),Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(foregroundMat, foregroundBmp);
            photoImageView.setImageBitmap(foregroundBmp);*/









            //int erosion_size = 5;
            //Mat element1 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new  Size(2*erosion_size + 1, 2*erosion_size+1));

            //Imgproc.dilate(grayscaleMat, foregroundMat, element1, new Point(-1, -1), 2, 1, 1);
            //Imgproc.erode(grayscaleMat, foregroundMat, new Mat(),new Point(-1,-1),2);
            //Imgproc.erode(grayscaleMat, foregroundMat, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(100,100), 2));



            //Show fg area:








            //Bitmap erotionBitmap = Erosion.binaryImage(thresholdGreenBitmap, true);
            //photoImageView.setImageBitmap(erotionBitmap);

            /*Mat mat = new Mat();
            Bitmap bmp32 = thresholdGreenBitmap.copy(Bitmap.Config.ARGB_8888, true);
            Utils.bitmapToMat(bmp32, mat);


            Mat grayscaleMat = new Mat();
            Imgproc.cvtColor(mat, grayscaleMat, Imgproc.COLOR_BGR2GRAY);
            //Imgproc.threshold(grayscaleMat, grayscaleMat, 0, 255, Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_OTSU);
            Mat erodeMat = grayscaleMat;
            Imgproc.erode(grayscaleMat, erodeMat, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(100,100)));




            //check if i dont need to load_image as grayscale, if cvtColor does does work or vice versa:


            //test grayscale: WORKS:
            *//*Bitmap grayscaleBmp = Bitmap.createBitmap(grayscaleMat.cols(), grayscaleMat.rows(),Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(grayscaleMat, grayscaleBmp);
            imgView_grayscale.setImageBitmap(grayscaleBmp);*//*


            Bitmap erodeBitmap = thresholdGreenBitmap;
            Utils.matToBitmap(erodeMat, erodeBitmap);*/
            //Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2RGBA);







            /*Mat originalPicMat = new Mat();
            Utils.bitmapToMat(thresholdGreenBitmap, originalPicMat);


            Mat grayscaleMat = new Mat();
            //check if i dont need to load_image as grayscale, if cvtColor does does work or vice versa:
            Imgproc.cvtColor(originalPicMat, grayscaleMat, Imgproc.COLOR_BGR2GRAY);
            Imgproc.threshold(grayscaleMat, grayscaleMat, 0, 255, Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_OTSU);
            //test grayscale: WORKS:
            Bitmap grayscaleBmp = Bitmap.createBitmap(grayscaleMat.cols(), grayscaleMat.rows(),Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(grayscaleMat, grayscaleBmp);
            photoImageView.setImageBitmap(grayscaleBmp);



            Mat foregroundMat = new Mat();
            //Imgproc.erode(grayscaleMat, foregroundMat, new Mat(),new Point(-1,-1),2);
            //Imgproc.erode(grayscaleMat, foregroundMat, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(100,100), 2));


            int erosion_size = 5;
            Mat element1 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new  Size(2*erosion_size + 1, 2*erosion_size+1));

            //Imgproc.dilate(grayscaleMat, foregroundMat, element1, new Point(-1, -1), 2, 1, 1);
            Imgproc.erode(grayscaleMat, foregroundMat, element1);

            //Show fg area:
            Bitmap foregroundBmp = Bitmap.createBitmap(foregroundMat.cols(), foregroundMat.rows(),Bitmap.Config.ARGB_8888);
            //Utils.matToBitmap(foregroundMat, foregroundBmp);
            photoImageView.setImageBitmap(foregroundBmp);*/


            //photoImageView.setImageBitmap(erodeBitmap);


            //Bitmap erotionBitmap = Erosion.binaryImage(thresholdGreenBitmap, true);
            //erotionImageView.setImageBitmap(thresholdGreenBitmap);
        }
    }

    private void initView() {
        processButton = (Button) findViewById(R.id.process_button);
        erotionImageView = (ImageView) findViewById(R.id.erotion_imageView);
        dilationImageView = (ImageView) findViewById(R.id.dilation_imageView);
        photoImageView = (ImageView) findViewById(R.id.photo_imageView);
        //if (bitmap != null) photoImageView.setImageBitmap(bitmap);
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
                initExtra(bitmap);
                //photoImageView.setImageBitmap(Threshold.thresholdGreen(bitmap));
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


    /*private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i("OpenCV", "OpenCV loaded successfully");
                    //imageMat=new Mat();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };*/


    @Override
    protected void onResume() {
        super.onResume();
        /*if (!OpenCVLoader.initDebug()) {
            Log.d("OpenCV", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d("OpenCV", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }*/
    }
}
