package com.application.mypetandroid.services.profile;

import com.application.mypetandroid.services.profile.data.PersonalFavPetSitInteractor;
import com.application.mypetandroid.utils.singleton_examples.PetSitterResultsSingletonClass;

public class PersonalFavPetSitPresenter implements ProfileContract.LoadFavPetSitListener {

    private final PersonalFavPetSitInteractor personalFavPetSitInteractor;
    private final ProfileContract.LoadFavPetView loadFavPetView;

    public PersonalFavPetSitPresenter(ProfileContract.LoadFavPetView loadFavPetView) {
        this.loadFavPetView = loadFavPetView;
        this.personalFavPetSitInteractor = new PersonalFavPetSitInteractor(this);
    }

    public void loadFavorites(String user) {
        this.personalFavPetSitInteractor.loadFavourites(user);
    }

    public void onLoadFavoritesSuccess(PetSitterResultsSingletonClass petSitterResultsSingletonClass) {
        this.loadFavPetView.hideProgressIndicator();
        this.loadFavPetView.onLoadFavoritesSuccess(petSitterResultsSingletonClass);
    }

    public void onLoadFavoritesFailed(String message) {
        this.loadFavPetView.hideProgressIndicator();
        this.loadFavPetView.onLoadFavoritesFailed(message);
    }

}
