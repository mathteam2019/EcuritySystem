package com.haomibo.haomibo.jsonfilter;

import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

public class ModelJsonFilters {

    public final static String FILTER_SYS_ORG = "filter_sys_org";

    public static SimpleFilterProvider getDefaultFilters() {
        SimpleBeanPropertyFilter theFilter = SimpleBeanPropertyFilter.serializeAll();
        return new SimpleFilterProvider().setDefaultFilter(theFilter);
    }

}
