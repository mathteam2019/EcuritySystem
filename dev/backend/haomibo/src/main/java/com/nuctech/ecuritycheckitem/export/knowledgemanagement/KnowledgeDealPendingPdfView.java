/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（KnowledgeDealPendingPdfView）
 * 文件名：	KnowledgeDealPendingPdfView.java
 * 描述：	KnowledgeDealPendingPdfView
 * 作者名：	Choe
 * 日期：	2019/11/28
 *
 */
package com.nuctech.ecuritycheckitem.export.knowledgemanagement;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.nuctech.ecuritycheckitem.config.ConstantDictionary;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.export.BasePdfView;
import com.nuctech.ecuritycheckitem.models.db.SerKnowledgeCaseDeal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Stream;

public class KnowledgeDealPendingPdfView extends BasePdfView {

    /**
     * build inputstream of data to be printed
     * @param exportDealList
     * @return
     */
    public static InputStream buildPDFDocument(List<SerKnowledgeCaseDeal> exportDealList) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();


        try {
            PdfWriter.getInstance(document, out);

            document.open();

            document.add(getTitle("待审批案例"));
            document.add(getTime());

            PdfPTable table = new PdfPTable(9);
            table.setWidthPercentage(100);
            Stream.of("序号", "任务编号", "工作模式", "任务结论", "现场", "安检仪", "判图站", "手检站", "查获物品")
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

                if(deal.getWorkMode() != null) {
                    addTableCell(table, ConstantDictionary.getDataValue(deal.getWorkMode().getModeName()));
                } else {
                    addTableCell(table, "无");
                }

                addTableCell(table, ConstantDictionary.getDataValue(deal.getHandTaskResult()));
                if(deal.getScanDevice() != null && deal.getScanDevice().getField() != null) {
                    addTableCell(table, deal.getScanDevice().getField().getFieldDesignation());
                } else {
                    addTableCell(table, "无");
                }

                if(deal.getScanDevice() != null) {
                    addTableCell(table, deal.getScanDevice().getDeviceName());
                } else {
                    addTableCell(table, "无");
                }

                if(deal.getJudgeDevice() != null) {
                    addTableCell(table, deal.getJudgeDevice().getDeviceName());
                } else {
                    addTableCell(table, "无");
                }

                if(deal.getHandDevice() != null) {
                    addTableCell(table, deal.getHandDevice().getDeviceName());
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
