/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（FieldManagementExcelView）
 * 文件名：	FieldManagementExcelView.java
 * 描述：	FieldManagementExcelView
 * 作者名：	Choe
 * 日期：	2019/11/29
 *
 */
package com.nuctech.ecuritycheckitem.export.fieldmanagement;

import com.nuctech.ecuritycheckitem.config.ConstantDictionary;
import com.nuctech.ecuritycheckitem.export.BaseExcelView;
import com.nuctech.ecuritycheckitem.models.db.SysField;
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

public class FieldManagementExcelView extends BaseExcelView {

    /**
     * Create header row
     * @param sheet
     */
    private static void setHeader(Sheet sheet) {
        Row header = sheet.createRow(3);


        Cell headerCellNo = header.createCell(0);
        headerCellNo.setCellValue(messageSource.getMessage("FieldManagement.No", null, currentLocale));

        Cell headerCellSerial = header.createCell(1);
        headerCellSerial.setCellValue(messageSource.getMessage("FieldManagement.Serial", null, currentLocale));

        Cell headerCellDesignation = header.createCell(2);
        headerCellDesignation.setCellValue(messageSource.getMessage("FieldManagement.Designation", null, currentLocale));

        Cell headerCellStatus = header.createCell(3);
        headerCellStatus.setCellValue(messageSource.getMessage("FieldManagement.Status", null, currentLocale));

        Cell headerCellParentSerial = header.createCell(4);
        headerCellParentSerial.setCellValue(messageSource.getMessage("FieldManagement.ParentSerial", null, currentLocale));

        Cell headerCellParentDesignation = header.createCell(5);
        headerCellParentDesignation.setCellValue(messageSource.getMessage("FieldManagement.ParentDesignation", null, currentLocale));

        Cell headerCellLeader = header.createCell(6);
        headerCellLeader.setCellValue(messageSource.getMessage("FieldManagement.Leader", null, currentLocale));

        Cell headerCellMobile = header.createCell(7);
        headerCellMobile.setCellValue(messageSource.getMessage("FieldManagement.Mobile", null, currentLocale));

        Cell headerCellNote = header.createCell(8);
        headerCellNote.setCellValue(messageSource.getMessage("FieldManagement.Note", null, currentLocale));
    }

    /**
     * build inputstream of data to be exported
     * @param exportFieldList
     * @return
     */
    public static InputStream buildExcelDocument(List<SysField> exportFieldList) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {

            Workbook workbook = new XSSFWorkbook();

            Sheet sheet = workbook.createSheet("Field");

            Row title = sheet.createRow(0);
            Cell titleCell = title.createCell(0);
            titleCell.setCellValue(messageSource.getMessage("FieldManagement.Title", null, currentLocale));
            titleCell.setCellStyle(getHeaderStyle(workbook));

            Row time = sheet.createRow(1);
            Cell timeCell = time.createCell(0);
            timeCell.setCellValue(getCurrentTime());

            setHeader(sheet);
            int counter = 4;

            CellStyle style = workbook.createCellStyle();
            style.setWrapText(true);
            for (SysField field : exportFieldList) {
                Row row = sheet.createRow(counter++);
                row.createCell(0).setCellValue(field.getFieldId().toString());
                row.createCell(1).setCellValue(field.getFieldSerial());
                row.createCell(2).setCellValue(field.getFieldDesignation());
                row.createCell(3).setCellValue(ConstantDictionary.getDataValue(field.getStatus()));
                if(field.getParent() != null) {
                    row.createCell(4).setCellValue(field.getParent().getFieldSerial());
                    row.createCell(5).setCellValue(field.getParent().getFieldDesignation());
                } else {
                    row.createCell(4).setCellValue("无");
                    row.createCell(5).setCellValue("无");

                }
                row.createCell(6).setCellValue(field.getLeader());
                row.createCell(7).setCellValue(field.getMobile());
                row.createCell(8).setCellValue(field.getNote());

            }

            workbook.write(out);
            workbook.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


        return new ByteArrayInputStream(out.toByteArray());

    }
}
