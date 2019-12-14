package com.nuctech.ecuritycheckitem.export.fieldmanagement;

import com.nuctech.ecuritycheckitem.config.ConstantDictionary;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.export.BaseWordView;
import com.nuctech.ecuritycheckitem.models.db.SysDevice;
import com.nuctech.ecuritycheckitem.models.db.SysField;
import org.apache.poi.xwpf.usermodel.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

public class FieldManagementWordView extends BaseWordView {

    private static void createHeaderPart(XWPFDocument document) {

        XWPFParagraph title = document.createParagraph();
        title.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun titleRun = title.createRun();
        titleRun.setText("场地管理");
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
        tableRowHeader.addNewTableCell().setText("场地编号");
        tableRowHeader.addNewTableCell().setText("场地");
        tableRowHeader.addNewTableCell().setText("生效");
        tableRowHeader.addNewTableCell().setText("上级场地编号");
        tableRowHeader.addNewTableCell().setText("上级场地");
        tableRowHeader.addNewTableCell().setText("负责人");
        tableRowHeader.addNewTableCell().setText("联系电话");
        tableRowHeader.addNewTableCell().setText("备注");

    }

    public static InputStream buildWordDocument(List<SysField> exportList) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            //Blank Document
            XWPFDocument document = new XWPFDocument();

            createHeaderPart(document);

            XWPFTable table = document.createTable();

            createTableHeader(table);

            for (Integer i = 0; i < exportList.size(); i ++) {

                SysField field = exportList.get(i);

                XWPFTableRow tableRow = table.createRow();

                tableRow.getCell(0).setText(field.getFieldId().toString());
                tableRow.getCell(1).setText(field.getFieldSerial());
                tableRow.getCell(2).setText(field.getFieldDesignation());
                tableRow.getCell(3).setText(ConstantDictionary.getDataValue(field.getStatus()));
                if(field.getParent() != null) {
                    tableRow.getCell(4).setText(field.getParent().getFieldSerial());
                    tableRow.getCell(5).setText(field.getParent().getFieldDesignation());
                } else {
                    tableRow.getCell(4).setText("无");
                    tableRow.getCell(5).setText("无");

                }
                tableRow.getCell(6).setText(field.getLeader());
                tableRow.getCell(7).setText(field.getMobile());
                tableRow.getCell(8).setText(field.getNote());
            }

            document.write(out);
            document.close();
        }
        catch (Exception e) {

        }

        return new ByteArrayInputStream(out.toByteArray());

    }

}
