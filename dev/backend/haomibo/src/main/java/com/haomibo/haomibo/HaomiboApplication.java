package com.haomibo.haomibo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HaomiboApplication {

    public static void main(String[] args) {
        SpringApplication.run(HaomiboApplication.class, args);
    }

}
