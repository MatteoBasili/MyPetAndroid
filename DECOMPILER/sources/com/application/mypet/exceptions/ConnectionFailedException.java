package com.application.mypet.exceptions;

public class ConnectionFailedException extends Exception {
    public ConnectionFailedException() {
        super("Connection to server failed. Check your Internet access");
    }
}
