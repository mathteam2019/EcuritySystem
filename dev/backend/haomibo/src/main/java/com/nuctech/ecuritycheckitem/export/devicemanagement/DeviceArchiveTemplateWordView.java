package com.nuctech.ecuritycheckitem.export.devicemanagement;

import com.nuctech.ecuritycheckitem.config.ConstantDictionary;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.export.BaseWordView;
import com.nuctech.ecuritycheckitem.models.db.SerArchive;
import com.nuctech.ecuritycheckitem.models.db.SerArchiveTemplate;
import org.apache.poi.xwpf.usermodel.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

public class DeviceArchiveTemplateWordView extends BaseWordView {

    private static void createHeaderPart(XWPFDocument document) {

        XWPFParagraph title = document.createParagraph();
        title.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun titleRun = title.createRun();
        titleRun.setText("模板设置");
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
        tableRowHeader.addNewTableCell().setText("模板编号");
        tableRowHeader.addNewTableCell().setText("模板");
        tableRowHeader.addNewTableCell().setText("生效");
        tableRowHeader.addNewTableCell().setText("设备分类");
        tableRowHeader.addNewTableCell().setText("生产厂商");
        tableRowHeader.addNewTableCell().setText("设备型号");

    }

    public static InputStream buildWordDocument(List<SerArchiveTemplate> exportList) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            //Blank Document
            XWPFDocument document = new XWPFDocument();

            createHeaderPart(document);

            XWPFTable table = document.createTable();

            createTableHeader(table);

            for (Integer i = 0; i < exportList.size(); i ++) {

                SerArchiveTemplate template = exportList.get(i);

                XWPFTableRow tableRow = table.createRow();

                tableRow.getCell(0).setText(template.getArchivesTemplateId().toString());
                tableRow.getCell(1).setText(template.getArchivesTemplateNumber());
                tableRow.getCell(2).setText(template.getTemplateName());
                tableRow.getCell(3).setText(ConstantDictionary.getDataValue(template.getStatus()));
                if(template.getDeviceCategory() != null) {
                    tableRow.getCell(4).setText(template.getDeviceCategory().getCategoryName());
                } else {
                    tableRow.getCell(4).setText("无");

                }
                tableRow.getCell(5).setText(template.getManufacturer());
                tableRow.getCell(6).setText(template.getOriginalModel());
            }

            document.write(out);
            document.close();
        }
        catch (Exception e) {

        }

        return new ByteArrayInputStream(out.toByteArray());

    }

}
