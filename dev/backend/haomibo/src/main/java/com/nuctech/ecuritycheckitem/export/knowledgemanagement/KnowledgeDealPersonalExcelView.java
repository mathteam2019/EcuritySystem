/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/26
 * @CreatedBy Choe.
 * @FileName KnowledgeDealPersonalExcelView.java
 * @ModifyHistory
 */
package com.nuctech.ecuritycheckitem.export.knowledgemanagement;

import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.models.db.SerKnowledgeCaseDeal;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class KnowledgeDealPersonalExcelView {

    public static InputStream buildExcelDocument(List<SerKnowledgeCaseDeal> exportDealList) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {

            Workbook workbook = new XSSFWorkbook();

            Sheet sheet = workbook.createSheet("Knowledge-Personal");

            Row header = sheet.createRow(0);

            CellStyle headerStyle = workbook.createCellStyle();

            XSSFFont font = ((XSSFWorkbook) workbook).createFont();
            font.setFontName(Constants.EXCEL_HEAD_FONT_NAME);
            font.setFontHeightInPoints(Constants.EXCEL_HEAD_FONT_SIZE);
            font.setBold(true);
            headerStyle.setFont(font);

            Cell headerCellNo = header.createCell(0);
            headerCellNo.setCellValue("序号");
            headerCellNo.setCellStyle(headerStyle);

            Cell headerCellNumber = header.createCell(1);
            headerCellNumber.setCellValue("任务编号");
            headerCellNumber.setCellStyle(headerStyle);

            Cell headerCellImage = header.createCell(2);
            headerCellImage.setCellValue("图像");
            headerCellImage.setCellStyle(headerStyle);

            Cell headerCellResult = header.createCell(3);
            headerCellResult.setCellValue("任务结论");
            headerCellResult.setCellStyle(headerStyle);

            Cell headerCellField = header.createCell(4);
            headerCellField.setCellValue("现场");
            headerCellField.setCellStyle(headerStyle);

            Cell headerCellDevicePassageWay = header.createCell(5);
            headerCellDevicePassageWay.setCellValue("通道");
            headerCellDevicePassageWay.setCellStyle(headerStyle);

            Cell headerCellGoods = header.createCell(6);
            headerCellGoods.setCellValue("查获物品");
            headerCellGoods.setCellStyle(headerStyle);



            int counter = 1;

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



                row.createCell(3).setCellValue(deal.getHandResult());
                if(deal.getScanDevice() != null && deal.getScanDevice().getField() != null) {
                    row.createCell(4).setCellValue(deal.getScanDevice().getField().getFieldDesignation());
                } else {
                    row.createCell(4).setCellValue("");
                }

                if(deal.getScanDevice() != null) {
                    row.createCell(5).setCellValue(deal.getScanDevice().getDevicePassageWay());
                } else {
                    row.createCell(5).setCellValue("");
                }

                row.createCell(6).setCellValue(deal.getHandResult());
            }

            workbook.write(out);
            workbook.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


        return new ByteArrayInputStream(out.toByteArray());

    }
}
