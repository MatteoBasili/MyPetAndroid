package com.application.mypetandroid.services.search;

import com.application.mypetandroid.services.search.data.PetSitSearchFilters;
import com.application.mypetandroid.services.search.data.PetSitterSearchInteractor;

public class PetSitterSearchPresenter implements PetSitterSearchContract.PetSitterSearchListener {

    private final PetSitterSearchInteractor petSitterSearchInteractor;
    private final PetSitterSearchContract.PetSitterSearchView petSitterSearchView;


    public PetSitterSearchPresenter(PetSitterSearchContract.PetSitterSearchView petSitterSearchView) {
        this.petSitterSearchView = petSitterSearchView;
        this.petSitterSearchInteractor = new PetSitterSearchInteractor(this);
    }

    public void findPetSitters(String user, PetSitSearchFilters filters) {
        this.petSitterSearchView.showProgressIndicator();
        this.petSitterSearchInteractor.findPetSitters(user, filters);
    }

    public void onFindResultsSuccess() {
        this.petSitterSearchView.hideProgressIndicator();
        this.petSitterSearchView.onFindResultsSuccess();
    }

    public void onFindResultsFailed(String message) {
        this.petSitterSearchView.hideProgressIndicator();
        this.petSitterSearchView.onFindResultsFailed(message);
    }

}
