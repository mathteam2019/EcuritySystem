/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（ResponseMessage enum）
 * 文件名：	ResponseMessage.java
 * 描述：	Response Message
 * 作者名：	Sandy
 * 日期：	2019/10/30
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
    OK("ok"), //message in case of OK
    INVALID_PARAMETER("invalid_parameter"), //response message in case of invalid parameter
    INVALID_SCANID("invalid_serscan_id"),  //response message in case of invalid serscan id detected
    USER_NOT_FOUND("user_not_found"),  //response message in case of user not found
    USER_PENDING_STATUS("user_pending_status"),  //response message in case of user pending statue
    USER_NON_ACTIVE_STATUS("user_non_active_status"),  //response message in case of user non active statue
    PRE_USER_PENDING_STATUS("pre_user_pending_status"),  //response message in case of one time before of user pending statue
    INVALID_PASSWORD("invalid_password"),  //response message in case of invalid password
    TOKEN_EXPIRED("token_expired"),  //response message in case of token expired
    INVALID_TOKEN("invalid_token"),  //response message in case of invalid token
    USED_EMAIL("used_email"),  //response message in case of used email
    SERVER_ERROR("server_error"),  //response message in case of server error
    EXIST_USER("exist_user"), //response message in case of create assign user role
    EXIST_USER_GROUP("exist_user_group"), //response message in case of create assign user group role
    HAS_CHILDREN("has_children"),  //response message in case of has children
    USED_USER_ACCOUNT("used_user_account"),  //response message in case of used user account
    USED_USER_NUMBER("used_user_number"),  //response message in case of used user account
    USED_MOBILE("used_mobile"),  //response message in case of used mobile
    FORBIDDEN("forbidden"),  //response message in case of forbidden
    BAD_REQUEST("bad_request"),  //response message in case of bad request
    HAS_USERS("has_users"),  //response message in case of has users
    HAS_USER_GROUPS("has_user_groups"),  //response message in case of has user groups
    HAS_DATA_GROUPS("has_data_groups"),  //response message in case of has data groups
    HAS_ROLES("has_roles"),  //response message in case of has roles
    HAS_FIELDS("has_fields"),  //response message in case of has fields
    HAS_RESOURCES("has_resources"),  //response message in case of has resources
    HAS_ARCHIVES("has_archives"),  //response message in case of has archives
    HAS_ARCHIVE_TEMPLATE("has_archive_template"),  //response message in case of has archive template
    HAS_DEVICES("has_devices"),  //response message in case of has devices
    DEVICE_INACTIVE_STATUS("device_inactive_status"),  //response message in case of device inactive status
    USED_CATEGORY_NAME("used_category_name"),  //response message in case of used category name
    USED_CATEGORY_NUMBER("used_category_number"),  //response message in case of used category number
    USED_TEMPLATE_NAME("used_template_name"),  //response message in case of used template_name
    USED_TEMPLATE_NUMBER("used_template_number"),  //response message in case of used template_number
    USED_ARCHIVE_NAME("used_archive_name"),  //response message in case of used archive_name
    USED_ARCHIVE_NUMBER("used_archive_number"),  //response message in case of used archive number
    USED_DEVICE_NAME("used_device_name"),  //response message in case of used device name
    USED_DEVICE_SERIAL("used_device_serial"),  //response message in case of used device serial
    USED_DEVICE("used_device"),  //response message in case of used device
    USED_DEVICE_GUID("used_device_guid"),  //response message in case of used device guid
    USED_FIELD_SERIAL("used_field_serial"),  //response message in case of used field serial
    USED_FIELD_DESIGNATION("used_field_designation"),  //response message in case of used field designation
    USED_ORG_NAME("used_org_name"),  //response message in case of used org name
    USED_ORG_NUMBER("used_org_number"),  //response message in case of used org number
    USED_USER_GROUP_NAME("used_user_group_name"),  //response message in case of used user group_name
    USED_USER_GROUP_NUMBER("used_user_group_number"),  //response message in case of used user group number
    USED_DATA_GROUP_NAME("used_data_group_name"),  //response message in case of used_data group name
    USED_DATA_GROUP_NUMBER("used_data_group_number"),  //response message in case of used data group_number
    USED_ROLE_NAME("used_role_name"),  //response message in case of used role name
    USED_ROLE_NUMBER("used_role_number"), //response message in case of used role number
    USED_SEIZED_GOOD("used_seized_good"),  //response message in case of used seized goods
    USED_DICTIONARY_NAME("used_dictionary_name"),  //response message in case of used seized goods
    USED_DICTIONARY_CODE("used_dictionary_code"),  //response message in case of used seized goods
    USED_DICTIONARY_VALUE("used_dictionary_value"),  //response message in case of used seized goods
    FAILED_INSERT_KNOWLEDGECASE("failed_insert_knowledgecase"), //response message in case of failed inserting new knowledgecase
    FAILED_UPDATE_KNOWLEDGECASE("failed_update_knowledgecase"), //response message in case of failed updating new knowledgecase
    FAILED_INSERT_KNOWLEDGECASEDEAL("failed_insert_knowledgecasedeal"); //response message in case of failed inserting new knowledgecasedeal


    private final String value;

}
