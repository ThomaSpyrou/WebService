package com.appsdev.app.ws.appdevws.service.implementation;

import com.appsdev.app.ws.appdevws.io.entity.AddressEntity;
import com.appsdev.app.ws.appdevws.io.entity.UserEntity;
import com.appsdev.app.ws.appdevws.io.repositories.AddressRepository;
import com.appsdev.app.ws.appdevws.io.repositories.UserRepository;
import com.appsdev.app.ws.appdevws.service.AddressService;
import com.appsdev.app.ws.appdevws.shared.dto.AddressDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class AddressServiceImplementation implements AddressService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AddressRepository addressRepository;

    @Override
    public List<AddressDTO> getUserAddresses(String userId) {
        List<AddressDTO> addressDTOList = new ArrayList<>();
        UserEntity userEntity = userRepository.findUserEntityByUserId(userId);
        ModelMapper modelMapper = new ModelMapper();

        if(userEntity == null){
            return addressDTOList;
        }

        Iterable<AddressEntity> addresses = addressRepository.findAllByUserDetails(userEntity);
        for(AddressEntity addressEntity: addresses){
            addressDTOList.add(modelMapper.map(addressEntity, AddressDTO.class));
        }

        return addressDTOList;
    }
}
