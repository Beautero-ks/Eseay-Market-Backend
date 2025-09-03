package com.ninehub.dreamshops;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class DreamShopsApplication {

	public static void main(String[] args) {
		SpringApplication.run(DreamShopsApplication.class, args);
		System.out.println("SpringBoot application started successfully... ✅");
	}

}
