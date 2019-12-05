/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/29
 * @CreatedBy Choe.
 * @FileName KnowledgeDealPersonalPdfView.java
 * @ModifyHistory
 */
package com.nuctech.ecuritycheckitem.export.knowledgemanagement;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.export.BasePdfView;
import com.nuctech.ecuritycheckitem.models.db.SerKnowledgeCaseDeal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

public class KnowledgeDealPersonalPdfView extends BasePdfView {
    public static InputStream buildPDFDocument(List<SerKnowledgeCaseDeal> exportDealList) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();


        try {
            PdfWriter.getInstance(document, out);

            document.open();

            document.add(getTitle("人员案例"));
            document.add(getTime());

            PdfPTable table = new PdfPTable(7);

            table.setWidthPercentage(100);
            Stream.of("序号", "任务编号", "图像", "任务结论", "现场", "通道", "查获物品")
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell();

                        header.setBorderWidth(2);
                        header.setPhrase(new Phrase(columnTitle, getFontWithSize(Constants.PDF_HEAD_FONT_SIZE)));
                        table.addCell(header);
                    });


            for (SerKnowledgeCaseDeal deal : exportDealList) {
                addTableCell(table, deal.getCaseDealId().toString());
                if(deal.getTask() != null) {
                    addTableCell(table, deal.getTask().getTaskNumber());
                } else {
                    addTableCell(table, "无");
                }

                if(deal.getScanImage() != null) {
                    addTableCell(table, deal.getScanImage().getImageUrl());
                } else {
                    addTableCell(table, "无");
                }


                addTableCell(table, deal.getHandResult());
                if(deal.getScanDevice() != null && deal.getScanDevice().getField() != null) {
                    addTableCell(table, deal.getScanDevice().getField().getFieldDesignation());
                } else {
                    addTableCell(table, "无");
                }

                if(deal.getScanDevice() != null) {
                    addTableCell(table, deal.getScanDevice().getDevicePassageWay());
                } else {
                    addTableCell(table, "无");
                }

                addTableCell(table, deal.getHandResult());
            }

            document.add(table);

            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
