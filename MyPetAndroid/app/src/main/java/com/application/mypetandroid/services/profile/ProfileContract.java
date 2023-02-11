package com.application.mypetandroid.services.profile;

import com.application.mypetandroid.registration.data.PetSitCaredPets;
import com.application.mypetandroid.registration.data.PetSitServices;
import com.application.mypetandroid.services.profile.data.PersonalInfo;
import com.application.mypetandroid.services.profile.data.PetSitProfileInfo;
import com.application.mypetandroid.utils.singleton_examples.PetSitterResultsSingletonClass;

public interface ProfileContract {

    interface PetSitProfileListener {
        void onLoadProfileFailed(String message);

        void onLoadProfileSuccess(PetSitProfileInfo petSitProfileInfo);

        void onStorePhotoFailed(String message);

        void onStorePhotoSuccess();
    }

    interface PetSitProfileView {
        void hideLoadProgressIndicator();

        void hideSaveProgressIndicator();

        void onLoadProfileFailed(String message);

        void onLoadProfileSuccess(PetSitProfileInfo petSitProfileInfo);

        void onStorePhotoFailed(String message);

        void onStorePhotoSuccess();

        void showSaveProgressIndicator();
    }

    interface PersonalInfoListener {
        void onLoadInfoFailed(String message);

        void onLoadInfoSuccess(PersonalInfo personalInfo);

        void onStoreInfoFailed(String message);

        void onStoreInfoSuccess();
    }

    interface PersonalInfoView {
        void hideLoadProgressIndicator();
        void hideSaveProgressIndicator();

        void onLoadInfoFailed(String message);

        void onLoadInfoSuccess(PersonalInfo personalInfo);

        void onStoreInfoFailed(String message);

        void onStoreInfoSuccess();

        void showSaveProgressIndicator();
    }

    interface CaredPetsListener {
        void onLoadPetsFailed(String message);

        void onLoadPetsSuccess(PetSitCaredPets petSitCaredPets);

        void onStorePetsFailed(String message);

        void onStorePetsSuccess();
    }

    interface CaredPetsView {
        void hideLoadProgressIndicator();
        void hideSaveProgressIndicator();

        void onLoadPetsFailed(String message);

        void onLoadPetsSuccess(PetSitCaredPets petSitCaredPets);

        void onStorePetsFailed(String message);

        void onStorePetsSuccess();

        void showSaveProgressIndicator();
    }

    interface ServicesListener {
        void onLoadServicesFailed(String message);

        void onLoadServicesSuccess(PetSitServices petSitServices);

        void onStoreServicesFailed(String message);

        void onStoreServicesSuccess();
    }

    interface ServicesView {
        void hideLoadProgressIndicator();
        void hideSaveProgressIndicator();

        void onLoadServicesFailed(String message);

        void onLoadServicesSuccess(PetSitServices petSitServices);

        void onStoreServicesFailed(String message);

        void onStoreServicesSuccess();

        void showSaveProgressIndicator();
    }

    interface LoadFavPetSitListener {

        void onLoadFavoritesFailed(String message);

        void onLoadFavoritesSuccess(PetSitterResultsSingletonClass petSitterResultsSingletonClass);

    }

    interface LoadFavPetView {

        void hideProgressIndicator();

        void onLoadFavoritesFailed(String message);

        void onLoadFavoritesSuccess(PetSitterResultsSingletonClass petSitterResultsSingletonClass);

    }

}
