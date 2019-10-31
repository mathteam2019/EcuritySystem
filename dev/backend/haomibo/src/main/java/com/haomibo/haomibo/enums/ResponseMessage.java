package com.haomibo.haomibo.enums;

import lombok.Getter;

/**
 * Defines response messages
 */
public enum ResponseMessage {
    OK("ok"),
    INVALID_PARAMETER("invalid_parameter"),
    USER_NOT_FOUND("user_not_found"),
    INVALID_PASSWORD("invalid_password"),
    TOKEN_EXPIRED("token_expired"),
    INVALID_TOKEN("invalid_token"),
    USED_EMAIL("used_email"),
    SERVER_ERROR("server_error"),
    HAS_CHILDREN("has_children"),
    USED_USER_ACCOUNT("used_user_account"),
    FORBIDDEN("forbidden");

    @Getter
    private final String value;

    ResponseMessage(String value) {
        this.value = value;
    }
}
