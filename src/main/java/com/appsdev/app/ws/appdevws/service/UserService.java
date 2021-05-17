package com.appsdev.app.ws.appdevws.service;

import com.appsdev.app.ws.appdevws.shared.dto.UserDTO;
import org.springframework.security.core.userdetails.UserDetailsService;
import java.util.List;

public interface UserService extends UserDetailsService {
    UserDTO createUser(UserDTO userDTO);
    UserDTO getUser(String email);
    UserDTO getUserByUserId(String userId);
    UserDTO updateUser(String userId, UserDTO userDTO);
    void deleteUserById(String userId);
    List<UserDTO> getAllUsers(int page, int pageLimit);
    boolean verifyEmailToken(String token);
    boolean requestPasswordReset(String mail);
    boolean resetPassword(String token, String password);
}
