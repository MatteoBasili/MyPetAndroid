package com.application.mypet.login;

import com.application.mypet.login.LoginContract;
import com.application.mypet.login.data.LoginCredentials;
import com.application.mypet.login.data.LoginInteractor;

public class LoginPresenter implements LoginContract.LoginListener {
    private LoginInteractor loginInteractor = new LoginInteractor(this);
    private LoginContract.LoginView loginView;

    public LoginPresenter(LoginContract.LoginView loginView2) {
        this.loginView = loginView2;
    }

    public void start(LoginCredentials credentials) {
        this.loginView.showProgressbar();
        if (this.loginInteractor.isValidInput(credentials)) {
            this.loginInteractor.login(credentials);
        }
    }

    public void onSuccess(int role) {
        this.loginView.hideProgressbar();
        this.loginView.onSuccess(role);
    }

    public void onFailed(String message) {
        this.loginView.hideProgressbar();
        this.loginView.onFailed(message);
    }
}
