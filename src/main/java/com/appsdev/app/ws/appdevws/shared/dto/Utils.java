package com.appsdev.app.ws.appdevws.shared.dto;
//used to generate id which will be send back in frontend

import com.appsdev.app.ws.appdevws.security.SecurityConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Random;

@Component
public class Utils {
    private final Random RANDOM = new SecureRandom();
    private final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmopqrstuvwxyz";

    public static String generatePasswordResetToken(String userId) {
        return Jwts.builder()
                .setSubject(userId)
                .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.PASSWORD_RESET_EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.getTokenSecret())
                .compact();
    }

    public String generateUserId(int length){
        return generateRandomString(length);
    }

    public String generateAddressId(int length){return generateRandomString(length);}

    private String generateRandomString(int length){
        StringBuilder returnValue = new StringBuilder(length);
        for(int index=0; index <length; index++){
            returnValue.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }

        return new String(returnValue);
    }

    public static boolean hasTokenExpired(String token){
        boolean returnValue = false;

        try {
            //decrypt the token
            Claims claims = Jwts.parser()
                    .setSigningKey(SecurityConstants.getTokenSecret())
                    .parseClaimsJws(token).getBody();

            Date tokenExpirationDate = claims.getExpiration();
            Date todayDate = new Date();
            returnValue = tokenExpirationDate.before(todayDate);
        }
        catch (MalformedJwtException malformedJwtException){
            returnValue = true;
        }

        return returnValue;
    }

    public String generateEmailVerificationToken(String userID){
        //token to be returned
        return Jwts.builder()
                .setSubject(userID)
                .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.getTokenSecret())
                .compact();
    }
}
