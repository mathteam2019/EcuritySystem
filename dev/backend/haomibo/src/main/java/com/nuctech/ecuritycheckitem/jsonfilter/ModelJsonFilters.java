/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/10/25
 * @CreatedBy Sandy.
 * @FileName ModelJsonFilters.java
 * @ModifyHistory
 *
 */
package com.nuctech.ecuritycheckitem.jsonfilter;

import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

/**
 * Defines filters for objects to convert json.
 */
public class ModelJsonFilters {

    public final static String FILTER_SYS_ORG = "filter_sys_org";
    public final static String FILTER_SYS_USER = "filter_sys_user";
    public final static String FILTER_SYS_USER_GROUP = "filter_sys_user_group";
    public final static String FILTER_SYS_RESOURCE = "filter_sys_resource";
    public final static String FILTER_SYS_ROLE = "filter_sys_role";
    public final static String FILTER_SYS_DATA_GROUP = "filter_sys_data_group";
    public final static String FILTER_SYS_FIELD = "filter_sys_field";
    public final static String FILTER_SYS_DEVICE_CATEGORY = "filter_sys_device_category";
    public final static String FILTER_SER_ARCHIVES = "filter_ser_archives";
    public final static String FILTER_SER_ARCHIVES_VALUE = "filter_ser_archives_value";
    public final static String FILTER_SER_ARCHIVE_TEMPLATE = "filter_ser_archive_template";
    public final static String FILTER_SER_ARCHIVE_INDICATORS = "filter_ser_archive_indicators";
    public final static String FILTER_SYS_DEVICE = "filter_sys_device";
    public final static String FILTER_SYS_MANUAL_DEVICE = "filter_sys_manual_device";
    public final static String FILTER_SYS_MANUAL_GROUP = "filter_sys_manual_group";
    public final static String FILTER_SYS_JUDGE_DEVICE = "filter_sys_judge_device";
    public final static String FILTER_SYS_JUDGE_GROUP = "filter_sys_judge_group";
    public final static String FILTER_SYS_WORK_MODE = "filter_sys_work_mode";
    public final static String FILTER_FROM_CONFIG_ID = "filter_from_config_id";
    public final static String FILTER_SYS_DEVICE_CONFIG = "filter_sys_device_config";
    public final static String FILTER_SYS_AUDIT_LOG = "filter_sys_audit_log";
    public final static String FILTER_SYS_ACCESS_LOG = "filter_sys_access_log";
    public final static String FILTER_SER_DEV_LOG = "filter_ser_dev_log";
    public final static String FILTER_SER_LOGIN_INFO = "filter_ser_login_info";
    public final static String FILTER_SER_DEVICE_STATUS = "filter_ser_device_status";
    public final static String FILTER_SER_DEVICE_REGISTER = "filter_ser_device_register";
    public final static String FILTER_SER_SCAN_PARAM = "filter_ser_scan_param";
    public final static String FILTER_SER_SCAN_PARAMS_FROM = "filter_ser_scan_params_from";
    public final static String FILTER_SER_PLATFORM_CHECK_PARAMS = "filter_ser_platform_check_params";
    public final static String FILTER_SER_PLATFORM_OTHER_PARAMS = "filter_ser_platform_other_params";
    public final static String FILTER_SER_TASK = "filter_ser_task";
    public final static String FILTER_HISTORY = "filter_history";
    public final static String FILTER_SER_IMAGE = "filter_ser_image";
    public final static String FILTER_SER_SCAN = "filter_ser_scan";
    public final static String FILTER_SER_JUDGE_GRAPH = "filter_ser_judge_graph";
    public final static String FILTER_SER_HAND_EXAMINATION = "filter_ser_hand_examination";
    public final static String FILTER_SYS_WORKFLOW = "filter_sys_workflow";
    public final static String FILTER_SER_KNOWLEDGE_CASE_DEAL = "filter_ser_knowledge_case_deal";
    public final static String FILTER_SER_KNOWLEDGE_CASE = "filter_ser_knowledge_case";


    /**
     * Used to get default filter
     *
     * @return SimpleFilterProvider object
     */
    public static SimpleFilterProvider getDefaultFilters() {
        // By the default, we don't need to filter the object, just serialize all its properties.
        SimpleBeanPropertyFilter theFilter = SimpleBeanPropertyFilter.serializeAll();
        return new SimpleFilterProvider().setDefaultFilter(theFilter);
    }

}
