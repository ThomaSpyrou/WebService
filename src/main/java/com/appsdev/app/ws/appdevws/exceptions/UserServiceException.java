package com.appsdev.app.ws.appdevws.exceptions;

public class UserServiceException extends RuntimeException{

    private static final long serialVersionUID = 12345676453323L;

    public UserServiceException(String message) {
        super(message);
    }
}
