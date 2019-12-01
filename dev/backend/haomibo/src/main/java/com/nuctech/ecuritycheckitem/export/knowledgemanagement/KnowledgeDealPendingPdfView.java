/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/28
 * @CreatedBy Choe.
 * @FileName KnowledgeDealPendingPdfView.java
 * @ModifyHistory
 */
package com.nuctech.ecuritycheckitem.export.knowledgemanagement;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.nuctech.ecuritycheckitem.models.db.SerKnowledgeCaseDeal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Stream;

public class KnowledgeDealPendingPdfView {
    public static InputStream buildExcelDocument(List<SerKnowledgeCaseDeal> exportDealList) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();


        try {
            PdfWriter.getInstance(document, out);

            document.open();

            PdfPTable table = new PdfPTable(10);
            Stream.of("序号", "任务编号", "图像", "工作模式", "任务结论", "现场", "安检仪", "判图站", "手检站", "查获物品")
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell();
                        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        header.setBorderWidth(2);
                        header.setPhrase(new Phrase(columnTitle));
                        table.addCell(header);
                    });


            for (SerKnowledgeCaseDeal deal : exportDealList) {
                table.addCell(deal.getCaseDealId().toString());
                if(deal.getTask() != null) {
                    table.addCell(deal.getTask().getTaskNumber());
                } else {
                    table.addCell("");
                }

                if(deal.getScanImage() != null) {
                    table.addCell(deal.getScanImage().getImageUrl());
                } else {
                    table.addCell("");
                }

                if(deal.getWorkMode() != null) {
                    table.addCell(deal.getWorkMode().getModeName());
                } else {
                    table.addCell("");
                }

                table.addCell(deal.getHandResult());
                if(deal.getScanDevice() != null && deal.getScanDevice().getField() != null) {
                    table.addCell(deal.getScanDevice().getField().getFieldDesignation());
                } else {
                    table.addCell("");
                }

                if(deal.getScanDevice() != null) {
                    table.addCell(deal.getScanDevice().getDeviceName());
                } else {
                    table.addCell("");
                }

                if(deal.getJudgeDevice() != null) {
                    table.addCell(deal.getJudgeDevice().getDeviceName());
                } else {
                    table.addCell("");
                }

                if(deal.getHandDevice() != null) {
                    table.addCell(deal.getHandDevice().getDeviceName());
                } else {
                    table.addCell("");
                }
                table.addCell(deal.getHandResult());
            }

            document.add(table);

            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
