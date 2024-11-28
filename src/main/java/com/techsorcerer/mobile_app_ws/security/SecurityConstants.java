package com.techsorcerer.mobile_app_ws.security;



import org.springframework.core.env.Environment;

import com.techsorcerer.mobile_app_ws.SpringApplicationContext;

public class SecurityConstants {
	
	public static final long EXPIRATION_TIME=864000000; //10 DAYS // When we generate a token it will be available for 10 days
	public static final String TOEKN_PREFIX="Brearer"; //after this will be the token
	public static final String HEADER_STRING="Authorization";
	public static final String SIGN_UP_URL="/users";
	public static final String TOKEN_SECRET="ugsiadgi383477mdim37habjksb377dfs29svsf45f5bfdk33ln38hvhjkv9490ghdsgfsife738hbshf73fs278bag93bi2cbbk3";
	
	
	public static String getTokenSecret() {
		Environment environment = (Environment) SpringApplicationContext.getBean("environment");
		return environment.getProperty("tokenSecret"); //lowercase the name of the class bcoz there is no bean created
	}

}
