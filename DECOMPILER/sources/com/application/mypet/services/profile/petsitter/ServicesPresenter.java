package com.application.mypet.services.profile.petsitter;

import com.application.mypet.registration.data.PetSitterServicesCredentials;
import com.application.mypet.services.profile.ProfileContract;
import com.application.mypet.services.profile.petsitter.data.ServicesInteractor;

public class ServicesPresenter implements ProfileContract.ServicesListener {
    private ServicesInteractor servicesInteractor = new ServicesInteractor(this);
    private ProfileContract.ServicesView servicesView;

    public ServicesPresenter(ProfileContract.ServicesView servicesView2) {
        this.servicesView = servicesView2;
    }

    public void loadServices(String user) {
        this.servicesInteractor.loadServices(user);
    }

    public void saveServices(String user, PetSitterServicesCredentials credentials) {
        this.servicesView.showSaveProgressbar();
        if (this.servicesInteractor.isValidInput(credentials)) {
            this.servicesInteractor.saveServices(user, credentials);
        }
    }

    public void onStoreSuccess() {
        this.servicesView.hideSaveProgressbar();
        this.servicesView.onStoreSuccess();
    }

    public void onLoadServicesSuccess(PetSitterServicesCredentials petSitterServicesCredentials) {
        this.servicesView.hideLoadProgressbar();
        this.servicesView.onLoadServicesSuccess(petSitterServicesCredentials);
    }

    public void onStoreFailed(String message) {
        this.servicesView.hideSaveProgressbar();
        this.servicesView.onStoreFailed(message);
    }

    public void onLoadFailed(String message) {
        this.servicesView.hideLoadProgressbar();
        this.servicesView.onLoadFailed(message);
    }
}
