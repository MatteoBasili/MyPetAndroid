package com.application.mypet.services.search;

import com.application.mypet.services.search.RatingContract;
import com.application.mypet.services.search.SearchContract;
import com.application.mypet.services.search.data.PetSitDetailsInfo;
import com.application.mypet.services.search.data.PetSitSearchFilters;
import com.application.mypet.services.search.data.PetSitSearchInteractor;
import com.application.mypet.services.search.data.PetSitterResultList;

public class PetSitSearchPresenter implements SearchContract.PetSitSearchInputListener, SearchContract.PetSitterSearchListener, SearchContract.PetSitterDetailsListener, RatingContract.RatePetSitListener {
    private SearchContract.PetSitSearchInputView petSitSearchInputView;
    private final PetSitSearchInteractor petSitSearchInteractor;
    private SearchContract.PetSitterDetailsView petSitterDetailsView;
    private SearchContract.PetSitterSearchView petSitterSearchView;
    private RatingContract.RatePetSitView ratePetSitView;

    public PetSitSearchPresenter(SearchContract.PetSitSearchInputView petSitSearchInputView2) {
        this.petSitSearchInputView = petSitSearchInputView2;
        this.petSitSearchInteractor = new PetSitSearchInteractor((SearchContract.PetSitSearchInputListener) this);
    }

    public PetSitSearchPresenter(SearchContract.PetSitterSearchView petSitterSearchView2) {
        this.petSitterSearchView = petSitterSearchView2;
        this.petSitSearchInteractor = new PetSitSearchInteractor((SearchContract.PetSitterSearchListener) this);
    }

    public PetSitSearchPresenter(SearchContract.PetSitterDetailsView petSitterDetailsView2) {
        this.petSitterDetailsView = petSitterDetailsView2;
        this.petSitSearchInteractor = new PetSitSearchInteractor((SearchContract.PetSitterDetailsListener) this);
    }

    public PetSitSearchPresenter(RatingContract.RatePetSitView ratePetSitView2) {
        this.ratePetSitView = ratePetSitView2;
        this.petSitSearchInteractor = new PetSitSearchInteractor((RatingContract.RatePetSitListener) this);
    }

    public void isValidInput(PetSitSearchFilters petSitSearchFilters) {
        this.petSitSearchInputView.showProgressbar();
        this.petSitSearchInteractor.isValidInput(petSitSearchFilters);
    }

    public void loadResults(String user, PetSitSearchFilters petSitSearchFilters) {
        this.petSitSearchInteractor.loadResults(user, petSitSearchFilters);
    }

    public void loadDetails(String user, String petSitter) {
        this.petSitSearchInteractor.loadDetails(user, petSitter);
    }

    public void ratePetSitter(String user, String petSitter, Enum<PetSitSearchInteractor.Rating> rating) {
        this.petSitSearchInteractor.ratePetSitter(user, petSitter, rating);
    }

    public void onInputSearchSuccess() {
        this.petSitSearchInputView.hideProgressbar();
        this.petSitSearchInputView.onInputSearchSuccess();
    }

    public void onInputSearchFailed(String message) {
        this.petSitSearchInputView.hideProgressbar();
        this.petSitSearchInputView.onInputSearchFailed(message);
    }

    public void onLoadResultsSuccess(PetSitterResultList petSitterResultList) {
        this.petSitterSearchView.hideProgressbar();
        this.petSitterSearchView.onLoadResultsSuccess(petSitterResultList);
    }

    public void onLoadResultsFailed(String message) {
        this.petSitterSearchView.hideProgressbar();
        this.petSitterSearchView.onLoadResultsFailed(message);
    }

    public void onLoadDetailsSuccess(PetSitDetailsInfo petSitDetailsInfo) {
        this.petSitterDetailsView.hideLoadProgressbar();
        this.petSitterDetailsView.onLoadDetailsSuccess(petSitDetailsInfo);
    }

    public void onLoadDetailsFailed(String message) {
        this.petSitterDetailsView.hideLoadProgressbar();
        this.petSitterDetailsView.onLoadDetailsFailed(message);
    }

    public void onRateSuccess() {
        this.ratePetSitView.onRateSuccess();
    }

    public void onRateFailed(String message) {
        this.ratePetSitView.onRateFailed(message);
    }
}
