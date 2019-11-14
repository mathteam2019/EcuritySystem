package com.nuctech.ecuritycheckitem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main start of the project.
 */
@SpringBootApplication
@EnableScheduling
public class HaomiboApplication {

    public static void main(String[] args) {
        SpringApplication.run(HaomiboApplication.class, args);
    }

}