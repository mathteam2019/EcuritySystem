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
import com.nuctech.ecuritycheckitem.models.db.SerKnowledgeCaseDeal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

public class KnowledgeDealPersonalPdfView {
    public static InputStream buildPDFDocument(List<SerKnowledgeCaseDeal> exportDealList) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();


        try {
            PdfWriter.getInstance(document, out);

            document.open();

            String fontName = "resources/fonts/NotoSansCJKsc-Regular.otf";

            Font font = FontFactory.getFont(fontName, Constants.PDF_TITLE_FONT_SIZE, Font.BOLD);
            Paragraph title = new Paragraph("人员案例", font);
            title.setSpacingAfter(Constants.PDF_TITLE_SPACING);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Date curTime = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.PDF_DATETIME_FORMAT);
            Paragraph time = new Paragraph(dateFormat.format(curTime));
            time.setSpacingAfter(Constants.PDF_TIME_SPACING);
            time.setAlignment(Element.ALIGN_RIGHT);
            document.add(time);

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
