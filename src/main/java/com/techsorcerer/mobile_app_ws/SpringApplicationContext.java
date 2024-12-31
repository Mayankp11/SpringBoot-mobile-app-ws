package com.techsorcerer.mobile_app_ws;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;


//This is an helper class to access Spring beans when you cannot create beans of a class
@Component
public class SpringApplicationContext implements ApplicationContextAware {

	private static ApplicationContext CONTEXT;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		CONTEXT = applicationContext; //ApplicationContext is an argument that can be used to access bean in the Spring Context
	}
	
	// can be used to access any bean
	public static Object getBean(String beanName) {
		return CONTEXT.getBean(beanName);
	}

}
