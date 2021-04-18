package com.appsdev.app.ws.appdevws.controller;

import com.appsdev.app.ws.appdevws.model.UserDetailsRequestModel;
import com.appsdev.app.ws.appdevws.model.response.UserRest;
import com.appsdev.app.ws.appdevws.service.UserService;
import com.appsdev.app.ws.appdevws.shared.dto.UserDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/users")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping(path = "/{userId}")
    public UserRest getUser(@PathVariable String userId){

        UserRest returnValue = new UserRest();
        UserDTO userDTO = userService.getUserByUserId(userId);
        BeanUtils.copyProperties(userDTO, returnValue);

        return returnValue;
    }

    @PostMapping
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails){
        UserRest returnValue = new UserRest();

        UserDTO userDto = new UserDTO();
        BeanUtils.copyProperties(userDetails, userDto); //copies propertied from source objects to target objects
        UserDTO createdUser = userService.createUser(userDto);
        BeanUtils.copyProperties(createdUser, returnValue);

        return returnValue;
    }

    @PutMapping
    public String updateUser(){
        return "Update User";
    }

    @DeleteMapping
    public String deleteUser(){
        return "Delete User";
    }
}
