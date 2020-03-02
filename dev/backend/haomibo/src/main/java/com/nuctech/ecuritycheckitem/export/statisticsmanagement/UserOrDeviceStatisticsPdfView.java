/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（UserOrDeviceStatisticsPdfView）
 * 文件名：	UserOrDeviceStatisticsPdfView.java
 * 描述：	UserOrDeviceStatisticsPdfView
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
import java.util.TreeMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class UserOrDeviceStatisticsPdfView extends BasePdfView {

    /**
     * build inputstream of data to be printed
     * @param detailedStatistics
     * @return
     */
    public static InputStream buildPDFDocument(TreeMap<Long, TotalStatistics> detailedStatistics, boolean type) {

        Document document = new Document();

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {

            PdfWriter.getInstance(document, out);

            document.open();
            if (type) {
                document.add(getTitle(messageSource.getMessage("UserStatisticsTableTitle", null,currentLocale)));
            } else {
                document.add(getTitle(messageSource.getMessage("DeviceStatisticsTableTitle", null,currentLocale)));
            }
            document.add(getTime());

            PdfPTable table = new PdfPTable(13);

            List<String> strHeaderList = new ArrayList<>();


            if (type) {
                strHeaderList = Arrays.asList(new String[]{"ID", "UserName", "TotalHandExam", "Missing", "MissingRate", "Mistake", "MistakeRate", "ArtificialJudge", "ArtificialJudgeMissing", "ArtificialJudgeMissingRate", "ArtificialJudgeMistake", "ArtificialJudgeMistakeRate", "IntelligenceJudge"});
            }
            else {
                strHeaderList = Arrays.asList(new String[]{"ID", "DeviceName", "TotalHandExam", "Missing", "MissingRate", "Mistake", "MistakeRate", "ArtificialJudge", "ArtificialJudgeMissing", "ArtificialJudgeMissingRate", "ArtificialJudgeMistake", "ArtificialJudgeMistakeRate", "IntelligenceJudge"});
            }


            table.setWidthPercentage(99);
            strHeaderList
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell();

                        header.setBorderWidth(2);
                        header.setPhrase(new Phrase(messageSource.getMessage(columnTitle, null, currentLocale), getFontWithSize(Constants.PDF_HEAD_FONT_SIZE)));
                        table.addCell(header);
                    });

            long index = 1;

            for (Map.Entry<Long, TotalStatistics> entry : detailedStatistics.entrySet()) {

                TotalStatistics record = entry.getValue();

                DecimalFormat df = new DecimalFormat("0.00");

                addTableCell(table, Long.toString(index++));
                addTableCell(table, record.getName());
                if (record.getScanStatistics() != null) {
                    addTableCell(table, Long.toString(record.getScanStatistics().getTotalScan()));
                    addTableCell(table, Long.toString(record.getScanStatistics().getInvalidScan()));
                    addTableCell(table, df.format(record.getScanStatistics().getInvalidScanRate()));
                }
                else {
                    addTableCell(table, "无");
                    addTableCell(table, "无");
                    addTableCell(table, "无");
                }

                if (record.getJudgeStatistics() != null) {
                    addTableCell(table, Long.toString(record.getJudgeStatistics().getTotalJudge()));
                }
                else {
                    addTableCell(table, "无");
                }

                if (record.getHandExaminationStatistics() != null) {
                    addTableCell(table, Long.toString(record.getHandExaminationStatistics().getTotalHandExamination()));
                }
                else {
                    addTableCell(table, "无");
                }

                if (record.getJudgeStatistics() != null) {
                    addTableCell(table, df.format(record.getJudgeStatistics().getNoSuspictionJudge()));
                    addTableCell(table, df.format(record.getJudgeStatistics().getNoSuspictionJudgeRate()));
                }
                else {
                    addTableCell(table, "无");
                    addTableCell(table, "无");
                }

                if (record.getHandExaminationStatistics() != null) {
                    addTableCell(table, Long.toString(record.getHandExaminationStatistics().getNoSeizureHandExamination()));
                    addTableCell(table, df.format(record.getHandExaminationStatistics().getNoSeizureHandExaminationRate()));
                    addTableCell(table, Long.toString(record.getHandExaminationStatistics().getSeizureHandExamination()));
                    addTableCell(table, df.format(record.getHandExaminationStatistics().getSeizureHandExaminationRate()));
                }
                else {
                    addTableCell(table, "无");
                    addTableCell(table, "无");
                    addTableCell(table, "无");
                    addTableCell(table, "无");
                }

            }


            document.add(table);

            document.close();

        } catch (
                DocumentException e) {
            e.printStackTrace();
        }

        return new

                ByteArrayInputStream(out.toByteArray());
    }
}
