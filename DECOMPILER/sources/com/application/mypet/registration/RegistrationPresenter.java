package com.application.mypet.registration;

import com.application.mypet.registration.RegistrationContract;
import com.application.mypet.registration.data.RegistrationCredentials;
import com.application.mypet.registration.data.RegistrationInteractor;

public class RegistrationPresenter implements RegistrationContract.RegistrationListener {
    private final RegistrationInteractor registrationInteractor = new RegistrationInteractor(this);
    private final RegistrationContract.RegistrationView registrationView;

    public RegistrationPresenter(RegistrationContract.RegistrationView registrationView2) {
        this.registrationView = registrationView2;
    }

    public void start(RegistrationCredentials credentials) {
        this.registrationView.showProgressbar();
        if (this.registrationInteractor.isValidInput(credentials)) {
            this.registrationInteractor.registerAccount(credentials);
        }
    }

    public void onSuccess() {
        this.registrationView.hideProgressbar();
        this.registrationView.onSuccess();
    }

    public void onFailed(String message) {
        this.registrationView.hideProgressbar();
        this.registrationView.onFailed(message);
    }
}
