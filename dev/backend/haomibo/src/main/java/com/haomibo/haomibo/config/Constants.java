package com.haomibo.haomibo.config;

public class Constants {

    public static final long ACCESS_TOKEN_VALIDITY_SECONDS = 24 * 60 * 60;
    public static final String SIGNING_KEY = "bszc1EKBohxgAaqwYq2ygyyv3Rp3EM7pmSyUPsDuJbzcI2HBdkheHl8i6wqyl1Pu";
    public static final String TOKEN_PREFIX = "haomibo_";
    public static final String HEADER_STRING = "X-AUTH-TOKEN";

    public static class ResponseMessages {
        public static final String OK = "ok";
        public static final String INVALID_PARAMETER = "invalid_parameter";
        public static final String USER_NOT_FOUND = "user_not_found";
        public static final String INVALID_PASSWORD = "invalid_password";
    }

}
