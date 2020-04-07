/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（UserOrDeviceStatisticsWordView）
 * 文件名：	UserOrDeviceStatisticsWordView.java
 * 描述：	UserOrDeviceStatisticsWordView
 * 作者名：	Tiny
 * 日期：	2019/11/30
 *
 */

package com.nuctech.ecuritycheckitem.export.statisticsmanagement;

import com.nuctech.ecuritycheckitem.config.ConstantDictionary;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.export.BaseWordView;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.DetailTimeStatistics;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.TotalStatistics;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.TotalTimeStatistics;
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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class UserOrDeviceStatisticsWordView extends BaseWordView {

    private static List<String> nameList;
    private static List<String> deviceCategoryList;

    /**
     * create title paragraph
     * @param document
     * @param isUserStatOrDeviceStat
     */
    private static void createHeaderPart(XWPFDocument document, boolean isUserStatOrDeviceStat) {

        XWPFParagraph title = document.createParagraph();
        title.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun titleRun = title.createRun();
        if (isUserStatOrDeviceStat) {
            titleRun.setText(messageSource.getMessage("UserStatisticsTableTitle", null, currentLocale));
        }
        else {
            titleRun.setText(messageSource.getMessage("DeviceStatisticsTableTitle", null, currentLocale));
        }

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
     * @param isUserStatOrDeviceStat
     */
    private static void createTableHeader(XWPFTable table, boolean isUserStatOrDeviceStat) {

        table.setWidthType(TableWidthType.DXA);
        //create first row
        XWPFTableRow tableRowHeader = table.getRow(0);

        tableRowHeader.getCell(0).setText(messageSource.getMessage("ID", null, currentLocale));

        for(int i = 0; i < deviceCategoryList.size(); i ++) {
            tableRowHeader.addNewTableCell().setText(ConstantDictionary.getDataValue(deviceCategoryList.get(i)));
        }

        for(int i = 0; i < nameList.size(); i ++) {
            tableRowHeader.addNewTableCell().setText(nameList.get(i));
        }
    }

    /**
     * build inputstream of data to be exported
     * @param detailedStatistics
     * @return
     */
    public static InputStream buildWordDocument(TreeMap<Long, TotalTimeStatistics> detailedStatistics, boolean isUserStatOrDeviceStat) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        TotalTimeStatistics firstRecord = detailedStatistics.firstEntry().getValue();
        List<DetailTimeStatistics> firstDetail = firstRecord.getDetailedStatistics();
        nameList = new ArrayList<>();
        deviceCategoryList = new ArrayList<>();
        for(int i = 0; i < firstDetail.size(); i ++) {
            if(i < 3) {
                deviceCategoryList.add(firstDetail.get(i).getUserName());
            } else {
                nameList.add(firstDetail.get(i).getUserName());
            }
        }

        try {
            //Blank Document
            XWPFDocument document = new XWPFDocument();

            createHeaderPart(document, isUserStatOrDeviceStat);

            XWPFTable table = document.createTable();

            createTableHeader(table, isUserStatOrDeviceStat);

            long index = 1;
            List<String> totalNameList = deviceCategoryList;
            for(int i = 0; i < nameList.size(); i ++) {
                totalNameList.add(nameList.get(i));
            }
            for (Map.Entry<Long, TotalTimeStatistics> entry : detailedStatistics.entrySet()) {

                TotalTimeStatistics record = entry.getValue();
                long key = entry.getKey();

                XWPFTableRow tableRow = table.createRow();

                DecimalFormat df = new DecimalFormat("0.00");
                int colNum = 0;
                tableRow.getCell(colNum ++).setText(Long.toString(index ++));

                List<DetailTimeStatistics> detailTimeStatistics = record.getDetailedStatistics();
                for(int i = 0; i < totalNameList.size(); i ++) {
                    String name = totalNameList.get(i);
                    for(int j = 0; j < detailTimeStatistics.size(); j ++) {
                        if(detailTimeStatistics.get(j).getUserName().equals(name)) {
                            tableRow.getCell(colNum ++).setText(String.valueOf(detailTimeStatistics.get(j).getWorkingTime()));
                        }
                    }
                }
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
