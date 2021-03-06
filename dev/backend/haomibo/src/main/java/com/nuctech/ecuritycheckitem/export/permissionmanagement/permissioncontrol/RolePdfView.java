/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（RolePdfView）
 * 文件名：	RolePdfView.java
 * 描述：	RolePdfView
 * 作者名：	Choe
 * 日期：	2019/11/30
 *
 */

package com.nuctech.ecuritycheckitem.export.permissionmanagement.permissioncontrol;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.export.BasePdfView;
import com.nuctech.ecuritycheckitem.models.db.SysResource;
import com.nuctech.ecuritycheckitem.models.db.SysRole;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class RolePdfView extends BasePdfView {

    /**
     * build inputstream of data to be printed
     * @param exportRoleList
     * @return
     */
    public static InputStream buildPDFDocument(List<SysRole> exportRoleList) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, out);
            document.open();


            document.add(getTitle(messageSource.getMessage("Role.Title", null, currentLocale)));
            document.add(getTime());

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(99);
            Stream.of("Role.No", "Role.Number", "Role.Name", "Role.Resource")
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell();

                        header.setBorderWidth(2);
                        header.setPhrase(new Phrase(messageSource.getMessage(columnTitle, null, currentLocale), getFontWithSize(Constants.PDF_HEAD_FONT_SIZE)));
                        table.addCell(header);
                    });


            int number = 0;

            for (SysRole role : exportRoleList) {
                addTableCell(table, String.valueOf(++ number));
                addTableCell(table, role.getRoleNumber());
                addTableCell(table, role.getRoleName());
                List<String> resourceNames = new ArrayList<>();
                for(SysResource resource: role.getResources()) {
                    resourceNames.add(resource.getResourceCaption());
                }
                String resourceName = StringUtils.join(resourceNames, ",");
                addTableCell(table, resourceName);
            }

            document.add(table);

            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
