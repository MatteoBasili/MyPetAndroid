package com.application.mypet.services.search;

import com.application.mypet.services.search.FavoritesContract;
import com.application.mypet.services.search.data.FavoritesInteractor;
import com.application.mypet.services.search.data.PetSitterResultList;

public class FavoritesPresenter implements FavoritesContract.AddPetSitToFavListener, FavoritesContract.LoadPetSitFavListener {
    private FavoritesContract.AddPetSitToFavView addPetSitToFavView;
    private FavoritesInteractor favoritesInteractor;
    private FavoritesContract.LoadPetSitFavView loadPetSitFavView;

    public FavoritesPresenter(FavoritesContract.AddPetSitToFavView addPetSitToFavView2) {
        this.addPetSitToFavView = addPetSitToFavView2;
        this.favoritesInteractor = new FavoritesInteractor((FavoritesContract.AddPetSitToFavListener) this);
    }

    public FavoritesPresenter(FavoritesContract.LoadPetSitFavView loadPetSitFavView2) {
        this.loadPetSitFavView = loadPetSitFavView2;
        this.favoritesInteractor = new FavoritesInteractor((FavoritesContract.LoadPetSitFavListener) this);
    }

    public void addToFav(String user, String petSitter, int pos) {
        this.favoritesInteractor.addToFavorites(user, petSitter, pos);
    }

    public void loadFav(String user) {
        this.favoritesInteractor.loadFavorites(user);
    }

    public void onAddToFavSuccess(String message, int pos) {
        this.addPetSitToFavView.onAddToFavSuccess(message, pos);
    }

    public void onAddToFavFailed(String message) {
        this.addPetSitToFavView.onAddToFavFailed(message);
    }

    public void onLoadFavSuccess(PetSitterResultList petSitterResultList) {
        this.loadPetSitFavView.hideLoadProgressbar();
        this.loadPetSitFavView.onLoadFavSuccess(petSitterResultList);
    }

    public void onLoadFavFailed(String message) {
        this.loadPetSitFavView.hideLoadProgressbar();
        this.loadPetSitFavView.onLoadFavFailed(message);
    }
}
