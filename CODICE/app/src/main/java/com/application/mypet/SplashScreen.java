package com.application.mypet;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.application.mypet.login.view.LoginActivity;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends Activity {

    Handler handler;

    int splashTimeOut = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        handler = new Handler();
        handler.postDelayed(() -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        },splashTimeOut);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        handler.removeCallbacksAndMessages(null);
        System.exit(0);
    }
}