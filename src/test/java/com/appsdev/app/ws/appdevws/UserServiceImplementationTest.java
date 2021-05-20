package com.appsdev.app.ws.appdevws;

import com.appsdev.app.ws.appdevws.exceptions.UserServiceException;
import com.appsdev.app.ws.appdevws.io.entity.UserEntity;
import com.appsdev.app.ws.appdevws.io.repositories.UserRepository;
import com.appsdev.app.ws.appdevws.service.implementation.UserServiceImplement;
import com.appsdev.app.ws.appdevws.shared.dto.AddressDTO;
import com.appsdev.app.ws.appdevws.shared.dto.AmazonSES;
import com.appsdev.app.ws.appdevws.shared.dto.UserDTO;
import com.appsdev.app.ws.appdevws.shared.dto.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class UserServiceImplementationTest {

    String userId = "fef32";
    String encryptedPassword = "32rfw3";
    UserEntity userEntity;

    @InjectMocks
    UserServiceImplement userServiceImplement;

    @Mock
    UserRepository userRepository;

    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    Utils utils;

    @Mock
    AmazonSES amazonSES;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setFirstName("thomas");
        userEntity.setLastName("sth");
        userEntity.setUserId(userId);
        userEntity.setEncryptedPassword(encryptedPassword);
        userEntity.setEmailVerificationToken("sdfgh");
        userEntity.setEmail("test@test.com");
    }


    @Test
    final void testGetUser() {
        when(userRepository.findUserEntitiesByEmail(anyString())).thenReturn(userEntity);

        UserDTO returnValue = userServiceImplement.getUser("test@test.com");

        assertNotNull(returnValue);
        assertEquals("thomas", returnValue.getFirstName());
    }

    @Test
    final void testGetUserUsernameNotFoundException(){
        when(userRepository.findUserEntitiesByEmail(anyString())).thenReturn(null);

        assertThrows(UsernameNotFoundException. class,
                () -> {
                    userServiceImplement.getUser("test@test.com");
                });
    }

    @Test
    final void testCreateUser(){
        when(userRepository.findUserEntitiesByEmail(anyString())).thenReturn(null);
        when(utils.generateAddressId(anyInt())).thenReturn("dasd3aasax");
        when(utils.generateUserId(anyInt())).thenReturn(userId);
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn(encryptedPassword);
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        Mockito.doNothing().when(amazonSES).verifyEmail(any(UserDTO.class));

        AddressDTO shippingAddressDTO = new AddressDTO();
        shippingAddressDTO.setType("shipping");
        shippingAddressDTO.setCity("skg");
        shippingAddressDTO.setCountry("gr");
        shippingAddressDTO.setStreetName("stg");
        shippingAddressDTO.setPostalCode("ak10");

        AddressDTO billingAddressDTO = new AddressDTO();
        billingAddressDTO.setType("shipping");
        billingAddressDTO.setCity("skg");
        billingAddressDTO.setCountry("gr");
        billingAddressDTO.setStreetName("stg");
        billingAddressDTO.setPostalCode("ak10");

        List<AddressDTO> addressDTOList = new ArrayList<>();
        addressDTOList.add(billingAddressDTO);
        addressDTOList.add(shippingAddressDTO);

        UserDTO userDto = new UserDTO();
        userDto.setAddresses(addressDTOList);
        userDto.setFirstName("thomas");

        UserDTO storedUser = userServiceImplement.createUser(userDto);

        assertNotNull(storedUser);
        assertEquals(userEntity.getFirstName(), storedUser.getFirstName());
        assertNotNull(storedUser.getUserId());
        verify(utils, times(2)).generateAddressId(30);
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    final void testCreateUserException(){
        when(userRepository.findUserEntitiesByEmail(anyString())).thenReturn(userEntity);
        UserDTO userDto = new UserDTO();
        userDto.setFirstName("thomas");
        userDto.setEmail("test@test.com");

        assertThrows(UserServiceException. class,
                () -> {
                    userServiceImplement.createUser(userDto);
                });
    }
}
