package com.teknokrait.tomatoclassification.view.scan;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.teknokrait.tomatoclassification.R;

public class ScanResultActivity extends AppCompatActivity {

    private ImageView photoImageView;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_result);
        initExtra();
        initView();
    }

    private void initExtra() {
        Intent intent = getIntent();
        bitmap = (Bitmap) intent.getParcelableExtra("BitmapImage");
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
