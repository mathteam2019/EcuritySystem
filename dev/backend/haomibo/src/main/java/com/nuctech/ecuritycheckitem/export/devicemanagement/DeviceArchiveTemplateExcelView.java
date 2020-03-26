/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（DeviceArchiveTemplateExcelView）
 * 文件名：	DeviceArchiveTemplateExcelView.java
 * 描述：	DeviceArchiveTemplate ExcelView
 * 作者名：	Choe
 * 日期：	2019/11/29
 *
 */
package com.nuctech.ecuritycheckitem.export.devicemanagement;

import com.nuctech.ecuritycheckitem.config.ConstantDictionary;
import com.nuctech.ecuritycheckitem.export.BaseExcelView;
import com.nuctech.ecuritycheckitem.models.db.SerArchiveTemplate;
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

public class DeviceArchiveTemplateExcelView extends BaseExcelView {

    /**
     * set header of excel sheet
     * @param sheet
     */
    private static void setHeader(Sheet sheet) {

        Row header = sheet.createRow(3);

        Cell headerCellNo = header.createCell(0);
        headerCellNo.setCellValue(messageSource.getMessage("DeviceArchiveTemplate.No", null, currentLocale));

        Cell headerCellNumber = header.createCell(1);
        headerCellNumber.setCellValue(messageSource.getMessage("DeviceArchiveTemplate.Number", null, currentLocale));

        Cell headerCellName = header.createCell(2);
        headerCellName.setCellValue(messageSource.getMessage("DeviceArchiveTemplate.Name", null, currentLocale));

        Cell headerCellStatus = header.createCell(3);
        headerCellStatus.setCellValue(messageSource.getMessage("DeviceArchiveTemplate.Status", null, currentLocale));

        Cell headerCellCategory = header.createCell(4);
        headerCellCategory.setCellValue(messageSource.getMessage("DeviceArchiveTemplate.Category", null, currentLocale));

        Cell headerCellManufacturer = header.createCell(5);
        headerCellManufacturer.setCellValue(messageSource.getMessage("DeviceArchiveTemplate.Manufacturer", null, currentLocale));

        Cell headerCellOriginalModel = header.createCell(6);
        headerCellOriginalModel.setCellValue(messageSource.getMessage("DeviceArchiveTemplate.OriginalModel", null, currentLocale));
    }

    /**
     * build inputstream of data to be exported
     * @param exportTemplateList
     * @return
     */
    public static InputStream buildExcelDocument(List<SerArchiveTemplate> exportTemplateList) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {

            Workbook workbook = new XSSFWorkbook();

            Sheet sheet = workbook.createSheet("DeviceArchiveTemplate");

            Row title = sheet.createRow(0);
            Cell titleCell = title.createCell(0);
            titleCell.setCellValue(messageSource.getMessage("DeviceArchiveTemplate.Title", null, currentLocale));
            titleCell.setCellStyle(getHeaderStyle(workbook));

            Row time = sheet.createRow(1);
            Cell timeCell = time.createCell(0);
            timeCell.setCellValue(getCurrentTime());

            setHeader(sheet);
            int counter = 4;

            CellStyle style = workbook.createCellStyle();
            style.setWrapText(true);
            int id = 0;
            for (SerArchiveTemplate template : exportTemplateList) {
                Row row = sheet.createRow(counter++);
                id ++;
                row.createCell(0).setCellValue(String.valueOf(id));
                row.createCell(1).setCellValue(template.getArchivesTemplateNumber());
                row.createCell(2).setCellValue(template.getTemplateName());
                row.createCell(3).setCellValue(ConstantDictionary.getDataValue(template.getStatus()));
                if(template.getDeviceCategory() != null) {
                    row.createCell(4).setCellValue(template.getDeviceCategory().getCategoryName());
                } else {
                    row.createCell(4).setCellValue(messageSource.getMessage("None", null, currentLocale));

                }
                row.createCell(5).setCellValue(ConstantDictionary.getDataValue(template.getManufacturer()));
                row.createCell(6).setCellValue(template.getOriginalModel());

            }

            workbook.write(out);
            workbook.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


        return new ByteArrayInputStream(out.toByteArray());

    }
}
