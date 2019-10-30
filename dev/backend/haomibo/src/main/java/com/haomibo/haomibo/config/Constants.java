package com.haomibo.haomibo.config;

import java.io.File;

/**
 * Defines constants for this project.
 */
public class Constants {

    public static final long JWT_VALIDITY_SECONDS = 2 * 60 * 60;
    public static final String REQUEST_HEADER_AUTH_TOKEN_KEY = "X-AUTH-TOKEN";
    public static final long TASK_PERIOD_SECONDS_CLEAN_FORBIDDEN_TOKEN_TABLE = 24 * 60 * 60;
    public static final String DEFAULT_PASSWORD_FOR_NEW_SYS_USER = "123456";

    public static final String PORTRAIT_FILE_UPLOAD_DIRECTORY = "storage" + File.separator + "portrait";
    public static final String PORTRAIT_FILE_SERVING_BASE_URL = "/portrait/";

    public static final String[] EXCLUDE_URL_PATTERNS = {
            "/",
            "/csrf",
            "/Auth/**",
            "/v2/api-docs", "/configuration/ui", "/swagger-resources/**", "/configuration/**", "/swagger-ui.html", "/webjars/**",
            "/auth/login",
            "/auth/register",
            Constants.PORTRAIT_FILE_SERVING_BASE_URL + "**"
    };

}
