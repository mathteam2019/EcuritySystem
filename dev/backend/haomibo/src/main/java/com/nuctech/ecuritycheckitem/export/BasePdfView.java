package com.nuctech.ecuritycheckitem.export;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPTable;
import com.nuctech.ecuritycheckitem.config.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BasePdfView {

    private static Resource res;

    public static void setResource(Resource res_other) {
        res = res_other;
    }

    public static BaseFont getBaseFont() {
        try {
            BaseFont baseFont = BaseFont.createFont(res.getURI().getPath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            return baseFont;
        }catch(Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static Font getFontWithSize(int size) {
        BaseFont baseFont = getBaseFont();
        Font font = new Font(baseFont, size);
        return font;
    }

    public static Paragraph getTitle(String name) {
        BaseFont baseFont = getBaseFont();
        Font font = new Font(baseFont, Constants.PDF_TITLE_FONT_SIZE);
        Paragraph title = new Paragraph(name, font);
        title.setSpacingAfter(Constants.PDF_TITLE_SPACING);
        title.setAlignment(Element.ALIGN_CENTER);
        return title;
    }

    public static void addTableCell(PdfPTable table, String str) {
        table.addCell(new Phrase(str, getFontWithSize(Constants.PDF_CONTENT_FONT_SIZE)));
    }

    public static Paragraph getTime() {
        Date curTime = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.PDF_DATETIME_FORMAT);
        Paragraph time = new Paragraph(dateFormat.format(curTime));
        time.setSpacingAfter(Constants.PDF_TIME_SPACING);
        time.setAlignment(Element.ALIGN_RIGHT);
        return time;
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
