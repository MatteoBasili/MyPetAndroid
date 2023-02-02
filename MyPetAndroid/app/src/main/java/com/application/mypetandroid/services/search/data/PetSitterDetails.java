package com.application.mypetandroid.services.search.data;

import com.application.mypetandroid.registration.data.PetSitCaredPets;
import com.application.mypetandroid.registration.data.PetSitServices;
import com.application.mypetandroid.registration.data.ProfileUserData;
import com.application.mypetandroid.services.profile.data.PetSitProfileInfo;

public class PetSitterDetails {

    private PetSitCaredPets petSitCaredPets;
    private PetSitServices petSitServices;
    private ProfileUserData profileUserData;
    private PetSitProfileInfo petSitProfileInfo;
    private PetSitterRating petSitterRating;
    private String email;

    public PetSitCaredPets getPetSitCaredPets() {
        return petSitCaredPets;
    }

    public void setPetSitCaredPets(PetSitCaredPets petSitCaredPets) {
        this.petSitCaredPets = petSitCaredPets;
    }

    public PetSitServices getPetSitServices() {
        return petSitServices;
    }

    public void setPetSitServices(PetSitServices petSitServices) {
        this.petSitServices = petSitServices;
    }

    public ProfileUserData getProfileUserData() {
        return profileUserData;
    }

    public void setProfileUserData(ProfileUserData profileUserData) {
        this.profileUserData = profileUserData;
    }

    public PetSitProfileInfo getPetSitProfileInfo() {
        return petSitProfileInfo;
    }

    public void setPetSitProfileInfo(PetSitProfileInfo petSitProfileInfo) {
        this.petSitProfileInfo = petSitProfileInfo;
    }

    public PetSitterRating getPetSitterRating() {
        return petSitterRating;
    }

    public void setPetSitterRating(PetSitterRating petSitterRating) {
        this.petSitterRating = petSitterRating;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
