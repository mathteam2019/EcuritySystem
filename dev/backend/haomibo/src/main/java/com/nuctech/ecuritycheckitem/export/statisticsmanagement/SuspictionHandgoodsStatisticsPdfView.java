/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（SuspictionHandgoodsStatisticsPdfView）
 * 文件名：	SuspictionHandgoodsStatisticsPdfView.java
 * 描述：	SuspictionHandgoodsStatisticsPdfView
 * 作者名：	Tiny
 * 日期：	2019/11/30
 *
 */

package com.nuctech.ecuritycheckitem.export.statisticsmanagement;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.nuctech.ecuritycheckitem.config.ConstantDictionary;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.controllers.taskmanagement.statisticsmanagement.SuspicionHandgoodsStatisticsController;
import com.nuctech.ecuritycheckitem.export.BasePdfView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.TreeMap;

public class SuspictionHandgoodsStatisticsPdfView extends BasePdfView {

    /**
     * build inputstream of data to be printed
     * @param detailedStatistics
     * @return
     */
    public static InputStream buildPDFDocument(TreeMap<Integer, TreeMap<String, Long>> detailedStatistics) {

        Document document = new Document();

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {

            PdfWriter.getInstance(document, out);

            document.open();
            document.add(getTitle(messageSource.getMessage("SuspictionHandgoodsStatisticsTableTitle", null, currentLocale)));
            document.add(getTime());

            PdfPTable table = new PdfPTable(SuspicionHandgoodsStatisticsController.handGoodsIDList.size() + 2);

            table.setWidthPercentage(99);

            PdfPCell headerNo = new PdfPCell();
            headerNo.setBorderWidth(2);
            headerNo.setPhrase(new Phrase(messageSource.getMessage("ID", null, currentLocale), getFontWithSize(Constants.PDF_HEAD_FONT_SIZE)));
            table.addCell(headerNo);

            PdfPCell headerTime = new PdfPCell();
            headerTime.setBorderWidth(2);
            headerTime.setPhrase(new Phrase(messageSource.getMessage("StatWidth", null, currentLocale), getFontWithSize(Constants.PDF_HEAD_FONT_SIZE)));
            table.addCell(headerTime);

            SuspicionHandgoodsStatisticsController.handGoodsIDList
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell();

                        header.setBorderWidth(2);
                        header.setPhrase(new Phrase(ConstantDictionary.getDataValue(columnTitle), getFontWithSize(Constants.PDF_HEAD_FONT_SIZE)));
                        table.addCell(header);
                    });

            long index = 1;

            for (Map.Entry<Integer, TreeMap<String, Long>> entry : detailedStatistics.entrySet()) {

                TreeMap<String, Long> record = entry.getValue();

                DecimalFormat df = new DecimalFormat("0.00");

                addTableCell(table, Long.toString(index ++));
                addTableCell(table, Long.toString(record.get("time")));


                for (int i = 0; i < SuspicionHandgoodsStatisticsController.handGoodsIDList.size(); i ++) {

                    addTableCell(table, record.get(SuspicionHandgoodsStatisticsController.handGoodsIDList.get(i)).toString());

                }



            }


            document.add(table);

            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
