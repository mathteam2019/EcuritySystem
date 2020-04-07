/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（BaseWordView）
 * 文件名：	BaseWordView.java
 * 描述：	BaseWordView
 * 作者名：	Choe
 * 日期：	2019/11/27
 *
 */

package com.nuctech.ecuritycheckitem.export;

import com.nuctech.ecuritycheckitem.config.Constants;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.springframework.context.MessageSource;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BaseWordView {

    public static MessageSource messageSource;
    public static Locale currentLocale = Locale.CHINESE;

    /**
     * set message aware variable
     * @param messageSource
     */
    public static void setMessageSource(MessageSource messageSource) {
        BaseWordView.messageSource = messageSource;
    }

    /**
     * set current locale
     * @param locale
     */
    public static void setCurrentLocale(Locale locale) {
        currentLocale = locale;
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
     * get formatted date
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

    public static void setWidth(XWPFTable table, XWPFDocument document) {
//        CTBody body = document.getDocument().getBody();
//        if(!body.isSetSectPr()) {
//            body.addNewSectPr();
//        }
//        CTSectPr sectPr = body.getSectPr();
//        if(!sectPr.isSetPgSz()) {
//            sectPr.addNewPgSz();
//        }
//        CTPageSz pageSize = sectPr.getPgSz();
//        pageSize.setOrient(STPageOrientation.LANDSCAPE);
//        double pageWidth = pageSize.getW().doubleValue();
//        CTPageMar pageMargin = sectPr.getPgMar();
//        double pageMarginLeft = pageMargin.getLeft().doubleValue();
//        double pageMarginRight = pageMargin.getRight().doubleValue();
//        double effectivePageWidth = pageWidth - pageMarginLeft - pageMarginRight;
        double effectivePageWidth = 9600;
        for(int i = 0; i < table.getNumberOfRows(); i ++) {
            XWPFTableRow tableRow = table.getRow(i);
            int colSize = tableRow.getTableCells().size();
            for(int j = 0; j < colSize; j ++) {
                XWPFTableCell cell = tableRow.getCell(j);
                CTTblWidth cellWidth = cell.getCTTc().addNewTcPr().addNewTcW();
                CTTcPr pr = cell.getCTTc().addNewTcPr();
                pr.addNewNoWrap();
                cellWidth.setW(BigInteger.valueOf((int) (effectivePageWidth / colSize)));
            }
        }

    }
}
