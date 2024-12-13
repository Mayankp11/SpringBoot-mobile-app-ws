package com.techsorcerer.mobile_app_ws.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.techsorcerer.mobile_app_ws.io.entity.AddressEntity;
import com.techsorcerer.mobile_app_ws.io.entity.UserEntity;
import com.techsorcerer.mobile_app_ws.io.repository.UserRepository;
import com.techsorcerer.mobile_app_ws.service.AddressService;
import com.techsorcerer.mobile_app_ws.shared.dto.AddressDto;

public class AddressServiceImpl implements AddressService {
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	AddressRepository addressRepository;

	@Override
	public List<AddressDto> getAddresses(String userId) {
		List<AddressDto> returnValue = new ArrayList<>();
		ModelMapper modelMapper = new ModelMapper();
		
		UserEntity userEntity = userRepository.findByUserId(userId);
		
		if(userEntity == null) return returnValue;
		
		Iterable<AddressEntity> addresses = addressRepository.findAllByUserDetails(userEntity);
		for(AddressEntity addressEntity:addresses) {
			returnValue.add(modelMapper.map(addressEntity, AddressDto.class));
		}
		
		return null;
	}

}
