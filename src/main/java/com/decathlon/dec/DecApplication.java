package com.decathlon.dec;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DecApplication {

	public static void main(String[] args) {
		//SpringApplication.run(DecApplication.class, args);
	SpringApplication app = new SpringApplication(DecApplication.class);
    app.setAdditionalProfiles("dev");
    app.run(args);
		
	}

}
