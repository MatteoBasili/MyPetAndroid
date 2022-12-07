package com.application.mypet.login;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.mypet.R;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class AccountRegistration2 extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_registration2);

        AtomicInteger colorForPetSitter = new AtomicInteger();
        AtomicBoolean clickable = new AtomicBoolean();

        ImageView back = findViewById(R.id.back_arrow);
        back.setOnClickListener(view -> finish());

        CheckBox petSitterBox = (CheckBox) findViewById(R.id.pet_sitter_checkBox);
        TextView caredPets = (TextView) findViewById(R.id.cared_pets);
        CheckBox caredPets1 = (CheckBox) findViewById(R.id.cared_pets_checkBox1);
        CheckBox caredPets2 = (CheckBox) findViewById(R.id.cared_pets_checkBox2);
        CheckBox caredPets3 = (CheckBox) findViewById(R.id.cared_pets_checkBox3);
        TextView services = (TextView) findViewById(R.id.offered_services);
        CheckBox service1 = (CheckBox) findViewById(R.id.services_checkBox1);
        CheckBox service2 = (CheckBox) findViewById(R.id.services_checkBox2);
        CheckBox service3 = (CheckBox) findViewById(R.id.services_checkBox3);
        CheckBox service4 = (CheckBox) findViewById(R.id.services_checkBox4);
        CheckBox service5 = (CheckBox) findViewById(R.id.services_checkBox5);

        petSitterBox.setOnClickListener(view -> {

            if (petSitterBox.isChecked()) {
                colorForPetSitter.set(R.color.black);
                clickable.set(true);
            } else {
                colorForPetSitter.set(R.color.disable_color);
                clickable.set(false);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                caredPets.setTextColor(getColor(colorForPetSitter.get()));
                caredPets1.setButtonTintList(ColorStateList.valueOf(getColor(colorForPetSitter.get())));
                caredPets1.setTextColor(getColor(colorForPetSitter.get()));
                caredPets1.setClickable(clickable.get());
                caredPets2.setButtonTintList(ColorStateList.valueOf(getColor(colorForPetSitter.get())));
                caredPets2.setTextColor(getColor(colorForPetSitter.get()));
                caredPets2.setClickable(clickable.get());
                caredPets3.setButtonTintList(ColorStateList.valueOf(getColor(colorForPetSitter.get())));
                caredPets3.setTextColor(getColor(colorForPetSitter.get()));
                caredPets3.setClickable(clickable.get());

                services.setTextColor(getColor(colorForPetSitter.get()));
                service1.setButtonTintList(ColorStateList.valueOf(getColor(colorForPetSitter.get())));
                service1.setTextColor(getColor(colorForPetSitter.get()));
                service1.setClickable(clickable.get());
                service2.setButtonTintList(ColorStateList.valueOf(getColor(colorForPetSitter.get())));
                service2.setTextColor(getColor(colorForPetSitter.get()));
                service2.setClickable(clickable.get());
                service3.setButtonTintList(ColorStateList.valueOf(getColor(colorForPetSitter.get())));
                service3.setTextColor(getColor(colorForPetSitter.get()));
                service3.setClickable(clickable.get());
                service4.setButtonTintList(ColorStateList.valueOf(getColor(colorForPetSitter.get())));
                service4.setTextColor(getColor(colorForPetSitter.get()));
                service4.setClickable(clickable.get());
                service5.setButtonTintList(ColorStateList.valueOf(getColor(colorForPetSitter.get())));
                service5.setTextColor(getColor(colorForPetSitter.get()));
                service5.setClickable(clickable.get());
            }
        });
    }
}