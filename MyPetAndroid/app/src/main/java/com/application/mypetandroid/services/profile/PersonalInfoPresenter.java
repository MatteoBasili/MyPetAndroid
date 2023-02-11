package com.application.mypetandroid.services.profile;

import com.application.mypetandroid.services.profile.data.PersonalInfo;
import com.application.mypetandroid.services.profile.data.PersonalInfoInteractor;

public class PersonalInfoPresenter implements ProfileContract.PersonalInfoListener {

    private final PersonalInfoInteractor personalInfoInteractor;
    private final ProfileContract.PersonalInfoView personalInfoView;


    public PersonalInfoPresenter(ProfileContract.PersonalInfoView personalInfoView) {
        this.personalInfoView = personalInfoView;
        this.personalInfoInteractor = new PersonalInfoInteractor(this);
    }

    public void loadInfo(String user) {
        this.personalInfoInteractor.loadPersonalInfo(user);
    }

    public void saveInfo(String user, PersonalInfo personalInfo) {
        this.personalInfoView.showSaveProgressIndicator();
        this.personalInfoInteractor.savePersonalInfo(user, personalInfo);
    }

    public void onLoadInfoSuccess(PersonalInfo personalInfo) {
        this.personalInfoView.hideLoadProgressIndicator();
        this.personalInfoView.onLoadInfoSuccess(personalInfo);
    }

    public void onLoadInfoFailed(String message) {
        this.personalInfoView.hideLoadProgressIndicator();
        this.personalInfoView.onLoadInfoFailed(message);
    }

    @Override
    public void onStoreInfoFailed(String message) {
        this.personalInfoView.hideSaveProgressIndicator();
        this.personalInfoView.onStoreInfoFailed(message);
    }

    @Override
    public void onStoreInfoSuccess() {
        this.personalInfoView.hideSaveProgressIndicator();
        this.personalInfoView.onStoreInfoSuccess();
    }

}
