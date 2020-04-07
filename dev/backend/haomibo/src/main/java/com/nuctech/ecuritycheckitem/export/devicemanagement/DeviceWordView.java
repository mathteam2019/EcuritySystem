/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（DeviceWordView）
 * 文件名：	DeviceWordView.java
 * 描述：	DeviceWordView
 * 作者名：	Choe
 * 日期：	2019/11/29
 *
 */

package com.nuctech.ecuritycheckitem.export.devicemanagement;

import com.nuctech.ecuritycheckitem.config.ConstantDictionary;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.export.BaseWordView;
import com.nuctech.ecuritycheckitem.models.db.SysDevice;
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

public class DeviceWordView extends BaseWordView {

    /**
     * create title paragraph
     * @param document
     */
    private static void createHeaderPart(XWPFDocument document) {

        XWPFParagraph title = document.createParagraph();
        title.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun titleRun = title.createRun();
        titleRun.setText(messageSource.getMessage("Device.Title", null, currentLocale));
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
        tableRowHeader.getCell(0).setText(messageSource.getMessage("Device.No", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("Device.Device", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("Device.Name", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("Device.Status", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("Device.Archive.Name", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("Device.Category", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("Device.Manufacturer", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("Device.OriginalModel", null, currentLocale));

    }

    /**
     * build inputstream of data to be exported
     * @param exportList
     * @return
     */
    public static InputStream buildWordDocument(List<SysDevice> exportList) {

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
                SysDevice device = exportList.get(i);

                XWPFTableRow tableRow = table.createRow();

                tableRow.getCell(0).setText(String.valueOf(id));
                tableRow.getCell(1).setText(device.getDeviceSerial());
                tableRow.getCell(2).setText(device.getDeviceName());

                tableRow.getCell(3).setText(ConstantDictionary.getDataValue(device.getStatus()));
                if(device.getArchive() != null) {
                    tableRow.getCell(4).setText(device.getArchive().getArchivesName());
                } else {
                    tableRow.getCell(4).setText(messageSource.getMessage("None", null, currentLocale));
                }
                if(device.getCategory() != null) {
                    tableRow.getCell(5).setText(device.getCategory().getCategoryName());
                } else {
                    tableRow.getCell(5).setText(messageSource.getMessage("None", null, currentLocale));
                }
                if(device.getArchive() != null && device.getArchive().getArchiveTemplate() != null) {
                    tableRow.getCell(6).setText(ConstantDictionary.getDataValue(device.getArchive().getArchiveTemplate().getManufacturer()));
                    tableRow.getCell(7).setText(device.getArchive().getArchiveTemplate().getOriginalModel());
                } else {
                    tableRow.getCell(6).setText(messageSource.getMessage("None", null, currentLocale));
                    tableRow.getCell(7).setText(messageSource.getMessage("None", null, currentLocale));
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
