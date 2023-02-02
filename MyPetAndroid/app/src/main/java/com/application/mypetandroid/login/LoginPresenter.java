package com.application.mypetandroid.login;

import com.application.mypetandroid.login.data.LoginCredentials;
import com.application.mypetandroid.login.data.LoginInteractor;

public class LoginPresenter implements LoginContract.LoginListener {

    private final LoginInteractor loginInteractor;
    private final LoginContract.LoginView loginView;

    public LoginPresenter(LoginContract.LoginView loginView) {
        this.loginView = loginView;
        this.loginInteractor = new LoginInteractor(this);
    }

    public void login(LoginCredentials credentials) {
        this.loginView.showProgressIndicator();
        this.loginInteractor.login(credentials);
    }

    public void onSuccess(int role) {
        this.loginView.hideProgressIndicator();
        this.loginView.onSuccess(role);
    }

    public void onFailed(String message) {
        this.loginView.hideProgressIndicator();
        this.loginView.onFailed(message);
    }

}
