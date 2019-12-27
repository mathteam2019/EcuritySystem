/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（Role）
 * 文件名：	Role.java
 * 描述：	Roles
 * 作者名：	Sandy
 * 日期：	2019/10/30
 *
 */
package com.nuctech.ecuritycheckitem.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum for Roles which are used to protect Controller methods from users who are not granted with specified roles.
 */
@AllArgsConstructor
@Getter
public enum Role {

    ORG_PRINT("org_print"), //can print org
    ORG_EXPORT("org_export"), //can export org
    ORG_CREATE("org_create"), //can create org
    ORG_MODIFY("org_modify"), //can modify org
    ORG_UPDATE_STATUS("org_update_status"), //can update org
    ORG_DELETE("org_delete"),  //can remove org
    USER_PRINT("user_print"), //can print user
    USER_EXPORT("user_export"), //can export user
    USER_CREATE("user_create"), //can create user
    USER_MODIFY("user_modify"), //can modify user
    USER_UPDATE_STATUS("user_update_status"), //can update user status
    USER_DELETE("user_delete"), //can remove user
    USER_GROUP_PRINT("user_group_print"), //can print user_group
    USER_GROUP_EXPORT("user_group_export"), //can export user_group
    USER_GROUP_CREATE("user_group_create"), //can create user_group
    USER_GROUP_MODIFY("user_group_modify"), //can modify user_group
    USER_GROUP_DELETE("user_group_delete"), //can remove user_group
    ROLE_PRINT("role_print"), //can print role
    ROLE_EXPORT("role_export"), //can export roles
    ROLE_CREATE("role_create"), //can create role
    ROLE_MODIFY("role_modify"),  //can edit role
    ROLE_DELETE("role_delete"), //can remove role
    DATA_GROUP_PRINT("data_group_print"), //can print data_group
    DATA_GROUP_EXPORT("data_group_export"), //can export data_group
    DATA_GROUP_CREATE("data_group_create"), //can create data_group
    DATA_GROUP_MODIFY("data_group_modify"), //can modify data_group
    DATA_GROUP_DELETE("data_group_delete"), //can remove data_group
    ASSIGN_USER_PRINT("assign_user_print"), //can print assign user
    ASSIGN_USER_EXPORT("assign_user_export"), //can export assign  user
    ASSIGN_USER_CREATE("assign_user_create"), //can create assign user
    ASSIGN_USER_MODIFY("assign_user_modify"), //can edit assign user
    ASSIGN_USER_DELETE("assign_user_delete"), //can remove assign user
    ASSIGN_USER_GROUP_PRINT("assign_user_group_print"), //can print assign user group
    ASSIGN_USER_GROUP_EXPORT("assign_user_group_export"), //can export assign user group
    ASSIGN_USER_GROUP_CREATE("assign_user_group_create"), //can create assign user group
    ASSIGN_USER_GROUP_MODIFY("assign_user_group_modify"), //can edit assign user group
    ASSIGN_USER_GROUP_DELETE("assign_user_group_delete"), //can remove assign user group
    FIELD_PRINT("field_print"), //can print field
    FIELD_EXPORT("field_export"), //can export field
    FIELD_CREATE("field_create"), //can create field
    FIELD_MODIFY("field_modify"), //can edit field
    FIELD_UPDATE_STATUS("field_update_status"), //can update field status
    FIELD_DELETE("field_delete"), //can remove field
    PLATFORM_CHECK_MODIFY("platform_check_modify"), //can modify platform check
    PLATFORM_OTHER_MODIFY("platform_other_modify"), //can modfiy platform other
    SCAN_PARAM_MODIFY("scan_param_modify"), //can modify scan param
    DEVICE_CATEGORY_PRINT("device_category_print"), //can print device_category
    DEVICE_CATEGORY_EXPORT("device_category_export"), //can export device_category
    DEVICE_CATEGORY_CREATE("device_category_create"), //can create device_category
    DEVICE_CATEGORY_MODIFY("device_category_modify"), //can edit device_category
    DEVICE_CATEGORY_UPDATE_STATUS("device_category_update_status"), //can update device category status
    DEVICE_CATEGORY_DELETE("device_category_delete"), //can remove device_category
    DEVICE_TEMPLATE_PRINT("device_template_print"), //can print device template
    DEVICE_TEMPLATE_EXPORT("device_template_export"), //can export device template
    DEVICE_TEMPLATE_CREATE("device_template_create"), //can create device template
    DEVICE_TEMPLATE_MODIFY("device_template_modify"), //can edit device template
    DEVICE_TEMPLATE_UPDATE_STATUS("device_template_update_status"), //can update device template status
    DEVICE_TEMPLATE_DELETE("device_template_delete"), //can remove device template
    DEVICE_INDICATOR_CREATE("device_indicator_create"), //can create device indicator
    DEVICE_INDICATOR_UPDATE_ISNULL("device_indicator_update_is_null"), //can update device indicator in case of null
    DEVICE_INDICATOR_DELETE("device_indicator_delete"), //can remove device indicator
    DEVICE_ARCHIVE_PRINT("device_archive_print"), //can print device archive
    DEVICE_ARCHIVE_EXPORT("device_archive_export"), //can export device archive
    DEVICE_ARCHIVE_CREATE("device_archive_create"), //can create device archive
    DEVICE_ARCHIVE_MODIFY("device_archive_modify"), //can edit device archive
    DEVICE_ARCHIVE_UPDATE_STATUS("device_archive_update_status"), //can update device archive status
    DEVICE_ARCHIVE_DELETE("device_archive_delete"), //can remove device archive
    DEVICE_PRINT("device_print"), //can print devices
    DEVICE_EXPORT("device_export"), //can export devices
    DEVICE_CREATE("device_create"), //can create devices
    DEVICE_MODIFY("device_modify"), //can edit devices
    DEVICE_UPDATE_STATUS("device_update_status"), //can update device status
    DEVICE_DELETE("device_delete"), //can remove device
    DEVICE_FIELD_PRINT("device_field_print"), //can print device field
    DEVICE_FIELD_EXPORT("device_field_export"), //can export device field
    DEVICE_FIELD_MODIFY("device_field_modify"), //can edit device field
    DEVICE_CONFIG_MODIFY("device_config_modify"), // can edit device config
    DEVICE_LOG_PRINT("device_log_print"), //can print device log
    DEVICE_LOG_EXPORT("device_log_export"), //can export device log
    AUDIT_LOG_PRINT("audit_log_print"), //can print audit log
    AUDIT_LOG_EXPORT("audit_log_export"), //can export audit log
    ACCESS_LOG_PRINT("access_log_print"), //can print access log
    ACCESS_LOG_EXPORT("access_log_export"),
    KNOWLEDGECASE_CREATE("knowledgecase_create"),
    KNOWLEDGECASE_MODIFY("knowledgecase_modify"),
    KNOWLEDGECASE_DELETE("knowledgecase_delete"),
    KNOWLEDGECASEDEAL_CREATE("knowledgecasedeal_create"),
    KNOWLEDGECASEDEAL_MODIFY("knowledgecasedeal_modify"),
    KNOWLEDGECASEDEAL_DELETE("knowledgecasedeal_delete"); //can export access log








