package com.application.mypet.login.view;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.application.mypet.R;
import com.application.mypet.login.LoginContract;
import com.application.mypet.login.PasswordRecoveryPresenter;
import com.application.mypet.login.data.PasswordRecoveryCredentials;
import com.application.mypet.utils.email.SendMail;

public class PasswordRecoveryActivity extends AppCompatActivity implements LoginContract.PasswordRecoveryView {
    private String email;
    private ProgressBar progressBar;
    private ScrollView scrollView;
    private Button sendEmail;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_password_recovery);
        this.progressBar = (ProgressBar) findViewById(R.id.progressBar);
        PasswordRecoveryPresenter presenter = new PasswordRecoveryPresenter(this);
        this.sendEmail = (Button) findViewById(R.id.send_button);
        this.scrollView = (ScrollView) findViewById(R.id.scrollView);
        ((ImageView) findViewById(R.id.back_arrow)).setOnClickListener(new PasswordRecoveryActivity$$ExternalSyntheticLambda0(this));
        this.sendEmail.setOnClickListener(new PasswordRecoveryActivity$$ExternalSyntheticLambda1(this, (EditText) findViewById(R.id.user_email), (EditText) findViewById(R.id.user_pet), presenter));
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreate$0$com-application-mypet-login-view-PasswordRecoveryActivity  reason: not valid java name */
    public /* synthetic */ void m52lambda$onCreate$0$comapplicationmypetloginviewPasswordRecoveryActivity(View view) {
        finish();
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onCreate$1$com-application-mypet-login-view-PasswordRecoveryActivity  reason: not valid java name */
    public /* synthetic */ void m53lambda$onCreate$1$comapplicationmypetloginviewPasswordRecoveryActivity(EditText userEmail, EditText userPetName, PasswordRecoveryPresenter presenter, View view) {
        this.sendEmail.setEnabled(false);
        try {
            ((InputMethodManager) getSystemService("input_method")).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 2);
        } catch (NullPointerException e) {
            Log.e("Null Pointer Error: ", e.getMessage());
        }
        this.email = userEmail.getText().toString().trim();
        presenter.start(new PasswordRecoveryCredentials(this.email, userPetName.getText().toString().trim()));
    }

    public void showProgressbar() {
        new Handler().postDelayed(new PasswordRecoveryActivity$$ExternalSyntheticLambda2(this.scrollView), 100);
        this.progressBar.setVisibility(0);
    }

    public void hideProgressbar() {
        this.progressBar.setVisibility(8);
    }

    public void onSuccess(String password) {
        new SendMail(this, this.email, "PASSWORD RECOVERY", "Here is your password:\n\n---   " + password + "   ---\n\nDon't forget it again, or our furry friends will get angry!", "Email sent").execute(new Void[0]);
        this.sendEmail.setEnabled(true);
    }

    public void onFailed(String message) {
        Toast.makeText(this, message, 0).show();
        this.sendEmail.setEnabled(true);
    }
}
