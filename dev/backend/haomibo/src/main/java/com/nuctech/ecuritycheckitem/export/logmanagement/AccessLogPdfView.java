/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（AccessLogPdfView）
 * 文件名：	AccessLogPdfView.java
 * 描述：	AccessLogPdfView
 * 作者名：	Choe
 * 日期：	2019/11/29
 *
 */

package com.nuctech.ecuritycheckitem.export.logmanagement;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.export.BasePdfView;
import com.nuctech.ecuritycheckitem.models.db.SysAccessLog;
import com.nuctech.ecuritycheckitem.models.es.EsSysAccessLog;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Stream;

public class AccessLogPdfView extends BasePdfView {

    /**
     * build inputstream of data to be printed
     * @param exportLogList
     * @return
     */
    public static InputStream buildPDFDocument(List<SysAccessLog> exportLogList) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();


        try {
            PdfWriter.getInstance(document, out);

            document.open();
            document.add(getTitle(messageSource.getMessage("AccessLog.Title", null, currentLocale)));
            document.add(getTime());

            PdfPTable table = new PdfPTable(8);
            table.setWidthPercentage(99);
            Stream.of("AccessLog.No", "AccessLog.OperateAccount", "AccessLog.OperateUser", "AccessLog.ClientIp", "AccessLog.Action",
                    "AccessLog.OperateResult", "AccessLog.ReasonCode", "AccessLog.OperateTime")
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell();

                        header.setBorderWidth(2);
                        header.setPhrase(new Phrase(messageSource.getMessage(columnTitle, null, currentLocale), getFontWithSize(Constants.PDF_HEAD_FONT_SIZE)));
                        table.addCell(header);
                    });


            for (SysAccessLog log : exportLogList) {
                addTableCell(table, log.getId().toString());
                addTableCell(table, log.getOperateAccount());
                addTableCell(table, log.getUser().getUserName());
                addTableCell(table, log.getClientIp());
                addTableCell(table, log.getAction());
                addTableCell(table, log.getOperateResult());
                addTableCell(table, log.getReasonCode());
                addTableCell(table, formatDate(log.getOperateTime()));
            }

            document.add(table);

            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
