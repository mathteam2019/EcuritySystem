package com.nuctech.ecuritycheckitem.export;

import com.nuctech.ecuritycheckitem.config.Constants;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BaseExcelView {
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
            dateFormat.format(date);
        } catch(Exception ex) {

        }


        return strDate;
    }
}
