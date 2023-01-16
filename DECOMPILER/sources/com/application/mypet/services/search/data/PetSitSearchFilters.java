package com.application.mypet.services.search.data;

import com.application.mypet.registration.data.PetSitterCaredPetsCredentials;
import java.io.Serializable;

public class PetSitSearchFilters implements Serializable {
    private PetSitterCaredPetsCredentials petSitterCaredPetsCredentials;
    private String province;
    private String region;

    public PetSitSearchFilters(PetSitterCaredPetsCredentials petSitterCaredPetsCredentials2, String region2, String province2) {
        this.petSitterCaredPetsCredentials = petSitterCaredPetsCredentials2;
        this.region = region2;
        this.province = province2;
    }

    public PetSitterCaredPetsCredentials getPetSitterCaredPetsCredentials() {
        return this.petSitterCaredPetsCredentials;
    }

    public String getRegion() {
        return this.region;
    }

    public String getProvince() {
        return this.province;
    }
}
