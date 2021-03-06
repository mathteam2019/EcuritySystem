/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（KnowledgeDealPendingWordView）
 * 文件名：	KnowledgeDealPendingWordView.java
 * 描述：	KnowledgeDealPendingWordView
 * 作者名：	Tiny
 * 日期：	2019/11/29
 *
 */

package com.nuctech.ecuritycheckitem.export.knowledgemanagement;

import com.nuctech.ecuritycheckitem.config.ConstantDictionary;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.export.BaseWordView;
import com.nuctech.ecuritycheckitem.models.db.SerKnowledgeCase;
import com.nuctech.ecuritycheckitem.utils.Utils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.poi.xwpf.usermodel.TableWidthType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

public class KnowledgeDealPendingWordView extends BaseWordView {

    /**
     * create title paragraph
     * @param document
     */
    private static void createHeaderPart(XWPFDocument document) {

        XWPFParagraph title = document.createParagraph();
        title.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun titleRun = title.createRun();
        titleRun.setText(messageSource.getMessage("KnowledgeDealPending.Title", null, currentLocale));
        titleRun.setFontSize(Constants.WORD_HEAD_FONT_SIZE);
        titleRun.setFontFamily(Constants.WORD_HEAD_FONT_NAME);

        XWPFParagraph subTitle = document.createParagraph();
        subTitle.setAlignment(ParagraphAlignment.RIGHT);

        XWPFRun subTitleRun = subTitle.createRun();
        subTitleRun.setText(getCurrentTime());
        titleRun.setFontSize(Constants.WORD_HEAD_FONT_SIZE);
        titleRun.setFontFamily(Constants.WORD_HEAD_FONT_NAME);

    }

    /**
     * create table header row
     * @param table
     */
    private static void createTableHeader(XWPFTable table) {

        table.setWidthType(TableWidthType.DXA);
        //create first row
        XWPFTableRow tableRowHeader = table.getRow(0);
        tableRowHeader.getCell(0).setText(messageSource.getMessage("KnowledgeDealPending.No", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("KnowledgeDealPending.Number", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("KnowledgeDealPending.Mode", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("KnowledgeDealPending.Result", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("KnowledgeDealPending.Field", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("KnowledgeDealPending.ScanDevice", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("KnowledgeDealPending.JudgeDevice", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("KnowledgeDealPending.HandDevice", null, currentLocale));
        tableRowHeader.addNewTableCell().setText(messageSource.getMessage("KnowledgeDealPending.Goods", null, currentLocale));

    }

    /**
     * build inputstream of data to be exported
     * @param exportList
     * @return
     */
    public static InputStream buildWordDocument(List<SerKnowledgeCase> exportList) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            //Blank Document
            XWPFDocument document = new XWPFDocument();

            createHeaderPart(document);

            XWPFTable table = document.createTable();

            createTableHeader(table);

            int number = 0;
            for (Integer i = 0; i < exportList.size(); i ++) {

                SerKnowledgeCase deal = exportList.get(i);

                XWPFTableRow tableRow = table.createRow();

                tableRow.getCell(0).setText(String.valueOf(++ number));
                if(deal != null) {
                    tableRow.getCell(1).setText(deal.getTaskNumber());
                } else {
                    tableRow.getCell(1).setText(messageSource.getMessage("None", null, currentLocale));
                }

                if(deal != null) {
                    tableRow.getCell(2).setText(ConstantDictionary.getDataValue(deal.getModeName()));
                } else {
                    tableRow.getCell(2).setText(messageSource.getMessage("None", null, currentLocale));
                }

                tableRow.getCell(3).setText(Utils.getTaskResultFromKnowledge(deal));
                if(!StringUtils.isEmpty(deal.getFieldDesignation())){
                    tableRow.getCell(4).setText(deal.getFieldDesignation());
                } else {
                    tableRow.getCell(4).setText(messageSource.getMessage("None", null, currentLocale));
                }

                if(deal.getScanDevice() != null) {
                    tableRow.getCell(5).setText(deal.getScanDevice().getDeviceName());
                } else {
                    tableRow.getCell(5).setText(messageSource.getMessage("None", null, currentLocale));
                }

                if(!StringUtils.isEmpty(deal.getJudgeDeviceName())) {
                    tableRow.getCell(6).setText(deal.getJudgeDeviceName());
                } else {
                    tableRow.getCell(6).setText(messageSource.getMessage("None", null, currentLocale));
                }

                if(!StringUtils.isEmpty(deal.getHandDeviceName())) {
                    tableRow.getCell(7).setText(deal.getHandDeviceName());
                } else {
                    tableRow.getCell(7).setText(messageSource.getMessage("None", null, currentLocale));
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

                tableRow.getCell(8).setText(convertGoods);

            }

            setWidth(table, document);
            document.write(out);
            document.close();
        }
        catch (Exception e) {

        }

        return new ByteArrayInputStream(out.toByteArray());

    }

}
