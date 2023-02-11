package com.application.mypetandroid.splash_screen;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import com.application.mypetandroid.R;
import com.application.mypetandroid.login.view.LoginActivity;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends Activity {
    private Handler handler;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        handler = new Handler();
        int splashTimeOut = 2500;
        handler.postDelayed(this::showMainScreen, splashTimeOut);
    }

    private void showMainScreen() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    public void onBackPressed() {
        super.onBackPressed();
        this.handler.removeCallbacksAndMessages(null);
        System.exit(0);
    }
}
