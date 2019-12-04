package com.nuctech.ecuritycheckitem.export;

import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.nuctech.ecuritycheckitem.config.Constants;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BasePdfView {
    public static Paragraph getTitle(String name) {
        String fontName = "resources/fonts/NotoSansCJKsc-Regular.otf";

        Font font = FontFactory.getFont(fontName, Constants.PDF_TITLE_FONT_SIZE, Font.BOLD);
        Paragraph title = new Paragraph(name, font);
        title.setSpacingAfter(Constants.PDF_TITLE_SPACING);
        title.setAlignment(Element.ALIGN_CENTER);
        return title;
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

        String strDate = dateFormat.format(date);

        return strDate;
    }
}
