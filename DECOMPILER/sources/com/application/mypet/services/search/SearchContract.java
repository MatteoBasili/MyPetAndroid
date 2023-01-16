package com.application.mypet.services.search;

import com.application.mypet.services.search.data.PetSitDetailsInfo;
import com.application.mypet.services.search.data.PetSitterResultList;

public interface SearchContract {

    public interface PetSitSearchInputListener {
        void onInputSearchFailed(String str);

        void onInputSearchSuccess();
    }

    public interface PetSitSearchInputView {
        void hideProgressbar();

        void onInputSearchFailed(String str);

        void onInputSearchSuccess();

        void showProgressbar();
    }

    public interface PetSitterDetailsListener {
        void onLoadDetailsFailed(String str);

        void onLoadDetailsSuccess(PetSitDetailsInfo petSitDetailsInfo);
    }

    public interface PetSitterDetailsView {
        void hideLoadProgressbar();

        void onLoadDetailsFailed(String str);

        void onLoadDetailsSuccess(PetSitDetailsInfo petSitDetailsInfo);
    }

    public interface PetSitterSearchListener {
        void onLoadResultsFailed(String str);

        void onLoadResultsSuccess(PetSitterResultList petSitterResultList);
    }

    public interface PetSitterSearchView {
        void hideProgressbar();

        void onLoadResultsFailed(String str);

        void onLoadResultsSuccess(PetSitterResultList petSitterResultList);
    }
}
