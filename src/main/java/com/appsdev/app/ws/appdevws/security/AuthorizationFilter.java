package com.appsdev.app.ws.appdevws.security;

import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;


//to authorize each method which a use allowed to do
public class AuthorizationFilter extends BasicAuthenticationFilter {

    public AuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }


    //when a method is been triggered this method is been called to filter the user
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {

        //access the request header
        String header = request.getHeader(SecurityConstants.HEADER_STRING);

        if(header == null || !header.startsWith(SecurityConstants.TOKEN_PREFIX)){
            chain.doFilter(request, response);
            return;
        }


        UsernamePasswordAuthenticationToken authenticationToken = getAuthentication(request);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest httpServletRequest){
        String token = httpServletRequest.getHeader(SecurityConstants.HEADER_STRING);

        if(token != null){
            token = token.replace(SecurityConstants.TOKEN_PREFIX, "");

            String user = Jwts.parser() //parse the user value decrypted and send it
                    .setSigningKey(SecurityConstants.getTokenSecret()) //must be same secret
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();

            if(user != null){
                return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
            }
            return null;
        }
        return null;
    }
}
