package com.application.mypet.services.profile;

import com.application.mypet.registration.data.PetSitterCaredPetsCredentials;
import com.application.mypet.registration.data.PetSitterServicesCredentials;
import com.application.mypet.services.profile.data.PersonalInformations;
import com.application.mypet.services.profile.petsitter.data.LoadProfileInfo;

public interface ProfileContract {

    public interface CaredPetsListener {
        void onLoadCaredPetsSuccess(PetSitterCaredPetsCredentials petSitterCaredPetsCredentials);

        void onLoadFailed(String str);

        void onStoreFailed(String str);

        void onStoreSuccess();
    }

    public interface CaredPetsView {
        void hideLoadProgressbar();

        void hideSaveProgressbar();

        void onLoadCaredPetsSuccess(PetSitterCaredPetsCredentials petSitterCaredPetsCredentials);

        void onLoadFailed(String str);

        void onStoreFailed(String str);

        void onStoreSuccess();

        void showSaveProgressbar();
    }

    public interface PersonalInfoListener {
        void onLoadFailed(String str);

        void onLoadPersonalInfoSuccess(PersonalInformations personalInformations);

        void onStoreFailed(String str);

        void onStoreSuccess();
    }

    public interface PersonalInfoView {
        void hideLoadProgressbar();

        void hideSaveProgressbar();

        void onLoadFailed(String str);

        void onLoadPersonalInfoSuccess(PersonalInformations personalInformations);

        void onStoreFailed(String str);

        void onStoreSuccess();

        void showSaveProgressbar();
    }

    public interface ProfileListener {
        void onLoadFailed(String str);

        void onLoadProfileSuccess(LoadProfileInfo loadProfileInfo);

        void onStoreFailed(String str);

        void onStoreSuccess();
    }

    public interface ProfileView {
        void hideLoadProgressbar();

        void hideSaveProgressbar();

        void onLoadFailed(String str);

        void onLoadProfileSuccess(LoadProfileInfo loadProfileInfo);

        void onStoreFailed(String str);

        void onStoreSuccess();

        void showSaveProgressbar();
    }

    public interface ServicesListener {
        void onLoadFailed(String str);

        void onLoadServicesSuccess(PetSitterServicesCredentials petSitterServicesCredentials);

        void onStoreFailed(String str);

        void onStoreSuccess();
    }

    public interface ServicesView {
        void hideLoadProgressbar();

        void hideSaveProgressbar();

        void onLoadFailed(String str);

        void onLoadServicesSuccess(PetSitterServicesCredentials petSitterServicesCredentials);

        void onStoreFailed(String str);

        void onStoreSuccess();

        void showSaveProgressbar();
    }
}
