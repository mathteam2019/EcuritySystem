package com.nuctech.ecuritycheckitem.config;

import lombok.var;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;


@Configuration
public class LanguageConfig {

    @Bean
    public ResourceBundleMessageSource messageSource() {

        System.out.println("tiny-debug messageConfiguration");
        var source = new ResourceBundleMessageSource();
        this.getClass().getClassLoader().getResource(Constants.PDF_HEADER_FONT_RESOURCE_PATH);

        source.setBasenames("language/messages");
        source.setUseCodeAsDefaultMessage(true);

        return source;
    }
}

