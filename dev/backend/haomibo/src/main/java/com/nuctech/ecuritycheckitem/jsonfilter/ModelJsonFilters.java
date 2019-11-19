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
