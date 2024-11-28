package com.techsorcerer.mobile_app_ws.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import com.techsorcerer.mobile_app_ws.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;



@Configuration
public class WebSecurity {

	private final UserService userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public WebSecurity(UserService userDetailsService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userDetailsService = userDetailsService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @SuppressWarnings("removal")
	@Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
    	
    	AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
    	authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    	
    	AuthenticationManager authenticationManager = authenticationManagerBuilder.build();
    	
    	// Customize Login URL path
    	AuthenticationFilter authenticationFilter = new AuthenticationFilter(authenticationManager);
    	authenticationFilter.setFilterProcessesUrl("/users/login");
    	
        http.csrf().disable() //because I'm building stateless REST API, this is disable, if it was web application then no need to disable it
            .authorizeHttpRequests(auth -> {
				try {
					auth
					    .requestMatchers(HttpMethod.POST, SecurityConstants.SIGN_UP_URL).permitAll()  // Allow sign-up without authentication
					    .anyRequest().authenticated() // All other requests require authentication
					    .and()
					    .authenticationManager(authenticationManager)
					    .addFilter(authenticationFilter)
					    .addFilter(new AuthorizationFilter(authenticationManager))
					    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); 
//this will mean that spring security will never create a HTTP session and will never use it to obtain security context. And this means that
//because there is no HTTP session created for user authorization, spring security will rely only on information that is inside of JSON web token. 
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
            )
            .httpBasic(); // Basic HTTP authentication (can be customized as needed)
        return http.build();
    }
}
