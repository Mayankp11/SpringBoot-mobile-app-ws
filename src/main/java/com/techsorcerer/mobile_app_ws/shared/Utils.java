package com.techsorcerer.mobile_app_ws.shared;

import java.security.SecureRandom;
import java.util.Date;
import java.util.Random;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import com.techsorcerer.mobile_app_ws.security.SecurityConstants;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class Utils {

	private final Random RANDOM = new SecureRandom();
	private final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqurstuvwxyz";

	public String generateUserId(int length) {
		return generateRandomString(length);
	}

	public String generateAddressId(int length) {
		return generateRandomString(length);
	}

	private String generateRandomString(int length) {
		StringBuilder returnValue = new StringBuilder();

		for (int i = 0; i < length; i++) {
			returnValue.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
		}

		return new String(returnValue);
	}

	public static boolean hasTokenExpired(String token) {
		try {
			// Parse the token using the parser builder
			SecretKey secretKey = Keys.hmacShaKeyFor(SecurityConstants.getTokenSecret().getBytes());

			Claims claims = Jwts.parser().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
			// Get the expiration date from the claims
			Date tokenExpirationDate = claims.getExpiration();

			// Get the current date and time
			Date todayDate = new Date();

			// Return true if the token has expired
			return tokenExpirationDate.before(todayDate);
		} catch (Exception e) {
			// Handle exceptions (e.g., token parsing errors)
			return true; // Consider the token expired or invalid
		}
	}

	  public String generateEmailVerificationToken(String userId) {
	        // Convert the secret key (string) into a SecretKey object
	        SecretKey secretKey = Keys.hmacShaKeyFor(SecurityConstants.getTokenSecret().getBytes());

	        // Generate the token with the SecretKey
	        String token = Jwts.builder()
	                .setSubject(userId)
	                .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
	                .signWith(secretKey, SignatureAlgorithm.HS512)  // Use SecretKey with signWith()
	                .compact();

	        return token;
	    }
}
