package com.application.mypetandroid.services.search.data;

import java.util.Objects;

public class PetSitSearchFilters {

    private boolean dog;
    private boolean cat;
    private boolean otherPets;
    private String region;
    private String province;

    public boolean isDog() {
        return dog;
    }

    public void setDog(boolean dog) {
        this.dog = dog;
    }

    public boolean isCat() {
        return cat;
    }

    public void setCat(boolean cat) {
        this.cat = cat;
    }

    public boolean isOtherPets() {
        return otherPets;
    }

    public void setOtherPets(boolean otherPets) {
        this.otherPets = otherPets;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        if (!Objects.equals(region, "Select your region")) {
            this.region = region;
        } else {
            this.region = "";
        }
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        if (Objects.equals(province, "Select your province") || province == null) {
            this.province = "";
        } else {
            this.province = province;
        }
    }
}
