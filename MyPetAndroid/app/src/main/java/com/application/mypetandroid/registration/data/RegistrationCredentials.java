package com.application.mypetandroid.registration.data;

public class RegistrationCredentials {

    private PetSitCaredPets petSitCaredPets;
    private PetSitServices petSitServices;
    private ProfileUserData profileUserData;
    private SystemUserData systemUserData;

    public SystemUserData getSystemUserData() {
        return this.systemUserData;
    }

    public ProfileUserData getProfileUserData() {
        return this.profileUserData;
    }

    public PetSitCaredPets getPetSitterCaredPets() {
        return this.petSitCaredPets;
    }

    public PetSitServices getPetSitterServices() {
        return this.petSitServices;
    }

    public void setPetSitCaredPets(PetSitCaredPets petSitCaredPets) {
        this.petSitCaredPets = petSitCaredPets;
    }

    public void setPetSitServices(PetSitServices petSitServices) {
        this.petSitServices = petSitServices;
    }

    public void setProfileUserData(ProfileUserData profileUserData) {
        this.profileUserData = profileUserData;
    }

    public void setSystemUserData(SystemUserData systemUserData) {
        this.systemUserData = systemUserData;
    }
}
