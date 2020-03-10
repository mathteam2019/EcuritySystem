/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（KnowledgeDealPersonalExcelView）
 * 文件名：	KnowledgeDealPersonalExcelView.java
 * 描述：	KnowledgeDealPersonalExcelView
 * 作者名：	Choe
 * 日期：	2019/11/26
 *
 */

package com.nuctech.ecuritycheckitem.export.knowledgemanagement;

import com.nuctech.ecuritycheckitem.config.ConstantDictionary;
import com.nuctech.ecuritycheckitem.export.BaseExcelView;
import com.nuctech.ecuritycheckitem.models.db.SerKnowledgeCaseDeal;
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

public class KnowledgeDealPersonalExcelView extends BaseExcelView {

    /**
     * create table header row
     * @param sheet
     */
    private static void setHeader(Sheet sheet) {
        Row header = sheet.createRow(3);

        Cell headerCellNo = header.createCell(0);
        headerCellNo.setCellValue(messageSource.getMessage("KnowledgeDealPersonal.No", null, currentLocale));

        Cell headerCellNumber = header.createCell(1);
        headerCellNumber.setCellValue(messageSource.getMessage("KnowledgeDealPersonal.Number", null, currentLocale));

        Cell headerCellResult = header.createCell(2);
        headerCellResult.setCellValue(messageSource.getMessage("KnowledgeDealPersonal.Result", null, currentLocale));

        Cell headerCellField = header.createCell(3);
        headerCellField.setCellValue(messageSource.getMessage("KnowledgeDealPersonal.Field", null, currentLocale));

        Cell headerCellDevicePassageWay = header.createCell(4);
        headerCellDevicePassageWay.setCellValue(messageSource.getMessage("KnowledgeDealPersonal.DevicePassageWay", null, currentLocale));

        Cell headerCellGoods = header.createCell(5);
        headerCellGoods.setCellValue(messageSource.getMessage("KnowledgeDealPersonal.Goods", null, currentLocale));
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

            Sheet sheet = workbook.createSheet("Knowledge-Personal");

            Row title = sheet.createRow(0);
            Cell titleCell = title.createCell(0);
            titleCell.setCellValue(messageSource.getMessage("KnowledgeDealPersonal.Title", null, currentLocale));
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

                row.createCell(2).setCellValue(ConstantDictionary.getDataValue(deal.getHandTaskResult()));
                if(deal.getScanDevice() != null && deal.getScanDevice().getField() != null) {
                    row.createCell(3).setCellValue(deal.getScanDevice().getField().getFieldDesignation());
                } else {
                    row.createCell(3).setCellValue("无");
                }

                if(deal.getScanDevice() != null) {
                    row.createCell(4).setCellValue(deal.getScanDevice().getDevicePassageWay());
                } else {
                    row.createCell(4).setCellValue("无");
                }
                String goods = deal.getHandGoods();
                String convertGoods = "";
                if(goods != null) {
                    String[] split = goods.split(",");

                    for(int id = 0; id < split.length; id ++) {
                        if(id > 0) {
                            convertGoods += ",";
                        }
                        convertGoods += ConstantDictionary.getDataValue(split[id]);
                    }
                }

                row.createCell(5).setCellValue(convertGoods);
            }

            workbook.write(out);
            workbook.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
