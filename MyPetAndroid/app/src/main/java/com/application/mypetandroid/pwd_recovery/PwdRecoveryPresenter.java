package com.application.mypetandroid.pwd_recovery;

import android.content.Context;

import com.application.mypetandroid.pwd_recovery.data.PwdRecoveryCredentials;
import com.application.mypetandroid.pwd_recovery.data.PwdRecoveryInteractor;

public class PwdRecoveryPresenter implements PwdRecoveryContract.PWDRecoveryListener {

    private final PwdRecoveryInteractor pwdRecoveryInteractor;
    private final PwdRecoveryContract.PWDRecoveryView pwdRecoveryView;


    public PwdRecoveryPresenter(PwdRecoveryContract.PWDRecoveryView pwdRecoveryView) {
        this.pwdRecoveryView = pwdRecoveryView;
        this.pwdRecoveryInteractor = new PwdRecoveryInteractor(this);
    }

    public void recoverPassword(PwdRecoveryCredentials credentials) {
        this.pwdRecoveryView.showProgressIndicator();
        this.pwdRecoveryInteractor.recoverPassword(credentials);
    }

    public void sendEmail(Context context, String email, String password) {
        this.pwdRecoveryInteractor.sendEmail(context, email, password);
    }

    public void onSuccess(String password) {
        this.pwdRecoveryView.hideProgressIndicator();
        this.pwdRecoveryView.onSuccess(password);
    }

    public void onFailed(String message) {
        this.pwdRecoveryView.hideProgressIndicator();
        this.pwdRecoveryView.onFailed(message);
    }

}
