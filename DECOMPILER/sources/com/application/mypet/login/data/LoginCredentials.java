package com.application.mypet.login.data;

public class LoginCredentials {
    private String password;
    private String username;

    public LoginCredentials(String username2, String password2) {
        this.username = username2;
        this.password = password2;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }
}
