package com.application.mypet.login;

public interface LoginContract {

    public interface LoginListener {
        void onFailed(String str);

        void onSuccess(int i);
    }

    public interface LoginView {
        void hideProgressbar();

        void onFailed(String str);

        void onSuccess(int i);

        void showProgressbar();
    }

    public interface PasswordRecoveryListener {
        void onFailed(String str);

        void onSuccess(String str);
    }

    public interface PasswordRecoveryView {
        void hideProgressbar();

        void onFailed(String str);

        void onSuccess(String str);

        void showProgressbar();
    }
}
