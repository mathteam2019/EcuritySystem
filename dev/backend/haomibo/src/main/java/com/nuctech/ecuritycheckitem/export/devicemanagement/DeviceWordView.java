package com.nuctech.ecuritycheckitem.export.devicemanagement;

import com.nuctech.ecuritycheckitem.config.ConstantDictionary;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.export.BaseWordView;
import com.nuctech.ecuritycheckitem.models.db.SysDevice;
import com.nuctech.ecuritycheckitem.models.db.SysDeviceCategory;
import org.apache.poi.xwpf.usermodel.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

public class DeviceWordView extends BaseWordView {

    private static void createHeaderPart(XWPFDocument document) {

        XWPFParagraph title = document.createParagraph();
        title.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun titleRun = title.createRun();
        titleRun.setText("设备管理");
        titleRun.setFontSize(Constants.WORD_HEAD_FONT_SIZE);
        titleRun.setFontFamily(Constants.WORD_HEAD_FONT_NAME);

        XWPFParagraph subTitle = document.createParagraph();
        subTitle.setAlignment(ParagraphAlignment.RIGHT);

        XWPFRun subTitleRun = subTitle.createRun();
        subTitleRun.setText(getCurrentTime());
        titleRun.setFontSize(Constants.WORD_HEAD_FONT_SIZE);
        titleRun.setFontFamily(Constants.WORD_HEAD_FONT_NAME);

    }

    private static void createTableHeader(XWPFTable table) {

        table.setWidthType(TableWidthType.DXA);
        //create first row
        XWPFTableRow tableRowHeader = table.getRow(0);
        tableRowHeader.getCell(0).setText("序号");
        tableRowHeader.addNewTableCell().setText("设备编号");
        tableRowHeader.addNewTableCell().setText("模板");
        tableRowHeader.addNewTableCell().setText("生效");
        tableRowHeader.addNewTableCell().setText("设备分类");
        tableRowHeader.addNewTableCell().setText("生产厂商");
        tableRowHeader.addNewTableCell().setText("设备型号");

    }

    public static InputStream buildWordDocument(List<SysDevice> exportList) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            //Blank Document
            XWPFDocument document = new XWPFDocument();

            createHeaderPart(document);

            XWPFTable table = document.createTable();

            createTableHeader(table);

            for (Integer i = 0; i < exportList.size(); i ++) {

                SysDevice device = exportList.get(i);

                XWPFTableRow tableRow = table.createRow();

                tableRow.getCell(0).setText(device.getDeviceId().toString());
                tableRow.getCell(1).setText(device.getDeviceSerial());
                if(device.getArchive() != null && device.getArchive().getArchiveTemplate() != null) {
                    tableRow.getCell(2).setText(device.getArchive().getArchiveTemplate().getTemplateName());
                } else {
                    tableRow.getCell(2).setText("无");
                }
                tableRow.getCell(3).setText(ConstantDictionary.getDataValue(device.getStatus()));
                if(device.getArchive() != null && device.getArchive().getArchiveTemplate() != null
                        && device.getArchive().getArchiveTemplate().getDeviceCategory() != null) {
                    tableRow.getCell(4).setText(device.getArchive().getArchiveTemplate().getDeviceCategory().getCategoryName());
                } else {
                    tableRow.getCell(4).setText("无");
                }
                if(device.getArchive() != null && device.getArchive().getArchiveTemplate() != null) {
                    tableRow.getCell(5).setText(device.getArchive().getArchiveTemplate().getManufacturer());
                    tableRow.getCell(6).setText(device.getArchive().getArchiveTemplate().getOriginalModel());
                } else {
                    tableRow.getCell(5).setText("无");
                    tableRow.getCell(6).setText("无");
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
