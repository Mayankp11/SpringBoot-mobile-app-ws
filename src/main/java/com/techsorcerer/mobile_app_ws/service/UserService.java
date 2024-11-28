package com.techsorcerer.mobile_app_ws.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.techsorcerer.mobile_app_ws.shared.dto.UserDto;

public interface UserService extends UserDetailsService {
	UserDto createUser(UserDto user);
	UserDto getUser(String email);
	UserDto getUserByUserId(String userId);
}
