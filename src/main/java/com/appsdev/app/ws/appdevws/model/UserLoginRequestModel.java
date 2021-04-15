package com.appsdev.app.ws.appdevws.model;


public class UserLoginRequestModel {
    //this class is used to validate the user when they login
    private String email;
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
