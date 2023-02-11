package com.application.mypetandroid.services.profile;

import com.application.mypetandroid.registration.data.PetSitCaredPets;
import com.application.mypetandroid.services.profile.data.CaredPetsInteractor;

public class CaredPetsPresenter implements ProfileContract.CaredPetsListener {

    private final CaredPetsInteractor caredPetsInteractor;
    private final ProfileContract.CaredPetsView caredPetsView;


    public CaredPetsPresenter(ProfileContract.CaredPetsView caredPetsView) {
        this.caredPetsView = caredPetsView;
        this.caredPetsInteractor = new CaredPetsInteractor(this);
    }

    public void loadCaredPets(String user) {
        this.caredPetsInteractor.loadCaredPets(user);
    }

    public void saveCaredPets(String user, PetSitCaredPets petSitCaredPets) {
        this.caredPetsView.showSaveProgressIndicator();
        this.caredPetsInteractor.saveCaredPets(user, petSitCaredPets);
    }

    public void onLoadPetsSuccess(PetSitCaredPets petSitCaredPets) {
        this.caredPetsView.hideLoadProgressIndicator();
        this.caredPetsView.onLoadPetsSuccess(petSitCaredPets);
    }

    public void onLoadPetsFailed(String message) {
        this.caredPetsView.hideLoadProgressIndicator();
        this.caredPetsView.onLoadPetsFailed(message);
    }

    @Override
    public void onStorePetsFailed(String message) {
        this.caredPetsView.hideSaveProgressIndicator();
        this.caredPetsView.onStorePetsFailed(message);
    }

    @Override
    public void onStorePetsSuccess() {
        this.caredPetsView.hideSaveProgressIndicator();
        this.caredPetsView.onStorePetsSuccess();
    }

}
