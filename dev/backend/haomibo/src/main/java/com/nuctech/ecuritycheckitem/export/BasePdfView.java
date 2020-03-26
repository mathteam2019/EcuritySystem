/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（BasePdfView）
 * 文件名：	BasePdfView.java
 * 描述：	BasePdfView
 * 作者名：	Choe
 * 日期：	2019/11/27
 *
 */

package com.nuctech.ecuritycheckitem.export;

import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Element;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPTable;
import com.nuctech.ecuritycheckitem.config.Constants;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BasePdfView {

    private static URL fontResource;


    private static Resource resourceFile;

    public static MessageSource messageSource;
    public static Locale currentLocale = Locale.CHINESE;

    /**
     * set MessageSourceAware
     * @param messageSource
     */
    public static void setMessageSource(MessageSource messageSource) {
        BasePdfView.messageSource = messageSource;
    }

    /**
     * set current locale
     * @param locale
     */
    public static void setCurrentLocale(Locale locale) {
        currentLocale = locale;
    }

    /**
     * set font resource
     * @param res_other
     */
    public static void setResource(URL res_other) {
        fontResource = res_other;
    }

    /**
     * set font resource
     * @param res_other
     */
    public static void setResourceFile(Resource res_other) {
        resourceFile = res_other;
    }

    /**
     * get base font
     * @return
     */
    public static BaseFont getBaseFont() {
        try {
            //BaseFont baseFont = BaseFont.createFont(resourceFile.getFile().getPath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            BaseFont baseFont = BaseFont.createFont(fontResource.getPath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            return baseFont;
        }catch(Exception ex) {
            //ex.printStackTrace();
        }
        return null;
    }

    /**
     * get font with size
     * @param size
     * @return
     */
    public static Font getFontWithSize(int size) {
        BaseFont baseFont = getBaseFont();
        Font font = new Font(baseFont, size);
        return font;
    }

    /**
     * get title
     * @param name
     * @return
     */
    public static Paragraph getTitle(String name) {
        BaseFont baseFont = getBaseFont();
        Font font = new Font(baseFont, Constants.PDF_TITLE_FONT_SIZE);
        Paragraph title = new Paragraph(name, font);
        title.setSpacingAfter(Constants.PDF_TITLE_SPACING);
        title.setAlignment(Element.ALIGN_CENTER);
        return title;
    }

    /**
     * add cell with specified font
     * @param table
     * @param str
     */
    public static void addTableCell(PdfPTable table, String str) {
        table.addCell(new Phrase(str, getFontWithSize(Constants.PDF_CONTENT_FONT_SIZE)));
    }

    /**
     * get current time
     * @return
     */
    public static Paragraph getTime() {
        Date curTime = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.PDF_DATETIME_FORMAT);
        Paragraph time = new Paragraph(dateFormat.format(curTime));
        time.setSpacingAfter(Constants.PDF_TIME_SPACING);
        time.setAlignment(Element.ALIGN_RIGHT);
        return time;
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
}
