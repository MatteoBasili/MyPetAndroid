package com.application.mypetandroid.utils.singleton_examples;

import com.application.mypetandroid.services.profile.data.PetSitProfileInfo;

import java.util.List;

public class PetSitterResultsSingletonClass {

    private static PetSitterResultsSingletonClass instance = null;

    private List<PetSitProfileInfo> petSitProfileInfo;
    private List<String> usernames;
    private List<String> provinces;
    private List<Boolean> favorites;
    private List<String> regions;

    public PetSitterResultsSingletonClass() {
        // requires empty constructor
    }

    public static synchronized PetSitterResultsSingletonClass getSingletonInstance() {
        if (PetSitterResultsSingletonClass.instance == null)
            PetSitterResultsSingletonClass.instance = new PetSitterResultsSingletonClass();
        return instance;
    }

    public List<PetSitProfileInfo> getPetSitProfileInfo() {
        return petSitProfileInfo;
    }

    public void setPetSitProfileInfo(List<PetSitProfileInfo> petSitProfileInfo) {
        this.petSitProfileInfo = petSitProfileInfo;
    }

    public List<String> getUsernames() {
        return usernames;
    }

    public void setUsernames(List<String> usernames) {
        this.usernames = usernames;
    }

    public List<String> getProvinces() {
        return provinces;
    }

    public void setProvinces(List<String> provinces) {
        this.provinces = provinces;
    }

    public List<Boolean> getFavorites() {
        return favorites;
    }

    public void setFavorites(List<Boolean> favorites) {
        this.favorites = favorites;
    }

    public List<String> getRegions() {
        return regions;
    }

    public void setRegions(List<String> regions) {
        this.regions = regions;
    }

    public int getResultsNumber() {
        return getUsernames().size();
    }
}
