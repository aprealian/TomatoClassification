package com.teknokrait.tomatoclassification.view.scan;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
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
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.teknokrait.tomatoclassification.R;
import com.teknokrait.tomatoclassification.model.Status;
import com.teknokrait.tomatoclassification.model.Tomato;
import com.teknokrait.tomatoclassification.processing.HistogramEQ;
import com.teknokrait.tomatoclassification.processing.Knn;
import com.teknokrait.tomatoclassification.processing.Threshold;
import com.teknokrait.tomatoclassification.realm.RealmController;
import com.teknokrait.tomatoclassification.util.GalleryCameraInvoker;
import com.teknokrait.tomatoclassification.view.trainning.ClassSpinnerAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class ScanResultActivity extends AppCompatActivity {

    private ImageView photoImageView;
    private Bitmap bitmap;

    //private LineChart mChart;
    private Realm realm;
    private LinearLayout mainLinearLayout;
    private ProgressBar progressBar;
    private Status status;
    private double colRed, colGreen, colBlue, colRedEQ, colGreenEQ, colBlueEQ;
    private List<Double> colorsBF, colorsEQ;
    private Status statusTomato;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_result);
        initView();
        initExtra();
    }

    private void initExtra() {
        Intent intent = getIntent();
        bitmap = (Bitmap) intent.getParcelableExtra("BitmapImage");

        if (bitmap != null){
            //mainLinearLayout.setVisibility(View.VISIBLE);
            setHistogram();
            classfication(colorsBF, colorsEQ);
        }
    }

    private void classfication(List<Double> colors, List<Double> colorsEq){

        RealmController.with(this).refresh();
        RealmResults<Tomato> tomatoes = RealmController.with(this).getTomatoes();

        //create objcet Tomato before save to Realm
        Tomato tomato = new Tomato();
        //tomato.setId(RealmController.with(this).getNextKey());
        tomato.setRed(colors.get(0));
        tomato.setGreen(colors.get(1));
        tomato.setBlue(colors.get(2));
        tomato.setRedEq(colorsEq.get(0));
        tomato.setGreenEq(colorsEq.get(1));
        tomato.setBlueEq(colorsEq.get(2));

        Knn knn = new Knn();
        int grade = knn.getClassification(tomatoes, tomato);

        Toast.makeText(this, String.valueOf(grade), Toast.LENGTH_SHORT).show();
    }

    private void setHistogram() {
        //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tomato_green);

        // TODO: Bitmap Before Equalization
        //beforeImageView.setImageBitmap(bitmap);


        //Bitmap newBitmap = hist.computeHistogramEQ(bitmap);
        // TODO: coba pakai helper
        //Bitmap newBitmap = Helper.histogramEqualization(bitmap, this);
        Bitmap newBitmap = HistogramEQ.computeHistogramEQ(bitmap);

        // TODO: 1/14/2018 Bitmap After Equalization
        //afterImageView.setImageBitmap(newBitmap);

        // TODO: Image Gresycale or Treshold
        //greyscaleImageView.setImageBitmap(toGrayscale(newBitmap));
        Bitmap bitmapThres = Threshold.threshold(bitmap);
        //greyscaleImageView.setImageBitmap(bitmapThres);

        //SET HISTOGRAM BEFORE EQUALIZATION

        HistogramEQ hist = new HistogramEQ();

        ArrayList<int[]> histo = hist.imageHistogram(bitmap);
        ArrayList<Entry> yValsRed = new ArrayList<Entry>();
        ArrayList<Entry> yValsGreen = new ArrayList<Entry>();
        ArrayList<Entry> yValsBlue = new ArrayList<Entry>();

        double totalRed = 0, totalGreen = 0, totalBlue = 0;
        int countRed = 0, countGreen = 0, countBlue = 0;
        for (int i = 0; i < histo.get(0).length; i++) {
            Log.e("warna "+String.valueOf(i)+" : ",String.valueOf(histo.get(0)[i]));
            yValsRed.add(new Entry(i, histo.get(0)[i]));
            yValsGreen.add(new Entry(i, histo.get(1)[i]));
            yValsBlue.add(new Entry(i, histo.get(2)[i]));
            totalRed+=i*histo.get(0)[i];
            totalGreen+=i*histo.get(1)[i];
            totalBlue+=i*histo.get(2)[i];

            countRed+=histo.get(0)[i];
            countGreen+=histo.get(1)[i];
            countBlue+=histo.get(2)[i];
        }




        double avgRed = totalRed/countRed;
        double avgGreen = totalGreen/countGreen;
        double avgBlue = totalBlue/countBlue;


        //SET HISTOGRAM AFTER EQUALIZATION
        ArrayList<int[]> histoAf = hist.imageHistogram(newBitmap);
        ArrayList<Entry> yValsRedAf = new ArrayList<Entry>();
        ArrayList<Entry> yValsGreenAf = new ArrayList<Entry>();
        ArrayList<Entry> yValsBlueAf = new ArrayList<Entry>();

        int totalRedAf=0, totalGreenAf=0, totalBlueAf=0;
        int countRedAf=0, countGreenAf=0, countBlueAf=0;
        for (int i = 0; i < histoAf.get(0).length; i++) {
            //Log.e("warna "+String.valueOf(i)+" : ",String.valueOf(histo.get(0)[i]));
            yValsRedAf.add(new Entry(i, histoAf.get(0)[i]));
            yValsGreenAf.add(new Entry(i, histoAf.get(1)[i]));
            yValsBlueAf.add(new Entry(i, histoAf.get(2)[i]));
            totalRedAf+=i*histoAf.get(0)[i];
            totalGreenAf+=i*histoAf.get(1)[i];
            totalBlueAf+=i*histoAf.get(2)[i];

            countRedAf+=histoAf.get(0)[i];
            countGreenAf+=histoAf.get(1)[i];
            countBlueAf+=histoAf.get(2)[i];
        }


        double avgRedEQ = totalRedAf/countRedAf;
        double avgGreenEQ = totalGreenAf/countGreenAf;
        double avgBlueEQ = totalBlueAf/countBlueAf;

        List<Double> colors = new ArrayList<>();
        colors.add(0,avgRed);
        colors.add(1,avgGreen);
        colors.add(2,avgBlue);
        List<Double> colorsEq = new ArrayList<>();
        colorsEq.add(0,avgRedEQ);
        colorsEq.add(1,avgGreenEQ);
        colorsEq.add(2,avgBlueEQ);


        //saveTomatoToRealm();
        colorsBF = new ArrayList<>();
        colorsBF.add(new Double(avgRed));
        colorsBF.add(new Double(avgGreen));
        colorsBF.add(new Double(avgBlue));

        colorsEQ = new ArrayList<>();
        colorsEQ.add(new Double(avgRedEQ));
        colorsEQ.add(new Double(avgGreenEQ));
        colorsEQ.add(new Double(avgBlueEQ));

    }


    private void initView() {
        photoImageView = (ImageView) findViewById(R.id.photo_imageView);
        if (bitmap != null) photoImageView.setImageBitmap(bitmap);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ScanResultActivity.this, CameraActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }






}
