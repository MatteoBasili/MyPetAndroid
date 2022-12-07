package com.application.mypet.login;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.application.mypet.R;

public class PasswordRecovery extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_recovery);

        ImageView back = findViewById(R.id.back_arrow);
        back.setOnClickListener(view -> finish());
    }
}