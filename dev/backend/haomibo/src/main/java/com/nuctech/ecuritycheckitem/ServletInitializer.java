/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/10/11
 * @CreatedBy Sandy.
 * @FileName ServletInitializer.java
 * @ModifyHistory
 *
 */
package com.nuctech.ecuritycheckitem;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Initialize spring servlet.
 */
public class ServletInitializer extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(HaomiboApplication.class);
    }

}
