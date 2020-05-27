/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（HistoryTaskWordView）
 * 文件名：	HistoryTaskWordView.java
 * 描述：	HistoryTaskWordView
 * 作者名：	Tiny
 * 日期：	2019/11/30
 *
 */

package com.nuctech.ecuritycheckitem.export.taskmanagement;

import com.nuctech.ecuritycheckitem.config.ConstantDictionary;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.export.BaseWordView;
import com.nuctech.ecuritycheckitem.models.simplifieddb.HistorySimplifiedForHistoryTableManagement;
import com.nuctech.ecuritycheckitem.models.simplifieddb.HistorySimplifiedForHistoryTaskManagement;
import com.nuctech.ecuritycheckitem.utils.Utils;
import org.apache.commons.lang3.StringUtils;
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
import java.util.List;

public class HistoryTaskWordView extends BaseWordView {

    /**
     * create title paragraph
     * @param document
     */
    private static void createHeaderPart(XWPFDocument document) {

        XWPFParagraph title = document.createParagraph();
        title.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun titleRun = title.createRun();
        titleRun.setText(messageSource.getMessage("HistoryTaskTableTitle", null, currentLocale));
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
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("TaskNumber", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("WorkMode", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("TaskResult", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("Scene", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("ScanDeviceName", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("ScanUserName", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("ScanStartTime", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("ScanEndTime", null, currentLocale));


    }

    /**
     * build inputstream of data to be exported
     * @param exportTaskList
     * @return
     */
    public static InputStream buildWordDocument(List<HistorySimplifiedForHistoryTableManagement> exportTaskList) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            //Blank Document
            XWPFDocument document = new XWPFDocument();

            createHeaderPart(document);

            XWPFTable table = document.createTable();

            createTableHeader(table);

            int number = 0;
            for (Integer i = 0; i < exportTaskList.size(); i ++) {

                HistorySimplifiedForHistoryTableManagement task = exportTaskList.get(i);

                XWPFTableRow tableRow = table.createRow();
                tableRow.getCell(0).setText(String.valueOf(++ number));

                tableRow.getCell(1).setText(task.getTaskNumber());

                if (!StringUtils.isEmpty(task.getModeName())){
                    tableRow.getCell(2).setText(ConstantDictionary.getDataValue(task.getModeName()));
                } else {
                    tableRow.getCell(2).setText(messageSource.getMessage("None", null, currentLocale));
                }

                tableRow.getCell(3).setText(Utils.getTaskResult(task));

                if (!StringUtils.isEmpty(task.getFieldDesignation())){
                    tableRow.getCell(4).setText(task.getFieldDesignation());
                } else {
                    tableRow.getCell(4).setText(messageSource.getMessage("None", null, currentLocale));
                }


                if (!StringUtils.isEmpty(task.getScanDeviceName())){
                    tableRow.getCell(5).setText(task.getScanDeviceName());
                } else {
                    tableRow.getCell(5).setText(messageSource.getMessage("None", null, currentLocale));
                }

                if (!StringUtils.isEmpty(task.getScanPointsManName())){
                    tableRow.getCell(6).setText(task.getScanPointsManName());
                } else {
                    tableRow.getCell(6).setText(messageSource.getMessage("None", null, currentLocale));
                }

                if (task.getScanStartTime() != null) {
                    tableRow.getCell(7).setText(formatDate(task.getScanStartTime()));
                } else {
                    tableRow.getCell(7).setText(messageSource.getMessage("None", null, currentLocale));
                }

                if (task.getScanEndTime() != null) {
                    tableRow.getCell(8).setText(formatDate(task.getScanEndTime()));
                } else {
                    tableRow.getCell(8).setText(messageSource.getMessage("None", null, currentLocale));
                }

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
