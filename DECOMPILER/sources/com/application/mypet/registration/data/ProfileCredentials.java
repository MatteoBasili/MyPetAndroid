package com.application.mypet.registration.data;

public class ProfileCredentials {
    private String address;
    private String name;
    private String phoneNumb;
    private String province;
    private String region;
    private String surname;

    public ProfileCredentials(String name2, String surname2, String region2, String province2, String address2, String phoneNumb2) {
        this.name = name2;
        this.surname = surname2;
        this.region = region2;
        this.province = province2;
        this.address = address2;
        this.phoneNumb = phoneNumb2;
    }

    public String getName() {
        return this.name;
    }

    public String getSurname() {
        return this.surname;
    }

    public String getRegion() {
        return this.region;
    }

    public String getProvince() {
        return this.province;
    }

    public String getAddress() {
        return this.address;
    }

    public String getPhoneNumb() {
        return this.phoneNumb;
    }
}
