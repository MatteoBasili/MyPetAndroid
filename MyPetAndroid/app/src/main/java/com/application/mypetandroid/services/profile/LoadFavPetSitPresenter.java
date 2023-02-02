package com.application.mypetandroid.services.profile;

import com.application.mypetandroid.services.profile.data.LoadFavPetSitInteractor;
import com.application.mypetandroid.utils.singleton_examples.PetSitterResultsSingletonClass;

public class LoadFavPetSitPresenter implements ProfileContract.LoadFavPetSitListener {

    private final LoadFavPetSitInteractor loadFavPetSitInteractor;
    private final ProfileContract.LoadFavPetView loadFavPetView;

    public LoadFavPetSitPresenter(ProfileContract.LoadFavPetView loadFavPetView) {
        this.loadFavPetView = loadFavPetView;
        this.loadFavPetSitInteractor = new LoadFavPetSitInteractor(this);
    }

    public void loadFavorites(String user) {
        this.loadFavPetSitInteractor.loadFavourites(user);
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
