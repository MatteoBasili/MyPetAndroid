package com.application.mypet.login.data;

public class PasswordRecoveryCredentials {
    private String email;
    private String petName;

    public PasswordRecoveryCredentials(String email2, String petName2) {
        this.email = email2;
        this.petName = petName2;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPetName() {
        return this.petName;
    }
}
