package com.ve.irrigation.irrigation;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ve.irrigation.customview.CustomTextViewBold;

public class SplashActivity extends AppCompatActivity {

    Handler mHandler;
    Runnable mRunnable;
    CustomTextViewBold mTextviewCurrentVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mTextviewCurrentVersion = (CustomTextViewBold) findViewById(R.id.textview_currentversion);
        mTextviewCurrentVersion.setText("Irrigation App Version : 5");
        startTimeToMoveToNextScreen();
    }

    private void startTimeToMoveToNextScreen() {
        mHandler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {

                startActivity(new Intent(SplashActivity.this, MainActivity.class));


                finish();
            }
        };
        mHandler.postDelayed(mRunnable, 5000);
    }
}
