/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（AssignUserPdfView）
 * 文件名：	AssignUserPdfView.java
 * 描述：	AssignUserPdfView
 * 作者名：	Choe
 * 日期：	2019/11/30
 *
 */

package com.nuctech.ecuritycheckitem.export.permissionmanagement.assignpermissionmanagement;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.nuctech.ecuritycheckitem.config.ConstantDictionary;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.export.BasePdfView;
import com.nuctech.ecuritycheckitem.models.db.SysAssignUser;
import com.nuctech.ecuritycheckitem.models.db.SysRole;
import com.nuctech.ecuritycheckitem.models.db.SysUser;
import com.nuctech.ecuritycheckitem.models.simplifieddb.SysRoleSimple;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class AssignUserPdfView extends BasePdfView {

    /**
     * build inputstream of data to be printed
     * @param exportUserList
     * @return
     */
    public static InputStream buildPDFDocument(List<SysAssignUser> exportUserList) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, out);
            document.open();

            document.add(getTitle(messageSource.getMessage("AssignUser.Title", null, currentLocale)));
            document.add(getTime());

            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(99);
            Stream.of("AssignUser.No", "AssignUser.Name", "AssignUser.Gender", "AssignUser.Account", "AssignUser.Group", "AssignUser.Role", "AssignUser.Category")
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell();

                        header.setBorderWidth(2);
                        header.setPhrase(new Phrase(messageSource.getMessage(columnTitle, null, currentLocale), getFontWithSize(Constants.PDF_HEAD_FONT_SIZE)));
                        table.addCell(header);
                    });



            int number = 0;
            for (SysAssignUser user : exportUserList) {
                addTableCell(table, String.valueOf(++ number));
                addTableCell(table, user.getUserName());
                addTableCell(table, ConstantDictionary.getDataValue(user.getGender()));
                addTableCell(table, user.getUserAccount());


                addTableCell(table, user.getOrg().getOrgName());

                List<SysRoleSimple> sysRoleList = new ArrayList<>();
                user.getRoles().forEach(sysRole -> {
                    sysRoleList.add(sysRole);
                });
                if(sysRoleList.size() > 0) {
                    String str = sysRoleList.get(0).getRoleName();
                    for(int i = 1; i < sysRoleList.size(); i ++) {
                        str = str + ", " + sysRoleList.get(i).getRoleName();
                    }
                    addTableCell(table, str);
                } else {
                    addTableCell(table, messageSource.getMessage("None", null, currentLocale));
                }

                if(SysAssignUser.DataRangeCategory.SPECIFIED.getValue().equals(user.getDataRangeCategory())) {
                    addTableCell(table, user.getDataGroups().get(0).getDataGroupName());
                } else {
                    addTableCell(table, ConstantDictionary.getDataValue(user.getDataRangeCategory()));
                }



            }

            document.add(table);

            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
