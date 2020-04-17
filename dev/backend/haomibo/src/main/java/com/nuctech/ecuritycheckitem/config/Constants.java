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
import java.util.TreeMap;

public class Constants {

    public static final int DEFAULT_JWT_VALIDITY_SECONDS = 20 * 60;
    public static final long MAX_JWT_VALIDITY_SECONDS = 60 * 60;
    public static final String REQUEST_HEADER_AUTH_TOKEN_KEY = "X-AUTH-TOKEN";
    public static final long TASK_PERIOD_SECONDS_CLEAN_FORBIDDEN_TOKEN_TABLE = 24 * 60 * 60;
    public static final String DEFAULT_PASSWORD_FOR_NEW_SYS_USER = "default";

    public static final String PORTRAIT_FILE_UPLOAD_DIRECTORY = "portrait";//"storage" + File.separator + "portrait";
    public static final String PORTRAIT_FILE_SERVING_BASE_URL = "/portrait/";

    public static final int JUDGE_CATEGORY_ID = 2;
    public static final int MANUAL_CATEGORY_ID = 4;
    public static final int SECURITY_CATEGORY_ID = 3;
    public static final int DEVICE_ONLINE = 0;
    public static final String LOGIN_STATUS = "1000001001";
    public static final long DEFAULT_MODE_ID = 1L;
    public static final long DEFAULT_AIR_CALIWARN_TIME = 120L;
    public static final long DEFAULT_STANDBY_TIME = 0L;
    public static final long DEFAULT_RECOGNIZE_RATE = 0L;
    public static final int DEFAULT_STORAGE_ALARM = 5;
    public static final int DEFAULT_STORAGE_PERCENT = 80;
    public static final String CHINESE_LOCALE = "zh";
    public static final String ENGLISH_LOCALE = "en";
    public static final int EXPIRE_TIME = 24 * 60 * 60;

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

    public static final String PDF_DATETIME_FORMAT = "MM/dd/yyyy HH:mm:ss";
    public static final String EXCEL_DATETIME_FORMAT = "yyyy/MM/dd";
    public static final String LOG_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    public static final String REDIS_SECURITY_INFO = "sys.security.info";
    public static final String REDIS_PLATFORM_CHECK = "sys.setting.platform.check";
    public static final String REDIS_CATEGORY_USER_INFO = "sys.category.user.info";

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




    public static Long SEIZED_DICTIONARY_ID = 16L;

    public static final Long DEFAULT_SYSTEM_USER = Long.valueOf(10000);

    public static final Long MAX_EXPORT_NUMBER = 50000L;

    public static final TreeMap<String, Long> userCategory = new TreeMap<String, Long>() {{
        put("1000002404", 180L);
        put("1000002403", 181L);
        put("1000002402", 182L);
    }};


    public static class WorkMode {

        public static final String MODE_1000002601 = "1000002601";
        public static final String MODE_1000002602 = "1000002602";
        public static final String MODE_1000002603 = "1000002603";
        public static final String MODE_1000002604 = "1000002604";

    }


}
