package com.application.mypetandroid.pwd_recovery.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.application.mypetandroid.R;
import com.application.mypetandroid.pwd_recovery.PwdRecoveryContract;
import com.application.mypetandroid.pwd_recovery.PwdRecoveryPresenter;
import com.application.mypetandroid.pwd_recovery.data.PwdRecoveryCredentials;
import com.application.mypetandroid.utils.singleton_examples.KeyboardSingletonClass;

public class PwdRecoveryActivity extends AppCompatActivity implements PwdRecoveryContract.PWDRecoveryView {
    private EditText emailView;
    private EditText firstPetNameView;
    private ProgressBar progressBar;
    private Button sendButton;

    private PwdRecoveryPresenter presenter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_recovery);

        ImageView backButton = findViewById(R.id.back_arrow);
        emailView = findViewById(R.id.user_email);
        firstPetNameView = findViewById(R.id.user_pet);
        progressBar = findViewById(R.id.progressBar);
        sendButton = findViewById(R.id.send_button);

        presenter = new PwdRecoveryPresenter(this);

        // For back button
        backButton.setOnClickListener(v -> back());

        // Set up the function for send button
        sendButton.setOnClickListener(v -> sendEmail());
    }

    private void back() {
        finish();
    }

    private void sendEmail() {
        hideKeyboard();
        sendButton.setEnabled(false);

        String emailInput = emailView.getText().toString().trim();
        String petNameInput = firstPetNameView.getText().toString().trim();
        PwdRecoveryCredentials pwdRecoveryCredentials = new PwdRecoveryCredentials();
        pwdRecoveryCredentials.setEmail(emailInput);
        pwdRecoveryCredentials.setPetName(petNameInput);

        presenter.recoverPassword(pwdRecoveryCredentials);
    }

    private void hideKeyboard() {
        KeyboardSingletonClass keyboardSingletonClass = KeyboardSingletonClass.getSingletonInstance();
        keyboardSingletonClass.hide(this);
    }

    @Override
    public void showProgressIndicator() {
        this.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressIndicator() {
        this.progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onSuccess(String password) {
        presenter.sendEmail(this, emailView.getText().toString().trim(), password);
        this.sendButton.setEnabled(true);
    }

    @Override
    public void onFailed(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        this.sendButton.setEnabled(true);
    }
}
