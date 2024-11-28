package com.techsorcerer.mobile_app_ws.security;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techsorcerer.mobile_app_ws.SpringApplicationContext;
import com.techsorcerer.mobile_app_ws.service.UserService;
import com.techsorcerer.mobile_app_ws.service.impl.UserServiceImpl;
import com.techsorcerer.mobile_app_ws.shared.dto.UserDto;
import com.techsorcerer.mobile_app_ws.ui.model.request.UserLoginRequestModel;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKey;


//this class is used to authenticate UserRequest, it will read username and password and check if it is correct and then allow login
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter{
	
	//AuthenticationManager is used during authenciation process and it has only one method called authenticate
	public AuthenticationFilter(AuthenticationManager authenticationManager) {
		super(authenticationManager);
	}
	
	 @Override
	 //has 2 parameters, login request will pass through  this filter
	    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
	            throws AuthenticationException {
	        try {

	        	//It will read the username and password from JSON body and the info will be mobbed to obj of UserLoginRequestModel
	            UserLoginRequestModel creds = new ObjectMapper().readValue(req.getInputStream(), UserLoginRequestModel.class);

	            return getAuthenticationManager().authenticate(
	            		// is used to read email and password and creats a obj UsernamePasswordAuthenticationToken and pass the obj and trigger
	            		//getAuthenticationManager().authenticate
	                    new UsernamePasswordAuthenticationToken(creds.getEmail(), creds.getPassword(), new ArrayList<>())); 

	        } catch (IOException e) {
	            throw new RuntimeException(e);
	        }
	    }
	 

	    @Override
	    //one authentication is successfull, this method will be revoked
	    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain,
	            Authentication auth) throws IOException, ServletException {

	    	//This method will specifically genetrate JWT access token and then return JWT. 
	    	//bytes will be taken from Token_Secret and then encode to new byte array by using Base64.getEncoder()
	        byte[] secretKeyBytes = Base64.getEncoder().encode(SecurityConstants.getTokenSecret().getBytes());
	      
	        // the byte array will generate secret key and use to sign JWT access token in the code below
			SecretKey secretKey = new SecretKeySpec(secretKeyBytes, SignatureAlgorithm.HS512.getJcaName());
	        Instant now = Instant.now(); //used to date of generation and expiration
 
	        String userName = ((User) auth.getPrincipal()).getUsername(); // read username and add it to JWT access token by setSubject
	       
			String token = Jwts.builder()
	                .setSubject(userName)
	                .setExpiration(
	                        Date.from(now.plusMillis(SecurityConstants.EXPIRATION_TIME)))
	                .setIssuedAt(Date.from(now)).signWith(secretKey, SignatureAlgorithm.HS512).compact();
			
			
		
			UserService userService =(UserService) SpringApplicationContext.getBean("userServiceImpl");//beacause it does not have a custom bean name, so use class name but starting with lowerCase
		    UserDto userDto = userService.getUser(userName);
		
	        // add it as a header, so that when client receives the response, will be able to read the token
	        res.addHeader(SecurityConstants.HEADER_STRING, SecurityConstants.TOEKN_PREFIX + token);
	        res.addHeader("UserId", userDto.getUserId());
	    }
}
