/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（DataGroupExcelView）
 * 文件名：	DataGroupExcelView.java
 * 描述：	DataGroupExcelView
 * 作者名：	Choe
 * 日期：	2019/11/30
 *
 */

package com.nuctech.ecuritycheckitem.export.permissionmanagement.permissioncontrol;

import com.nuctech.ecuritycheckitem.export.BaseExcelView;
import com.nuctech.ecuritycheckitem.models.db.SysDataGroup;
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

public class DataGroupExcelView extends BaseExcelView {

    /**
     * create table header row
     * @param sheet
     */
    private static void setHeader(Sheet sheet) {
        Row header = sheet.createRow(3);


        Cell headerCellNo = header.createCell(0);
        headerCellNo.setCellValue("序号");

        Cell headerCellNumber = header.createCell(1);
        headerCellNumber.setCellValue("数据组编号");

        Cell headerCellName = header.createCell(2);
        headerCellName.setCellValue("数据组");

        Cell headerCellRange = header.createCell(3);
        headerCellRange.setCellValue("数据组范围");
    }

    /**
     * build inputstream of data to be exported
     * @param exportDataGroupList
     * @return
     */
    public static InputStream buildExcelDocument(List<SysDataGroup> exportDataGroupList) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {

            Workbook workbook = new XSSFWorkbook();

            Sheet sheet = workbook.createSheet("DataGroup");

            Row title = sheet.createRow(0);
            Cell titleCell = title.createCell(0);
            titleCell.setCellValue("数据组");
            titleCell.setCellStyle(getHeaderStyle(workbook));

            Row time = sheet.createRow(1);
            Cell timeCell = time.createCell(0);
            timeCell.setCellValue(getCurrentTime());

            setHeader(sheet);
            int counter = 4;

            CellStyle style = workbook.createCellStyle();
            style.setWrapText(true);
            for (SysDataGroup dataGroup : exportDataGroupList) {
                Row row = sheet.createRow(counter++);
                row.createCell(0).setCellValue(dataGroup.getDataGroupId().toString());
                row.createCell(1).setCellValue(dataGroup.getDataGroupNumber());
                row.createCell(2).setCellValue(dataGroup.getDataGroupName());
                /**
                 * Todo
                 * data group range value
                 *
                */
                row.createCell(3).setCellValue("无");
            }

            workbook.write(out);
            workbook.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


        return new ByteArrayInputStream(out.toByteArray());

    }
}
