package com.powerstats;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PowerGenerationStatApplication {

    public static void main(String[] args) {
        SpringApplication.run(PowerGenerationStatApplication.class, args);
    }
}
