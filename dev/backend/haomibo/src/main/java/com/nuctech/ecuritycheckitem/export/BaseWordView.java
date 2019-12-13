package com.nuctech.ecuritycheckitem.export;

import com.nuctech.ecuritycheckitem.config.Constants;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BaseWordView {

    public static String getCurrentTime() {
        Date curTime = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.EXCEL_DATETIME_FORMAT);
        return dateFormat.format(curTime);
    }

    public static String formatDate(Date date) {

        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.PDF_DATETIME_FORMAT);

        String strDate = "";
        try {
            strDate = dateFormat.format(date);
        } catch(Exception ex) {

        }

        return strDate;
    }

}
