/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（DeviceExcelView）
 * 文件名：	DeviceExcelView.java
 * 描述：	DeviceExcelView
 * 作者名：	Choe
 * 日期：	2019/11/29
 *
 */

package com.nuctech.ecuritycheckitem.export.devicemanagement;

import com.nuctech.ecuritycheckitem.config.ConstantDictionary;
import com.nuctech.ecuritycheckitem.export.BaseExcelView;
import com.nuctech.ecuritycheckitem.models.db.SysDevice;
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

public class DeviceExcelView extends BaseExcelView {

    /**
     * create header of excel sheet
     * @param sheet
     */
    private static void setHeader(Sheet sheet) {
        Row header = sheet.createRow(3);

        Cell headerCellNo = header.createCell(0);
        headerCellNo.setCellValue(messageSource.getMessage("Device.No", null, currentLocale));

        Cell headerCellDevice = header.createCell(1);
        headerCellDevice.setCellValue(messageSource.getMessage("Device.Device", null, currentLocale));

        Cell headerCellName = header.createCell(2);
        headerCellName.setCellValue(messageSource.getMessage("Device.Name", null, currentLocale));

        Cell headerCellStatus = header.createCell(3);
        headerCellStatus.setCellValue(messageSource.getMessage("Device.Status", null, currentLocale));

        Cell headerCellArchive = header.createCell(4);
        headerCellArchive.setCellValue(messageSource.getMessage("Device.Archive.Name", null, currentLocale));



        Cell headerCellCategory = header.createCell(5);
        headerCellCategory.setCellValue(messageSource.getMessage("Device.Category", null, currentLocale));

        Cell headerCellManufacturer = header.createCell(6);
        headerCellManufacturer.setCellValue(messageSource.getMessage("Device.Manufacturer", null, currentLocale));

        Cell headerCellOriginalModel = header.createCell(7);
        headerCellOriginalModel.setCellValue(messageSource.getMessage("Device.OriginalModel", null, currentLocale));

        Cell headerCellGuid = header.createCell(8);
        headerCellGuid.setCellValue(messageSource.getMessage("Device.GUID", null, currentLocale));
    }

    /**
     * build inputstream of data to be exported
     * @param exportDeviceList
     * @return
     */
    public static InputStream buildExcelDocument(List<SysDevice> exportDeviceList) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {

            Workbook workbook = new XSSFWorkbook();

            Sheet sheet = workbook.createSheet("DeviceArchive");

            Row title = sheet.createRow(0);
            Cell titleCell = title.createCell(0);
            titleCell.setCellValue(messageSource.getMessage("Device.Title", null, currentLocale));
            titleCell.setCellStyle(getHeaderStyle(workbook));

            Row time = sheet.createRow(1);
            Cell timeCell = time.createCell(0);
            timeCell.setCellValue(getCurrentTime());

            setHeader(sheet);
            int counter = 4;

            CellStyle style = workbook.createCellStyle();
            style.setWrapText(true);
            int id = 0;
            for (SysDevice device : exportDeviceList) {
                id ++;
                Row row = sheet.createRow(counter++);
                row.createCell(0).setCellValue(String.valueOf(id));
                row.createCell(1).setCellValue(device.getDeviceSerial());
                row.createCell(2).setCellValue(device.getDeviceName());

                row.createCell(3).setCellValue(ConstantDictionary.getDataValue(device.getStatus()));
                if(device.getArchive() != null) {
                    row.createCell(4).setCellValue(device.getArchive().getArchivesName());
                } else {
                    row.createCell(4).setCellValue(messageSource.getMessage("None", null, currentLocale));
                }
                if(device.getCategory() != null) {
                    row.createCell(5).setCellValue(ConstantDictionary.getDataValue(device.getCategoryId().toString(), "DeviceCategory"));
                } else {
                    row.createCell(5).setCellValue(messageSource.getMessage("None", null, currentLocale));
                }
                if(device.getArchive() != null && device.getArchive().getArchiveTemplate() != null) {
                    row.createCell(6).setCellValue(ConstantDictionary.getDataValue(device.getArchive().getArchiveTemplate().getManufacturer()));
                    row.createCell(7).setCellValue(device.getArchive().getArchiveTemplate().getOriginalModel());
                } else {
                    row.createCell(6).setCellValue(messageSource.getMessage("None", null, currentLocale));
                    row.createCell(7).setCellValue(messageSource.getMessage("None", null, currentLocale));
                }
                row.createCell(8).setCellValue(device.getGuid());

            }

            workbook.write(out);
            workbook.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
