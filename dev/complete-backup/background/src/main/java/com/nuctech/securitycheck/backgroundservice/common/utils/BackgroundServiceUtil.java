package com.nuctech.securitycheck.backgroundservice.common.utils;

import com.google.common.collect.Maps;
import com.nuctech.securitycheck.backgroundservice.common.enums.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import javax.validation.groups.Default;
import java.util.Map;

/**
 * BackgroundServiceUtil
 *
 * @author PiaoCangGe
 * @version v1.0
 * @since 2019-11-27
 */
@Slf4j
public class BackgroundServiceUtil {

    private static PropertiesLoader bsLoader = new PropertiesLoader("classpath:bs.properties");
    private static Map<String, String> map = Maps.newHashMap();
    private static BackgroundServiceUtil bsUtil = null;

    private BackgroundServiceUtil() {
    }

    public static synchronized BackgroundServiceUtil getInstance() {
        if (bsUtil == null) {
            synchronized (BackgroundServiceUtil.class) {
                if (bsUtil == null)
                    bsUtil = new BackgroundServiceUtil();
            }
        }
        return bsUtil;
    }

    public static String getConfig(String key) {
        String value = map.get(key);
        if (value == null) {
            value = bsLoader.getProperty(key).trim();
            map.put(key, value != null ? value : "");
        }
        return value;
    }
    
    public static String generateTaskNumber(String guid) {
        try {
            StringBuilder taskNumber = new StringBuilder();
            taskNumber.append(guid).append(System.currentTimeMillis());
            return taskNumber.toString();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    public static String dictConvert(String deviceDict) {
        if (StringUtils.isNotBlank(deviceDict)) {
            if (deviceDict.equals(DeviceDefaultType.TRUE.getValue())) {
                return DeviceInfoDefaultType.TRUE.getValue();
            } else {
                return DeviceInfoDefaultType.FALSE.getValue();
            }
        } else {
            return "";
        }
    }

    public static String judgeConvert(String deviceDict) {
        if (StringUtils.isNotBlank(deviceDict)) {
            if (deviceDict.equals(DeviceDefaultType.TRUE.getValue())) {
                return JudgeResultType.TRUE.getValue();
            } else {
                return JudgeResultType.FALSE.getValue();
            }
        } else {
            return "";
        }
    }

    public static String genderConvert(Integer gender) {
        if (gender != null) {
            if (String.valueOf(gender).equals(DeviceGenderType.MALE.getValue())) {
                return GenderType.MALE.getValue();
            } else {
                return GenderType.FEMALE.getValue();
            }
        } else {
            return "";
        }
    }

}
