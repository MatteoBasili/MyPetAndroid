package com.application.mypetandroid.registration.data;

public class PetSitCaredPets {

    private boolean cat;
    private boolean dog;
    private boolean otherPets;

    public boolean isDog() {
        return this.dog;
    }

    public boolean isCat() {
        return this.cat;
    }

    public boolean isOtherPets() {
        return this.otherPets;
    }

    public void setCat(boolean cat) {
        this.cat = cat;
    }

    public void setDog(boolean dog) {
        this.dog = dog;
    }

    public void setOtherPets(boolean otherPets) {
        this.otherPets = otherPets;
    }
}
