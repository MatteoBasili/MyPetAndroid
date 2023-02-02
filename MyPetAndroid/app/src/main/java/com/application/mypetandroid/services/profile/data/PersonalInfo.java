package com.application.mypetandroid.services.profile.data;

import com.application.mypetandroid.registration.data.ProfileUserData;

public class PersonalInfo {

    private ProfileUserData profileUserData;
    private String email;
    private String firstPetName;

    public ProfileUserData getProfileUserData() {
        return profileUserData;
    }

    public void setProfileUserData(ProfileUserData profileUserData) {
        this.profileUserData = profileUserData;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstPetName() {
        return firstPetName;
    }

    public void setFirstPetName(String firstPetName) {
        this.firstPetName = firstPetName;
    }
}
