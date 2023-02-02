package com.application.mypetandroid.utils.exceptions;

public class AlreadyUsedException extends Exception {
    public AlreadyUsedException(String input) {
        super("The " + input + " is already used");
    }
}
