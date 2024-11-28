package com.techsorcerer.mobile_app_ws.io.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.techsorcerer.mobile_app_ws.io.entity.UserEntity;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Long> {
	//This are called as Query methods and we have to use find
	UserEntity findByEmail(String email);
	UserEntity findByUserId(String userId);
}
