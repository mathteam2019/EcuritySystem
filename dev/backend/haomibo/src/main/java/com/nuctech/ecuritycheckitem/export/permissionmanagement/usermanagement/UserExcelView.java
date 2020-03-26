/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（UserExcelView）
 * 文件名：	UserExcelView.java
 * 描述：	UserExcelView
 * 作者名：	Choe
 * 日期：	2019/11/30
 *
 */

package com.nuctech.ecuritycheckitem.export.permissionmanagement.usermanagement;

import com.nuctech.ecuritycheckitem.config.ConstantDictionary;
import com.nuctech.ecuritycheckitem.export.BaseExcelView;
import com.nuctech.ecuritycheckitem.models.db.SysUser;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class UserExcelView extends BaseExcelView {

    /**
     * create table header row
     * @param sheet
     */
    private static void setHeader(Sheet sheet) {
        Row header = sheet.createRow(3);


        Cell headerCellNo = header.createCell(0);
        headerCellNo.setCellValue(messageSource.getMessage("User.No", null, currentLocale));

        Cell headerCellNumber = header.createCell(1);
        headerCellNumber.setCellValue(messageSource.getMessage("User.Number", null, currentLocale));

        Cell headerCellName = header.createCell(2);
        headerCellName.setCellValue(messageSource.getMessage("User.Name", null, currentLocale));

        Cell headerCellGender = header.createCell(3);
        headerCellGender.setCellValue(messageSource.getMessage("User.Gender", null, currentLocale));

        Cell headerCellStatus = header.createCell(4);
        headerCellStatus.setCellValue(messageSource.getMessage("User.Status", null, currentLocale));

        Cell headerCellCategory = header.createCell(5);
        headerCellCategory.setCellValue(messageSource.getMessage("User.Category", null, currentLocale));

        Cell headerCellAccount = header.createCell(6);
        headerCellAccount.setCellValue(messageSource.getMessage("User.Account", null, currentLocale));
    }

    /**
     * build inputstream of data to be exported
     * @param exportUserList
     * @return
     */
    public static InputStream buildExcelDocument(List<SysUser> exportUserList) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {

            Workbook workbook = new XSSFWorkbook();

            Sheet sheet = workbook.createSheet("User");

            Row title = sheet.createRow(0);
            Cell titleCell = title.createCell(0);
            titleCell.setCellValue(messageSource.getMessage("User.Title", null, currentLocale));
            titleCell.setCellStyle(getHeaderStyle(workbook));

            Row time = sheet.createRow(1);
            Cell timeCell = time.createCell(0);
            timeCell.setCellValue(getCurrentTime());

            setHeader(sheet);
            int counter = 4;

            int number = 0;

            CellStyle style = workbook.createCellStyle();
            style.setWrapText(true);
            for (SysUser user : exportUserList) {
                Row row = sheet.createRow(counter++);
                row.createCell(0).setCellValue(String.valueOf(++ number));
                row.createCell(1).setCellValue(user.getUserNumber());
                row.createCell(2).setCellValue(user.getUserName());
                row.createCell(3).setCellValue(ConstantDictionary.getDataValue(user.getGender()));
                row.createCell(4).setCellValue(ConstantDictionary.getDataValue(user.getStatus()));
                row.createCell(5).setCellValue(user.getOrg().getOrgName());
                row.createCell(6).setCellValue(user.getUserAccount());

            }

            workbook.write(out);
            workbook.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


        return new ByteArrayInputStream(out.toByteArray());

    }
}
