package com.application.mypetandroid.registration.data;

public class SystemUserData {

    private String email;
    private String firstPetName;
    private String password;
    private String passwordConfirm;
    private boolean petSitter;
    private String username;

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

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstPetName(String firstPetName) {
        this.firstPetName = firstPetName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    public void setPetSitter(boolean petSitter) {
        this.petSitter = petSitter;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
