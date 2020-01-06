package com.nuctech.securitycheck.backgroundservice.common.utils;

import com.google.common.collect.Maps;
import com.nuctech.securitycheck.backgroundservice.common.enums.DefaultType;
import com.nuctech.securitycheck.backgroundservice.common.enums.DeviceDefaultType;
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
                return DefaultType.TRUE.getValue();
            } else {
                return DefaultType.FALSE.getValue();
            }
        } else {
            return "";
        }
    }

}
