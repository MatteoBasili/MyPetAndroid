package com.application.mypet.services.profile.data;

import com.application.mypet.registration.data.ProfileCredentials;

public class PersonalInformations {
    private String email;
    private String firstPetName;
    private ProfileCredentials profileCredentials;

    public PersonalInformations(ProfileCredentials profileCredentials2, String email2, String firstPetName2) {
        this.profileCredentials = profileCredentials2;
        this.email = email2;
        this.firstPetName = firstPetName2;
    }

    public ProfileCredentials getProfileCredentials() {
        return this.profileCredentials;
    }

    public String getEmail() {
        return this.email;
    }

    public String getFirstPetName() {
        return this.firstPetName;
    }
}
