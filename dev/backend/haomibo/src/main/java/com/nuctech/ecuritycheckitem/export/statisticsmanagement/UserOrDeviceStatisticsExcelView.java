/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（UserOrDeviceStatisticsExcelView）
 * 文件名：	UserOrDeviceStatisticsExcelView.java
 * 描述：	UserOrDeviceStatisticsExcelView
 * 作者名：	Tiny
 * 日期：	2019/11/30
 *
 */

package com.nuctech.ecuritycheckitem.export.statisticsmanagement;

import com.nuctech.ecuritycheckitem.config.ConstantDictionary;
import com.nuctech.ecuritycheckitem.export.BaseExcelView;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.DetailTimeStatistics;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.TotalStatistics;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.TotalTimeStatistics;
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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class UserOrDeviceStatisticsExcelView extends BaseExcelView {

    private static List<String> nameList;
    private static List<String> deviceCategoryList;

    /**
     * create table header row
     * @param sheet
     * @param type
     */
    private static void setHeader(Sheet sheet, boolean type) {
        Row header = sheet.createRow(3);


        int colNum = 0;
        Cell headerCellNo = header.createCell(colNum ++);
        headerCellNo.setCellValue(messageSource.getMessage("ID", null, currentLocale));

        for(int i = 0; i < deviceCategoryList.size(); i ++) {
            Cell headerCellTime = header.createCell(colNum ++);
            headerCellTime.setCellValue(ConstantDictionary.getDataValue(deviceCategoryList.get(i)));
        }

        for(int i = 0; i < nameList.size(); i ++) {
            Cell headerCellTime = header.createCell(colNum ++);
            headerCellTime.setCellValue(nameList.get(i));
        }

    }

    /**
     * build inputstream of data to be exported
     * @param detailedStatistics
     * @param type : true -> by user, false -> by device
     * @return
     */
    public static InputStream buildExcelDocument(TreeMap<Long, TotalTimeStatistics> detailedStatistics, Boolean type) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        TotalTimeStatistics firstRecord = detailedStatistics.firstEntry().getValue();
        List<DetailTimeStatistics> firstDetail = firstRecord.getDetailedStatistics();
        nameList = new ArrayList<>();
        deviceCategoryList = new ArrayList<>();
        for(int i = 0; i < firstDetail.size(); i ++) {
            if(i < 3) {
                deviceCategoryList.add(firstDetail.get(i).getUserName());
            } else {
                nameList.add(firstDetail.get(i).getUserName());
            }
        }

        try {

            Workbook workbook = new XSSFWorkbook();

            Sheet sheet = workbook.createSheet("statistics");

            Row title = sheet.createRow(0);
            Cell titleCell = title.createCell(0);

            if (type) {
                titleCell.setCellValue(messageSource.getMessage("UserStatisticsTableTitle", null,currentLocale));
            } else {
                titleCell.setCellValue(messageSource.getMessage("DeviceStatisticsTableTitle", null, currentLocale));
            }
            titleCell.setCellStyle(getHeaderStyle(workbook));

            Row time = sheet.createRow(1);
            Cell timeCell = time.createCell(0);
            timeCell.setCellValue(getCurrentTime());

            setHeader(sheet, type);

            int counter = 4;
            CellStyle style = workbook.createCellStyle();
            style.setWrapText(true);

            long index = 1;
            List<String> totalNameList = deviceCategoryList;
            for(int i = 0; i < nameList.size(); i ++) {
                totalNameList.add(nameList.get(i));
            }
            for (Map.Entry<Long, TotalTimeStatistics> entry : detailedStatistics.entrySet()) {

                TotalTimeStatistics record = entry.getValue();

                Row row = sheet.createRow(counter++);

                DecimalFormat df = new DecimalFormat("0.00");
                int colNum = 0;
                row.createCell(colNum ++).setCellValue(index++);

                List<DetailTimeStatistics> detailTimeStatistics = record.getDetailedStatistics();
                for(int i = 0; i < totalNameList.size(); i ++) {
                    String name = totalNameList.get(i);
                    for(int j = 0; j < detailTimeStatistics.size(); j ++) {
                        if(detailTimeStatistics.get(j).getUserName().equals(name)) {
                            row.createCell(colNum ++).setCellValue(String.valueOf(detailTimeStatistics.get(j).getWorkingTime()));
                        }
                    }
                }
            }

            workbook.write(out);
            workbook.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


        return new ByteArrayInputStream(out.toByteArray());

    }
}
