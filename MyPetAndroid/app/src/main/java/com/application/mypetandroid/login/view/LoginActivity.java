package com.application.mypetandroid.login.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.application.mypetandroid.R;
import com.application.mypetandroid.login.LoginContract;
import com.application.mypetandroid.login.LoginPresenter;
import com.application.mypetandroid.login.data.LoginCredentials;
import com.application.mypetandroid.pwd_recovery.view.PwdRecoveryActivity;
import com.application.mypetandroid.registration.view.RegistrationActivity;
import com.application.mypetandroid.services.HomeActivity;
import com.application.mypetandroid.utils.singleton_examples.KeyboardSingletonClass;
import com.application.mypetandroid.utils.singleton_examples.UserSingletonClass;

public class LoginActivity extends AppCompatActivity implements LoginContract.LoginView {

    private static final String KEY = "Password";

    private EditText usernameView;
    private EditText passwordView;
    private Button loginButton;
    private ProgressBar progressBar;
    private CheckBox saveLoginCheckBox;
    private ImageView showPwdView;
    private ImageView hidePwdView;
    private LoginPresenter presenter;

    private SharedPreferences.Editor loginPrefsEditor;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameView = findViewById(R.id.username);
        passwordView = findViewById(R.id.pwd);
        loginButton = findViewById(R.id.enter_button);
        progressBar = findViewById(R.id.progressBar);
        TextView recoverPwdText = findViewById(R.id.forgot_pwd);
        RelativeLayout googleLogin = findViewById(R.id.googleLayout);
        RelativeLayout facebookLogin = findViewById(R.id.facebookLayout);
        saveLoginCheckBox = findViewById(R.id.rem_me);
        showPwdView = findViewById(R.id.show_pwd);
        hidePwdView = findViewById(R.id.hide_pwd);
        Button signIn = findViewById(R.id.sign_in_button);

        // For remember me function
        SharedPreferences loginPreferences = getSharedPreferences("loginPrefs", 0);
        loginPrefsEditor = loginPreferences.edit();
        if (loginPreferences.getBoolean("RememberMe", false)) {
            usernameView.setText(loginPreferences.getString("Username", ""));
            passwordView.setText(loginPreferences.getString(KEY, ""));
            this.saveLoginCheckBox.setChecked(true);
        }

        presenter = new LoginPresenter(this);

        // Show/hide password
        showPwdView.setOnClickListener(v -> showPassword());
        hidePwdView.setOnClickListener(v -> hidePassword());

        // For social login
        googleLogin.setOnClickListener(v -> handleLoginWithSocial());
        facebookLogin.setOnClickListener(v -> handleLoginWithSocial());

        // For forgot pwd
        recoverPwdText.setOnClickListener(v -> showPasswordRecoveryPage());

        // Set up function for login button
        loginButton.setOnClickListener(v -> login());

        // For sign in button
        signIn.setOnClickListener(v -> showRegistrationPage());
    }

    private void showPassword() {
        passwordView.setTransformationMethod(new HideReturnsTransformationMethod());
        passwordView.setSelection(passwordView.getText().length());
        hidePwdView.setVisibility(View.VISIBLE);
        showPwdView.setVisibility(View.GONE);
    }

    private void hidePassword() {
        passwordView.setTransformationMethod(new PasswordTransformationMethod());
        passwordView.setSelection(passwordView.getText().length());
        showPwdView.setVisibility(View.VISIBLE);
        hidePwdView.setVisibility(View.GONE);
    }

    private void handleLoginWithSocial() {
        Toast.makeText(this, "Sorry, the service is currently unavailable", Toast.LENGTH_SHORT).show();
    }

    private void showPasswordRecoveryPage() {
        startActivity(new Intent(this, PwdRecoveryActivity.class));
    }

    private void login() {
        hideKeyboard();
        loginButton.setEnabled(false);

        String usernameInput = usernameView.getText().toString().trim();
        String passwordInput = passwordView.getText().toString();
        LoginCredentials loginCredentials = new LoginCredentials();
        loginCredentials.setUsername(usernameInput);
        loginCredentials.setPassword(passwordInput);

        presenter.login(loginCredentials);
    }

    private void hideKeyboard() {
        KeyboardSingletonClass keyboardSingletonClass = KeyboardSingletonClass.getSingletonInstance();
        keyboardSingletonClass.hide(this);
    }

    private void showRegistrationPage() {
        startActivity(new Intent(this, RegistrationActivity.class));
    }

    @Override
    public void hideProgressIndicator() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onFailed(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        loginButton.setEnabled(true);
    }

    @Override
    public void onSuccess(int role) {
        if (saveLoginCheckBox.isChecked()) {
            rememberLogin();
        } else {
            this.loginPrefsEditor.clear();
            this.loginPrefsEditor.commit();
        }

        // Set user singleton class
        UserSingletonClass userSingletonClass = UserSingletonClass.getSingletonInstance();
        userSingletonClass.setUsername(usernameView.getText().toString().trim());
        userSingletonClass.setRole(role);

        Intent i = new Intent(this, HomeActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void showProgressIndicator() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void rememberLogin() {
        loginPrefsEditor.putBoolean("RememberMe", true);
        loginPrefsEditor.putString("Username", usernameView.getText().toString().trim());
        loginPrefsEditor.putString(KEY, passwordView.getText().toString());
        loginPrefsEditor.commit();
    }

}
