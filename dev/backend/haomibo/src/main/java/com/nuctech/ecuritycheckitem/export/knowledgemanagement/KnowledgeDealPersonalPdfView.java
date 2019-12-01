/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/29
 * @CreatedBy Choe.
 * @FileName KnowledgeDealPersonalPdfView.java
 * @ModifyHistory
 */
package com.nuctech.ecuritycheckitem.export.knowledgemanagement;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.nuctech.ecuritycheckitem.models.db.SerKnowledgeCaseDeal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Stream;

public class KnowledgeDealPersonalPdfView {
    public static InputStream buildExcelDocument(List<SerKnowledgeCaseDeal> exportDealList) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();


        try {
            PdfWriter.getInstance(document, out);

            document.open();

            PdfPTable table = new PdfPTable(7);
            Stream.of("序号", "任务编号", "图像", "任务结论", "现场", "通道", "查获物品")
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


                table.addCell(deal.getHandResult());
                if(deal.getScanDevice() != null && deal.getScanDevice().getField() != null) {
                    table.addCell(deal.getScanDevice().getField().getFieldDesignation());
                } else {
                    table.addCell("");
                }

                if(deal.getScanDevice() != null) {
                    table.addCell(deal.getScanDevice().getDevicePassageWay());
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
