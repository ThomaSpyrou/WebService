package com.appsdev.app.ws.appdevws.service.implementation;

import com.appsdev.app.ws.appdevws.exceptions.UserServiceException;
import com.appsdev.app.ws.appdevws.io.repositories.UserRepository;
import com.appsdev.app.ws.appdevws.io.entity.UserEntity;
import com.appsdev.app.ws.appdevws.model.response.ErrorMessages;
import com.appsdev.app.ws.appdevws.model.response.UserRest;
import com.appsdev.app.ws.appdevws.service.UserService;
import com.appsdev.app.ws.appdevws.shared.dto.AddressDTO;
import com.appsdev.app.ws.appdevws.shared.dto.UserDTO;
import com.appsdev.app.ws.appdevws.shared.dto.Utils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
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

        for(int index=0; index<user.getAddresses().size();index++){
            AddressDTO addressDTO = user.getAddresses().get(index);
            addressDTO.setUserDetails(user);
            addressDTO.setAddressId(utils.generateAddressId(30));
            user.getAddresses().set(index, addressDTO);
            System.out.println(addressDTO.getAddressId());
        }

        //BeanUtils.copyProperties(user, userEntity);
        ModelMapper modelMapper = new ModelMapper();
        UserEntity userEntity = modelMapper.map(user, UserEntity.class);
        //generate userId
        String publicUserId = utils.generateUserId(30);
        userEntity.setUserId(publicUserId);
        //to encrypt password before store it to database
        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        UserEntity storedUserDetails = userRepository.save(userEntity);

       // BeanUtils.copyProperties(storedUserDetails, returnValue);
        UserDTO returnValue = modelMapper.map(storedUserDetails, UserDTO.class);

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

    @Override
    public List<UserDTO> getAllUsers(int page, int pageLimit) {
        List<UserDTO> returnListOfUsers = new ArrayList<>();

        if(page > 0){
            page -=1;
        }

        Pageable pageRequestLimit =  PageRequest.of(page, pageLimit);
        Page<UserEntity> userEntityPage = userRepository.findAll(pageRequestLimit);
        List<UserEntity> listOfUsers = userEntityPage.getContent();

        if(listOfUsers.isEmpty()){
            throw new UserServiceException(ErrorMessages.NO_USERS_IN_DB.getErrorMessage());
        }

        for(UserEntity userEntity: listOfUsers){
            UserDTO userToBeImported = new UserDTO();
            BeanUtils.copyProperties(userEntity, userToBeImported);
            returnListOfUsers.add(userToBeImported);
        }

        return returnListOfUsers;
    }


}
