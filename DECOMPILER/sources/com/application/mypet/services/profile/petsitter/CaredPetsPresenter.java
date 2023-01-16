package com.application.mypet.services.profile.petsitter;

import com.application.mypet.registration.data.PetSitterCaredPetsCredentials;
import com.application.mypet.services.profile.ProfileContract;
import com.application.mypet.services.profile.petsitter.data.CaredPetsInteractor;

public class CaredPetsPresenter implements ProfileContract.CaredPetsListener {
    private CaredPetsInteractor caredPetsInteractor = new CaredPetsInteractor(this);
    private ProfileContract.CaredPetsView caredPetsView;

    public CaredPetsPresenter(ProfileContract.CaredPetsView caredPetsView2) {
        this.caredPetsView = caredPetsView2;
    }

    public void loadCaredPets(String user) {
        this.caredPetsInteractor.loadCaredPets(user);
    }

    public void saveCaredPets(String user, PetSitterCaredPetsCredentials credentials) {
        this.caredPetsView.showSaveProgressbar();
        if (this.caredPetsInteractor.isValidInput(credentials)) {
            this.caredPetsInteractor.saveCaredPets(user, credentials);
        }
    }

    public void onStoreSuccess() {
        this.caredPetsView.hideSaveProgressbar();
        this.caredPetsView.onStoreSuccess();
    }

    public void onLoadCaredPetsSuccess(PetSitterCaredPetsCredentials petSitterCaredPetsCredentials) {
        this.caredPetsView.hideLoadProgressbar();
        this.caredPetsView.onLoadCaredPetsSuccess(petSitterCaredPetsCredentials);
    }

    public void onStoreFailed(String message) {
        this.caredPetsView.hideSaveProgressbar();
        this.caredPetsView.onStoreFailed(message);
    }

    public void onLoadFailed(String message) {
        this.caredPetsView.hideLoadProgressbar();
        this.caredPetsView.onLoadFailed(message);
    }
}
