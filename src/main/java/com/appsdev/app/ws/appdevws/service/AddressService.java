package com.appsdev.app.ws.appdevws.service;

import com.appsdev.app.ws.appdevws.shared.dto.AddressDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AddressService {
    List<AddressDTO> getUserAddresses(String userId);
}
