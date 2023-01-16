package com.application.mypet.services.search;

public interface RatingContract {

    public interface RatePetSitListener {
        void onRateFailed(String str);

        void onRateSuccess();
    }

    public interface RatePetSitView {
        void onRateFailed(String str);

        void onRateSuccess();
    }
}
