package com.application.mypet.login;

import com.application.mypet.login.LoginContract;
import com.application.mypet.login.data.PasswordRecoveryCredentials;
import com.application.mypet.login.data.PasswordRecoveryInteractor;

public class PasswordRecoveryPresenter implements LoginContract.PasswordRecoveryListener {
    private PasswordRecoveryInteractor passwordRecoveryInteractor = new PasswordRecoveryInteractor(this);
    private LoginContract.PasswordRecoveryView passwordRecoveryView;

    public PasswordRecoveryPresenter(LoginContract.PasswordRecoveryView passwordRecoveryView2) {
        this.passwordRecoveryView = passwordRecoveryView2;
    }

    public void start(PasswordRecoveryCredentials credentials) {
        this.passwordRecoveryView.showProgressbar();
        if (this.passwordRecoveryInteractor.isValidInput(credentials)) {
            this.passwordRecoveryInteractor.recoverPassword(credentials);
        }
    }

    public void onSuccess(String password) {
        this.passwordRecoveryView.hideProgressbar();
        this.passwordRecoveryView.onSuccess(password);
    }

    public void onFailed(String message) {
        this.passwordRecoveryView.hideProgressbar();
        this.passwordRecoveryView.onFailed(message);
    }
}
