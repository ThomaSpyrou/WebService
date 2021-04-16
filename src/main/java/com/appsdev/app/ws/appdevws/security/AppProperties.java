package com.appsdev.app.ws.appdevws.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class AppProperties {

    @Autowired //to be available in app properties
    private Environment environment;

    public String getTokenSecret(){
        return environment.getProperty("tokenSecret");
    }

}
