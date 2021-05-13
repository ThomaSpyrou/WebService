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
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping(path = "/api/users")
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
    public CollectionModel<AddressesRest> getListOfAddresses(@PathVariable String userId) throws Exception{
        List<AddressesRest> returnList = new ArrayList<>();
        List<AddressDTO> addressDTOList = addressService.getUserAddresses(userId);

        ModelMapper modelMapper = new ModelMapper();
        Type listType = new TypeToken<List<AddressesRest>>() {}.getType();
        returnList = modelMapper.map(addressDTOList, listType);

        for(AddressesRest addressIterate: returnList){
            Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder
                    .methodOn(UserController.class)
                    .getUserAddress(userId, addressIterate.getAddressId()))
                    .withSelfRel();
            addressIterate.add(selfLink);
        }

        Link userLink = WebMvcLinkBuilder.linkTo(UserController.class)
                .slash(userId)
                .withRel("user");

        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder
                .methodOn(UserController.class)
                .getListOfAddresses(userId))
                .withSelfRel();

        return CollectionModel.of(returnList, userLink, selfLink);
    }

    @GetMapping(path = "/{userId}/addresses/{addressId}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public EntityModel<AddressesRest> getUserAddress(@PathVariable String userId, @PathVariable String addressId) throws Exception{
        AddressDTO addressDTO = addressService.getUserAddress(addressId);

        ModelMapper modelMapper = new ModelMapper();
        AddressesRest returnValue = modelMapper.map(addressDTO, AddressesRest.class);

        //the link will be: http://localohost:8080/api/users
        //used to add links in payload
        Link userLink = WebMvcLinkBuilder.linkTo(UserController.class)
                .slash(userId)
                .withRel("user");
        Link userAddressesLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getListOfAddresses(userId)) //methodOn().method that i want to return as link
//                .slash(userId)
//                .slash("addresses") //if methodOn is used no need to build the entire url
                .withRel("addresses");
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddress(userId, addressId))
//                .slash(userId)
//                .slash("addresses")
//                .slash(addressId)
                .withSelfRel();

        EntityModel.of(returnValue, Arrays.asList(userLink, userAddressesLink, selfLink));

//        returnValue.add(userLink);
//        returnValue.add(userAddressesLink);
//        returnValue.add(selfLink);

        return EntityModel.of(returnValue, Arrays.asList(userLink, userAddressesLink, selfLink));
    }


    @GetMapping(path = "/email-verification", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public OperationStatusModel verifyEmailToken(@RequestParam(value = "token") String token){
        OperationStatusModel returnValue = new OperationStatusModel();
        returnValue.setOperationName(RequestOperationName.VERIFY_EMAIL.name());

        boolean isVerified = userService.verifyEmailToken(token);

        if(isVerified){
            returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
        }
        else{
            returnValue.setOperationResult(RequestOperationStatus.ERROR.name());
        }

        return returnValue;
    }
}
