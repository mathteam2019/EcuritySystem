/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/26
 * @CreatedBy Choe.
 * @FileName KnowledgeDealPersonalExcelView.java
 * @ModifyHistory
 */
package com.nuctech.ecuritycheckitem.export.knowledgemanagement;

import com.nuctech.ecuritycheckitem.config.ConstantDictionary;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.export.BaseExcelView;
import com.nuctech.ecuritycheckitem.models.db.SerKnowledgeCaseDeal;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class KnowledgeDealPersonalExcelView extends BaseExcelView {

    private static void setHeader(Sheet sheet) {
        Row header = sheet.createRow(3);

        Cell headerCellNo = header.createCell(0);
        headerCellNo.setCellValue("序号");

        Cell headerCellNumber = header.createCell(1);
        headerCellNumber.setCellValue("任务编号");


        Cell headerCellResult = header.createCell(2);
        headerCellResult.setCellValue("任务结论");

        Cell headerCellField = header.createCell(3);
        headerCellField.setCellValue("现场");

        Cell headerCellDevicePassageWay = header.createCell(4);
        headerCellDevicePassageWay.setCellValue("通道");

        Cell headerCellGoods = header.createCell(5);
        headerCellGoods.setCellValue("查获物品");
    }

    public static InputStream buildExcelDocument(List<SerKnowledgeCaseDeal> exportDealList) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {

            Workbook workbook = new XSSFWorkbook();

            Sheet sheet = workbook.createSheet("Knowledge-Personal");

            Row title = sheet.createRow(0);
            Cell titleCell = title.createCell(0);
            titleCell.setCellValue("人员案例");
            titleCell.setCellStyle(getHeaderStyle(workbook));

            Row time = sheet.createRow(1);
            Cell timeCell = time.createCell(0);
            timeCell.setCellValue(getCurrentTime());

            setHeader(sheet);
            int counter = 4;

            CellStyle style = workbook.createCellStyle();
            style.setWrapText(true);
            for (SerKnowledgeCaseDeal deal : exportDealList) {
                Row row = sheet.createRow(counter++);
                row.createCell(0).setCellValue(deal.getCaseDealId());
                if(deal.getTask() != null) {
                    row.createCell(1).setCellValue(deal.getTask().getTaskNumber());
                } else {
                    row.createCell(1).setCellValue("无");
                }




                row.createCell(2).setCellValue(ConstantDictionary.getDataValue(deal.getHandTaskResult()));
                if(deal.getScanDevice() != null && deal.getScanDevice().getField() != null) {
                    row.createCell(3).setCellValue(deal.getScanDevice().getField().getFieldDesignation());
                } else {
                    row.createCell(3).setCellValue("无");
                }

                if(deal.getScanDevice() != null) {
                    row.createCell(4).setCellValue(deal.getScanDevice().getDevicePassageWay());
                } else {
                    row.createCell(4).setCellValue("无");
                }

                row.createCell(5).setCellValue(deal.getHandResult());
            }

            workbook.write(out);
            workbook.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


        return new ByteArrayInputStream(out.toByteArray());

    }
}
