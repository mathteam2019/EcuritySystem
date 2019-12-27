/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（BaseExcelView）
 * 文件名：	BaseExcelView.java
 * 描述：	BaseExcelView
 * 作者名：	Choe
 * 日期：	2019/11/27
 *
 */

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

    /**
     * set MessageSourceAware
     * @param messageSource
     */
    public static void setMessageSource(MessageSource messageSource) {
        BaseExcelView.messageSource = messageSource;
    }

    /**
     * set Current Locale
     * @param locale
     */
    public static void setCurrentLocale(Locale locale) {
        currentLocale = locale;
    }

    /**
     * set row header style
     * @param workbook
     * @return
     */
    public static CellStyle getHeaderStyle(Workbook workbook) {
        CellStyle headerStyle = workbook.createCellStyle();
        XSSFFont font = ((XSSFWorkbook) workbook).createFont();
        font.setFontName(Constants.EXCEL_HEAD_FONT_NAME);
        font.setFontHeightInPoints(Constants.EXCEL_HEAD_FONT_SIZE);
        font.setBold(true);
        headerStyle.setFont(font);
        return headerStyle;
    }

    /**
     * get current time
     * @return
     */
    public static String getCurrentTime() {
        Date curTime = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.EXCEL_DATETIME_FORMAT);
        return dateFormat.format(curTime);
    }

    /**
     * get formated date
     * @param date
     * @return
     */
    public static String formatDate(Date date) {

        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.PDF_DATETIME_FORMAT);

        String strDate = "";
        try {
            strDate = dateFormat.format(date);
        } catch(Exception ex) { }

        return strDate;
    }
}