    // This is for @Preauthorize annotation.
    public static class Authority {

        // The Spring Security has ROLE_PREFIX. We can override Spring Security settings and change or remove the prefix but for now, we just respect it.
        public static final String ROLE_PREFIX = "ROLE_";

        public static final String HAS_ORG_PRINT = "hasRole('ROLE_org_print')";
        public static final String HAS_ORG_EXPORT = "hasRole('ROLE_org_export')";
        public static final String HAS_ORG_TOWORD = "hasRole('ROLE_org_toword')";
        public static final String HAS_ORG_CREATE = "hasRole('ROLE_org_create')";
        public static final String HAS_ORG_MODIFY = "hasRole('ROLE_org_modify')";
        public static final String HAS_ORG_UPDATE_STATUS = "hasRole('ROLE_org_update_status')";
        public static final String HAS_ORG_DELETE = "hasRole('ROLE_org_delete')";
        public static final String HAS_USER_PRINT = "hasRole('ROLE_user_print')";
        public static final String HAS_USER_EXPORT = "hasRole('ROLE_user_export')";
        public static final String HAS_USER_TOWORD = "hasRole('ROLE_user_toword')";
        public static final String HAS_USER_CREATE = "hasRole('ROLE_user_create')";
        public static final String HAS_USER_MODIFY = "hasRole('ROLE_user_modify')";
        public static final String HAS_USER_UPDATE_STATUS = "hasRole('ROLE_user_update_status')";
        public static final String HAS_USER_DELETE = "hasRole('ROLE_user_delete')";
        public static final String HAS_USER_GROUP_PRINT = "hasRole('ROLE_user_group_print')";
        public static final String HAS_USER_GROUP_EXPORT = "hasRole('ROLE_user_group_export')";
        public static final String HAS_USER_GROUP_TOWORD = "hasRole('ROLE_user_group_toword')";
        public static final String HAS_USER_GROUP_CREATE = "hasRole('ROLE_user_group_create')";
        public static final String HAS_USER_GROUP_MODIFY = "hasRole('ROLE_user_group_modify')";
        public static final String HAS_USER_GROUP_DELETE = "hasRole('ROLE_user_group_delete')";
        public static final String HAS_ROLE_PRINT = "hasRole('ROLE_role_print')";
        public static final String HAS_ROLE_EXPORT = "hasRole('ROLE_role_export')";
        public static final String HAS_ROLE_TOWORD = "hasRole('ROLE_role_toword')";
        public static final String HAS_ROLE_CREATE = "hasRole('ROLE_role_create')";
        public static final String HAS_ROLE_MODIFY = "hasRole('ROLE_role_modify')";
        public static final String HAS_ROLE_DELETE = "hasRole('ROLE_role_delete')";
        public static final String HAS_DATA_GROUP_PRINT = "hasRole('ROLE_data_group_print')";
        public static final String HAS_DATA_GROUP_EXPORT = "hasRole('ROLE_data_group_export')";
        public static final String HAS_DATA_GROUP_TOWORD = "hasRole('ROLE_data_group_toword')";
        public static final String HAS_DATA_GROUP_CREATE = "hasRole('ROLE_data_group_create')";
        public static final String HAS_DATA_GROUP_MODIFY = "hasRole('ROLE_data_group_modify')";
        public static final String HAS_DATA_GROUP_DELETE = "hasRole('ROLE_data_group_delete')";
        public static final String HAS_ASSIGN_USER_PRINT = "hasRole('ROLE_assign_user_print')";
        public static final String HAS_ASSIGN_USER_EXPORT = "hasRole('ROLE_assign_user_export')";
        public static final String HAS_ASSIGN_USER_TOWORD = "hasRole('ROLE_assign_user_toword')";
        public static final String HAS_ASSIGN_USER_CREATE = "hasRole('ROLE_assign_user_create')";
        public static final String HAS_ASSIGN_USER_MODIFY = "hasRole('ROLE_assign_user_modify')";
        public static final String HAS_ASSIGN_USER_DELETE = "hasRole('ROLE_assign_user_delete')";
        public static final String HAS_ASSIGN_USER_GROUP_PRINT = "hasRole('ROLE_assign_user_group_print')";
        public static final String HAS_ASSIGN_USER_GROUP_EXPORT = "hasRole('ROLE_assign_user_group_export')";
        public static final String HAS_ASSIGN_USER_GROUP_TOWORD = "hasRole('ROLE_assign_user_group_toword')";
        public static final String HAS_ASSIGN_USER_GROUP_CREATE = "hasRole('ROLE_assign_user_group_create')";
        public static final String HAS_ASSIGN_USER_GROUP_MODIFY = "hasRole('ROLE_assign_user_group_modify')";
        public static final String HAS_ASSIGN_USER_GROUP_DELETE = "hasRole('ROLE_assign_user_group_delete')";
        public static final String HAS_FIELD_PRINT = "hasRole('ROLE_field_print')";
        public static final String HAS_FIELD_EXPORT = "hasRole('ROLE_field_export')";
        public static final String HAS_FIELD_TOWORD = "hasRole('ROLE_field_toword')";
        public static final String HAS_FIELD_CREATE = "hasRole('ROLE_field_create')";
        public static final String HAS_FIELD_MODIFY = "hasRole('ROLE_field_modify')";
        public static final String HAS_FIELD_UPDATE_STATUS = "hasRole('ROLE_field_update_status')";
        public static final String HAS_FIELD_DELETE = "hasRole('ROLE_field_delete')";
        public static final String HAS_PLATFORM_CHECK_MODIFY = "hasRole('ROLE_platform_check_modify')";
        public static final String HAS_PLATFORM_OTHER_MODIFY = "hasRole('ROLE_platform_other_modify')";
        public static final String HAS_SCAN_PARAM_MODIFY = "hasRole('ROLE_scan_param_modify')";
        public static final String HAS_DEVICE_CATEGORY_PRINT = "hasRole('ROLE_device_category_print')";
        public static final String HAS_DEVICE_CATEGORY_EXPORT = "hasRole('ROLE_device_category_export')";
        public static final String HAS_DEVICE_CATEGORY_TOWORD = "hasRole('ROLE_device_category_word')";
        public static final String HAS_DEVICE_CATEGORY_CREATE = "hasRole('ROLE_device_category_create')";
        public static final String HAS_DEVICE_CATEGORY_MODIFY = "hasRole('ROLE_device_category_modify')";
        public static final String HAS_DEVICE_CATEGORY_UPDATE_STATUS = "hasRole('ROLE_device_category_update_status')";
        public static final String HAS_DEVICE_CATEGORY_DELETE = "hasRole('ROLE_device_category_delete')";
        public static final String HAS_DEVICE_TEMPLATE_PRINT = "hasRole('ROLE_device_template_print')";
        public static final String HAS_DEVICE_TEMPLATE_EXPORT = "hasRole('ROLE_device_template_export')";
        public static final String HAS_DEVICE_TEMPLATE_TOWORD = "hasRole('ROLE_device_template_toword')";
        public static final String HAS_DEVICE_TEMPLATE_CREATE = "hasRole('ROLE_device_template_create')";
        public static final String HAS_DEVICE_TEMPLATE_MODIFY = "hasRole('ROLE_device_template_modify')";
        public static final String HAS_DEVICE_TEMPLATE_UPDATE_STATUS = "hasRole('ROLE_device_template_update_status')";
        public static final String HAS_DEVICE_TEMPLATE_DELETE = "hasRole('ROLE_device_template_delete')";
        public static final String HAS_DEVICE_INDICATOR_CREATE = "hasRole('ROLE_device_indicator_create')";
        public static final String HAS_DEVICE_INDICATOR_UPDATE_ISNULL = "hasRole('ROLE_device_indicator_update_is_null')";
        public static final String HAS_DEVICE_INDICATOR_DELETE = "hasRole('ROLE_device_indicator_delete')";
        public static final String HAS_DEVICE_ARCHIVE_PRINT = "hasRole('ROLE_device_archive_print')";
        public static final String HAS_DEVICE_ARCHIVE_EXPORT = "hasRole('ROLE_device_archive_export')";
        public static final String HAS_DEVICE_ARCHIVE_TOWORD = "hasRole('ROLE_device_archive_toword')";
        public static final String HAS_DEVICE_ARCHIVE_CREATE = "hasRole('ROLE_device_archive_create')";
        public static final String HAS_DEVICE_ARCHIVE_MODIFY = "hasRole('ROLE_device_archive_modify')";
        public static final String HAS_DEVICE_ARCHIVE_UPDATE_STATUS = "hasRole('ROLE_device_archive_update_status')";
        public static final String HAS_DEVICE_ARCHIVE_DELETE = "hasRole('ROLE_device_archive_delete')";
        public static final String HAS_DEVICE_PRINT = "hasRole('ROLE_device_print')";
        public static final String HAS_DEVICE_EXPORT = "hasRole('ROLE_device_export')";
        public static final String HAS_DEVICE_TOWORD = "hasRole('ROLE_device_toword')";
        public static final String HAS_DEVICE_CREATE = "hasRole('ROLE_device_create')";
        public static final String HAS_DEVICE_MODIFY = "hasRole('ROLE_device_modify')";
        public static final String HAS_DEVICE_UPDATE_STATUS = "hasRole('ROLE_device_update_status')";
        public static final String HAS_DEVICE_DELETE = "hasRole('ROLE_device_delete')";
        public static final String HAS_DEVICE_FIELD_PRINT = "hasRole('ROLE_device_field_print')";
        public static final String HAS_DEVICE_FIELD_EXPORT = "hasRole('ROLE_device_field_export')";
        public static final String HAS_DEVICE_FIELD_MODIFY = "hasRole('ROLE_device_field_modify')";
        public static final String HAS_DEVICE_CONFIG_MODIFY = "hasRole('ROLE_device_config_modify')";
        public static final String HAS_DEVICE_LOG_PRINT = "hasRole('ROLE_device_log_print')";
        public static final String HAS_DEVICE_LOG_EXPORT = "hasRole('ROLE_device_log_export')";
        public static final String HAS_DEVICE_LOG_TOWORD = "hasRole('ROLE_device_log_toword')";
        public static final String HAS_AUDIT_LOG_PRINT = "hasRole('ROLE_audit_log_print')";
        public static final String HAS_AUDIT_LOG_EXPORT = "hasRole('ROLE_audit_log_export')";
        public static final String HAS_AUDIT_LOG_TOWORD = "hasRole('ROLE_audit_log_toword')";
        public static final String HAS_ACCESS_LOG_PRINT = "hasRole('ROLE_access_log_print')";
        public static final String HAS_ACCESS_LOG_EXPORT = "hasRole('ROLE_access_log_export')";
        public static final String HAS_ACCESS_LOG_TOWORD = "hasRole('ROLE_access_log_toword')";
        public static final String HAS_KNOWLEDGECASE_CREATE = "hasRole('ROLE_knowledgecase_create')";
        public static final String HAS_KNOWLEDGECASE_MODIFY = "hasRole('ROLE_knowledgecase_modify')";
        public static final String HAS_KNOWLEDGECASE_DELETE = "hasRole('ROLE_knowledgecase_delete')";
        public static final String HAS_KNOWLEDGECASEDEAL_CREATE = "hasRole('ROLE_knowledgecasedeal_create')";
        public static final String HAS_KNOWLEDGECASEDEAL_MODIFY = "hasRole('ROLE_knowledgecasedeal_modify')";
        public static final String HAS_KNOWLEDGECASEDEAL_DELETE = "hasRole('ROLE_knowledgecasedeal_delete')";


    }

    private final String value;

}
