package com.haomibo.haomibo.config;

import java.io.File;

public class Constants {

    public static final long JWT_VALIDITY_SECONDS = 2 * 60 * 60;
    public static final String TOKEN_PREFIX = "haomibo_";
    public static final String REQUEST_HEADER_AUTH_TOKEN_KEY = "X-AUTH-TOKEN";
    public static final long TASK_PERIOD_SECONDS_CLEAN_FORBIDDEN_TOKEN_TABLE = 24 * 60 * 60;
    public static final String DEFAULT_PASSWORD_FOR_NEW_SYS_USER = "123456";

    public static final String PORTRAIT_FILE_UPLOAD_DIRECTORY = "storage" + File.separator + "portrait";
    public static final String PORTRAIT_FILE_SERVING_BASE_URL = "/portrait/";


    public static class Roles {
        public static final String SYS_USER = "ROLE_SYS_USER";
    }

    public static class ResponseMessages {
        public static final String OK = "ok";
        public static final String INVALID_PARAMETER = "invalid_parameter";
        public static final String USER_NOT_FOUND = "user_not_found";
        public static final String INVALID_PASSWORD = "invalid_password";
        public static final String TOKEN_EXPIRED = "token_expired";
        public static final String INVALID_TOKEN = "invalid_token";
        public static final String USED_EMAIL = "used_email";
        public static final String SERVER_ERROR = "server_error";
        public static final String HAS_CHILDREN = "has_children";
        public static final String USED_USER_ACCOUNT = "used_user_account";
    }


}
