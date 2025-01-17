package com.techsorcerer.mobile_app_ws.service.impl;




import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


import com.techsorcerer.mobile_app_ws.exceptions.UserServiceException;
import com.techsorcerer.mobile_app_ws.io.entity.UserEntity;
import com.techsorcerer.mobile_app_ws.io.repository.UserRepository;
import com.techsorcerer.mobile_app_ws.service.UserService;
import com.techsorcerer.mobile_app_ws.shared.Utils;
import com.techsorcerer.mobile_app_ws.shared.dto.AddressDto;
import com.techsorcerer.mobile_app_ws.shared.dto.UserDto;
import com.techsorcerer.mobile_app_ws.ui.response.ErrorMessages;



@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	Utils utils;

	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@Override
	public UserDto createUser(UserDto user) {

		if (userRepository.findByEmail(user.getEmail()) != null)
			throw new RuntimeException("Record already exists");
		
		//Now each addressDTO has a list of addresses and has a user_id, user details and can set the adresses for each user
		for(int i = 0; i < user.getAddresses().size(); i++) {
			AddressDto addressDto = user.getAddresses().get(i);
			addressDto.setUserDetails(user);
			addressDto.setAddressId(utils.generateAddressId(30));
			user.getAddresses().set(i, addressDto);   //set all the details to userDto object(user) which is going to be returned
		}

//		UserEntity userEntity = new UserEntity();
//		BeanUtils.copyProperties(user, userEntity);
		ModelMapper modelMapper = new ModelMapper();
		UserEntity userEntity = modelMapper.map(user, UserEntity.class);

		String publicUserId = utils.generateUserId(30);
		userEntity.setUserId(publicUserId);
		userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		userEntity.setEmailVerificationToken(utils.generateEmailVerificationToken(publicUserId));
		userEntity.setEmailVerificationStatus(false);

		UserEntity storeUserDetails = userRepository.save(userEntity);

//		BeanUtils.copyProperties(storeUserDetails, returnValue);
		UserDto returnValue = modelMapper.map(storeUserDetails, UserDto.class);
		

		return returnValue;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserEntity userEntity = userRepository.findByEmail(username);

		if (userEntity == null)
			throw new UsernameNotFoundException(username);
//		return new User(username, userEntity.getEncryptedPassword(), new ArrayList<>());
		return new User(userEntity.getEmail(),userEntity.getEncryptedPassword(), userEntity.getEmailVerificationStatus(), true,
				true, true,
				new ArrayList<>());
	}

	@Override
	public UserDto getUser(String email) {
		UserEntity userEntity = userRepository.findByEmail(email);
		if (userEntity == null)
			throw new UsernameNotFoundException(email);

		UserDto returnValue = new UserDto();
		BeanUtils.copyProperties(userEntity, returnValue);
		return returnValue;
	}

	@Override
	public UserDto getUserByUserId(String userId) {
		UserDto returnValue = new UserDto();
		UserEntity userEntity = userRepository.findByUserId(userId);

		if (userEntity == null)
			throw new UserServiceException("User with "+userId+" not found");

		BeanUtils.copyProperties(userEntity, returnValue);

		return returnValue;
	}

	@Override
	public UserDto updateUser(String userId, UserDto userDto) {
		UserDto returnValue = new UserDto();
		UserEntity userEntity = userRepository.findByUserId(userId);
		
		if (userEntity == null)
			throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
		
		if(userDto.getFirstName() == null|| userDto.getFirstName().isBlank() || userDto.getLastName() == null || userDto.getLastName().isBlank() ) {
			throw new UserServiceException("FirstName and LastName are required");
		}
		
		userEntity.setFirstName(userDto.getFirstName());
		userEntity.setLastName(userDto.getLastName());
		
		UserEntity upatedUserDetails = userRepository.save(userEntity);
		
		BeanUtils.copyProperties(upatedUserDetails, returnValue);
		
		return returnValue;
	}

	@Override
	public void deleteUser(String userId) {
		
		UserEntity userEntity = userRepository.findByUserId(userId);
		
		if (userEntity == null)
			throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
		
		userRepository.delete(userEntity);
		
	}

	@Override
	public List<UserDto> getUsers(int page, int limit) {
		List<UserDto> returnValue = new ArrayList<>();
		
		if(page>0) {
			page = page -1 ;
		}
		
		Pageable pageableRequest =  PageRequest.of(page, limit);
		
		
		Page<UserEntity> usersPage =  userRepository.findAll(pageableRequest);
		List<UserEntity> users = usersPage.getContent();
		
		for(UserEntity userEntity : users) {
			UserDto userDto = new UserDto();
			BeanUtils.copyProperties(userEntity, userDto);
			returnValue.add(userDto);
		}
		
		return returnValue ; 
	}

	@Override
	public boolean verifyEmailToken(String token) {
		boolean returnvalue = false;
		
		//find user by token
		UserEntity userEntity = userRepository.findUserByEmailVerificationToken(token);
		
		if(userEntity != null) {
			boolean hasTokenExpired = Utils.hasTokenExpired(token);
			if(!hasTokenExpired) {
				userEntity.setEmailVerificationToken(null);
				userEntity.setEmailVerificationStatus(Boolean.TRUE);
				userRepository.save(userEntity);
				returnvalue = true;
			}
		}
		return returnvalue;
	}
	
	

}
