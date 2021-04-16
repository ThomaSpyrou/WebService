package com.appsdev.app.ws.appdevws.service;

import com.appsdev.app.ws.appdevws.shared.dto.UserDTO;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    UserDTO createUser(UserDTO userDTO);
    UserDTO getUser(String email);
}
