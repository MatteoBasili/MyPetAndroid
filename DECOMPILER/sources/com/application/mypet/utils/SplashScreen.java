package com.application.mypet.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.vectordrawable.graphics.drawable.PathInterpolatorCompat;
import com.application.mypet.R;
import com.application.mypet.login.view.LoginActivity;

public class SplashScreen extends Activity {
    Handler handler;
    int splashTimeOut = PathInterpolatorCompat.MAX_NUM_POINTS;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Handler handler2 = new Handler();
        this.handler = handler2;
        handler2.postDelayed(new SplashScreen$$ExternalSyntheticLambda0(this), (long) this.splashTimeOut);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreate$0$com-application-mypet-utils-SplashScreen  reason: not valid java name */
    public /* synthetic */ void m22lambda$onCreate$0$comapplicationmypetutilsSplashScreen() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    public void onBackPressed() {
        super.onBackPressed();
        this.handler.removeCallbacksAndMessages((Object) null);
        System.exit(0);
    }
}
