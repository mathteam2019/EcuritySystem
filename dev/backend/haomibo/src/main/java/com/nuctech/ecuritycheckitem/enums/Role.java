/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/10/30
 * @CreatedBy Sandy.
 * @FileName Role.java
 * @ModifyHistory
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

    ORG_PRINT("org_print"),
    ORG_EXPORT("org_export"),
    ORG_CREATE("org_create"),
    ORG_MODIFY("org_modify"),
    ORG_UPDATE_STATUS("org_update_status"),
    ORG_DELETE("org_delete"),
    USER_PRINT("user_print"),
    USER_EXPORT("user_export"),
    USER_CREATE("user_create"),
    USER_MODIFY("user_modify"),
    USER_UPDATE_STATUS("user_update_status"),
    USER_DELETE("user_delete"),
    USER_GROUP_PRINT("user_group_print"),
    USER_GROUP_EXPORT("user_group_export"),
    USER_GROUP_CREATE("user_group_create"),
    USER_GROUP_MODIFY("user_group_modify"),
    USER_GROUP_DELETE("user_group_delete"),
    ROLE_PRINT("role_print"),
    ROLE_EXPORT("role_export"),
    ROLE_CREATE("role_create"),
    ROLE_MODIFY("role_modify"),
    ROLE_DELETE("role_delete"),
    DATA_GROUP_PRINT("data_group_print"),
    DATA_GROUP_EXPORT("data_group_export"),
    DATA_GROUP_CREATE("data_group_create"),
    DATA_GROUP_MODIFY("data_group_modify"),
    DATA_GROUP_DELETE("data_group_delete"),
    ASSIGN_USER_PRINT("assign_user_print"),
    ASSIGN_USER_EXPORT("assign_user_export"),
    ASSIGN_USER_CREATE("assign_user_create"),
    ASSIGN_USER_MODIFY("assign_user_modify"),
    ASSIGN_USER_DELETE("assign_user_delete"),
    ASSIGN_USER_GROUP_PRINT("assign_user_group_print"),
    ASSIGN_USER_GROUP_EXPORT("assign_user_group_export"),
    ASSIGN_USER_GROUP_CREATE("assign_user_group_create"),
    ASSIGN_USER_GROUP_MODIFY("assign_user_group_modify"),
    ASSIGN_USER_GROUP_DELETE("assign_user_group_delete"),
    FIELD_PRINT("field_print"),
    FIELD_EXPORT("field_export"),
    FIELD_CREATE("field_create"),
    FIELD_MODIFY("field_modify"),
    FIELD_UPDATE_STATUS("field_update_status"),
    FIELD_DELETE("field_delete"),
    PLATFORM_CHECK_MODIFY("platform_check_modify"),
    PLATFORM_OTHER_MODIFY("platform_other_modify"),
    SCAN_PARAM_MODIFY("scan_param_modify"),
    DEVICE_CATEGORY_PRINT("device_category_print"),
    DEVICE_CATEGORY_EXPORT("device_category_export"),
    DEVICE_CATEGORY_CREATE("device_category_create"),
    DEVICE_CATEGORY_MODIFY("device_category_modify"),
    DEVICE_CATEGORY_UPDATE_STATUS("device_category_update_status"),
    DEVICE_CATEGORY_DELETE("device_category_delete"),
    DEVICE_TEMPLATE_PRINT("device_template_print"),
    DEVICE_TEMPLATE_EXPORT("device_template_export"),
    DEVICE_TEMPLATE_CREATE("device_template_create"),
    DEVICE_TEMPLATE_MODIFY("device_template_modify"),
    DEVICE_TEMPLATE_UPDATE_STATUS("device_template_update_status"),
    DEVICE_TEMPLATE_DELETE("device_template_delete"),
    DEVICE_INDICATOR_CREATE("device_indicator_create"),
    DEVICE_INDICATOR_UPDATE_ISNULL("device_indicator_update_is_null"),
    DEVICE_INDICATOR_DELETE("device_indicator_delete"),
    DEVICE_ARCHIVE_PRINT("device_archive_print"),
    DEVICE_ARCHIVE_EXPORT("device_archive_export"),
    DEVICE_ARCHIVE_CREATE("device_archive_create"),
    DEVICE_ARCHIVE_MODIFY("device_archive_modify"),
    DEVICE_ARCHIVE_UPDATE_STATUS("device_archive_update_status"),
    DEVICE_ARCHIVE_DELETE("device_archive_delete"),
    DEVICE_PRINT("device_print"),
    DEVICE_EXPORT("device_export"),
    DEVICE_CREATE("device_create"),
    DEVICE_MODIFY("device_modify"),
    DEVICE_UPDATE_STATUS("device_update_status"),
    DEVICE_DELETE("device_delete"),
    DEVICE_FIELD_PRINT("device_field_print"),
    DEVICE_FIELD_EXPORT("device_field_export"),
    DEVICE_FIELD_MODIFY("device_field_modify"),
    DEVICE_CONFIG_MODIFY("device_config_modify"),
    DEVICE_LOG_PRINT("device_log_print"),
    DEVICE_LOG_EXPORT("device_log_export"),
    AUDIT_LOG_PRINT("audit_log_print"),
    AUDIT_LOG_EXPORT("audit_log_export"),
    ACCESS_LOG_PRINT("access_log_print"),
    ACCESS_LOG_EXPORT("access_log_export");



    // This is for @Preauthorize annotation.
    public static class Authority {

        // The Spring Security has ROLE_PREFIX. We can override Spring Security settings and change or remove the prefix but for now, we just respect it.
        public static final String ROLE_PREFIX = "ROLE_";

        public static final String HAS_ORG_PRINT = "hasRole('ROLE_org_print')";
        public static final String HAS_ORG_EXPORT = "hasRole('ROLE_org_export')";
        public static final String HAS_ORG_CREATE = "hasRole('ROLE_org_create')";
        public static final String HAS_ORG_MODIFY = "hasRole('ROLE_org_modify')";
        public static final String HAS_ORG_UPDATE_STATUS = "hasRole('ROLE_org_update_status')";
        public static final String HAS_ORG_DELETE = "hasRole('ROLE_org_delete')";
        public static final String HAS_USER_PRINT = "hasRole('ROLE_user_print')";
        public static final String HAS_USER_EXPORT = "hasRole('ROLE_user_export')";
        public static final String HAS_USER_CREATE = "hasRole('ROLE_user_create')";
        public static final String HAS_USER_MODIFY = "hasRole('ROLE_user_modify')";
        public static final String HAS_USER_UPDATE_STATUS = "hasRole('ROLE_user_update_status')";
        public static final String HAS_USER_DELETE = "hasRole('ROLE_user_delete')";
        public static final String HAS_USER_GROUP_PRINT = "hasRole('ROLE_user_group_print')";
        public static final String HAS_USER_GROUP_EXPORT = "hasRole('ROLE_user_group_export')";
        public static final String HAS_USER_GROUP_CREATE = "hasRole('ROLE_user_group_create')";
        public static final String HAS_USER_GROUP_MODIFY = "hasRole('ROLE_user_group_modify')";
        public static final String HAS_USER_GROUP_DELETE = "hasRole('ROLE_user_group_delete')";
        public static final String HAS_ROLE_PRINT = "hasRole('ROLE_role_print')";
        public static final String HAS_ROLE_EXPORT = "hasRole('ROLE_role_export')";
        public static final String HAS_ROLE_CREATE = "hasRole('ROLE_role_create')";
        public static final String HAS_ROLE_MODIFY = "hasRole('ROLE_role_modify')";
        public static final String HAS_ROLE_DELETE = "hasRole('ROLE_role_delete')";
        public static final String HAS_DATA_GROUP_PRINT = "hasRole('ROLE_data_group_print')";
        public static final String HAS_DATA_GROUP_EXPORT = "hasRole('ROLE_data_group_export')";
        public static final String HAS_DATA_GROUP_CREATE = "hasRole('ROLE_data_group_create')";
        public static final String HAS_DATA_GROUP_MODIFY = "hasRole('ROLE_data_group_modify')";
        public static final String HAS_DATA_GROUP_DELETE = "hasRole('ROLE_data_group_delete')";
        public static final String HAS_ASSIGN_USER_PRINT = "hasRole('ROLE_assign_user_print')";
        public static final String HAS_ASSIGN_USER_EXPORT = "hasRole('ROLE_assign_user_export')";
        public static final String HAS_ASSIGN_USER_CREATE = "hasRole('ROLE_assign_user_create')";
        public static final String HAS_ASSIGN_USER_MODIFY = "hasRole('ROLE_assign_user_modify')";
        public static final String HAS_ASSIGN_USER_DELETE = "hasRole('ROLE_assign_user_delete')";
        public static final String HAS_ASSIGN_USER_GROUP_PRINT = "hasRole('ROLE_assign_user_group_print')";
        public static final String HAS_ASSIGN_USER_GROUP_EXPORT = "hasRole('ROLE_assign_user_group_export')";
        public static final String HAS_ASSIGN_USER_GROUP_CREATE = "hasRole('ROLE_assign_user_group_create')";
        public static final String HAS_ASSIGN_USER_GROUP_MODIFY = "hasRole('ROLE_assign_user_group_modify')";
        public static final String HAS_ASSIGN_USER_GROUP_DELETE = "hasRole('ROLE_assign_user_group_delete')";
        public static final String HAS_FIELD_PRINT = "hasRole('ROLE_field_print')";
        public static final String HAS_FIELD_EXPORT = "hasRole('ROLE_field_export')";
        public static final String HAS_FIELD_CREATE = "hasRole('ROLE_field_create')";
        public static final String HAS_FIELD_MODIFY = "hasRole('ROLE_field_modify')";
        public static final String HAS_FIELD_UPDATE_STATUS = "hasRole('ROLE_field_update_status')";
        public static final String HAS_FIELD_DELETE = "hasRole('ROLE_field_delete')";
        public static final String HAS_PLATFORM_CHECK_MODIFY = "hasRole('ROLE_platform_check_modify')";
        public static final String HAS_PLATFORM_OTHER_MODIFY = "hasRole('ROLE_platform_other_modify')";
        public static final String HAS_SCAN_PARAM_MODIFY = "hasRole('ROLE_scan_param_modify')";
        public static final String HAS_DEVICE_CATEGORY_PRINT = "hasRole('ROLE_device_category_print')";
        public static final String HAS_DEVICE_CATEGORY_EXPORT = "hasRole('ROLE_device_category_export')";
        public static final String HAS_DEVICE_CATEGORY_CREATE = "hasRole('ROLE_device_category_create')";
        public static final String HAS_DEVICE_CATEGORY_MODIFY = "hasRole('ROLE_device_category_modify')";
        public static final String HAS_DEVICE_CATEGORY_UPDATE_STATUS = "hasRole('ROLE_device_category_update_status')";
        public static final String HAS_DEVICE_CATEGORY_DELETE = "hasRole('ROLE_device_category_delete')";
        public static final String HAS_DEVICE_TEMPLATE_PRINT = "hasRole('ROLE_device_template_print')";
        public static final String HAS_DEVICE_TEMPLATE_EXPORT = "hasRole('ROLE_device_template_export')";
        public static final String HAS_DEVICE_TEMPLATE_CREATE = "hasRole('ROLE_device_template_create')";
        public static final String HAS_DEVICE_TEMPLATE_MODIFY = "hasRole('ROLE_device_template_modify')";
        public static final String HAS_DEVICE_TEMPLATE_UPDATE_STATUS = "hasRole('ROLE_device_template_update_status')";
        public static final String HAS_DEVICE_TEMPLATE_DELETE = "hasRole('ROLE_device_template_delete')";
        public static final String HAS_DEVICE_INDICATOR_CREATE = "hasRole('ROLE_device_indicator_create')";
        public static final String HAS_DEVICE_INDICATOR_UPDATE_ISNULL = "hasRole('ROLE_device_indicator_update_is_null')";
        public static final String HAS_DEVICE_INDICATOR_DELETE = "hasRole('ROLE_device_indicator_delete')";
        public static final String HAS_DEVICE_ARCHIVE_PRINT = "hasRole('ROLE_device_archive_print')";
        public static final String HAS_DEVICE_ARCHIVE_EXPORT = "hasRole('ROLE_device_archive_export')";
        public static final String HAS_DEVICE_ARCHIVE_CREATE = "hasRole('ROLE_device_archive_create')";
        public static final String HAS_DEVICE_ARCHIVE_MODIFY = "hasRole('ROLE_device_archive_modify')";
        public static final String HAS_DEVICE_ARCHIVE_UPDATE_STATUS = "hasRole('ROLE_device_archive_update_status')";
        public static final String HAS_DEVICE_ARCHIVE_DELETE = "hasRole('ROLE_device_archive_delete')";
        public static final String HAS_DEVICE_PRINT = "hasRole('ROLE_device_print')";
        public static final String HAS_DEVICE_EXPORT = "hasRole('ROLE_device_export')";
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
        public static final String HAS_AUDIT_LOG_PRINT = "hasRole('ROLE_audit_log_print')";
        public static final String HAS_AUDIT_LOG_EXPORT = "hasRole('ROLE_audit_log_export')";
        public static final String HAS_ACCESS_LOG_PRINT = "hasRole('ROLE_access_log_print')";
        public static final String HAS_ACCESS_LOG_EXPORT = "hasRole('ROLE_access_log_export')";



    }

    private final String value;

}
