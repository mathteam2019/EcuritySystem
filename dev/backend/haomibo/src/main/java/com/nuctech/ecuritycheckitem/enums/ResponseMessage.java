/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/10/30
 * @CreatedBy Sandy.
 * @FileName ResponseMessage.java
 * @ModifyHistory
 *
 */
package com.nuctech.ecuritycheckitem.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Defines response messages
 */
@AllArgsConstructor
@Getter
public enum ResponseMessage {
    OK("ok"),
    INVALID_PARAMETER("invalid_parameter"),
    INVALID_SCANID("invalid_serscan_id"),
    USER_NOT_FOUND("user_not_found"),
    INVALID_PASSWORD("invalid_password"),
    TOKEN_EXPIRED("token_expired"),
    INVALID_TOKEN("invalid_token"),
    USED_EMAIL("used_email"),
    SERVER_ERROR("server_error"),
    HAS_CHILDREN("has_children"),
    USED_USER_ACCOUNT("used_user_account"),
    USED_MOBILE("used_mobile"),
    FORBIDDEN("forbidden"),
    BAD_REQUEST("bad_request"),
    HAS_USERS("has_users"),
    HAS_USER_GROUPS("has_user_groups"),
    HAS_RESOURCES("has_resources"),
    HAS_ARCHIVES("has_archives"),
    HAS_ARCHIVE_TEMPLATE("has_archive_template"),
    HAS_DEVICES("has_devices"),
    USED_CATEGORY_NAME("used_category_name"),
    USED_CATEGORY_NUMBER("used_category_number"),
    USED_TEMPLATE_NAME("used_template_name"),
    USED_TEMPLATE_NUMBER("used_template_number"),
    USED_ARCHIVE_NAME("used_archive_name"),
    USED_ARCHIVE_NUMBER("used_archive_number"),
    USED_DEVICE_NAME("used_device_name"),
    USED_DEVICE_SERIAL("used_device_serial"),
    USED_DEVICE_GUID("used_device_guid"),;

    private final String value;

}
