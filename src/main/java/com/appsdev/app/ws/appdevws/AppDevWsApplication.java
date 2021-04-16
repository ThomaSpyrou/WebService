package com.appsdev.app.ws.appdevws;

import com.appsdev.app.ws.appdevws.security.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class AppDevWsApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppDevWsApplication.class, args);
	}


	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder(){
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SpringApplicationContext springApplicationContext(){return new SpringApplicationContext();}

	@Bean(name = "AppProperties")
	public AppProperties appProperties(){return new AppProperties();}


}
