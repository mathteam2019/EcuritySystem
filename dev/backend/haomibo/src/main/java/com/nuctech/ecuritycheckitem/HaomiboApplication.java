/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/10/11
 * @CreatedBy Sandy.
 * @FileName HamobiApplication.java
 * @ModifyHistory
 *
 */
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
