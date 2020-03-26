/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（KnowledgeDealPendingExcelView）
 * 文件名：	KnowledgeDealPendingExcelView.java
 * 描述：	KnowledgeDealPendingExcelView
 * 作者名：	Choe
 * 日期：	2019/11/29
 *
 */

package com.nuctech.ecuritycheckitem.export.knowledgemanagement;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.nuctech.ecuritycheckitem.config.ConstantDictionary;
import com.nuctech.ecuritycheckitem.export.BaseExcelView;
import com.nuctech.ecuritycheckitem.models.db.SerKnowledgeCaseDeal;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class KnowledgeDealPendingExcelView  extends BaseExcelView {

    /**
     * create table header row
     * @param sheet
     */
    private static void setHeader(Sheet sheet) {
        Row header = sheet.createRow(3);

        Cell headerCellNo = header.createCell(0);
        headerCellNo.setCellValue(messageSource.getMessage("KnowledgeDealPending.No", null, currentLocale));

        Cell headerCellNumber = header.createCell(1);
        headerCellNumber.setCellValue(messageSource.getMessage("KnowledgeDealPending.Number", null, currentLocale));

        Cell headerCellMode = header.createCell(2);
        headerCellMode.setCellValue(messageSource.getMessage("KnowledgeDealPending.Mode", null, currentLocale));

        Cell headerCellResult = header.createCell(3);
        headerCellResult.setCellValue(messageSource.getMessage("KnowledgeDealPending.Result", null, currentLocale));

        Cell headerCellField = header.createCell(4);
        headerCellField.setCellValue(messageSource.getMessage("KnowledgeDealPending.Field", null, currentLocale));

        Cell headerCellScanDevice = header.createCell(5);
        headerCellScanDevice.setCellValue(messageSource.getMessage("KnowledgeDealPending.ScanDevice", null, currentLocale));

        Cell headerCellJudgeDevice = header.createCell(6);
        headerCellJudgeDevice.setCellValue(messageSource.getMessage("KnowledgeDealPending.JudgeDevice", null, currentLocale));

        Cell headerCellHandDevice = header.createCell(7);
        headerCellHandDevice.setCellValue(messageSource.getMessage("KnowledgeDealPending.HandDevice", null, currentLocale));

        Cell headerCellGoods = header.createCell(8);
        headerCellGoods.setCellValue(messageSource.getMessage("KnowledgeDealPending.Goods", null, currentLocale));
    }

    /**
     * build inputstream of data to be exported
     * @param exportDealList
     * @return
     */
    public static InputStream buildExcelDocument(List<SerKnowledgeCaseDeal> exportDealList) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {

            Workbook workbook = new XSSFWorkbook();

            Sheet sheet = workbook.createSheet("Knowledge-Pending");


            Row title = sheet.createRow(0);
            Cell titleCell = title.createCell(0);
            titleCell.setCellValue(messageSource.getMessage("KnowledgeDealPending.Title", null, currentLocale));
            titleCell.setCellStyle(getHeaderStyle(workbook));

            Row time = sheet.createRow(1);
            Cell timeCell = time.createCell(0);
            timeCell.setCellValue(getCurrentTime());

            setHeader(sheet);
            int counter = 4;

            CellStyle style = workbook.createCellStyle();
            int id = 0;
            style.setWrapText(true);
            for (SerKnowledgeCaseDeal deal : exportDealList) {
                Row row = sheet.createRow(counter++);
                row.createCell(0).setCellValue(String.valueOf(++ id));
                if(deal.getTask() != null) {
                    row.createCell(1).setCellValue(deal.getTask().getTaskNumber());
                } else {
                    row.createCell(1).setCellValue(messageSource.getMessage("None", null, currentLocale));
                }

                if(deal.getWorkMode() != null) {
                    row.createCell(2).setCellValue(ConstantDictionary.getDataValue(deal.getWorkMode().getModeName()));
                } else {
                    row.createCell(2).setCellValue(messageSource.getMessage("None", null, currentLocale));
                }

                row.createCell(3).setCellValue(ConstantDictionary.getDataValue(deal.getHandTaskResult()));
                if(deal.getScanDevice() != null && deal.getScanDevice().getField() != null) {
                    row.createCell(4).setCellValue(deal.getScanDevice().getField().getFieldDesignation());
                } else {
                    row.createCell(4).setCellValue(messageSource.getMessage("None", null, currentLocale));
                }

                if(deal.getScanDevice() != null) {
                    row.createCell(5).setCellValue(deal.getScanDevice().getDeviceName());
                } else {
                    row.createCell(5).setCellValue(messageSource.getMessage("None", null, currentLocale));
                }

                if(deal.getJudgeDevice() != null) {
                    row.createCell(6).setCellValue(deal.getJudgeDevice().getDeviceName());
                } else {
                    row.createCell(6).setCellValue(messageSource.getMessage("None", null, currentLocale));
                }

                if(deal.getHandDevice() != null) {
                    row.createCell(7).setCellValue(deal.getHandDevice().getDeviceName());
                } else {
                    row.createCell(7).setCellValue(messageSource.getMessage("None", null, currentLocale));
                }
                String goods = deal.getHandGoods();
                String convertGoods = "";
                if(goods != null) {
                    String[] split = goods.split(",");

                    for(int i = 0; i < split.length; i ++) {
                        if(i > 0) {
                            convertGoods += ",";
                        }
                        convertGoods += ConstantDictionary.getDataValue(split[i]);
                    }
                }

                row.createCell(8).setCellValue(convertGoods);
            }

            workbook.write(out);
            workbook.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


        return new ByteArrayInputStream(out.toByteArray());

    }
}
