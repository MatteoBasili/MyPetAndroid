package com.application.mypetandroid.registration;


import android.content.Context;

import com.application.mypetandroid.registration.data.RegistrationCredentials;
import com.application.mypetandroid.registration.data.RegistrationInteractor;

public class RegistrationPresenter implements RegistrationContract.RegistrationListener {

    private final RegistrationInteractor registrationInteractor;
    private final RegistrationContract.RegistrationView registrationView;


    public RegistrationPresenter(RegistrationContract.RegistrationView registrationView) {
        this.registrationView = registrationView;
        this.registrationInteractor = new RegistrationInteractor(this);
    }

    public void registerUser(RegistrationCredentials credentials) {
        this.registrationView.showProgressIndicator();
        this.registrationInteractor.registerAccount(credentials);
    }

    public void sendEmail(Context context, String username, String email) {
        this.registrationInteractor.sendEmail(context, username, email);
    }

    public void onSuccess(){
        this.registrationView.hideProgressIndicator();
        this.registrationView.onSuccess();
    }

    public void onFailed(String message) {
        this.registrationView.hideProgressIndicator();
        this.registrationView.onFailed(message);
    }

}
