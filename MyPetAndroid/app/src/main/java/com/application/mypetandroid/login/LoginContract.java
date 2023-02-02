package com.application.mypetandroid.login;

public interface LoginContract {

    interface LoginListener {
        void onFailed(String message);

        void onSuccess(int role);
    }

    interface LoginView {
        void hideProgressIndicator();

        void onFailed(String message);

        void onSuccess(int role);

        void showProgressIndicator();
    }

}
