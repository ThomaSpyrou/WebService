package com.appsdev.app.ws.appdevws.controller;

import com.appsdev.app.ws.appdevws.exceptions.UserServiceException;
import com.appsdev.app.ws.appdevws.model.UserDetailsRequestModel;
import com.appsdev.app.ws.appdevws.model.response.*;
import com.appsdev.app.ws.appdevws.service.UserService;
import com.appsdev.app.ws.appdevws.shared.dto.UserDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "api/users")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping(path = "/{userId}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
            //json is the default, if it is defined the order matters
    public UserRest getUser(@PathVariable String userId){
        UserRest returnValue = new UserRest();
        UserDTO userDTO = userService.getUserByUserId(userId);
        BeanUtils.copyProperties(userDTO, returnValue);

        return returnValue;
    }

    @PostMapping(
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) throws Exception {
        if(userDetails.getFirstName().isEmpty()){
            throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());
        }

        UserRest returnValue = new UserRest();
        UserDTO userDto = new UserDTO();
        BeanUtils.copyProperties(userDetails, userDto); //copies propertied from source objects to target objects
        UserDTO createdUser = userService.createUser(userDto);
        BeanUtils.copyProperties(createdUser, returnValue);

        return returnValue;
    }

    @PutMapping(path = "/{userId}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public UserRest updateUser(@PathVariable String userId,
                             @RequestBody UserDetailsRequestModel userDetailsRequestModel) throws Exception{
        UserRest returnValue = new UserRest();
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(userDetailsRequestModel, userDTO);
        UserDTO updatedUser = userService.updateUser(userId, userDTO);
        BeanUtils.copyProperties(updatedUser, returnValue);

        return returnValue;
    }

    @DeleteMapping(path = "/{userId}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public OperationStatusModel deleteUser(@PathVariable String userId) throws Exception{
        OperationStatusModel returnValue = new OperationStatusModel();
        userService.deleteUserById(userId);
        returnValue.setOperationName(RequestOperationName.DELETE.name());
        returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());

        return returnValue;
    }

    //use pagination in order to select a limit number of record per page
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public List<UserRest> getAllUser(@RequestParam(value = "pageID", defaultValue = "0") int page,
                                     @RequestParam(value = "pageLimit", defaultValue = "50") int pageLimit) throws Exception{
        List<UserRest> returnListOfUsers = new ArrayList<>();

        List<UserDTO> listOfUsers = userService.getAllUsers(page, pageLimit);

        for(UserDTO user: listOfUsers){
            UserRest userModel = new UserRest();
            BeanUtils.copyProperties(user, userModel);
            returnListOfUsers.add(userModel);
        }

        return returnListOfUsers;
    }
}
