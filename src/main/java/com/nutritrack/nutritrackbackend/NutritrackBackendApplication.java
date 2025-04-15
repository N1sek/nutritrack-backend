package com.nutritrack.nutritrackbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
public class NutritrackBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(NutritrackBackendApplication.class, args);
    }

}
