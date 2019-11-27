/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/26
 * @CreatedBy Choe.
 * @FileName KnowledgeDealPendingExcelView.java
 * @ModifyHistory
 */
package com.nuctech.ecuritycheckitem.excel.knowledgemanagement;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.nuctech.ecuritycheckitem.models.db.SerKnowledgeCaseDeal;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.servlet.view.document.AbstractXlsxView;

public class KnowledgeDealPendingExcelView  {

    public Workbook buildExcelDocument(List<SerKnowledgeCaseDeal> exportDealList) throws Exception {

        Workbook workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet("Employee Report");
        Row header = sheet.createRow(0);

        header.createCell(0).setCellValue("序号");
        header.createCell(1).setCellValue("任务编号");
        header.createCell(2).setCellValue("图像");
        header.createCell(3).setCellValue("工作模式");
        header.createCell(4).setCellValue("任务结论");
        header.createCell(5).setCellValue("现场");
        header.createCell(6).setCellValue("安检仪");
        header.createCell(7).setCellValue("判图站");
        header.createCell(8).setCellValue("手检站");
        header.createCell(9).setCellValue("查获物品");
        int counter = 1;
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
        return workbook;
    }
}
