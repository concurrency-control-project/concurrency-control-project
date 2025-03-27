package com.example.concurrencycontrolproject;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.*;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableCaching
@EnableScheduling
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class ConcurrencyControlProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConcurrencyControlProjectApplication.class, args);
	}

}
