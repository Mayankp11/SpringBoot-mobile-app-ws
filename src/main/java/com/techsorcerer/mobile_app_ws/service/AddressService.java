package com.techsorcerer.mobile_app_ws.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.techsorcerer.mobile_app_ws.shared.dto.AddressDto;


public interface AddressService {
	List<AddressDto> getAddresses(String userId);

}
