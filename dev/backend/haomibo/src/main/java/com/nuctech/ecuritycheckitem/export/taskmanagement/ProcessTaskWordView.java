/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（ProcessTaskWordView）
 * 文件名：	ProcessTaskWordView.java
 * 描述：	ProcessTaskWordView
 * 作者名：	Tiny
 * 日期：	2019/11/30
 *
 */

package com.nuctech.ecuritycheckitem.export.taskmanagement;

import com.nuctech.ecuritycheckitem.config.ConstantDictionary;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.export.BaseWordView;
import com.nuctech.ecuritycheckitem.models.simplifieddb.SerTaskSimplifiedForProcessTableManagement;
import com.nuctech.ecuritycheckitem.models.simplifieddb.SerTaskSimplifiedForProcessTaskManagement;
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

public class ProcessTaskWordView extends BaseWordView {

    /**
     * create title paragraph
     * @param document
     */
    private static void createHeaderPart(XWPFDocument document) {

        XWPFParagraph title = document.createParagraph();
        title.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun titleRun = title.createRun();
        titleRun.setText(messageSource.getMessage("ProcessTaskTableTitle", null, currentLocale));
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
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("TaskStatus", null, currentLocale));
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
    public static InputStream buildWordDocument(List<SerTaskSimplifiedForProcessTableManagement> exportTaskList) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            //Blank Document
            XWPFDocument document = new XWPFDocument();

            createHeaderPart(document);

            XWPFTable table = document.createTable();

            createTableHeader(table);

            int number = 0;
            for (Integer i = 0; i < exportTaskList.size(); i ++) {

                SerTaskSimplifiedForProcessTableManagement task = exportTaskList.get(i);

                XWPFTableRow tableRow = table.createRow();
                tableRow.getCell(0).setText(String.valueOf(++ number));
                tableRow.getCell(1).setText(task.getTaskNumber());

                if (task.getWorkFlow() != null) {
                    if (task.getWorkFlow().getWorkMode() != null) {
                        tableRow.getCell(2).setText(ConstantDictionary.getDataValue(task.getWorkFlow().getWorkMode().getModeName()));
                    } else {
                        tableRow.getCell(2).setText(messageSource.getMessage("None", null, currentLocale));
                    }
                } else {

                }

                tableRow.getCell(3).setText(ConstantDictionary.getDataValue(task.getTaskStatus()));

                if (task.getField() != null) {
                    tableRow.getCell(4).setText(task.getField().getFieldDesignation());
                } else {
                    tableRow.getCell(4).setText(messageSource.getMessage("None", null, currentLocale));
                }

                if (task.getSerScan() != null) {
                    if (task.getSerScan().getScanDevice() != null) {
                        tableRow.getCell(5).setText(task.getSerScan().getScanDevice().getDeviceName());
                    } else {
                        tableRow.getCell(5).setText(messageSource.getMessage("None", null, currentLocale));
                    }

                    if (task.getSerScan().getScanPointsman() != null) {
                        tableRow.getCell(6).setText(task.getSerScan().getScanPointsman().getUserName());
                    } else {
                        tableRow.getCell(6).setText(messageSource.getMessage("None", null, currentLocale));
                    }

                    if (task.getSerScan().getScanStartTime() != null) {
                        tableRow.getCell(7).setText(formatDate(task.getSerScan().getScanStartTime()));
                    }
                    else {
                        tableRow.getCell(7).setText(messageSource.getMessage("None", null, currentLocale));
                    }

                    if (task.getSerScan().getScanEndTime() != null) {
                        tableRow.getCell(8).setText(formatDate(task.getSerScan().getScanEndTime()));
                    }
                    else {
                        tableRow.getCell(8).setText(messageSource.getMessage("None", null, currentLocale));
                    }


                } else {
                    tableRow.getCell(5).setText(messageSource.getMessage("None", null, currentLocale));
                    tableRow.getCell(6).setText(messageSource.getMessage("None", null, currentLocale));
                    tableRow.getCell(7).setText(messageSource.getMessage("None", null, currentLocale));
                    tableRow.getCell(8).setText(messageSource.getMessage("None", null, currentLocale));
                }
            }

            document.write(out);
            document.close();
        }
        catch (Exception e) {

        }

        return new ByteArrayInputStream(out.toByteArray());

    }

}
