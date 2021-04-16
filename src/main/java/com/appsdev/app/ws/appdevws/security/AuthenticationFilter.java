package com.appsdev.app.ws.appdevws.security;

import com.appsdev.app.ws.appdevws.SpringApplicationContext;
import com.appsdev.app.ws.appdevws.model.UserLoginRequestModel;
import com.appsdev.app.ws.appdevws.service.UserService;
import com.appsdev.app.ws.appdevws.shared.dto.UserDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import io.jsonwebtoken.*;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;

    public AuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }


    //this method will be triggered when the user try to login with email and password
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {

        try{
            UserLoginRequestModel userLoginRequestModel = new ObjectMapper()
                    .readValue(request.getInputStream(), UserLoginRequestModel.class);

            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userLoginRequestModel.getEmail(),
                            userLoginRequestModel.getPassword(),
                            new ArrayList<>()
                    )
            );
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    //in case that the authentication is success
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {

        String userName = ((User) authResult.getPrincipal()).getUsername();

        String token = Jwts.builder()
                .setSubject(userName)
                .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.getTokenSecret())
                .compact();

        UserService userService = (UserService) SpringApplicationContext.getBean("userServiceImplement");

        UserDTO userDTO = userService.getUser(userName);

        response.addHeader(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
        response.addHeader("UserID", userDTO.getUserId());
    }
}
