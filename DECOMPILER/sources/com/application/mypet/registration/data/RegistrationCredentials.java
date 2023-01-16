package com.application.mypet.registration.data;

public class RegistrationCredentials {
    private final PetSitterCaredPetsCredentials petSitterCaredPetsCredentials;
    private final PetSitterServicesCredentials petSitterServicesCredentials;
    private final ProfileCredentials profileCredentials;
    private final SystemCredentials systemCredentials;

    public RegistrationCredentials(SystemCredentials systemCredentials2, ProfileCredentials profileCredentials2, PetSitterCaredPetsCredentials petSitterCaredPetsCredentials2, PetSitterServicesCredentials petSitterServicesCredentials2) {
        this.systemCredentials = systemCredentials2;
        this.profileCredentials = profileCredentials2;
        this.petSitterCaredPetsCredentials = petSitterCaredPetsCredentials2;
        this.petSitterServicesCredentials = petSitterServicesCredentials2;
    }

    public SystemCredentials getSystemCredentials() {
        return this.systemCredentials;
    }

    public ProfileCredentials getProfileCredentials() {
        return this.profileCredentials;
    }

    public PetSitterCaredPetsCredentials getPetSitterCaredPets() {
        return this.petSitterCaredPetsCredentials;
    }

    public PetSitterServicesCredentials getPetSitterServices() {
        return this.petSitterServicesCredentials;
    }
}
