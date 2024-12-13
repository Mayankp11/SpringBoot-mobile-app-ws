package com.techsorcerer.mobile_app_ws.ui.controller;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.techsorcerer.mobile_app_ws.exceptions.UserServiceException;
import com.techsorcerer.mobile_app_ws.service.AddressService;
import com.techsorcerer.mobile_app_ws.service.UserService;
import com.techsorcerer.mobile_app_ws.shared.dto.AddressDto;
import com.techsorcerer.mobile_app_ws.shared.dto.UserDto;
import com.techsorcerer.mobile_app_ws.ui.model.request.UserDetailsRequestModel;
import com.techsorcerer.mobile_app_ws.ui.response.AddressesRest;
import com.techsorcerer.mobile_app_ws.ui.response.ErrorMessages;
import com.techsorcerer.mobile_app_ws.ui.response.OperationStatusModel;
import com.techsorcerer.mobile_app_ws.ui.response.RequestOperationName;
import com.techsorcerer.mobile_app_ws.ui.response.RequestOperationStatus;
import com.techsorcerer.mobile_app_ws.ui.response.UserRest;

@RestController
@RequestMapping("users") // http://localhost:8080/users
public class UserController {

	@Autowired
	UserService userService;
	
	@Autowired
	AddressService addressesService;

	@GetMapping(path = "/{id}", produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public UserRest getUser(@PathVariable String id) {

		UserRest returnValue = new UserRest();
		UserDto userDto = userService.getUserByUserId(id);
		BeanUtils.copyProperties(userDto, returnValue);
		return returnValue;
	}

	@PostMapping(consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) throws Exception {
		// The JSON will contain UserDetailsRequestModel
		// obj(firstName,lastName,email,password) then the JSON will be converted to
		// java obj of the class and used in business logic
		
		
		UserRest returnValue = new UserRest();

		if (userDetails.getFirstName().isEmpty())
			throw new NullPointerException("The object is null");
//		if(userDetails.getEmail().isEmpty()) throw new UserServiceException(ErrorMessages.EMAIL_ID_IS_MISSING.getErrorMessage());

//		UserDto userDto = new UserDto();
//		BeanUtils.copyProperties(userDetails, userDto);
		
		ModelMapper modelMapper = new ModelMapper();
		UserDto userDto = modelMapper.map(userDetails, UserDto.class);

		UserDto createdUser = userService.createUser(userDto);
		returnValue = modelMapper.map(createdUser, UserRest.class);

		return returnValue;
	}

	@PutMapping(path="/{id}",consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
	public UserRest updateUser(@PathVariable String id, @RequestBody UserDetailsRequestModel userDetails) {
		UserRest returnValue = new UserRest();
		
//		UserDto userDto = new UserDto();
//		BeanUtils.copyProperties(userDetails, userDto);
		
		ModelMapper modelMapper = new ModelMapper();
		UserDto userDto = modelMapper.map(userDetails, UserDto.class);
		
		UserDto updateUser = userService.updateUser(id, userDto);
		BeanUtils.copyProperties(updateUser, returnValue);
		
		return returnValue;
	}

	@DeleteMapping(path="/{id}",produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
	public OperationStatusModel deleteUser(@PathVariable String id) {
		
		OperationStatusModel returnValue = new OperationStatusModel();
		returnValue.setOperationName(RequestOperationName.DELETE.name());
		
		userService.deleteUser(id);
		
		returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		return returnValue;
	}
	
	@GetMapping(produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
	public List<UserRest> getUsers(@RequestParam(value="page", defaultValue = "0")int page, 
			@RequestParam(value = "limit", defaultValue = "50")int limit){
		List<UserRest> returnValue = new ArrayList<>();
		
		List<UserDto> users = userService.getUsers(page, limit);
		
		for(UserDto userDto : users) {
			UserRest userModel = new UserRest();
			BeanUtils.copyProperties(userDto, userModel);
			returnValue.add(userModel);
		}
		
		return returnValue;
		
	}
	
	//http://localhost:8080/mobile-app-ws/users/hjlcdskjchlswui/address
	@GetMapping(path = "/{id}/addresses", produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public List<AddressesRest> getUserAddresses(@PathVariable String id) {
		List<AddressesRest> returnValue = new ArrayList<>();
		
		List<AddressDto> addressesDto = addressesService.getAddresses(id);
		
		if(addressesDto != null && !addressesDto.isEmpty()) {
			
		java.lang.reflect.Type listType = new TypeToken<List<AddressesRest>>() {}.getType();
		returnValue =  new ModelMapper().map(addressesDto, listType);
		}
		
		return returnValue;
	}
	
}





