package com.nuctech.ecuritycheckitem.export.permissionmanagement;

import com.nuctech.ecuritycheckitem.config.ConstantDictionary;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.export.BaseWordView;
import com.nuctech.ecuritycheckitem.models.db.SysOrg;
import com.nuctech.ecuritycheckitem.models.db.SysUser;
import org.apache.poi.xwpf.usermodel.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

public class OrganizationWordView extends BaseWordView {

    private static void createHeaderPart(XWPFDocument document) {

        XWPFParagraph title = document.createParagraph();
        title.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun titleRun = title.createRun();
        titleRun.setText("机构管理");
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
        tableRowHeader.addNewTableCell().setText("机构编号");
        tableRowHeader.addNewTableCell().setText("机构名称");
        tableRowHeader.addNewTableCell().setText("生效");
        tableRowHeader.addNewTableCell().setText("上级机构编号");
        tableRowHeader.addNewTableCell().setText("上级机构");
        tableRowHeader.addNewTableCell().setText("负责人");
        tableRowHeader.addNewTableCell().setText("联系方式");
        tableRowHeader.addNewTableCell().setText("备注");
    }

    public static InputStream buildWordDocument(List<SysOrg> exportList) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            //Blank Document
            XWPFDocument document = new XWPFDocument();

            createHeaderPart(document);

            XWPFTable table = document.createTable();

            createTableHeader(table);

            for (Integer i = 0; i < exportList.size(); i ++) {

                SysOrg org = exportList.get(i);

                XWPFTableRow tableRow = table.createRow();

                tableRow.getCell(0).setText(org.getOrgId().toString());
                tableRow.getCell(1).setText(org.getOrgNumber());
                tableRow.getCell(2).setText(org.getOrgName());
                tableRow.getCell(3).setText(ConstantDictionary.getDataValue(org.getStatus()));
                if(org.getParent() != null) {
                    tableRow.getCell(4).setText(org.getParent().getOrgNumber());
                    tableRow.getCell(5).setText(org.getParent().getOrgName());
                } else {
                    tableRow.getCell(4).setText("无");
                    tableRow.getCell(5).setText("无");

                }
                tableRow.getCell(6).setText(org.getLeader());
                tableRow.getCell(7).setText(org.getMobile());
                tableRow.getCell(8).setText(org.getNote());

            }

            document.write(out);
            document.close();
        }
        catch (Exception e) {

        }

        return new ByteArrayInputStream(out.toByteArray());

    }

}
