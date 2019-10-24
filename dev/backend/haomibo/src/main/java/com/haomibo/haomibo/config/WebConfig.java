package com.haomibo.haomibo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.nio.file.Paths;


@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedMethods("HEAD", "GET", "PUT", "POST", "DELETE", "PATCH");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // we can use System.getProperty("user.dir") too ~~
        String baseAbsolutePath = Paths.get("").toAbsolutePath().toString() + File.separator;

        registry
                .addResourceHandler(Constants.PORTRAIT_FILE_SERVING_BASE_URL + "**")
                .addResourceLocations("file:///" + baseAbsolutePath + Constants.PORTRAIT_FILE_UPLOAD_DIRECTORY + File.separator);

    }
}
