/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（ScanStatisticsWordView）
 * 文件名：	ScanStatisticsWordView.java
 * 描述：	ScanStatisticsWordView
 * 作者名：	Tiny
 * 日期：	2019/11/30
 *
 */

package com.nuctech.ecuritycheckitem.export.statisticsmanagement;

import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.export.BaseWordView;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.ScanStatistics;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.poi.xwpf.usermodel.TableWidthType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.TreeMap;

public class ScanStatisticsWordView extends BaseWordView {

    /**
     * create title paragraph
     * @param document
     */
    private static void createHeaderPart(XWPFDocument document) {

        XWPFParagraph title = document.createParagraph();
        title.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun titleRun = title.createRun();
        titleRun.setText(messageSource.getMessage("ScanStatisticsTableTitle", null, currentLocale));
        titleRun.setFontSize(Constants.WORD_HEAD_FONT_SIZE);
        titleRun.setFontFamily(Constants.WORD_HEAD_FONT_NAME);

        XWPFParagraph subTitle = document.createParagraph();
        subTitle.setAlignment(ParagraphAlignment.RIGHT);

        XWPFRun subTitleRun = subTitle.createRun();
        subTitleRun.setText(getCurrentTime());
        titleRun.setFontSize(Constants.WORD_HEAD_FONT_SIZE);
        titleRun.setFontFamily(Constants.WORD_HEAD_FONT_NAME);

    }

    /**
     * create table header row
     * @param table
     */
    private static void createTableHeader(XWPFTable table) {

        table.setWidthType(TableWidthType.DXA);
        //create first row
        XWPFTableRow tableRowHeader = table.getRow(0);
        tableRowHeader.getCell(0).setText(messageSource.getMessage("ID", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("StatWidth", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("TotalScan", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("ValidScans", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("ValidScanRate", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("InvalidScans", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("InvalidScanRate", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("PassedScans", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("PassedScanRate", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("AlarmScans", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("AlarmScanRate", null, currentLocale));

    }

    /**
     * build inputstream of data to be exported
     * @param detailedStatistics
     * @return
     */
    public static InputStream buildWordDocument(TreeMap<Integer, ScanStatistics> detailedStatistics) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            //Blank Document
            XWPFDocument document = new XWPFDocument();

            createHeaderPart(document);

            XWPFTable table = document.createTable();

            createTableHeader(table);

            long index = 1;

            for (Map.Entry<Integer, ScanStatistics> entry : detailedStatistics.entrySet()) {

                ScanStatistics record = entry.getValue();

                XWPFTableRow tableRow = table.createRow();

                DecimalFormat df = new DecimalFormat("0.00");

                tableRow.getCell(0).setText(Long.toString(index ++));
                tableRow.getCell(1).setText(Long.toString(record.getTime()));
                tableRow.getCell(2).setText(Long.toString(record.getTotalScan()));
                tableRow.getCell(3).setText(Long.toString(record.getValidScan()));
                tableRow.getCell(4).setText(df.format(record.getValidScanRate()));
                tableRow.getCell(5).setText(Long.toString(record.getInvalidScan()));
                tableRow.getCell(6).setText(df.format(record.getInvalidScanRate()));
                tableRow.getCell(7).setText(Long.toString(record.getPassedScan()));
                tableRow.getCell(8).setText(df.format(record.getPassedScanRate()));
                tableRow.getCell(9).setText(Long.toString(record.getAlarmScan()));
                tableRow.getCell(10).setText(df.format(record.getAlarmScanRate()));

            }

            setWidth(table, document);
            document.write(out);
            document.close();
        }
        catch (Exception e) {

        }

        return new ByteArrayInputStream(out.toByteArray());

    }

}
