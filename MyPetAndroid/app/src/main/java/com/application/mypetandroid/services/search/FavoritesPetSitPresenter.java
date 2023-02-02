package com.application.mypetandroid.services.search;

import android.widget.ImageView;

import com.application.mypetandroid.services.search.data.FavoritesPetSitInteractor;

public class FavoritesPetSitPresenter implements PetSitterSearchContract.PetSitterFavoritesListener {

    private final FavoritesPetSitInteractor favoritesPetSitInteractor;
    private final PetSitterSearchContract.PetSitterFavoritesView petSitterFavoritesView;

    public FavoritesPetSitPresenter(PetSitterSearchContract.PetSitterFavoritesView petSitterFavoritesView) {
        this.petSitterFavoritesView = petSitterFavoritesView;
        this.favoritesPetSitInteractor = new FavoritesPetSitInteractor(this);
    }

    public void setFavorite(String user, String petSitter, int position, ImageView favIcon, ImageView noFavIcon) {
        this.favoritesPetSitInteractor.setPetSitterToFavorites(user, petSitter, position, favIcon, noFavIcon);
    }

    public void onSetFavoriteSuccess(int position, ImageView favIcon, ImageView noFavIcon) {
        this.petSitterFavoritesView.onSetFavoriteSuccess(position, favIcon, noFavIcon);
    }

    public void onSetFavoriteFailed(String message) {
        this.petSitterFavoritesView.onSetFavoriteFailed(message);
    }

}
