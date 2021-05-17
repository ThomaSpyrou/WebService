package com.appsdev.app.ws.appdevws.security;

import com.appsdev.app.ws.appdevws.SpringApplicationContext;

public class SecurityConstants {
    public static final long EXPIRATION_TIME = 90000000; //ms
    public static final long PASSWORD_RESET_EXPIRATION_TIME = 1000*60*60; //1 hour
//    public static final String TOKEN_SECRET = ;
    public static final String TOKEN_PREFIX = "Bearer"; //authorization header
    public static final String HEADER_STRING = "Authorization";
    public static final String SING_UP_URL = "/api/users";
    public static final String VERIFICATION_EMAIL_URL = "/api/users/email-verification";
    public static final String PASSWORD_RESET_REQUEST_URL = "/api/users/passwordResetRequest";
    public static final String PASSWORD_RESET_URL = "/api/users/passwordReset";

    public static String getTokenSecret(){
        AppProperties appProperties = (AppProperties) SpringApplicationContext.getBean("AppProperties");
        return appProperties.getTokenSecret();
    }
}
