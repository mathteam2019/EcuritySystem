/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（PreviewStatisticsPdfView）
 * 文件名：	PreviewStatisticsPdfView.java
 * 描述：	PreviewStatisticsPdfView
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
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.export.BasePdfView;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.TotalStatistics;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

public class PreviewStatisticsPdfView extends BasePdfView {

    /**
     * build inputstream of data to be printed
     * @param detailedStatistics
     * @return
     */
    public static InputStream buildPDFDocument(List<TotalStatistics> detailedStatistics) {

        Document document = new Document();

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {

            PdfWriter.getInstance(document, out);

            document.open();
            document.add(getTitle(messageSource.getMessage("PreviewStatisticsTableTitle", null, currentLocale)));
            document.add(getTime());

            PdfPTable table = new PdfPTable(15);

            table.setWidthPercentage(99);
            Stream.of("ID", "StatWidth", "TotalScan", "ValidScans", "ValidScanRate", "TotalJudge",  "Suspicion", "SuspicionRate", "Nosuspicion", "ScanNosuspictionRate",
                    "TotalHands", "Seizure", "SeizureRate", "NoSeizure", "NoSeizureRate")
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell();

                        header.setBorderWidth(2);
                        header.setPhrase(new Phrase(messageSource.getMessage(columnTitle, null, currentLocale), getFontWithSize(Constants.PDF_HEAD_FONT_SIZE)));
                        table.addCell(header);
                    });


            long index = 1;

            for (TotalStatistics record : detailedStatistics) {


                DecimalFormat df = new DecimalFormat("0.00");

                addTableCell(table, Long.toString(index ++));
                addTableCell(table, record.getTime());
                addTableCell(table, Long.toString(record.getScanStatistics().getTotalScan()));
                addTableCell(table, Long.toString(record.getScanStatistics().getValidScan()));
                addTableCell(table, df.format(record.getScanStatistics().getValidScanRate()));
                addTableCell(table, Long.toString(record.getJudgeStatistics().getTotalJudge()));
                addTableCell(table, Long.toString(record.getJudgeStatistics().getSuspictionJudge()));
                addTableCell(table, df.format(record.getJudgeStatistics().getSuspictionJudgeRate()));
                addTableCell(table, Long.toString(record.getJudgeStatistics().getNoSuspictionJudge()));
                addTableCell(table, df.format(record.getJudgeStatistics().getNoSuspictionJudgeRate()));
                addTableCell(table, Long.toString(record.getHandExaminationStatistics().getTotalHandExamination()));
                addTableCell(table, Long.toString(record.getHandExaminationStatistics().getSeizureHandExamination()));
                addTableCell(table, df.format(record.getHandExaminationStatistics().getSeizureHandExaminationRate()));
                addTableCell(table, Long.toString(record.getHandExaminationStatistics().getNoSeizureHandExamination()));
                addTableCell(table, df.format(record.getHandExaminationStatistics().getNoSeizureHandExaminationRate()));


            }


            document.add(table);

            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
