/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/10/14
 * @CreatedBy Sandy.
 * @FileName Constants.java
 * @ModifyHistory
 */
package com.nuctech.ecuritycheckitem.config;

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

    public static final short EXCEL_HEAD_FONT_SIZE = 14;
    public static final String EXCEL_HEAD_FONT_NAME = "Arial";

    public static final short PDF_TITLE_FONT_SIZE = 24;
    public static final String PDF_TITLE_FONT_NAME = "Arial";
    public static final short PDF_HEAD_FONT_SIZE = 14;
    public static final short PDF_CONTENT_FONT_SIZE = 14;
    public static final short PDF_TITLE_SPACING = 20;
    public static final short PDF_TIME_SPACING = 10;

    public static final String PDF_DATETIME_FORMAT = "MM/dd/yyyy HH:mm";
    public static final String EXCEL_DATETIME_FORMAT = "yyyy/MM/dd";
    public static final String LOG_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

}
