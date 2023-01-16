package com.application.mypet.services.profile.petsitter;

import com.application.mypet.services.profile.ProfileContract;
import com.application.mypet.services.profile.petsitter.data.LoadProfileInfo;
import com.application.mypet.services.profile.petsitter.data.PetSitProfileInteractor;
import com.application.mypet.services.profile.petsitter.data.SavePhotoInfo;

public class PetSitProfilePresenter implements ProfileContract.ProfileListener {
    private PetSitProfileInteractor petSitProfileInteractor = new PetSitProfileInteractor(this);
    private ProfileContract.ProfileView profileView;

    public PetSitProfilePresenter(ProfileContract.ProfileView profileView2) {
        this.profileView = profileView2;
    }

    public void loadPhoto(String user) {
        this.petSitProfileInteractor.loadProfile(user);
    }

    public void savePhoto(SavePhotoInfo credentials) {
        this.profileView.showSaveProgressbar();
        this.petSitProfileInteractor.savePhoto(credentials);
    }

    public void onStoreSuccess() {
        this.profileView.hideSaveProgressbar();
        this.profileView.onStoreSuccess();
    }

    public void onLoadProfileSuccess(LoadProfileInfo loadProfileInfo) {
        this.profileView.hideLoadProgressbar();
        this.profileView.onLoadProfileSuccess(loadProfileInfo);
    }

    public void onStoreFailed(String message) {
        this.profileView.hideSaveProgressbar();
        this.profileView.onStoreFailed(message);
    }

    public void onLoadFailed(String message) {
        this.profileView.hideLoadProgressbar();
        this.profileView.onLoadFailed(message);
    }
}
