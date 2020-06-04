/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（JudgeStatisticsPdfView）
 * 文件名：	JudgeStatisticsPdfView.java
 * 描述：	JudgeStatisticsPdfView
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
import com.nuctech.ecuritycheckitem.models.response.userstatistics.JudgeStatisticsResponseModel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

public class JudgeStatisticsPdfView extends BasePdfView {

    /**
     * build inputstream of data to be printed
     * @param detailedStatistics
     * @return
     */
    public static InputStream buildPDFDocument(List<JudgeStatisticsResponseModel> detailedStatistics) {

        Document document = new Document();

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {

            PdfWriter.getInstance(document, out);

            document.open();
            document.add(getTitle(messageSource.getMessage("JudgeStatisticsTableTitle", null, currentLocale)));
            document.add(getTime());

            PdfPTable table = new PdfPTable(19);

            table.setWidthPercentage(99);
            Stream.of("ID", "StatWidth", "TotalJudge", "ArtificialResult", "ArtificialResultRate", "AssignTimoutResult", "AssignTimeoutResultRate", "JudgeTimeoutResult",
                    "JudgeTimeoutResultRate", "AtrResult", "AtrResultRate", "NoSuspicion", "NoSuspicionRate", "Suspicion", "SuspicionRate", "ArtificialJudgeDefaultTime",
                    "ArtificialJudgeAvgTime", "ArtificialJudgeMaxTime", "ArtificialJudgeMinTime")
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell();

                        header.setBorderWidth(2);
                        header.setPhrase(new Phrase(messageSource.getMessage(columnTitle, null, currentLocale), getFontWithSize(Constants.PDF_HEAD_FONT_SIZE)));
                        table.addCell(header);
                    });

            long index = 1;

            for (JudgeStatisticsResponseModel record: detailedStatistics) {


                DecimalFormat df = new DecimalFormat("0.00");

                addTableCell(table, Long.toString(index ++));
                addTableCell(table, record.getTime());
                addTableCell(table, Long.toString(record.getTotal()));
                addTableCell(table, Long.toString(record.getArtificialJudge()));
                addTableCell(table, df.format(record.getArtificialResultRate()));
                addTableCell(table, Long.toString(record.getAssignTimeout()));
                addTableCell(table, df.format(record.getAssignTimeoutResultRate()));
                addTableCell(table, Long.toString(record.getJudgeTimeout()));
                addTableCell(table, df.format(record.getJudgeTimeoutResultRate()));
                addTableCell(table, Long.toString(record.getAtrResult()));
                addTableCell(table, df.format(record.getAtrResultRate()));
                addTableCell(table, Long.toString(record.getNoSuspiction()));
                addTableCell(table, df.format(record.getNoSuspictionRate()));
                addTableCell(table, Long.toString(record.getSuspiction()));
                addTableCell(table, df.format(record.getSuspictionRate()));
                addTableCell(table, Long.toString(record.getLimitedArtificialDuration()));
                addTableCell(table, df.format(record.getAvgArtificialJudgeDuration()));
                addTableCell(table, Long.toString(record.getMaxArtificialJudgeDuration()));
                addTableCell(table, Long.toString(record.getMinArtificialJudgeDuration()));

            }


            document.add(table);

            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
