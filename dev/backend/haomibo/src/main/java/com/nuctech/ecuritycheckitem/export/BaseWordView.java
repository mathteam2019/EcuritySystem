package com.nuctech.ecuritycheckitem.export;

import com.nuctech.ecuritycheckitem.config.Constants;
import org.springframework.context.MessageSource;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BaseWordView {

    public static MessageSource messageSource;
    public static Locale currentLocale = Locale.CHINESE;

    public static void setMessageSource(MessageSource messageSource) {
        BaseWordView.messageSource = messageSource;
    }
    public static void setCurrentLocale(Locale locale) {
        currentLocale = locale;
    }

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
