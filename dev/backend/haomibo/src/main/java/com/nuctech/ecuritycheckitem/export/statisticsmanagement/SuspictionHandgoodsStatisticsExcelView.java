/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（SuspictionHandgoodsStatisticsExcelView）
 * 文件名：	SuspictionHandgoodsStatisticsExcelView.java
 * 描述：	SuspictionHandgoodsStatisticsExcelView
 * 作者名：	Tiny
 * 日期：	2019/11/30
 *
 */

package com.nuctech.ecuritycheckitem.export.statisticsmanagement;

import com.nuctech.ecuritycheckitem.config.ConstantDictionary;
import com.nuctech.ecuritycheckitem.controllers.taskmanagement.statisticsmanagement.SuspicionHandgoodsStatisticsController;
import com.nuctech.ecuritycheckitem.export.BaseExcelView;
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
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SuspictionHandgoodsStatisticsExcelView extends BaseExcelView {

    /**
     * set table header row
     * @param sheet
     */
    private static void setHeader(Sheet sheet) {
        Row header = sheet.createRow(3);

        Cell headerCellNo = header.createCell(0);
        headerCellNo.setCellValue(messageSource.getMessage("ID", null, currentLocale));

       Cell headerCellTime = header.createCell(1);
        headerCellTime.setCellValue(messageSource.getMessage("StatWidth", null, currentLocale));

        for (int i = 0; i < SuspicionHandgoodsStatisticsController.handGoodsIDList.size(); i ++) {
            Cell headerCellItem = header.createCell(i + 2);
            headerCellItem.setCellValue(ConstantDictionary.getDataValue(SuspicionHandgoodsStatisticsController.handGoodsIDList.get(i)));
        }
    }

    /**
     * build inputstream of data to be exported
     * @param detailedStatistics
     * @return
     */
    public static InputStream buildExcelDocument(List<TreeMap<String, String>> detailedStatistics) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {

            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("suspictionHandgoodsStatistics");

            Row title = sheet.createRow(0);
            Cell titleCell = title.createCell(0);
            titleCell.setCellValue(messageSource.getMessage("SuspictionHandgoodsStatisticsTableTitle", null, currentLocale));
            titleCell.setCellStyle(getHeaderStyle(workbook));

            Row time = sheet.createRow(1);
            Cell timeCell = time.createCell(0);
            timeCell.setCellValue(getCurrentTime());

            setHeader(sheet);

            int counter = 4;
            CellStyle style = workbook.createCellStyle();
            style.setWrapText(true);

            long index = 1;

            for (TreeMap<String, String> record : detailedStatistics) {

                Row row = sheet.createRow(counter ++);
                DecimalFormat df = new DecimalFormat("0.00");
                row.createCell(0).setCellValue(index ++);
                row.createCell(1).setCellValue(record.get("time"));

                for (int i = 0; i < SuspicionHandgoodsStatisticsController.handGoodsIDList.size(); i ++) {
                    row.createCell(i + 2).setCellValue(record.get(ConstantDictionary.getDataValue(SuspicionHandgoodsStatisticsController.handGoodsIDList.get(i))));
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
