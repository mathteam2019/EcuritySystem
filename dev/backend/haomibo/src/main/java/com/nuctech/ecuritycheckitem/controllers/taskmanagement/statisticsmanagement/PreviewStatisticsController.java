/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（统计预览 controller 1.0）
 * 文件名：	PreviewStatisticsController.java
 * 描述：	Controller to get statistics for preview
 * 作者名：	Tiny
 * 日期：	2019/12/20
 *
 */


package com.nuctech.ecuritycheckitem.controllers.taskmanagement.statisticsmanagement;

import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.export.statisticsmanagement.PreviewStatisticsExcelView;
import com.nuctech.ecuritycheckitem.export.statisticsmanagement.PreviewStatisticsPdfView;
import com.nuctech.ecuritycheckitem.export.statisticsmanagement.PreviewStatisticsWordView;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.InputStream;
import java.util.Date;
import java.util.TreeMap;
import java.util.Map;

@RestController
@RequestMapping("/task/statistics/")
public class PreviewStatisticsController extends BaseController {


    /**
     * Preview Statistics RequestBody
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class StatisticsRequestBody {

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        static class Filter {

            Long fieldId; //field id
            Long deviceId; //device id
            Long userCategory; //user category id
            String userName; //user name
            @DateTimeFormat(style = Constants.DATETIME_FORMAT)
            Date startTime; //start time
            @DateTimeFormat(style = Constants.DATETIME_FORMAT)
            Date endTime; //end time
            String statWidth; //statistics width (possible values: hour, day, week, month, quarter, year)

        }

        Integer currentPage; //current page no
        Integer perPage; //record count per page
        StatisticsRequestBody.Filter filter;

    }

    /**
     * preview statistics generate request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class StatisticsGenerateRequestBody {

        String idList; //id list of tasks which is combined with comma. ex: "1,2,3"
        @NotNull
        Boolean isAll; //true or false. is isAll is true, ignore idList and print all data

        StatisticsRequestBody filter;
    }

    /**
     * get preview statistics request
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/preview", method = RequestMethod.POST)
    public Object previewStatisticsGet(
            @RequestBody @Valid StatisticsRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            //check validation and return invalid_parameter in case of invalid parameters are input
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        //get statistics from database through previewStatisticsService
        TotalStatisticsResponse response = previewStatisticsService.getStatistics(
                requestBody.getFilter().getFieldId(),//get field id from input parameter
                requestBody.getFilter().getDeviceId(),//get device id from input parameter
                requestBody.getFilter().getUserCategory(),//get user category id from input parameter
                requestBody.getFilter().getUserName(),//get user name from input parameter
                requestBody.getFilter().getStartTime(),//get start time from input parameter
                requestBody.getFilter().getEndTime(),//get end time from input parameter
                requestBody.getFilter().getStatWidth(),//get statistics width from input parameter
                requestBody.getCurrentPage(),//get current page no from input parameter
                requestBody.getPerPage());//get record count per page from input parameter

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, response));

        return value;

    }


    /**
     * Preview Statistics generate pdf file request.
     */
    @RequestMapping(value = "/preview/generate/pdf", method = RequestMethod.POST)
    public Object previewStatisticsPDFGenerateFile(@RequestBody @Valid StatisticsGenerateRequestBody requestBody,
                                                   BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            //check validation and return invalid_parameter in case of invalid parameters are input
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        StatisticsRequestBody body = requestBody.getFilter();

        //get statistics from database through previewStatisticsService
        TreeMap<Long, TotalStatistics> totalStatistics = previewStatisticsService.getStatistics(
                requestBody.getFilter().getFilter().getFieldId(),//get field id from input parameter
                requestBody.getFilter().getFilter().getDeviceId(),//get device id from input parameter
                requestBody.getFilter().getFilter().getUserCategory(),//get user category id from input parameter
                requestBody.getFilter().getFilter().getUserName(),//get user name from input parameter
                requestBody.getFilter().getFilter().getStartTime(),//get start time from input parameter
                requestBody.getFilter().getFilter().getEndTime(),//get end time from input parameter
                requestBody.getFilter().getFilter().getStatWidth(),//get statistics width from input parameter
                null,
                null).getDetailedStatistics();

        TreeMap<Long, TotalStatistics> exportList = getExportList(totalStatistics, requestBody.getIsAll(), requestBody.getIdList());
        PreviewStatisticsPdfView.setResource(getFontResource());  //set header font
        setDictionary(); //set dictionary data key and values
        InputStream inputStream = PreviewStatisticsPdfView.buildPDFDocument(exportList);  //make inputstream of data to be printed

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=previewStatistics.pdf");  //set filename

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));
    }

    /**
     * Preview Statistics generate excel file request.
     */
    @RequestMapping(value = "/preview/generate/xlsx", method = RequestMethod.POST)
    public Object previewStatisticsGenerateExcelFile(@RequestBody @Valid StatisticsGenerateRequestBody requestBody,
                                                     BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            //return invalid_parameter message when invalid parameters were input
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        //get statistics fromd database through previewStatisticsService
        TreeMap<Long, TotalStatistics> totalStatistics = previewStatisticsService.getStatistics(
                requestBody.getFilter().getFilter().getFieldId(),//get field id from input parameter
                requestBody.getFilter().getFilter().getDeviceId(),//get device id from input parameter
                requestBody.getFilter().getFilter().getUserCategory(),//get user category id from input parameter
                requestBody.getFilter().getFilter().getUserName(),//get user name from input parameter
                requestBody.getFilter().getFilter().getStartTime(),//get start time from input parameter
                requestBody.getFilter().getFilter().getEndTime(),//get end time from input parameter
                requestBody.getFilter().getFilter().getStatWidth(),//get statistics width from input parameter
                null,
                null).getDetailedStatistics();

        TreeMap<Long, TotalStatistics> exportList = getExportList(totalStatistics, requestBody.getIsAll(), requestBody.getIdList());
        setDictionary();   //set dictionary data key and values
        InputStream inputStream = PreviewStatisticsExcelView.buildExcelDocument(exportList);//make inputstream of data to be exported

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=previewStatistics.xlsx"); //set filename

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msexcel"))
                .body(new InputStreamResource(inputStream));
    }

    /**
     * Preview Statistics generate Word file request.
     */
    @RequestMapping(value = "/preview/generate/docx", method = RequestMethod.POST)
    public Object previewStatisticsGenerateWordFile(@RequestBody @Valid StatisticsGenerateRequestBody requestBody,
                                                     BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            //return invalid_parameter message when invalid parameters were input
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        //get statistics from database through previewStatisticsService
        TreeMap<Long, TotalStatistics> totalStatistics = previewStatisticsService.getStatistics(
                requestBody.getFilter().getFilter().getFieldId(),//get field id from input parameter
                requestBody.getFilter().getFilter().getDeviceId(),//get device id from input parameter
                requestBody.getFilter().getFilter().getUserCategory(),//get user category id from input parameter
                requestBody.getFilter().getFilter().getUserName(),//get user name from input parameter
                requestBody.getFilter().getFilter().getStartTime(),//get start time from input parameter
                requestBody.getFilter().getFilter().getEndTime(),//get end time from input parameter
                requestBody.getFilter().getFilter().getStatWidth(),//get statistics width from input parameter
                null,
                null).getDetailedStatistics();

        TreeMap<Long, TotalStatistics> exportList = getExportList(totalStatistics, requestBody.getIsAll(), requestBody.getIdList());
        setDictionary(); //set dictionary data key and values
        InputStream inputStream = PreviewStatisticsWordView.buildWordDocument(exportList);  //make input stream of data to be exported

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=previewStatistics.docx"); //set file name

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msword"))
                .body(new InputStreamResource(inputStream));
    }

    /**
     * Extract records to export documents(word/excel/pdf)
     * @param detailedStatistics : total records
     * @param isAll : true - print all, false - print records in idList
     * @param idList : idList to be extracted
     * @return
     */
    private TreeMap<Long, TotalStatistics> getExportList(TreeMap<Long, TotalStatistics> detailedStatistics, boolean isAll, String idList) {

        TreeMap<Long, TotalStatistics> exportList = new TreeMap<>();

        if (isAll == false) {
            String[] splits = idList.split(",");  //get ids from idList

            for (Map.Entry<Long, TotalStatistics> entry : detailedStatistics.entrySet()) {

                TotalStatistics record = entry.getValue();

                boolean isExist = false;
                for (int j = 0; j < splits.length; j++) {
                    if (splits[j].equals(Long.toString(record.getTime()))) {  //if specified id is contained idList
                        isExist = true;
                        break;
                    }
                }
                if (isExist == true) {  //if exist
                    exportList.put(entry.getKey(), record);
                }

            }

        } else { //if isAll is true
            exportList = detailedStatistics;
        }

        return exportList;
    }

}
