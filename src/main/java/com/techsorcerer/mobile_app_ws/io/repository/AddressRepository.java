package com.techsorcerer.mobile_app_ws.io.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.techsorcerer.mobile_app_ws.io.entity.AddressEntity;
import com.techsorcerer.mobile_app_ws.io.entity.UserEntity;

@Repository
public interface AddressRepository extends CrudRepository<AddressEntity, Long> { //In AddressEntity class, we have the db id which is auto incremented in the AddressEntity
	List<AddressEntity> findAllByUserDetails(UserEntity userEntity);

	AddressEntity findByAddressId(String addressId);
}
