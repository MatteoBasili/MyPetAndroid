package com.application.mypet.services.profile;

import com.application.mypet.services.profile.ProfileContract;
import com.application.mypet.services.profile.data.PersonalInfoInteractor;
import com.application.mypet.services.profile.data.PersonalInformations;

public class PersonalInfoPresenter implements ProfileContract.PersonalInfoListener {
    private PersonalInfoInteractor personalInfoInteractor = new PersonalInfoInteractor(this);
    private ProfileContract.PersonalInfoView personalInfoView;

    public PersonalInfoPresenter(ProfileContract.PersonalInfoView personalInfoView2) {
        this.personalInfoView = personalInfoView2;
    }

    public void loadInfo(String user) {
        this.personalInfoInteractor.loadInfo(user);
    }

    public void saveInfo(String user, PersonalInformations informations) {
        this.personalInfoView.showSaveProgressbar();
        if (this.personalInfoInteractor.isValidInput(informations)) {
            this.personalInfoInteractor.saveInfo(user, informations);
        }
    }

    public void onStoreSuccess() {
        this.personalInfoView.hideSaveProgressbar();
        this.personalInfoView.onStoreSuccess();
    }

    public void onLoadPersonalInfoSuccess(PersonalInformations personalInformations) {
        this.personalInfoView.hideLoadProgressbar();
        this.personalInfoView.onLoadPersonalInfoSuccess(personalInformations);
    }

    public void onStoreFailed(String message) {
        this.personalInfoView.hideSaveProgressbar();
        this.personalInfoView.onStoreFailed(message);
    }

    public void onLoadFailed(String message) {
        this.personalInfoView.hideLoadProgressbar();
        this.personalInfoView.onLoadFailed(message);
    }
}
