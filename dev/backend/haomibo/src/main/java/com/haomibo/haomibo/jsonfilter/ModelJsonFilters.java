package com.haomibo.haomibo.jsonfilter;

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
