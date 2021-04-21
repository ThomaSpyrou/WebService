package com.appsdev.app.ws.appdevws.service.implementation;

import com.appsdev.app.ws.appdevws.exceptions.UserServiceException;
import com.appsdev.app.ws.appdevws.io.repositories.UserRepository;
import com.appsdev.app.ws.appdevws.io.entity.UserEntity;
import com.appsdev.app.ws.appdevws.model.response.ErrorMessages;
import com.appsdev.app.ws.appdevws.service.UserService;
import com.appsdev.app.ws.appdevws.shared.dto.UserDTO;
import com.appsdev.app.ws.appdevws.shared.dto.Utils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class UserServiceImplement implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    Utils utils;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserDTO createUser(UserDTO user){

        //check if user exists by email
        if(userRepository.findUserEntitiesByEmail(user.getEmail()) != null){
            throw new RuntimeException("User already exists");
        }

        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(user, userEntity);

        //generate userId
        String publicUserId = utils.generateUserId(30);
        userEntity.setUserId(publicUserId);
        //to encrypt password before store it to database
        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        UserEntity storedUserDetails = userRepository.save(userEntity);

        UserDTO returnValue = new UserDTO();
        BeanUtils.copyProperties(storedUserDetails, returnValue);

        return returnValue;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity =  userRepository.findUserEntitiesByEmail(email);

        if(userEntity == null){
            throw new UsernameNotFoundException(email);
        }

        return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new ArrayList<>());
    }

    @Override
    public UserDTO getUser(String email){
        UserEntity userEntity = userRepository.findUserEntitiesByEmail(email);

        if(userEntity == null){
            throw new UsernameNotFoundException(email);
        }

        UserDTO returnValue = new UserDTO();
        BeanUtils.copyProperties(userEntity, returnValue);

        return returnValue;


    }

    @Override
    public UserDTO getUserByUserId(String userId) {
        UserDTO returnValue = new UserDTO();
        UserEntity userEntity = userRepository.findUserEntityByUserId(userId);

        if(userEntity == null){
            throw new UsernameNotFoundException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage() + userId);
        }

        BeanUtils.copyProperties(userEntity, returnValue);

        return returnValue;
    }

    @Override
    public UserDTO updateUser(String userId, UserDTO userDTO) {
        UserDTO returnValue = new UserDTO();
        UserEntity userEntity = userRepository.findUserEntityByUserId(userId);

        if(userEntity == null){
            throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
        }

        userEntity.setFirstName(userDTO.getFirstName());
        userEntity.setLastName(userDTO.getLastName());
        UserEntity updatedUser = userRepository.save(userEntity);
        BeanUtils.copyProperties(updatedUser, returnValue);

        return returnValue;
    }

    @Override
    public void deleteUserById(String userId) {
        UserDTO returnValue = new UserDTO();
        UserEntity userEntity = userRepository.findUserEntityByUserId(userId);

        if(userEntity == null){
            throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
        }

        userRepository.delete(userEntity);
    }

}
