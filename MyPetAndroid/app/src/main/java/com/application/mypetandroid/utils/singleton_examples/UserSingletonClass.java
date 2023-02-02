package com.application.mypetandroid.utils.singleton_examples;

public class UserSingletonClass {

    private static UserSingletonClass instance = null;

    private String username;
    private int role;

    protected UserSingletonClass() {

    }

    public static synchronized UserSingletonClass getSingletonInstance() {
        if (UserSingletonClass.instance == null)
            UserSingletonClass.instance = new UserSingletonClass();
        return instance;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }
}
