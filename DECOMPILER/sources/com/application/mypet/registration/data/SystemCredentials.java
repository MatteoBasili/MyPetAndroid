package com.application.mypet.registration.data;

public class SystemCredentials {
    private String email;
    private String firstPetName;
    private String password;
    private String passwordConfirm;
    private boolean petSitter;
    private String username;

    public SystemCredentials(String username2, String email2, String password2, String passwordConfirm2, String firstPetName2, boolean petSitter2) {
        this.username = username2;
        this.email = email2;
        this.password = password2;
        this.passwordConfirm = passwordConfirm2;
        this.firstPetName = firstPetName2;
        this.petSitter = petSitter2;
    }

    public String getUsername() {
        return this.username;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return this.password;
    }

    public String getPasswordConfirm() {
        return this.passwordConfirm;
    }

    public String getFirstPetName() {
        return this.firstPetName;
    }

    public boolean isPetSitter() {
        return this.petSitter;
    }
}
