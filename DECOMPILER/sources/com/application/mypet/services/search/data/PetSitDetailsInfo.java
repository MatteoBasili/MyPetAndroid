package com.application.mypet.services.search.data;

import com.application.mypet.registration.data.PetSitterCaredPetsCredentials;
import com.application.mypet.registration.data.PetSitterServicesCredentials;
import com.application.mypet.registration.data.ProfileCredentials;
import com.application.mypet.services.profile.petsitter.data.LoadProfileInfo;

public class PetSitDetailsInfo {
    private String email;
    private LoadProfileInfo loadProfileInfo;
    private PetSitRating petSitRating;
    private PetSitterCaredPetsCredentials petSitterCaredPetsCredentials;
    private PetSitterServicesCredentials petSitterServicesCredentials;
    private ProfileCredentials profileCredentials;

    public PetSitDetailsInfo(LoadProfileInfo loadProfileInfo2, PetSitRating petSitRating2, ProfileCredentials profileCredentials2, PetSitterCaredPetsCredentials petSitterCaredPetsCredentials2, PetSitterServicesCredentials petSitterServicesCredentials2, String email2) {
        this.loadProfileInfo = loadProfileInfo2;
        this.petSitRating = petSitRating2;
        this.profileCredentials = profileCredentials2;
        this.petSitterCaredPetsCredentials = petSitterCaredPetsCredentials2;
        this.petSitterServicesCredentials = petSitterServicesCredentials2;
        this.email = email2;
    }

    public LoadProfileInfo getLoadProfileInfo() {
        return this.loadProfileInfo;
    }

    public PetSitRating getPetSitRating() {
        return this.petSitRating;
    }

    public ProfileCredentials getProfileCredentials() {
        return this.profileCredentials;
    }

    public PetSitterCaredPetsCredentials getPetSitterCaredPetsCredentials() {
        return this.petSitterCaredPetsCredentials;
    }

    public PetSitterServicesCredentials getPetSitterServicesCredentials() {
        return this.petSitterServicesCredentials;
    }

    public String getEmail() {
        return this.email;
    }
}
