package com.example.concurrencycontrolproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ConcurrencyControlProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConcurrencyControlProjectApplication.class, args);
    }

}
