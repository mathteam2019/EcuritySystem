/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（EvaluateJudgeStatisticsPdfView）
 * 文件名：	EvaluateJudgeStatisticsPdfView.java
 * 描述：	EvaluateJudgeStatisticsPdfView
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
import com.nuctech.ecuritycheckitem.models.response.userstatistics.EvaluateJudgeResponseModel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

public class EvaluateJudgeStatisticsPdfView extends BasePdfView {

    /**
     * build inputstream of data to be printed
     * @param detailedStatistics
     * @return
     */
    public static InputStream buildPDFDocument(List<EvaluateJudgeResponseModel> detailedStatistics) {

        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {

            PdfWriter.getInstance(document, out);

            document.open();
            document.add(getTitle(messageSource.getMessage("EvaluateJudgeStatisticsTableTitle", null, currentLocale)));
            document.add(getTime());

            PdfPTable table = new PdfPTable(17);

            table.setWidthPercentage(99);
            Stream.of("ID", "StatWidth", "TotalHandExam", "Missing", "MissingRate", "Mistake", "MistakeRate", "ArtificialJudge", "ArtificialJudgeMissing",
                    "ArtificialJudgeMissingRate", "ArtificialJudgeMistake", "ArtificialJudgeMistakeRate", "IntelligenceJudge", "IntelligenceJudgeMissing", "IntelligenceJudgeMissingRate", "IntelligenceJudgeMistake", "IntelligenceJudgeMistakeRate")
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell();
                        header.setBorderWidth(2);
                        header.setPhrase(new Phrase(messageSource.getMessage(columnTitle, null, currentLocale), getFontWithSize(Constants.PDF_HEAD_FONT_SIZE)));
                        table.addCell(header);
                    });

            long index = 1;

            for(EvaluateJudgeResponseModel record: detailedStatistics) {

                DecimalFormat df = new DecimalFormat("0.00");

                addTableCell(table, Long.toString(index++));
                addTableCell(table, record.getTime());
                addTableCell(table, Long.toString(record.getTotal()));
                addTableCell(table, Long.toString(record.getMissingReport()));
                addTableCell(table, df.format(record.getMissingReportRate()));
                addTableCell(table, Long.toString(record.getMistakeReport()));
                addTableCell(table, df.format(record.getMistakeReportRate()));

                addTableCell(table, Long.toString(record.getArtificialJudge()));
                addTableCell(table, Long.toString(record.getArtificialJudgeMissing()));
                addTableCell(table, df.format(record.getArtificialJudgeMissingRate()));
                addTableCell(table, Long.toString(record.getArtificialJudgeMistake()));
                addTableCell(table, df.format(record.getArtificialJudgeMistakeRate()));

                addTableCell(table, Long.toString(record.getIntelligenceJudge()));
                addTableCell(table, df.format(record.getIntelligenceJudgeMissing()));
                addTableCell(table, df.format(record.getIntelligenceJudgeMissingRate()));
                addTableCell(table, df.format(record.getIntelligenceJudgeMistake()));
                addTableCell(table, df.format(record.getIntelligenceJudgeMistakeRate()));

            }
            document.add(table);
            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
