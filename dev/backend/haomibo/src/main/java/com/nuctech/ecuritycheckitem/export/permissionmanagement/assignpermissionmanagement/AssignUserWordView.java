package com.nuctech.ecuritycheckitem.export.permissionmanagement.assignpermissionmanagement;

import com.nuctech.ecuritycheckitem.config.ConstantDictionary;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.export.BaseWordView;
import com.nuctech.ecuritycheckitem.models.db.SerDevLog;
import com.nuctech.ecuritycheckitem.models.db.SysRole;
import com.nuctech.ecuritycheckitem.models.db.SysUser;
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
import java.util.ArrayList;
import java.util.List;

public class AssignUserWordView extends BaseWordView {

    private static void createHeaderPart(XWPFDocument document) {

        XWPFParagraph title = document.createParagraph();
        title.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun titleRun = title.createRun();
        titleRun.setText("人员授权");
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
        tableRowHeader.addNewTableCell().setText("人员");
        tableRowHeader.addNewTableCell().setText("性别");
        tableRowHeader.addNewTableCell().setText("账号");
        tableRowHeader.addNewTableCell().setText("隶属机构");
        tableRowHeader.addNewTableCell().setText("角色");
        tableRowHeader.addNewTableCell().setText("数据范围");

    }

    public static InputStream buildWordDocument(List<SysUser> exportList) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            //Blank Document
            XWPFDocument document = new XWPFDocument();

            createHeaderPart(document);

            XWPFTable table = document.createTable();

            createTableHeader(table);

            for (Integer i = 0; i < exportList.size(); i ++) {

                SysUser user = exportList.get(i);

                XWPFTableRow tableRow = table.createRow();

                tableRow.getCell(0).setText(user.getUserId().toString());
                tableRow.getCell(1).setText(user.getUserName());
                tableRow.getCell(2).setText(ConstantDictionary.getDataValue(user.getGender()));
                tableRow.getCell(3).setText(user.getUserAccount());
                tableRow.getCell(4).setText(user.getOrg().getOrgName());
                List<SysRole> sysRoleList = new ArrayList<>();
                user.getRoles().forEach(sysRole -> {
                    sysRoleList.add(sysRole);
                });
                if(sysRoleList.size() > 0) {
                    String str = sysRoleList.get(0).getRoleName();
                    for(int j = 1; j < sysRoleList.size(); j ++) {
                        str = str + ", " + sysRoleList.get(j).getRoleName();
                    }
                    tableRow.getCell(5).setText(str);
                } else {
                    tableRow.getCell(5).setText("无");
                }

                tableRow.getCell(6).setText(user.getDataRangeCategory());

            }

            document.write(out);
            document.close();
        }
        catch (Exception e) {

        }

        return new ByteArrayInputStream(out.toByteArray());

    }

}
