package com.application.mypet.login.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.application.mypet.R;
import com.application.mypet.login.LoginContract;
import com.application.mypet.login.LoginPresenter;
import com.application.mypet.login.data.LoginCredentials;
import com.application.mypet.registration.view.RegistrationActivity;
import com.application.mypet.services.HomeActivity;
import com.mysql.jdbc.NonRegisteringDriver;

public class LoginActivity extends AppCompatActivity implements LoginContract.LoginView {
    private Button loginButton;
    private SharedPreferences.Editor loginPrefsEditor;
    private String password;
    private ProgressBar progressBar;
    private CheckBox saveLoginCheckBox;
    private Button signIn;
    private String username;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_login);
        this.progressBar = (ProgressBar) findViewById(R.id.progressBar);
        LoginPresenter presenter = new LoginPresenter(this);
        ImageView showPwdView = (ImageView) findViewById(R.id.show_pwd);
        ImageView hidePwdView = (ImageView) findViewById(R.id.hide_pwd);
        TextView recoverPwd = (TextView) findViewById(R.id.forgot_pwd);
        this.signIn = (Button) findViewById(R.id.sign_in_button);
        EditText usernameView = (EditText) findViewById(R.id.username);
        EditText passwordView = (EditText) findViewById(R.id.pwd);
        RelativeLayout googleLayout = (RelativeLayout) findViewById(R.id.googleLayout);
        RelativeLayout facebookLayout = (RelativeLayout) findViewById(R.id.facebookLayout);
        this.loginButton = (Button) findViewById(R.id.enter_button);
        this.saveLoginCheckBox = (CheckBox) findViewById(R.id.rem_me);
        SharedPreferences loginPreferences = getSharedPreferences("loginPrefs", 0);
        this.loginPrefsEditor = loginPreferences.edit();
        if (loginPreferences.getBoolean("saveLogin", false)) {
            usernameView.setText(loginPreferences.getString("username", ""));
            passwordView.setText(loginPreferences.getString(NonRegisteringDriver.PASSWORD_PROPERTY_KEY, ""));
            this.saveLoginCheckBox.setChecked(true);
        }
        showPwdView.setOnClickListener(new LoginActivity$$ExternalSyntheticLambda0(passwordView, hidePwdView, showPwdView));
        hidePwdView.setOnClickListener(new LoginActivity$$ExternalSyntheticLambda1(passwordView, showPwdView, hidePwdView));
        recoverPwd.setOnClickListener(new LoginActivity$$ExternalSyntheticLambda2(this));
        this.signIn.setOnClickListener(new LoginActivity$$ExternalSyntheticLambda3(this));
        this.loginButton.setOnClickListener(new LoginActivity$$ExternalSyntheticLambda4(this, usernameView, passwordView, presenter));
        googleLayout.setOnClickListener(new LoginActivity$$ExternalSyntheticLambda5(this));
        facebookLayout.setOnClickListener(new LoginActivity$$ExternalSyntheticLambda6(this));
    }

    static /* synthetic */ void lambda$onCreate$0(EditText passwordView, ImageView hidePwdView, ImageView showPwdView, View view) {
        passwordView.setTransformationMethod(new HideReturnsTransformationMethod());
        passwordView.setSelection(passwordView.getText().length());
        hidePwdView.setVisibility(0);
        showPwdView.setVisibility(4);
    }

    static /* synthetic */ void lambda$onCreate$1(EditText passwordView, ImageView showPwdView, ImageView hidePwdView, View view) {
        passwordView.setTransformationMethod(new PasswordTransformationMethod());
        passwordView.setSelection(passwordView.getText().length());
        showPwdView.setVisibility(0);
        hidePwdView.setVisibility(4);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreate$2$com-application-mypet-login-view-LoginActivity  reason: not valid java name */
    public /* synthetic */ void m47lambda$onCreate$2$comapplicationmypetloginviewLoginActivity(View view) {
        startActivity(new Intent(this, PasswordRecoveryActivity.class));
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreate$3$com-application-mypet-login-view-LoginActivity  reason: not valid java name */
    public /* synthetic */ void m48lambda$onCreate$3$comapplicationmypetloginviewLoginActivity(View view) {
        startActivity(new Intent(this, RegistrationActivity.class));
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreate$4$com-application-mypet-login-view-LoginActivity  reason: not valid java name */
    public /* synthetic */ void m49lambda$onCreate$4$comapplicationmypetloginviewLoginActivity(EditText usernameView, EditText passwordView, LoginPresenter presenter, View v) {
        this.loginButton.setEnabled(false);
        this.signIn.setEnabled(false);
        try {
            ((InputMethodManager) getSystemService("input_method")).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 2);
        } catch (NullPointerException e) {
            Log.e("Null Pointer Error: ", e.getMessage());
        }
        this.username = usernameView.getText().toString().trim();
        this.password = passwordView.getText().toString();
        presenter.start(new LoginCredentials(this.username, this.password));
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreate$5$com-application-mypet-login-view-LoginActivity  reason: not valid java name */
    public /* synthetic */ void m50lambda$onCreate$5$comapplicationmypetloginviewLoginActivity(View view) {
        Toast.makeText(this, "Sorry, the service is not available...", 0).show();
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreate$6$com-application-mypet-login-view-LoginActivity  reason: not valid java name */
    public /* synthetic */ void m51lambda$onCreate$6$comapplicationmypetloginviewLoginActivity(View view) {
        Toast.makeText(this, "Sorry, the service is not available...", 0).show();
    }

    public void showProgressbar() {
        this.progressBar.setVisibility(0);
    }

    public void hideProgressbar() {
        this.progressBar.setVisibility(8);
    }

    public void onSuccess(int role) {
        if (this.saveLoginCheckBox.isChecked()) {
            this.loginPrefsEditor.putBoolean("saveLogin", true);
            this.loginPrefsEditor.putString("username", this.username);
            this.loginPrefsEditor.putString(NonRegisteringDriver.PASSWORD_PROPERTY_KEY, this.password);
            this.loginPrefsEditor.commit();
        } else {
            this.loginPrefsEditor.clear();
            this.loginPrefsEditor.commit();
        }
        Intent i = new Intent(this, HomeActivity.class);
        i.putExtra("LoggedUser", this.username);
        i.putExtra("UserRole", role);
        startActivity(i);
        finish();
    }

    public void onFailed(String message) {
        Toast.makeText(this, message, 0).show();
        this.loginButton.setEnabled(true);
        this.signIn.setEnabled(true);
    }
}
