/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（DeviceCategoryExcelView）
 * 文件名：	DeviceCategoryExcelView.java
 * 描述：	DeviceCategoryExcelView
 * 作者名：	Choe
 * 日期：	2019/11/29
 *
 */

package com.nuctech.ecuritycheckitem.export.devicemanagement;

import com.nuctech.ecuritycheckitem.config.ConstantDictionary;
import com.nuctech.ecuritycheckitem.export.BaseExcelView;
import com.nuctech.ecuritycheckitem.models.db.SysDeviceCategory;
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

public class DeviceCategoryExcelView extends BaseExcelView {

    /**
     * set title
     * @param sheet
     */
    private static void setHeader(Sheet sheet) {
        Row header = sheet.createRow(3);


        Cell headerCellNo = header.createCell(0);
        headerCellNo.setCellValue(messageSource.getMessage("DeviceCategory.No", null, currentLocale));

        Cell headerCellNumber = header.createCell(1);
        headerCellNumber.setCellValue(messageSource.getMessage("DeviceCategory.Number", null, currentLocale));

        Cell headerCellName = header.createCell(2);
        headerCellName.setCellValue(messageSource.getMessage("DeviceCategory.Name", null, currentLocale));

        Cell headerCellStatus = header.createCell(3);
        headerCellStatus.setCellValue(messageSource.getMessage("DeviceCategory.Status", null, currentLocale));

        Cell headerCellParentNumber = header.createCell(4);
        headerCellParentNumber.setCellValue(messageSource.getMessage("DeviceCategory.ParentNumber", null, currentLocale));

        Cell headerCellParentName = header.createCell(5);
        headerCellParentName.setCellValue(messageSource.getMessage("DeviceCategory.ParentName", null, currentLocale));

        Cell headerCellNote = header.createCell(6);
        headerCellNote.setCellValue(messageSource.getMessage("DeviceCategory.Note", null, currentLocale));
    }

    /**
     * build inputstream of data to be exported
     * @param exportCategoryList
     * @return
     */
    public static InputStream buildExcelDocument(List<SysDeviceCategory> exportCategoryList) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            Workbook workbook = new XSSFWorkbook();

            Sheet sheet = workbook.createSheet("DeviceCategory");

            Row title = sheet.createRow(0);
            Cell titleCell = title.createCell(0);
            titleCell.setCellValue(messageSource.getMessage("DeviceCategory.Title", null, currentLocale));
            titleCell.setCellStyle(getHeaderStyle(workbook));

            Row time = sheet.createRow(1);
            Cell timeCell = time.createCell(0);
            timeCell.setCellValue(getCurrentTime());

            setHeader(sheet);
            int counter = 4;

            CellStyle style = workbook.createCellStyle();
            style.setWrapText(true);
            for (SysDeviceCategory category : exportCategoryList) {
                Row row = sheet.createRow(counter++);
                row.createCell(0).setCellValue(category.getCategoryId().toString());
                row.createCell(1).setCellValue(category.getCategoryNumber());
                row.createCell(2).setCellValue(category.getCategoryName());
                row.createCell(3).setCellValue(ConstantDictionary.getDataValue(category.getStatus()));
                if(category.getParent() != null) {
                    row.createCell(4).setCellValue(category.getParent().getCategoryNumber());
                    row.createCell(5).setCellValue(category.getParent().getCategoryName());
                } else {
                    row.createCell(4).setCellValue("无");
                    row.createCell(5).setCellValue("无");

                }
                row.createCell(6).setCellValue(category.getNote());

            }

            workbook.write(out);
            workbook.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
