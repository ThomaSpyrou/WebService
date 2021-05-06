package com.appsdev.app.ws.appdevws.service;

import com.appsdev.app.ws.appdevws.shared.dto.AddressDTO;
import java.util.List;


public interface AddressService {
    List<AddressDTO> getUserAddresses(String userId);
    AddressDTO getUserAddress(String addressId);
}
