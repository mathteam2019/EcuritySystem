/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（OrganizationExcelView）
 * 文件名：	OrganizationExcelView.java
 * 描述：	OrganizationExcelView
 * 作者名：	Choe
 * 日期：	2019/11/30
 *
 */

package com.nuctech.ecuritycheckitem.export.permissionmanagement;

import com.nuctech.ecuritycheckitem.config.ConstantDictionary;
import com.nuctech.ecuritycheckitem.export.BaseExcelView;
import com.nuctech.ecuritycheckitem.models.db.SysOrg;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class OrganizationExcelView extends BaseExcelView {

    /**
     * build inputstream of data to be exported
     * @param sheet
     */
    private static void setHeader(Sheet sheet) {
        Row header = sheet.createRow(3);

        Cell headerCellNo = header.createCell(0);
        headerCellNo.setCellValue(messageSource.getMessage("Organization.No", null, currentLocale));

        Cell headerCellNumber = header.createCell(1);
        headerCellNumber.setCellValue(messageSource.getMessage("Organization.Number", null, currentLocale));

        Cell headerCellName = header.createCell(2);
        headerCellName.setCellValue(messageSource.getMessage("Organization.Name", null, currentLocale));

        Cell headerCellStatus = header.createCell(3);
        headerCellStatus.setCellValue(messageSource.getMessage("Organization.Status", null, currentLocale));

        Cell headerCellParentNumber = header.createCell(4);
        headerCellParentNumber.setCellValue(messageSource.getMessage("Organization.ParentNumber", null, currentLocale));

        Cell headerCellParentName = header.createCell(5);
        headerCellParentName.setCellValue(messageSource.getMessage("Organization.ParentName", null, currentLocale));

        Cell headerCellLeader = header.createCell(6);
        headerCellLeader.setCellValue(messageSource.getMessage("Organization.Leader", null, currentLocale));

        Cell headerCellMobile = header.createCell(7);
        headerCellMobile.setCellValue(messageSource.getMessage("Organization.Mobile", null, currentLocale));

        Cell headerCellNote = header.createCell(8);
        headerCellNote.setCellValue(messageSource.getMessage("Organization.Note", null, currentLocale));
    }

    /**
     * build inputstream of data to be exported
     * @param exportOrgList
     * @return
     */
    public static InputStream buildExcelDocument(List<SysOrg> exportOrgList) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {

            Workbook workbook = new XSSFWorkbook();

            Sheet sheet = workbook.createSheet("Organization");

            Row title = sheet.createRow(0);
            Cell titleCell = title.createCell(0);
            titleCell.setCellValue(messageSource.getMessage("Organization.Title", null, currentLocale));
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
