/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（KnowledgeDealPendingExcelView）
 * 文件名：	KnowledgeDealPendingExcelView.java
 * 描述：	KnowledgeDealPendingExcelView
 * 作者名：	Choe
 * 日期：	2019/11/29
 *
 */

package com.nuctech.ecuritycheckitem.export.knowledgemanagement;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.nuctech.ecuritycheckitem.config.ConstantDictionary;
import com.nuctech.ecuritycheckitem.export.BaseExcelView;
import com.nuctech.ecuritycheckitem.models.db.SerKnowledgeCaseDeal;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class KnowledgeDealPendingExcelView  extends BaseExcelView {

    /**
     * create table header row
     * @param sheet
     */
    private static void setHeader(Sheet sheet) {
        Row header = sheet.createRow(3);

        Cell headerCellNo = header.createCell(0);
        headerCellNo.setCellValue("序号");

        Cell headerCellNumber = header.createCell(1);
        headerCellNumber.setCellValue("任务编号");

        Cell headerCellMode = header.createCell(2);
        headerCellMode.setCellValue("工作模式");

        Cell headerCellResult = header.createCell(3);
        headerCellResult.setCellValue("任务结论");

        Cell headerCellField = header.createCell(4);
        headerCellField.setCellValue("现场");

        Cell headerCellScanDevice = header.createCell(5);
        headerCellScanDevice.setCellValue("安检仪");

        Cell headerCellJudgeDevice = header.createCell(6);
        headerCellJudgeDevice.setCellValue("判图站");

        Cell headerCellHandDevice = header.createCell(7);
        headerCellHandDevice.setCellValue("手检站");

        Cell headerCellGoods = header.createCell(8);
        headerCellGoods.setCellValue("查获物品");
    }

    /**
     * build inputstream of data to be exported
     * @param exportDealList
     * @return
     */
    public static InputStream buildExcelDocument(List<SerKnowledgeCaseDeal> exportDealList) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {

            Workbook workbook = new XSSFWorkbook();

            Sheet sheet = workbook.createSheet("Knowledge-Pending");

            Row title = sheet.createRow(0);
            Cell titleCell = title.createCell(0);
            titleCell.setCellValue("待审批案例");
            titleCell.setCellStyle(getHeaderStyle(workbook));

            Row time = sheet.createRow(1);
            Cell timeCell = time.createCell(0);
            timeCell.setCellValue(getCurrentTime());

            setHeader(sheet);
            int counter = 4;

            CellStyle style = workbook.createCellStyle();
            style.setWrapText(true);
            for (SerKnowledgeCaseDeal deal : exportDealList) {
                Row row = sheet.createRow(counter++);
                row.createCell(0).setCellValue(deal.getCaseDealId());
                if(deal.getTask() != null) {
                    row.createCell(1).setCellValue(deal.getTask().getTaskNumber());
                } else {
                    row.createCell(1).setCellValue("无");
                }

                if(deal.getWorkMode() != null) {
                    row.createCell(2).setCellValue(ConstantDictionary.getDataValue(deal.getWorkMode().getModeName()));
                } else {
                    row.createCell(2).setCellValue("无");
                }

                row.createCell(3).setCellValue(ConstantDictionary.getDataValue(deal.getHandTaskResult()));
                if(deal.getScanDevice() != null && deal.getScanDevice().getField() != null) {
                    row.createCell(4).setCellValue(deal.getScanDevice().getField().getFieldDesignation());
                } else {
                    row.createCell(4).setCellValue("无");
                }

                if(deal.getScanDevice() != null) {
                    row.createCell(5).setCellValue(deal.getScanDevice().getDeviceName());
                } else {
                    row.createCell(5).setCellValue("无");
                }

                if(deal.getJudgeDevice() != null) {
                    row.createCell(6).setCellValue(deal.getJudgeDevice().getDeviceName());
                } else {
                    row.createCell(6).setCellValue("无");
                }

                if(deal.getHandDevice() != null) {
                    row.createCell(7).setCellValue(deal.getHandDevice().getDeviceName());
                } else {
                    row.createCell(7).setCellValue("无");
                }
                row.createCell(8).setCellValue(deal.getHandResult());
            }

            workbook.write(out);
            workbook.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


        return new ByteArrayInputStream(out.toByteArray());

    }
}
