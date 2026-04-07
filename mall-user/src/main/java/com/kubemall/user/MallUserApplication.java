package com.kubemall.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.kubemall.user.security.JwtProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class MallUserApplication {

	
	public static void main(String[] args) {
		SpringApplication.run(MallUserApplication.class, args);
	}

}
