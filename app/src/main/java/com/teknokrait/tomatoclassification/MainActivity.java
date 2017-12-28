package com.teknokrait.tomatoclassification;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.teknokrait.tomatoclassification.view.trainning.DataTrainingActivity;

public class MainActivity extends AppCompatActivity {

    private ImageView dataTrainingImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initControl();
    }

    private void initControl() {
        dataTrainingImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DataTrainingActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initView() {
        dataTrainingImageView = (ImageView) findViewById(R.id.data_training_imageView);
    }


}
