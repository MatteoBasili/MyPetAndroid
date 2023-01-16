package com.application.mypet.services.search;

import com.application.mypet.services.search.data.PetSitterResultList;

public interface FavoritesContract {

    public interface AddPetSitToFavListener {
        void onAddToFavFailed(String str);

        void onAddToFavSuccess(String str, int i);
    }

    public interface AddPetSitToFavView {
        void onAddToFavFailed(String str);

        void onAddToFavSuccess(String str, int i);
    }

    public interface LoadPetSitFavListener {
        void onLoadFavFailed(String str);

        void onLoadFavSuccess(PetSitterResultList petSitterResultList);
    }

    public interface LoadPetSitFavView {
        void hideLoadProgressbar();

        void onLoadFavFailed(String str);

        void onLoadFavSuccess(PetSitterResultList petSitterResultList);
    }
}
