package com.teknokrait.tomatoclassification.view.trainning;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
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
import com.teknokrait.tomatoclassification.helper.Helper;
import com.teknokrait.tomatoclassification.model.Status;
import com.teknokrait.tomatoclassification.model.Tomato;
import com.teknokrait.tomatoclassification.processing.HistogramEQ;
import com.teknokrait.tomatoclassification.processing.Threshold;
import com.teknokrait.tomatoclassification.realm.RealmController;
import com.teknokrait.tomatoclassification.util.GalleryCameraInvoker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class TrainingResultActivity extends Activity implements SeekBar.OnSeekBarChangeListener,
        GalleryCameraInvoker.CallbackWithProcessing {

    private static final int REQ_CODE_CAMERA = 100;
    private static final int REQ_CODE_GALLERY = 101;

    //private LineChart mChart;
    private Realm realm;
    private GalleryCameraInvoker invoker;
    private File changedPhotoFile;
    private boolean isPickingPhoto;
    private Bitmap bitmap;
    private LineChart beforeEQChart, afterEQChart;
    private SeekBar mSeekBarX, mSeekBarY;
    private TextView tvX, tvY;
    private ImageView beforeImageView, afterImageView, greyscaleImageView;
    private LinearLayout mainLinearLayout;
    private ProgressBar progressBar;
    private Spinner methodSpinner;
    private Spinner sp;
    private EditText urlEditText;
    private Button processButton, saveButton;
    private Status status;
    private String imageUrl;
    private String imagePath;

    private double colRed, colGreen, colBlue, colRedEQ, colGreenEQ, colBlueEQ;
    private List<Double> colorsBF, colorsEQ;
    private Status statusTomato;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_training);
        initExtra();
        initView();
        if (bitmap != null){
            mainLinearLayout.setVisibility(View.VISIBLE);
            initChartBeforeEQ();
            initChartAfterEQ();
            setHistogram();
        } else {
            sp.setVisibility(View.VISIBLE);
            methodSpinner.setVisibility(View.VISIBLE);
            processButton.setVisibility(View.VISIBLE);

            List<Status> statusList = new ArrayList<>();
            statusList.add(new Status(1,"Matang"));
            statusList.add(new Status(2,"Mentah"));
            sp = (Spinner)findViewById(R.id.class_spinner);
            ArrayAdapter<Status> myAdapter = new ClassSpinnerAdapter(this, R.layout.spinner_item_class, statusList);
            sp.setAdapter(myAdapter);
        }

        processButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveButton.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                statusTomato = (Status) sp.getSelectedItem();
                Toast.makeText(TrainingResultActivity.this, statusTomato.getStatus(), Toast.LENGTH_SHORT).show();
                isPickingPhoto = false;
                onChangePhoto();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTomatoToRealm(colorsBF, colorsEQ, statusTomato, "", changedPhotoFile.getAbsolutePath());
                Toast.makeText(TrainingResultActivity.this, "Data has been saved", Toast.LENGTH_SHORT).show();
                finish();
            }
        });


    }

    private void initExtra() {
        Intent intent = getIntent();
        bitmap = (Bitmap) intent.getParcelableExtra("BitmapImage");
        status = (Status) intent.getParcelableExtra("Status");
        imagePath = intent.getStringExtra("ImagePath");
        imageUrl = intent.getStringExtra("ImageUrl");

        colorsBF = new ArrayList<>();
        colorsEQ = new ArrayList<>();
    }

    private void initChartAfterEQ() {
        mSeekBarX.setProgress(45);
        mSeekBarY.setProgress(100);

        mSeekBarY.setOnSeekBarChangeListener(this);
        mSeekBarX.setOnSeekBarChangeListener(this);

        afterEQChart = (LineChart) findViewById(R.id.chart2);
        afterEQChart.setViewPortOffsets(0, 0, 0, 0);
        afterEQChart.setBackgroundColor(Color.rgb(104, 241, 175));

        // no description text
        afterEQChart.getDescription().setEnabled(false);

        // enable touch gestures
        afterEQChart.setTouchEnabled(true);

        // enable scaling and dragging
        afterEQChart.setDragEnabled(true);
        afterEQChart.setScaleEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        afterEQChart.setPinchZoom(false);

        afterEQChart.setDrawGridBackground(false);
        afterEQChart.setMaxHighlightDistance(300);

        XAxis x = afterEQChart.getXAxis();
        x.setEnabled(false);

        YAxis y = afterEQChart.getAxisLeft();
        //y.setTypeface(mTfLight);
        y.setLabelCount(6, false);
        y.setTextColor(Color.WHITE);
        y.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        y.setDrawGridLines(false);
        y.setAxisLineColor(Color.WHITE);

        afterEQChart.getAxisRight().setEnabled(false);

        // add data
        //setData(45, 100);

        afterEQChart.getLegend().setEnabled(false);

        afterEQChart.animateXY(2000, 2000);
        afterEQChart.setAutoScaleMinMaxEnabled(true);
        // dont forget to refresh the drawing
        afterEQChart.invalidate();
    }

    private void initChartBeforeEQ() {
        mSeekBarX.setProgress(45);
        mSeekBarY.setProgress(100);

        mSeekBarY.setOnSeekBarChangeListener(this);
        mSeekBarX.setOnSeekBarChangeListener(this);

        beforeEQChart = (LineChart) findViewById(R.id.chart1);
        beforeEQChart.setViewPortOffsets(0, 0, 0, 0);
        beforeEQChart.setBackgroundColor(Color.rgb(104, 241, 175));

        // no description text
        beforeEQChart.getDescription().setEnabled(false);

        // enable touch gestures
        beforeEQChart.setTouchEnabled(true);

        // enable scaling and dragging
        beforeEQChart.setDragEnabled(true);
        beforeEQChart.setScaleEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        beforeEQChart.setPinchZoom(false);

        beforeEQChart.setDrawGridBackground(false);
        beforeEQChart.setMaxHighlightDistance(300);

        XAxis x = beforeEQChart.getXAxis();
        x.setEnabled(false);

        YAxis y = beforeEQChart.getAxisLeft();
        //y.setTypeface(mTfLight);
        y.setLabelCount(6, false);
        y.setTextColor(Color.WHITE);
        y.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        y.setDrawGridLines(false);
        y.setAxisLineColor(Color.WHITE);

        beforeEQChart.getAxisRight().setEnabled(false);

        // add data
        //setData(45, 100);

        beforeEQChart.getLegend().setEnabled(false);

        beforeEQChart.animateXY(2000, 2000);
        beforeEQChart.setAutoScaleMinMaxEnabled(true);
        // dont forget to refresh the drawing
        beforeEQChart.invalidate();
    }

    private void initView() {

        //Log.e("tes ya", "hallo 2");

        tvX = (TextView) findViewById(R.id.tvXMax);
        tvY = (TextView) findViewById(R.id.tvYMax);
        beforeImageView = (ImageView) findViewById(R.id.before_imageView);
        afterImageView = (ImageView) findViewById(R.id.after_imageView);
        greyscaleImageView = (ImageView) findViewById(R.id.greyscale_imageView);
        mainLinearLayout = (LinearLayout) findViewById(R.id.main_linearLayout);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        mSeekBarX = (SeekBar) findViewById(R.id.seekBar1);
        mSeekBarY = (SeekBar) findViewById(R.id.seekBar2);

        methodSpinner = (Spinner) findViewById(R.id.method_spinner);
        sp = (Spinner) findViewById(R.id.class_spinner);
        urlEditText = (EditText) findViewById(R.id.url_edittext);
        processButton = (Button) findViewById(R.id.process_button);
        saveButton = (Button) findViewById(R.id.save_button);


        //SET SPINNER
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, getResources()
                .getStringArray(R.array.method_array));//setting the country_array to spinner
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        methodSpinner.setAdapter(adapter);
        //if you want to set any action you can do in this listener
        methodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });


    }


    private void setHistogram() {
        //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tomato_green);

        beforeImageView.setImageBitmap(bitmap);


        //Bitmap newBitmap = hist.computeHistogramEQ(bitmap);
        // TODO: coba pakai helper
        //Bitmap newBitmap = Helper.histogramEqualization(bitmap, this);
        Bitmap newBitmap = HistogramEQ.computeHistogramEQ(bitmap);

        afterImageView.setImageBitmap(newBitmap);

        //greyscaleImageView.setImageBitmap(toGrayscale(newBitmap));
        Bitmap bitmapThres = Threshold.threshold(bitmap);
        greyscaleImageView.setImageBitmap(bitmapThres);

        //newBitmap = bitmap;

        /*int[] rhistogram = new int[256];
        int[] ghistogram = new int[256];
        int[] bhistogram = new int[256];

        for(int i=0; i<rhistogram.length; i++) rhistogram[i] = 0;
        for(int i=0; i<ghistogram.length; i++) ghistogram[i] = 0;
        for(int i=0; i<bhistogram.length; i++) bhistogram[i] = 0;

        for(int i=0; i<bitmapThres.getWidth(); i++) {
            for(int j=0; j<bitmapThres.getHeight(); j++) {
                int colour = newBitmap.getPixel(i, j);
                if (Color.red(colour) < 255 || Color.green(colour) < 255 || Color.blue(colour) < 255){
                    int red = Color.red(colour);
                    int blue = Color.blue(colour);
                    int green = Color.green(colour);
                    int alpha = Color.alpha(colour);
                    // Increase the values of colors
                    rhistogram[red]++; ghistogram[green]++; bhistogram[blue]++;
                }
            }
        }*/



        /*//Find average pixel color in RGB
        double totalRed = 0;
        int countRed = 0;
        for (int i=0; i < rhistogram.length; i++){
            totalRed += (i*rhistogram[i]);
            countRed += rhistogram[i];
        }
        Log.e("original count RED : ", String.valueOf(countRed));
        Log.e("original average RED : ", String.valueOf(totalRed/countRed));


        double totalGreen = 0;
        int countGreen = 0;
        for (int i=0; i < ghistogram.length; i++){
            totalGreen += (i*ghistogram[i]);
            countGreen += ghistogram[i];
        }
        Log.e("average count Gren : ", String.valueOf(countGreen));
        Log.e("average Green : ", String.valueOf(totalGreen/countGreen));


        double totalBlue = 0;
        int countBlue = 0;
        for (int i=0; i < bhistogram.length; i++){
            totalBlue += (i*bhistogram[i]);
            countBlue += bhistogram[i];
        }
        Log.e("average count Blue : ", String.valueOf(countBlue));
        Log.e("average Blue : ", String.valueOf(totalBlue/countBlue));

        ArrayList<int[]> histss = new ArrayList<int[]>();
        histss.add(rhistogram);
        histss.add(ghistogram);
        histss.add(bhistogram);*/



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

        Log.e("color count RED : ", String.valueOf(countRed));
        Log.e("color avg RED : ", String.valueOf(avgRed));
        Log.e("color count GREEN: ", String.valueOf(countGreen));
        Log.e("color avg GREEN : ", String.valueOf(avgGreen));
        Log.e("color count BLUE : ", String.valueOf(countBlue));
        Log.e("color avg BLUE : ", String.valueOf(avgBlue));
        //Log.e("warna avg : ", String.valueOf(total/(bitmap.getWidth()*bitmap.getHeight())));


        LineDataSet setRed;
        LineDataSet setGreen;
        LineDataSet setBlue;

        if (beforeEQChart.getData() != null && beforeEQChart.getData().getDataSetCount() > 0) {
            setRed = (LineDataSet)beforeEQChart.getData().getDataSetByIndex(0);
            setGreen = (LineDataSet)beforeEQChart.getData().getDataSetByIndex(1);
            setBlue = (LineDataSet)beforeEQChart.getData().getDataSetByIndex(2);
            setRed.setValues(yValsRed);
            setGreen.setValues(yValsGreen);
            setBlue.setValues(yValsBlue);
            beforeEQChart.getData().notifyDataChanged();
            beforeEQChart.notifyDataSetChanged();
        } else {
            // RED create a dataset and give it a type
            setRed = new LineDataSet(yValsRed, "DataSet 1");
            setRed.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            setRed.setCubicIntensity(0.2f);
            //set1.setDrawFilled(true);
            setRed.setDrawCircles(false);
            setRed.setLineWidth(1.8f);
            setRed.setCircleRadius(4f);
            setRed.setCircleColor(Color.RED);
            setRed.setHighLightColor(Color.rgb(244, 117, 117));
            setRed.setColor(Color.RED);
            setRed.setFillColor(Color.RED);
            setRed.setFillAlpha(100);
            setRed.setDrawHorizontalHighlightIndicator(false);
            setRed.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return -10;
                }
            });

            // GREEN create a dataset and give it a type
            setGreen = new LineDataSet(yValsGreen, "DataSet 2");
            setGreen.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            setGreen.setCubicIntensity(0.2f);
            //set1.setDrawFilled(true);
            setGreen.setDrawCircles(false);
            setGreen.setLineWidth(1.8f);
            setGreen.setCircleRadius(4f);
            setGreen.setCircleColor(Color.GREEN);
            setGreen.setHighLightColor(Color.rgb(244, 117, 117));
            setGreen.setColor(Color.GREEN);
            setGreen.setFillColor(Color.GREEN);
            setGreen.setFillAlpha(100);
            setGreen.setDrawHorizontalHighlightIndicator(false);
            setGreen.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return -10;
                }
            });


            // BLUE create a dataset and give it a type
            setBlue = new LineDataSet(yValsBlue, "DataSet 3");
            setBlue.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            setBlue.setCubicIntensity(0.2f);
            //set1.setDrawFilled(true);
            setBlue.setDrawCircles(false);
            setBlue.setLineWidth(1.8f);
            setBlue.setCircleRadius(4f);
            setBlue.setCircleColor(Color.BLUE);
            setBlue.setHighLightColor(Color.rgb(244, 117, 117));
            setBlue.setColor(Color.BLUE);
            setBlue.setFillColor(Color.BLUE);
            setBlue.setFillAlpha(100);
            setBlue.setDrawHorizontalHighlightIndicator(false);
            setBlue.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return -10;
                }
            });

            // create a data object with the datasets
            LineData data = new LineData();
            data.addDataSet(setRed);
            data.addDataSet(setGreen);
            data.addDataSet(setBlue);
            //data.setValueTypeface(mTfLight);
            data.setValueTextSize(9f);
            data.setDrawValues(false);

            // set data
            beforeEQChart.setData(data);
            //mChart.setData(data);
        }




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
        Log.e("color EQ count RED : ", String.valueOf(countRedAf));
        Log.e("color EQ avg RED : ", String.valueOf(avgRedEQ));
        Log.e("color EQ count GREEN: ", String.valueOf(countGreenAf));
        Log.e("color EQ avg GREEN : ", String.valueOf(avgGreenEQ));
        Log.e("color EQ count BLUE : ", String.valueOf(countBlueAf));
        Log.e("color EQ avg BLUE : ", String.valueOf(avgBlueEQ));

        List<Double> colors = new ArrayList<>();
        colors.add(0,avgRed);
        colors.add(1,avgGreen);
        colors.add(2,avgBlue);
        List<Double> colorsEq = new ArrayList<>();
        colorsEq.add(0,avgRedEQ);
        colorsEq.add(1,avgGreenEQ);
        colorsEq.add(2,avgBlueEQ);


        Toast.makeText(this, "hello", Toast.LENGTH_SHORT).show();
        //store data to Realm
        //saveTomatoToRealm(colors, colorsEq, status, imageUrl, imagePath);

        //Toast.makeText(this, status.toString(), Toast.LENGTH_SHORT).show();


        if (afterEQChart.getData() != null && afterEQChart.getData().getDataSetCount() > 0) {
            setRed = (LineDataSet)afterEQChart.getData().getDataSetByIndex(0);
            setGreen = (LineDataSet)afterEQChart.getData().getDataSetByIndex(1);
            setBlue = (LineDataSet)afterEQChart.getData().getDataSetByIndex(2);
            setRed.setValues(yValsRedAf);
            setGreen.setValues(yValsGreenAf);
            setBlue.setValues(yValsBlueAf);
            afterEQChart.getData().notifyDataChanged();
            afterEQChart.notifyDataSetChanged();
        } else {
            // RED create a dataset and give it a type
            setRed = new LineDataSet(yValsRedAf, "DataSet 1");
            setRed.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            setRed.setCubicIntensity(0.2f);
            //set1.setDrawFilled(true);
            setRed.setDrawCircles(false);
            setRed.setLineWidth(1.8f);
            setRed.setCircleRadius(4f);
            setRed.setCircleColor(Color.RED);
            setRed.setHighLightColor(Color.rgb(244, 117, 117));
            setRed.setColor(Color.RED);
            setRed.setFillColor(Color.RED);
            setRed.setFillAlpha(100);
            setRed.setDrawHorizontalHighlightIndicator(false);
            setRed.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return -10;
                }
            });

            // GREEN create a dataset and give it a type
            setGreen = new LineDataSet(yValsGreenAf, "DataSet 2");
            setGreen.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            setGreen.setCubicIntensity(0.2f);
            //set1.setDrawFilled(true);
            setGreen.setDrawCircles(false);
            setGreen.setLineWidth(1.8f);
            setGreen.setCircleRadius(4f);
            setGreen.setCircleColor(Color.GREEN);
            setGreen.setHighLightColor(Color.rgb(244, 117, 117));
            setGreen.setColor(Color.GREEN);
            setGreen.setFillColor(Color.GREEN);
            setGreen.setFillAlpha(100);
            setGreen.setDrawHorizontalHighlightIndicator(false);
            setGreen.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return -10;
                }
            });


            // BLUE create a dataset and give it a type
            setBlue = new LineDataSet(yValsBlueAf, "DataSet 3");
            setBlue.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            setBlue.setCubicIntensity(0.2f);
            //set1.setDrawFilled(true);
            setBlue.setDrawCircles(false);
            setBlue.setLineWidth(1.8f);
            setBlue.setCircleRadius(4f);
            setBlue.setCircleColor(Color.BLUE);
            setBlue.setHighLightColor(Color.rgb(244, 117, 117));
            setBlue.setColor(Color.BLUE);
            setBlue.setFillColor(Color.BLUE);
            setBlue.setFillAlpha(100);
            setBlue.setDrawHorizontalHighlightIndicator(false);
            setBlue.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return -10;
                }
            });

            // create a data object with the datasets
            LineData data = new LineData();
            data.addDataSet(setRed);
            data.addDataSet(setGreen);
            data.addDataSet(setBlue);
            //data.setValueTypeface(mTfLight);
            data.setValueTextSize(9f);
            data.setDrawValues(false);

            // set data
            afterEQChart.setData(data);
            //mChart.setData(data);


            //saveTomatoToRealm();
            colorsBF.add(new Double(avgRed));
            colorsBF.add(new Double(avgGreen));
            colorsBF.add(new Double(avgBlue));

            colorsEQ.add(new Double(avgRedEQ));
            colorsEQ.add(new Double(avgGreenEQ));
            colorsEQ.add(new Double(avgBlueEQ));


        }

    }

    private void saveTomatoToRealm(List<Double> colors, List<Double> colorsEq, Status status, String imageUrl, String imagePath){
        //create objcet Tomato before save to Realm
        Tomato tomato = new Tomato();
        tomato.setId(RealmController.with(this).getNextKey());
        tomato.setImageUrl(imageUrl);
        tomato.setImagePath(imagePath);
        tomato.setStatus(status.getStatus());
        tomato.setClassification(status.getId());
        tomato.setRed(colors.get(0));
        tomato.setGreen(colors.get(1));
        tomato.setBlue(colors.get(2));
        tomato.setRedEq(colorsEq.get(0));
        tomato.setGreenEq(colorsEq.get(1));
        tomato.setBlueEq(colorsEq.get(2));

        //save to RealmDB
        this.realm = RealmController.with(this).getRealm();
        realm.beginTransaction();
        realm.copyToRealm(tomato);
        realm.commitTransaction();
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        /*tvX.setText("" + (mSeekBarX.getProgress() + 1));
        tvY.setText("" + (mSeekBarY.getProgress()));

        setData(mSeekBarX.getProgress() + 1, mSeekBarY.getProgress());

        // redraw
        mChart.invalidate();*/
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

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
                initChartBeforeEQ();
                initChartAfterEQ();
                setHistogram();
                progressBar.setVisibility(View.GONE);
                mainLinearLayout.setVisibility(View.VISIBLE);
                saveButton.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onCancelGalleryCameraInvoker() {
        isPickingPhoto = false;
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onProcessing() {

    }


}