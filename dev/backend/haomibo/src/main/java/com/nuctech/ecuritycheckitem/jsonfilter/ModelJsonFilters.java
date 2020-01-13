/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（ModelJsonFilters）
 * 文件名：	ModelJsonFilters.java
 * 描述：	ModelJsonFilters
 * 作者名：	Sandy
 * 日期：	2019/10/25
 *
 */

package com.nuctech.ecuritycheckitem.jsonfilter;

import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

/**
 * Defines filters for objects to convert json.
 */
public class ModelJsonFilters {

    public final static String FILTER_SYS_ORG = "filter_sys_org"; //filter to serialize SysOrg
    public final static String FILTER_SYS_USER = "filter_sys_user"; //filter to serialize SysUser
    public final static String FILTER_SYS_USER_GROUP = "filter_sys_user_group"; //filter to serialize SysUserGroup
    public final static String FILTER_SYS_RESOURCE = "filter_sys_resource"; //filter to serialize SysResource
    public final static String FILTER_SYS_ROLE = "filter_sys_role"; //filter to serialize SysRole
    public final static String FILTER_SYS_DATA_GROUP = "filter_sys_data_group"; //filter to serialize SysDataGroup
    public final static String FILTER_SYS_FIELD = "filter_sys_field"; //filter to serialize SysField
    public final static String FILTER_SYS_DEVICE_CATEGORY = "filter_sys_device_category"; //filter to serialize SysDeviceCategory
    public final static String FILTER_SER_ARCHIVES = "filter_ser_archives"; //filter to serialize SerArchive
    public final static String FILTER_SER_ARCHIVES_VALUE = "filter_ser_archives_value"; //filter to serialize SerArchiveValues
    public final static String FILTER_SER_ARCHIVE_TEMPLATE = "filter_ser_archive_template"; //filter to serialize SerArchiveTemplate
    public final static String FILTER_SER_ARCHIVE_INDICATORS = "filter_ser_archive_indicators"; //filter to serialize SerArchiveIndicator
    public final static String FILTER_SYS_DEVICE = "filter_sys_device"; //filter to serialize SysDevice
    public final static String FILTER_SYS_DICTONARY_DATA = "filter_sys_dictionary_data"; //filter to serialize SysDictionaryData
    public final static String FILTER_SYS_DICTONARY = "filter_sys_dictionary"; //filter to serialize SysDictionaryData
    public final static String FILTER_SYS_DEVICE_DICTIONARY_DATA = "filter_sys_dictionary_data"; //filter to serialize SysDeviceDictionaryData
    public final static String FILTER_SYS_MANUAL_DEVICE = "filter_sys_manual_device"; //filter to serialize SysManualDevice
    public final static String FILTER_SYS_MANUAL_GROUP = "filter_sys_manual_group"; //filter to serialize SysManualGroup
    public final static String FILTER_SYS_JUDGE_DEVICE = "filter_sys_judge_device"; //filter to serialize SysJudgeDevice
    public final static String FILTER_SYS_JUDGE_GROUP = "filter_sys_judge_group"; //filter to serialize SysJudgeGroup
    public final static String FILTER_SYS_WORK_MODE = "filter_sys_work_mode"; //filter to serialize SysWorkMode
    public final static String FILTER_FROM_CONFIG_ID = "filter_from_config_id"; //filter to serialize SysConfig
    public final static String FILTER_SYS_DEVICE_CONFIG = "filter_sys_device_config"; //filter to serialize SysDeviceConfig
    public final static String FILTER_SYS_AUDIT_LOG = "filter_sys_audit_log"; //filter to serialize SysAuditLog
    public final static String FILTER_SYS_ACCESS_LOG = "filter_sys_access_log"; //filter to serialize SysAccessLog
    public final static String FILTER_SER_DEV_LOG = "filter_ser_dev_log"; //filter to serialize SerDevLog
    public final static String FILTER_SER_LOGIN_INFO = "filter_ser_login_info"; //filter to serialize SerLoginInfo
    public final static String FILTER_SER_DEVICE_STATUS = "filter_ser_device_status"; //filter to serialize SerDeviceStatus
    public final static String FILTER_SER_DEVICE_REGISTER = "filter_ser_device_register"; //filter to serialize SerDeviceRegister
    public final static String FILTER_SER_SCAN_PARAM = "filter_ser_scan_param"; //filter to serialize SerScanParam
    public final static String FILTER_SER_SCAN_PARAMS_FROM = "filter_ser_scan_params_from"; //filter to serialize SerScanParamsFrom
    public final static String FILTER_SER_PLATFORM_CHECK_PARAMS = "filter_ser_platform_check_params"; //filter to serialize SerPlatformCheckParams
    public final static String FILTER_SER_PLATFORM_OTHER_PARAMS = "filter_ser_platform_other_params"; //filter to serialize SerPlatformOtherParams
    public final static String FILTER_SER_TASK = "filter_ser_task"; //filter to serialize SerTask
    public final static String FILTER_HISTORY = "filter_history"; //filter to serialize History
    public final static String FILTER_SER_ASSIGN = "filter_ser_assign"; //filter to serialize History
    public final static String FILTER_SER_IMAGE = "filter_ser_image"; //filter to serialize SerImage
    public final static String FILTER_SER_SCAN = "filter_ser_scan"; //filter to serialize SerScan
    public final static String FILTER_SER_JUDGE_GRAPH = "filter_ser_judge_graph"; //filter to serialize SerJudgeGrarph
    public final static String FILTER_SER_HAND_EXAMINATION = "filter_ser_hand_examination"; //filter to serialize SerHandExamination
    public final static String FILTER_SYS_WORKFLOW = "filter_sys_workflow"; //filter to serialize SysWorkflow
    public final static String FILTER_SER_KNOWLEDGE_CASE_DEAL = "filter_ser_knowledge_case_deal"; //filter to SerKnowledgeCaseDeal
    public final static String FILTER_SER_KNOWLEDGE_CASE = "filter_ser_knowledge_case"; //filter to serialize SerKnowledgeCase
    public final static String FILTER_SER_CHECK_RESULT = "fitler_ser_check_result"; //filter to serialize SerImage
    public final static String FILTER_SER_SEIZED_GOOD = "fitler_ser_seized_good"; //filter to serialize SerSeizedGood
    public final static String FILTER_SER_TASK_TAG = "filter_ser_task_tag"; //filter to serialize SerTaskTag

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
