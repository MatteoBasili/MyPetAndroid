package com.application.mypet.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.application.mypet.R;

public class AccountRegistration1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_registration1);

        ImageView back = findViewById(R.id.back_arrow);
        back.setOnClickListener(view -> finish());

        ImageView showPwd1 = findViewById(R.id.show_pwd1);
        ImageView hidePwd1 = findViewById(R.id.hide_pwd1);
        ImageView showPwd2 = findViewById(R.id.show_pwd2);
        ImageView hidePwd2 = findViewById(R.id.hide_pwd2);

        showPwd1.setOnClickListener(view -> {
            hidePwd1.setVisibility(View.VISIBLE);
            showPwd1.setVisibility(View.INVISIBLE);
        });

        hidePwd1.setOnClickListener(view -> {
            showPwd1.setVisibility(View.VISIBLE);
            hidePwd1.setVisibility(View.INVISIBLE);
        });

        showPwd2.setOnClickListener(view -> {
            hidePwd2.setVisibility(View.VISIBLE);
            showPwd2.setVisibility(View.INVISIBLE);
        });

        hidePwd2.setOnClickListener(view -> {
            showPwd2.setVisibility(View.VISIBLE);
            hidePwd2.setVisibility(View.INVISIBLE);
        });

        Button next = findViewById(R.id.continue_button);
        next.setOnClickListener(view -> {
            Intent i = new Intent(AccountRegistration1.this, AccountRegistration2.class);
            startActivity(i);
        });
    }
}