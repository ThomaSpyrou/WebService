package com.appsdev.app.ws.appdevws;

import com.appsdev.app.ws.appdevws.controller.UserController;
import com.appsdev.app.ws.appdevws.model.response.UserRest;
import com.appsdev.app.ws.appdevws.service.implementation.UserServiceImplement;
import com.appsdev.app.ws.appdevws.shared.dto.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    @InjectMocks
    UserController userController;

    @Mock
    UserServiceImplement userService;

    UserDTO userDto;
    final String USER_ID = "234adf3";

    @BeforeEach
    void setUp() throws Exception{
        MockitoAnnotations.initMocks(this);

        userDto = new UserDTO();
        userDto.setFirstName("thomas");
        userDto.setLastName("spyrou");
        userDto.setEmail("sth@test.com");
        userDto.setEmailVerificationStatus(Boolean.FALSE);
        userDto.setEmailVerificationToken(null);
        userDto.setUserId(USER_ID);
        userDto.setEncryptedPassword("daf4343a");
    }

    @Test
    void testGetUserMethod() {
        when(userService.getUserByUserId(anyString())).thenReturn(userDto);
        UserRest userRest = userController.getUser(USER_ID);

        assertNotNull(userRest);
        assertEquals(USER_ID, userRest.getUserId());
    }
}
