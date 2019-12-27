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
import com.nuctech.ecuritycheckitem.models.db.History;
import com.nuctech.ecuritycheckitem.models.db.SerTask;
import com.nuctech.ecuritycheckitem.models.simplifieddb.HistorySimplifiedForHistoryTaskManagement;
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
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("JudgeDeviceName", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("JudgeUserName", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("JudgeStartTime", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("JudgeEndTime", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("HandExaminationDeviceName", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("HandExaminationUserName", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("HandExaminationStartTime", null, currentLocale));

    }

    /**
     * build inputstream of data to be exported
     * @param exportTaskList
     * @return
     */
    public static InputStream buildWordDocument(List<HistorySimplifiedForHistoryTaskManagement> exportTaskList) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            //Blank Document
            XWPFDocument document = new XWPFDocument();

            createHeaderPart(document);

            XWPFTable table = document.createTable();

            createTableHeader(table);

            for (Integer i = 0; i < exportTaskList.size(); i ++) {

                HistorySimplifiedForHistoryTaskManagement task = exportTaskList.get(i);

                XWPFTableRow tableRow = table.createRow();
                tableRow.getCell(0).setText(task.getHistoryId().toString());

                if (task.getTask() != null) {
                    if (task.getTask().getTaskNumber() != null) {
                        tableRow.getCell(1).setText(task.getTask().getTaskNumber());
                    }
                    else {
                        tableRow.getCell(1).setText("无");
                    }
                }
                else {
                    tableRow.getCell(1).setText("无");
                }

                if (task.getWorkMode() != null) {
                    tableRow.getCell(2).setText(ConstantDictionary.getDataValue(task.getWorkMode().getModeName()));
                } else {
                    tableRow.getCell(2).setText("无");
                }

                tableRow.getCell(3).setText(ConstantDictionary.getDataValue(task.getHandTaskResult()));

                if (task.getTask() != null) {
                    if (task.getTask().getField() != null) {
                        if (task.getTask().getField().getFieldDesignation() != null) {
                            tableRow.getCell(4).setText(task.getTask().getField().getFieldDesignation());
                        } else {
                            tableRow.getCell(4).setText("无");
                        }
                    }
                    else {
                        tableRow.getCell(4).setText("无");
                    }
                }
                else {
                    tableRow.getCell(4).setText("无");
                }


                if (task.getScanDevice() != null) {
                    tableRow.getCell(5).setText(task.getScanDevice().getDeviceName());
                } else {
                    tableRow.getCell(5).setText("无");
                }

                if (task.getScanPointsman() != null) {
                    tableRow.getCell(6).setText(task.getScanPointsman().getUserName());
                } else {
                    tableRow.getCell(6).setText("无");
                }

                if (task.getScanStartTime() != null) {
                    tableRow.getCell(7).setText(formatDate(task.getScanStartTime()));
                } else {
                    tableRow.getCell(7).setText("无");
                }

                if (task.getScanEndTime() != null) {
                    tableRow.getCell(8).setText(formatDate(task.getScanEndTime()));
                } else {
                    tableRow.getCell(8).setText("无");
                }

                if (task.getJudgeDevice() != null) {
                    tableRow.getCell(9).setText(task.getJudgeDevice().getDeviceName());
                } else {
                    tableRow.getCell(9).setText("无");
                }

                if (task.getJudgeUser() != null) {
                    tableRow.getCell(10).setText(task.getJudgeUser().getUserName());
                } else {
                    tableRow.getCell(10).setText("无");
                }

                if (task.getJudgeStartTime() != null) {
                    tableRow.getCell(11).setText(formatDate(task.getJudgeStartTime()));
                } else {
                    tableRow.getCell(11).setText("无");
                }

                if (task.getJudgeEndTime() != null) {
                    tableRow.getCell(12).setText(formatDate(task.getJudgeEndTime()));
                } else {
                    tableRow.getCell(12).setText("无");
                }



                if (task.getHandDevice() != null) {
                    tableRow.getCell(13).setText(task.getHandDevice().getDeviceName());
                } else {
                    tableRow.getCell(13).setText("无");
                }

                if (task.getHandUser() != null) {
                    tableRow.getCell(14).setText(task.getHandUser().getUserName());
                } else {
                    tableRow.getCell(14).setText("无");
                }

                if (task.getHandEndTime() != null) {
                    tableRow.getCell(15).setText(formatDate(task.getHandEndTime()));
                } else {
                    tableRow.getCell(15).setText("无");
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
