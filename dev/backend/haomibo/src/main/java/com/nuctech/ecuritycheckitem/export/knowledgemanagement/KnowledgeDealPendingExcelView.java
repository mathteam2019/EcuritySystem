/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/26
 * @CreatedBy Choe.
 * @FileName KnowledgeDealPendingExcelView.java
 * @ModifyHistory
 */
package com.nuctech.ecuritycheckitem.export.knowledgemanagement;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.models.db.SerKnowledgeCaseDeal;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class KnowledgeDealPendingExcelView  {

    public static InputStream buildExcelDocument(List<SerKnowledgeCaseDeal> exportDealList) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {

            Workbook workbook = new XSSFWorkbook();

            Sheet sheet = workbook.createSheet("Knowledge-Pending");



            CellStyle headerStyle = workbook.createCellStyle();

            XSSFFont font = ((XSSFWorkbook) workbook).createFont();
            font.setFontName(Constants.EXCEL_HEAD_FONT_NAME);
            font.setFontHeightInPoints(Constants.EXCEL_HEAD_FONT_SIZE);
            font.setBold(true);
            headerStyle.setFont(font);

            Row title = sheet.createRow(0);
            Cell titleCell = title.createCell(0);
            titleCell.setCellValue("待审批案例");
            titleCell.setCellStyle(headerStyle);

            Row time = sheet.createRow(1);
            Cell timeCell = time.createCell(0);
            Date curTime = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.PDF_DATETIME_FORMAT);
            timeCell.setCellValue(dateFormat.format(curTime));

            Row header = sheet.createRow(3);
            Cell headerCellNo = header.createCell(0);
            headerCellNo.setCellValue("序号");

            Cell headerCellNumber = header.createCell(1);
            headerCellNumber.setCellValue("任务编号");

            Cell headerCellImage = header.createCell(2);
            headerCellImage.setCellValue("图像");

            Cell headerCellMode = header.createCell(3);
            headerCellMode.setCellValue("工作模式");

            Cell headerCellResult = header.createCell(4);
            headerCellResult.setCellValue("任务结论");

            Cell headerCellField = header.createCell(5);
            headerCellField.setCellValue("现场");

            Cell headerCellScanDevice = header.createCell(6);
            headerCellScanDevice.setCellValue("安检仪");

            Cell headerCellJudgeDevice = header.createCell(7);
            headerCellJudgeDevice.setCellValue("判图站");

            Cell headerCellHandDevice = header.createCell(8);
            headerCellHandDevice.setCellValue("手检站");

            Cell headerCellGoods = header.createCell(9);
            headerCellGoods.setCellValue("查获物品");



            int counter = 4;

            CellStyle style = workbook.createCellStyle();
            style.setWrapText(true);
            for (SerKnowledgeCaseDeal deal : exportDealList) {
                Row row = sheet.createRow(counter++);
                row.createCell(0).setCellValue(deal.getCaseDealId());
                if(deal.getTask() != null) {
                    row.createCell(1).setCellValue(deal.getTask().getTaskNumber());
                } else {
                    row.createCell(1).setCellValue("");
                }

                if(deal.getScanImage() != null) {
                    row.createCell(2).setCellValue(deal.getScanImage().getImageUrl());
                } else {
                    row.createCell(2).setCellValue("");
                }

                if(deal.getWorkMode() != null) {
                    row.createCell(3).setCellValue(deal.getWorkMode().getModeName());
                } else {
                    row.createCell(3).setCellValue("");
                }

                row.createCell(4).setCellValue(deal.getHandResult());
                if(deal.getScanDevice() != null && deal.getScanDevice().getField() != null) {
                    row.createCell(5).setCellValue(deal.getScanDevice().getField().getFieldDesignation());
                } else {
                    row.createCell(5).setCellValue("");
                }

                if(deal.getScanDevice() != null) {
                    row.createCell(6).setCellValue(deal.getScanDevice().getDeviceName());
                } else {
                    row.createCell(6).setCellValue("");
                }

                if(deal.getJudgeDevice() != null) {
                    row.createCell(7).setCellValue(deal.getJudgeDevice().getDeviceName());
                } else {
                    row.createCell(7).setCellValue("");
                }

                if(deal.getHandDevice() != null) {
                    row.createCell(8).setCellValue(deal.getHandDevice().getDeviceName());
                } else {
                    row.createCell(8).setCellValue("");
                }
                row.createCell(9).setCellValue(deal.getHandResult());
            }

            workbook.write(out);
            workbook.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


        return new ByteArrayInputStream(out.toByteArray());

    }
}