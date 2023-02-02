package com.application.mypetandroid.services.search;

import com.application.mypetandroid.services.search.data.PetSitterRatingInteractor;

public class PetSitterRatingPresenter implements PetSitterSearchContract.PetSitterRatingListener {

    private final PetSitterRatingInteractor petSitterRatingInteractor;
    private final PetSitterSearchContract.PetSitterRatingView petSitterRatingView;


    public PetSitterRatingPresenter(PetSitterSearchContract.PetSitterRatingView petSitterRatingView) {
        this.petSitterRatingView = petSitterRatingView;
        this.petSitterRatingInteractor = new PetSitterRatingInteractor(this);
    }

    public void ratePetSitter(String user, String petSitter, int vote) {
        this.petSitterRatingInteractor.ratePetSitter(user, petSitter, vote);
    }

    public void onRateSuccess() {
        this.petSitterRatingView.onRateSuccess();
    }

    public void onRateFailed(String message) {
        this.petSitterRatingView.onRateFailed(message);
    }

}
