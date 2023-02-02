package com.application.mypetandroid.registration;

public interface RegistrationContract {

    interface RegistrationListener {
        void onFailed(String message);

        void onSuccess();
    }

    interface RegistrationView {
        void hideProgressIndicator();

        void onFailed(String message);

        void onSuccess();

        void showProgressIndicator();
    }

}
