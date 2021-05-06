package com.appsdev.app.ws.appdevws.controller;

import com.appsdev.app.ws.appdevws.exceptions.UserServiceException;
import com.appsdev.app.ws.appdevws.model.UserDetailsRequestModel;
import com.appsdev.app.ws.appdevws.model.response.*;
import com.appsdev.app.ws.appdevws.service.AddressService;
import com.appsdev.app.ws.appdevws.service.UserService;
import com.appsdev.app.ws.appdevws.shared.dto.AddressDTO;
import com.appsdev.app.ws.appdevws.shared.dto.UserDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "api/users")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    AddressService addressService;

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
        //UserDTO userDTO = new UserDTO();
        //BeanUtils.copyProperties(userDetails, userDto); //copies propertied from source objects to target objects
        //BeanUtils should not be used for multiple objects-lists
        ModelMapper modelMapper = new ModelMapper();
        UserDTO userDTO = modelMapper.map(userDetails, UserDTO.class);
        UserDTO createdUser = userService.createUser(userDTO);
        returnValue = modelMapper.map(createdUser, UserRest.class);

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

    @GetMapping(path = "/{userId}/addresses",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public List<AddressesRest> getListOfAddresses(@PathVariable String userId) throws Exception{
        List<AddressesRest> returnList = new ArrayList<>();
        List<AddressDTO> addressDTOList = addressService.getUserAddresses(userId);

        ModelMapper modelMapper = new ModelMapper();
        Type listType = new TypeToken<List<AddressesRest>>() {}.getType();
        returnList = modelMapper.map(addressDTOList, listType);

        return returnList;
    }

    @GetMapping(path = "/{userId}/addresses/{addressId}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public AddressesRest getUserAddress(@PathVariable String userId, @PathVariable String addressId) throws Exception{
        AddressDTO addressDTO = addressService.getUserAddress(addressId);

        ModelMapper modelMapper = new ModelMapper();
        AddressesRest returnValue = modelMapper.map(addressDTO, AddressesRest.class);

        //the link will be: http://localohost:8080/api/users
        Link userLink = WebMvcLinkBuilder.linkTo(UserController.class).slash(userId).withRel("user");
        Link userAddressesLink = WebMvcLinkBuilder.linkTo(UserController.class)
                .slash(userId)
                .slash("addresses")
                .withRel("addresses");

        Link selfLink = WebMvcLinkBuilder.linkTo(UserController.class)
                .slash(userId)
                .slash("addresses")
                .slash(addressId)
                .withSelfRel();

        returnValue.add(userLink);
        returnValue.add(userAddressesLink);
        returnValue.add(selfLink);

        return returnValue;
    }
}
