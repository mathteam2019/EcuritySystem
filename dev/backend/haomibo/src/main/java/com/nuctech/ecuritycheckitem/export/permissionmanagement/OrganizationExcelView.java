/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/30
 * @CreatedBy Choe.
 * @FileName OrganizationExcelView.java
 * @ModifyHistory
 */
package com.nuctech.ecuritycheckitem.export.permissionmanagement;

import com.nuctech.ecuritycheckitem.config.ConstantDictionary;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.export.BaseExcelView;
import com.nuctech.ecuritycheckitem.models.db.SysOrg;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class OrganizationExcelView extends BaseExcelView {


    private static void setHeader(Sheet sheet) {
        Row header = sheet.createRow(3);

        Cell headerCellNo = header.createCell(0);
        headerCellNo.setCellValue("序号");

        Cell headerCellNumber = header.createCell(1);
        headerCellNumber.setCellValue("机构编号");

        Cell headerCellName = header.createCell(2);
        headerCellName.setCellValue("机构名称");

        Cell headerCellStatus = header.createCell(3);
        headerCellStatus.setCellValue("生效");

        Cell headerCellParentNumber = header.createCell(4);
        headerCellParentNumber.setCellValue("上级机构编号");

        Cell headerCellParentName = header.createCell(5);
        headerCellParentName.setCellValue("上级机构");

        Cell headerCellLeader = header.createCell(6);
        headerCellLeader.setCellValue("负责人");

        Cell headerCellMobile = header.createCell(7);
        headerCellMobile.setCellValue("联系方式");

        Cell headerCellNote = header.createCell(8);
        headerCellNote.setCellValue("备注");
    }

    public static InputStream buildExcelDocument(List<SysOrg> exportOrgList) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {

            Workbook workbook = new XSSFWorkbook();

            Sheet sheet = workbook.createSheet("Organization");

            Row title = sheet.createRow(0);
            Cell titleCell = title.createCell(0);
            titleCell.setCellValue("机构管理");
            titleCell.setCellStyle(getHeaderStyle(workbook));

            Row time = sheet.createRow(1);
            Cell timeCell = time.createCell(0);
            timeCell.setCellValue(getCurrentTime());

            setHeader(sheet);
            int counter = 4;

            CellStyle style = workbook.createCellStyle();
            style.setWrapText(true);
            for (SysOrg org : exportOrgList) {
                Row row = sheet.createRow(counter++);
                row.createCell(0).setCellValue(org.getOrgId().toString());
                row.createCell(1).setCellValue(org.getOrgNumber());
                row.createCell(2).setCellValue(org.getOrgName());
                row.createCell(3).setCellValue(ConstantDictionary.getDataValue(org.getStatus()));
                if(org.getParent() != null) {
                    row.createCell(4).setCellValue(org.getParent().getOrgNumber());
                    row.createCell(5).setCellValue(org.getParent().getOrgName());
                } else {
                    row.createCell(4).setCellValue("无");
                    row.createCell(5).setCellValue("无");

                }
                row.createCell(6).setCellValue(org.getLeader());
                row.createCell(7).setCellValue(org.getMobile());
                row.createCell(8).setCellValue(org.getNote());

            }

            workbook.write(out);
            workbook.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


        return new ByteArrayInputStream(out.toByteArray());

    }
}
