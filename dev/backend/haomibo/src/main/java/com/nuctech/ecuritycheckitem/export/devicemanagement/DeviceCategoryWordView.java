/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（DeviceCategoryWordView）
 * 文件名：	DeviceCategoryWordView.java
 * 描述：	DeviceCategoryWordView
 * 作者名：	Tiny
 * 日期：	2019/11/29
 *
 */

package com.nuctech.ecuritycheckitem.export.devicemanagement;

import com.nuctech.ecuritycheckitem.config.ConstantDictionary;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.export.BaseWordView;
import com.nuctech.ecuritycheckitem.models.db.SysDeviceCategory;
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

public class DeviceCategoryWordView extends BaseWordView {

    /**
     * create title paragraph
     * @param document
     */
    private static void createHeaderPart(XWPFDocument document) {

        XWPFParagraph title = document.createParagraph();
        title.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun titleRun = title.createRun();
        titleRun.setText(messageSource.getMessage("DeviceCategory.Title", null, currentLocale));
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
        tableRowHeader.getCell(0).setText(messageSource.getMessage("DeviceCategory.No", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("DeviceCategory.Number", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("DeviceCategory.Name", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("DeviceCategory.Status", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("DeviceCategory.ParentNumber", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("DeviceCategory.ParentName", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("DeviceCategory.Note", null, currentLocale));

    }

    /**
     * build inputstream of data to be exported
     * @param exportList
     * @return
     */
    public static InputStream buildWordDocument(List<SysDeviceCategory> exportList) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            //Blank Document
            XWPFDocument document = new XWPFDocument();
            createHeaderPart(document);
            XWPFTable table = document.createTable();
            createTableHeader(table);

            int id = 0;
            for (Integer i = 0; i < exportList.size(); i ++) {
                id ++;
                SysDeviceCategory category = exportList.get(i);
                XWPFTableRow tableRow = table.createRow();
                tableRow.getCell(0).setText(String.valueOf(id));
                tableRow.getCell(1).setText(category.getCategoryNumber());
                tableRow.getCell(2).setText(category.getCategoryName());
                tableRow.getCell(3).setText(ConstantDictionary.getDataValue(category.getStatus()));
                if(category.getParent() != null) {
                    tableRow.getCell(4).setText(category.getParent().getCategoryNumber());
                    tableRow.getCell(5).setText(category.getParent().getCategoryName());
                } else {
                    tableRow.getCell(4).setText(messageSource.getMessage("None", null, currentLocale));
                    tableRow.getCell(5).setText(messageSource.getMessage("None", null, currentLocale));
                }
                tableRow.getCell(6).setText(category.getNote());
            }

            document.write(out);
            document.close();
        }
        catch (Exception e) {

        }

        return new ByteArrayInputStream(out.toByteArray());
    }

}
