package com.application.mypetandroid.services.search;

import android.widget.ImageView;

import com.application.mypetandroid.services.search.data.PetSitterDetails;

public interface PetSitterSearchContract {

    interface PetSitterSearchListener {

        void onFindResultsFailed(String message);

        void onFindResultsSuccess();
    }

    interface PetSitterSearchView {

        void hideProgressIndicator();

        void onFindResultsFailed(String message);

        void onFindResultsSuccess();

        void showProgressIndicator();
    }

    interface PetSitterFavoritesListener {

        void onSetFavoriteFailed(String message);

        void onSetFavoriteSuccess(int position, ImageView favIcon, ImageView noFavIcon);
    }

    interface PetSitterFavoritesView {

        void onSetFavoriteFailed(String message);

        void onSetFavoriteSuccess(int position, ImageView favIcon, ImageView noFavIcon);

    }

    interface PetSitterRatingListener {

        void onRateFailed(String message);

        void onRateSuccess();
    }

    interface PetSitterRatingView {

        void onRateFailed(String message);

        void onRateSuccess();

    }

    interface PetSitterDetailsListener {

        void onLoadDetailsFailed(String message);

        void onLoadDetailsSuccess(PetSitterDetails petSitterDetails);
    }

    interface PetSitterDetailsView {

        void hideProgressIndicator();

        void onLoadDetailsFailed(String message);

        void onLoadDetailsSuccess(PetSitterDetails petSitterDetails);

    }

}
