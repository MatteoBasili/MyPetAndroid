package com.application.mypet.registration.data;

public class PetSitterCaredPetsCredentials {
    private boolean cat;
    private boolean dog;
    private boolean otherPets;

    public PetSitterCaredPetsCredentials(boolean dog2, boolean cat2, boolean otherPets2) {
        this.dog = dog2;
        this.cat = cat2;
        this.otherPets = otherPets2;
    }

    public boolean isDog() {
        return this.dog;
    }

    public boolean isCat() {
        return this.cat;
    }

    public boolean isOtherPets() {
        return this.otherPets;
    }
}
