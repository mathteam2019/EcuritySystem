/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（UserOrDeviceStatisticsPdfView）
 * 文件名：	UserOrDeviceStatisticsPdfView.java
 * 描述：	UserOrDeviceStatisticsPdfView
 * 作者名：	Tiny
 * 日期：	2019/11/30
 *
 */

package com.nuctech.ecuritycheckitem.export.statisticsmanagement;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.nuctech.ecuritycheckitem.config.ConstantDictionary;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.export.BasePdfView;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.DetailTimeStatistics;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.TotalStatistics;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.TotalTimeStatistics;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.TreeMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class UserOrDeviceStatisticsPdfView extends BasePdfView {

    private static List<String> nameList;
    private static List<String> deviceCategoryList;
    /**
     * build inputstream of data to be printed
     * @param detailedStatistics
     * @return
     */
    public static InputStream buildPDFDocument(TreeMap<Long, TotalTimeStatistics> detailedStatistics, boolean type) {

        Document document = new Document();

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

            PdfWriter.getInstance(document, out);

            document.open();
            if (type) {
                document.add(getTitle(messageSource.getMessage("UserStatisticsTableTitle", null,currentLocale)));
            } else {
                document.add(getTitle(messageSource.getMessage("DeviceStatisticsTableTitle", null,currentLocale)));
            }
            document.add(getTime());

            PdfPTable table = new PdfPTable(deviceCategoryList.size() + nameList.size() + 1);

            List<String> strHeaderList = Arrays.asList(new String[]{messageSource.getMessage("ID", null, currentLocale)});

            for(int i = 0; i < deviceCategoryList.size(); i ++) {
                strHeaderList.add(ConstantDictionary.getDataValue(deviceCategoryList.get(i)));
            }
            for(int i = 0; i < nameList.size(); i ++) {
                strHeaderList.add(nameList.get(i));
            }


            table.setWidthPercentage(99);
            strHeaderList
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell();

                        header.setBorderWidth(2);
                        header.setPhrase(new Phrase(messageSource.getMessage(columnTitle, null, currentLocale), getFontWithSize(Constants.PDF_HEAD_FONT_SIZE)));
                        table.addCell(header);
                    });

            long index = 1;
            List<String> totalNameList = deviceCategoryList;
            for(int i = 0; i < nameList.size(); i ++) {
                totalNameList.add(nameList.get(i));
            }

            for (Map.Entry<Long, TotalTimeStatistics> entry : detailedStatistics.entrySet()) {

                TotalTimeStatistics record = entry.getValue();

                DecimalFormat df = new DecimalFormat("0.00");

                addTableCell(table, Long.toString(index++));
                int colNum = 0;

                List<DetailTimeStatistics> detailTimeStatistics = record.getDetailedStatistics();
                for(int i = 0; i < totalNameList.size(); i ++) {
                    String name = totalNameList.get(i);
                    for(int j = 0; j < detailTimeStatistics.size(); j ++) {
                        if(detailTimeStatistics.get(j).getUserName().equals(name)) {
                            addTableCell(table, String.valueOf(detailTimeStatistics.get(j).getWorkingTime()));
                        }
                    }
                }

            }


            document.add(table);

            document.close();

        } catch (
                DocumentException e) {
            e.printStackTrace();
        }

        return new

                ByteArrayInputStream(out.toByteArray());
    }
}
