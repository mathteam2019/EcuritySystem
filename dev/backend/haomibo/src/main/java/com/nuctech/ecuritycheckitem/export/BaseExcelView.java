package com.nuctech.ecuritycheckitem.export;

import com.nuctech.ecuritycheckitem.config.Constants;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.MessageSource;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BaseExcelView {

    public static MessageSource messageSource;
    public static Locale currentLocale = Locale.CHINESE;

    public static void setMessageSource(MessageSource messageSource) {
        BaseExcelView.messageSource = messageSource;
    }
    public static void setCurrentLocale(Locale locale) {
        currentLocale = locale;
    }

    public static CellStyle getHeaderStyle(Workbook workbook) {
        CellStyle headerStyle = workbook.createCellStyle();
        XSSFFont font = ((XSSFWorkbook) workbook).createFont();
        font.setFontName(Constants.EXCEL_HEAD_FONT_NAME);
        font.setFontHeightInPoints(Constants.EXCEL_HEAD_FONT_SIZE);
        font.setBold(true);
        headerStyle.setFont(font);
        return headerStyle;
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
