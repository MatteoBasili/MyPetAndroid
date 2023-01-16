package com.application.mypet.registration;

public interface RegistrationContract {

    public interface RegistrationListener {
        void onFailed(String str);

        void onSuccess();
    }

    public interface RegistrationView {
        void hideProgressbar();

        void onFailed(String str);

        void onSuccess();

        void showProgressbar();
    }
}
