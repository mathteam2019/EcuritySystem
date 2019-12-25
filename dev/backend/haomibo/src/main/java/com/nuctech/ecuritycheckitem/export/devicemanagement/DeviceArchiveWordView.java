/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（DeviceArchiveWordView）
 * 文件名：	DeviceArchiveWordView.java
 * 描述：	DeviceArchive WordView
 * 作者名：	Choe
 * 日期：	2019/11/29
 *
 */

package com.nuctech.ecuritycheckitem.export.devicemanagement;

import com.nuctech.ecuritycheckitem.config.ConstantDictionary;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.export.BaseWordView;
import com.nuctech.ecuritycheckitem.models.db.SerArchive;
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

public class DeviceArchiveWordView extends BaseWordView {

    /**
     * create title paragraph
     * @param document
     */
    private static void createHeaderPart(XWPFDocument document) {

        XWPFParagraph title = document.createParagraph();
        title.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun titleRun = title.createRun();
        titleRun.setText("档案管理");
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
        tableRowHeader.getCell(0).setText("序号");
        tableRowHeader.addNewTableCell().setText("档案编号");
        tableRowHeader.addNewTableCell().setText("模板");
        tableRowHeader.addNewTableCell().setText("生效");
        tableRowHeader.addNewTableCell().setText("设备分类");
        tableRowHeader.addNewTableCell().setText("生产厂商");
        tableRowHeader.addNewTableCell().setText("设备型号");

    }

    /**
     * build inputstream to be exported
     * @param exportList
     * @return
     */
    public static InputStream buildWordDocument(List<SerArchive> exportList) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            //Blank Document
            XWPFDocument document = new XWPFDocument();

            createHeaderPart(document);

            XWPFTable table = document.createTable();

            createTableHeader(table);

            for (Integer i = 0; i < exportList.size(); i ++) {

                SerArchive archive = exportList.get(i);

                XWPFTableRow tableRow = table.createRow();

                tableRow.getCell(0).setText(archive.getArchiveId().toString());
                tableRow.getCell(1).setText(archive.getArchivesNumber());
                if(archive.getArchiveTemplate() != null) {
                    tableRow.getCell(2).setText(archive.getArchiveTemplate().getTemplateName());
                } else {
                    tableRow.getCell(2).setText("无");
                }
                tableRow.getCell(3).setText(ConstantDictionary.getDataValue(archive.getStatus()));
                if(archive.getArchiveTemplate() != null && archive.getArchiveTemplate().getDeviceCategory() != null) {
                    tableRow.getCell(4).setText(archive.getArchiveTemplate().getDeviceCategory().getCategoryName());
                } else {
                    tableRow.getCell(4).setText("无");
                }
                if(archive.getArchiveTemplate() != null) {
                    tableRow.getCell(5).setText(archive.getArchiveTemplate().getManufacturer());
                    tableRow.getCell(6).setText(archive.getArchiveTemplate().getOriginalModel());
                } else {
                    tableRow.getCell(5).setText("无");
                    tableRow.getCell(6).setText("无");
                }
            }

            document.write(out);
            document.close();
        }
        catch (Exception e) { }

        return new ByteArrayInputStream(out.toByteArray());
    }

}
