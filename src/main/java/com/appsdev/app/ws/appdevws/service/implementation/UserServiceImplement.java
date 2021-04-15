package com.appsdev.app.ws.appdevws.service.implementation;

import com.appsdev.app.ws.appdevws.UserRepository;
import com.appsdev.app.ws.appdevws.io.entity.UserEntity;
import com.appsdev.app.ws.appdevws.service.UserService;
import com.appsdev.app.ws.appdevws.shared.dto.UserDTO;
import com.appsdev.app.ws.appdevws.shared.dto.Utils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

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
}