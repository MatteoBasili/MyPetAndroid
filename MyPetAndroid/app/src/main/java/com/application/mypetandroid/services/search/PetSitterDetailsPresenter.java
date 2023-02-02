package com.application.mypetandroid.services.search;

import com.application.mypetandroid.services.search.data.PetSitterDetails;
import com.application.mypetandroid.services.search.data.PetSitterDetailsInteractor;

public class PetSitterDetailsPresenter implements PetSitterSearchContract.PetSitterDetailsListener {

    private final PetSitterDetailsInteractor petSitterDetailsInteractor;
    private final PetSitterSearchContract.PetSitterDetailsView petSitterDetailsView;


    public PetSitterDetailsPresenter(PetSitterSearchContract.PetSitterDetailsView petSitterDetailsView) {
        this.petSitterDetailsView = petSitterDetailsView;
        this.petSitterDetailsInteractor = new PetSitterDetailsInteractor(this);
    }

    public void loadPetSitterDetails(String user, String petSitter) {
        this.petSitterDetailsInteractor.loadDetails(user, petSitter);
    }

    public void onLoadDetailsSuccess(PetSitterDetails petSitterDetails) {
        this.petSitterDetailsView.hideProgressIndicator();
        this.petSitterDetailsView.onLoadDetailsSuccess(petSitterDetails);
    }

    public void onLoadDetailsFailed(String message) {
        this.petSitterDetailsView.hideProgressIndicator();
        this.petSitterDetailsView.onLoadDetailsFailed(message);
    }

}
