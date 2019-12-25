/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（Static Constant Values）
 * 文件名：	Constants.java
 * 描述：	Static constants used in System
 * 作者名：	Sandy
 * 日期：	2019/10/14
 *
 */

package com.nuctech.ecuritycheckitem.config;

import java.io.File;

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
    public static final String PDF_HEADER_FONT_RESOURCE_PATH = "classpath:font/NotoSansCJKsc-Regular.otf";

    public static final short WORD_HEAD_FONT_SIZE = 24;
    public static final String WORD_HEAD_FONT_NAME = "Arial";

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

    //add by tiny 2019/12/12
    public static class StatisticWidth {
        public static final String HOUR = "hour";
        public static final String DAY = "day";
        public static final String WEEK = "week";
        public static final String MONTH = "month";
        public static final String QUARTER = "quarter";
        public static final String YEAR = "year";
    }

    //add by tiny 2019/12/24
    public static class SortOrder {
        public static final String ASC = "asc";
        public static final String DESC = "desc";
    }

}
