package com.nuctech.securitycheck.backgroundservice.common.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {
    static final String DATEFORMAT = "yyyy-MM-dd HH:mm:ss";

    public static String GetUTCdatetimeAsString()
    {
        final SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        final String utcTime = sdf.format(new Date());

        return utcTime;
    }

    public static Date StringDateToDate(String StrDate)
    {
        Date dateToReturn = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATEFORMAT);

        try
        {
            dateToReturn = (Date)dateFormat.parse(StrDate);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return dateToReturn;
    }

    public static Date getCurrentDate() {
        return new Date();
        //return StringDateToDate(GetUTCdatetimeAsString());
    }
}
