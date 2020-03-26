/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（毫米波人体查验手检统计 controller 1.0）
 * 文件名：	SuspicionHandgoodsStatisticsController.java
 * 描述：	Controller to get suspiction handgoods statistics
 * 作者名：	Tiny
 * 日期：	2019/12/20
 *
 */

package com.nuctech.ecuritycheckitem.controllers.taskmanagement.statisticsmanagement;

import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.export.statisticsmanagement.*;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.SuspicionHandGoodsPaginationResponse;
import com.nuctech.ecuritycheckitem.utils.Utils;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.InputStream;
import java.util.*;

@RestController
@RequestMapping("/task/statistics/")
public class SuspicionHandgoodsStatisticsController extends BaseController {

    /**
     * Suspiction hand goods Statistics RequestBody
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

            Long fieldId; //scene id
            Long deviceId; //device id
            Long userCategory; //user category id
            String userName; //user name
            @DateTimeFormat(style = Constants.DATETIME_FORMAT)
            Date startTime; //start time
            @DateTimeFormat(style = Constants.DATETIME_FORMAT)
            Date endTime; //end time
            String statWidth; //statistics width

        }

        Integer currentPage; //current page no
        Integer perPage; //record count per page
        String sort;
        StatisticsRequestBody.Filter filter;

    }

    /**
     * Hand goods list
     */
    public static final List<String> handGoodsIDList = Arrays.asList(new String[]{
            "1000001601",
            "1000001602",
            "1000001603",
            "1000001604",
            "1000001605"
    });

    /**
     * Suspiction hand goods statistics generate request body.
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
        String sort;
        StatisticsRequestBody filter;
        String locale;
    }

    /**
     * Get suspiction hand goods statistics request
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/get-suspicionhandgoods-statistics", method = RequestMethod.POST)
    public Object getSuspicionHandGoodsSummary(
            @RequestBody @Valid StatisticsRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            //check validation and return invalid_parameter in case of invalid parameters are input
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        SuspicionHandGoodsPaginationResponse response = new SuspicionHandGoodsPaginationResponse();
        String sortBy = "";
        String order = "";
        Map<String, String> sortParams = new HashMap<String, String>();
        if (requestBody.getSort() != null && !requestBody.getSort().isEmpty()) {
            sortParams = Utils.getSortParams(requestBody.getSort());
            if (!sortParams.isEmpty()) {
                sortBy = sortParams.get("sortBy");
                order = sortParams.get("order");
            }
        }
        response = suspictionHandgoodsStatisticsService.getStatistics(sortBy, order,
                requestBody.getFilter().getFieldId(), //get field if from input parameter
                requestBody.getFilter().getDeviceId(), //get device id from input parameter
                requestBody.getFilter().getUserCategory(), //get user category id from input parameter
                requestBody.getFilter().getUserName(), //get user name from input parameter
                requestBody.getFilter().getStartTime(), //get start time from input parameter
                requestBody.getFilter().getEndTime(), //get end time from input parameter
                requestBody.getFilter().getStatWidth(), //get statistics width from input parameter
                requestBody.getCurrentPage(), //get current page no from input parameter
                requestBody.getPerPage()); //get record count per page from input parameter

        return new CommonResponseBody(ResponseMessage.OK, response);

    }

    /**
     * Suspiction handgoods  generate pdf file request.
     */
    @RequestMapping(value = "/suspiciongoods/generate/pdf", method = RequestMethod.POST)
    public Object suspicionGoodsStatisticsPDFGenerateFile(@RequestBody @Valid StatisticsGenerateRequestBody requestBody,
                                                          BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            //check validation and return invalid_parameter in case of invalid parameters are input
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }
        String sortBy = "";
        String order = "";
        Map<String, String> sortParams = new HashMap<String, String>();
        if (requestBody.getSort() != null && !requestBody.getSort().isEmpty()) {
            sortParams = Utils.getSortParams(requestBody.getSort());
            if (!sortParams.isEmpty()) {
                sortBy = sortParams.get("sortBy");
                order = sortParams.get("order");
            }
        }
        TreeMap<Integer, TreeMap<String, Long>> totalStatistics = suspictionHandgoodsStatisticsService.getStatistics(sortBy, order,
                requestBody.getFilter().getFilter().getFieldId(),//get field if from input parameter
                requestBody.getFilter().getFilter().getDeviceId(),//get device id from input parameter
                requestBody.getFilter().getFilter().getUserCategory(),//get user category id from input parameter
                requestBody.getFilter().getFilter().getUserName(),//get user name from input parameter
                requestBody.getFilter().getFilter().getStartTime(),//get start time from input parameter
                requestBody.getFilter().getFilter().getEndTime(),//get end time from input parameter
                requestBody.getFilter().getFilter().getStatWidth(),//get statistics width from input parameter
                null,
                null).getDetailedStatistics();

        TreeMap<Integer, TreeMap<String, Long>> exportList = getExportList(totalStatistics, requestBody.getIsAll(), requestBody.getIdList());
        HandExaminationStatisticsPdfView.setResource(getFontResource()); //get header font
        setDictionary(requestBody.getLocale());//set dictionary data key and values
        SuspictionHandgoodsStatisticsPdfView.setMessageSource(messageSource);
        if(Constants.CHINESE_LOCALE.equals(requestBody.getLocale())) {
            SuspictionHandgoodsStatisticsPdfView.setCurrentLocale(Locale.CHINESE);
        } else {
            SuspictionHandgoodsStatisticsPdfView.setCurrentLocale(Locale.ENGLISH);
        }
        InputStream inputStream = SuspictionHandgoodsStatisticsPdfView.buildPDFDocument(exportList); //make inputstream of data to be printed

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=suspicionGoodsStatistics.pdf");//set filename

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));
    }

    /**
     * Suspiction handgoods Statistics generate excel file request.
     */
    @RequestMapping(value = "/suspiciongoods/generate/xlsx", method = RequestMethod.POST)
    public Object suspicioGoodsStatisticsGenerateExcelFile(@RequestBody @Valid StatisticsGenerateRequestBody requestBody,
                                                           BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            //check validation and return invalid_parameter in case of invalid parameters are input
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }
        String sortBy = "";
        String order = "";
        Map<String, String> sortParams = new HashMap<String, String>();
        if (requestBody.getSort() != null && !requestBody.getSort().isEmpty()) {
            sortParams = Utils.getSortParams(requestBody.getSort());
            if (!sortParams.isEmpty()) {
                sortBy = sortParams.get("sortBy");
                order = sortParams.get("order");
            }
        }
        //get statistics from database through suspictionHandgoodsStatisticsService
        TreeMap<Integer, TreeMap<String, Long>> totalStatistics = suspictionHandgoodsStatisticsService.getStatistics(sortBy, order,
                requestBody.getFilter().getFilter().getFieldId(),//get field id from input parameter
                requestBody.getFilter().getFilter().getDeviceId(),//get device id from input parameter
                requestBody.getFilter().getFilter().getUserCategory(),//get user category id from input parameter
                requestBody.getFilter().getFilter().getUserName(),//get user name from input parameter
                requestBody.getFilter().getFilter().getStartTime(),//get start time from input parameter
                requestBody.getFilter().getFilter().getEndTime(),//get end time from input parameter
                requestBody.getFilter().getFilter().getStatWidth(),//get statistics width from input parameter
                null,
                null).getDetailedStatistics();

        TreeMap<Integer, TreeMap<String, Long>> exportList = getExportList(totalStatistics, requestBody.getIsAll(), requestBody.getIdList());
        setDictionary(requestBody.getLocale()); //set dictionary data key and values
        SuspictionHandgoodsStatisticsExcelView.setMessageSource(messageSource);
        if(Constants.CHINESE_LOCALE.equals(requestBody.getLocale())) {
            SuspictionHandgoodsStatisticsExcelView.setCurrentLocale(Locale.CHINESE);
        } else {
            SuspictionHandgoodsStatisticsExcelView.setCurrentLocale(Locale.ENGLISH);
        }
        InputStream inputStream = SuspictionHandgoodsStatisticsExcelView.buildExcelDocument(exportList); //make inputstream of data to be exported

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=suspicionGoodsStatistics.xlsx"); //set filename

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msexcel"))
                .body(new InputStreamResource(inputStream));
    }

    /**
     * Suspiction handgoods Statistics generate word file request.
     */
    @RequestMapping(value = "/suspiciongoods/generate/docx", method = RequestMethod.POST)
    public Object suspicioGoodsStatisticsGenerateWordFile(@RequestBody @Valid StatisticsGenerateRequestBody requestBody,
                                                           BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            //return invalid_parameter message when invalid parameters were input
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }
        String sortBy = "";
        String order = "";
        Map<String, String> sortParams = new HashMap<String, String>();
        if (requestBody.getSort() != null && !requestBody.getSort().isEmpty()) {
            sortParams = Utils.getSortParams(requestBody.getSort());
            if (!sortParams.isEmpty()) {
                sortBy = sortParams.get("sortBy");
                order = sortParams.get("order");
            }
        }
        TreeMap<Integer, TreeMap<String, Long>> totalStatistics = suspictionHandgoodsStatisticsService.getStatistics(sortBy, order,
                requestBody.getFilter().getFilter().getFieldId(),//get field id from input parameter
                requestBody.getFilter().getFilter().getDeviceId(),//get device id from input parameter
                requestBody.getFilter().getFilter().getUserCategory(),//get user category id from input parameter
                requestBody.getFilter().getFilter().getUserName(),//get user name from input parameter
                requestBody.getFilter().getFilter().getStartTime(),//get start time from input parameter
                requestBody.getFilter().getFilter().getEndTime(),//get end time from input parameter
                requestBody.getFilter().getFilter().getStatWidth(),//get statistics width from input parameter
                null,
                null).getDetailedStatistics();

        TreeMap<Integer, TreeMap<String, Long>> exportList = getExportList(totalStatistics, requestBody.getIsAll(), requestBody.getIdList());
        setDictionary(requestBody.getLocale());   //set dictionary data key and values
        SuspictionHandgoodsStatisticsWordView.setMessageSource(messageSource);
        if(Constants.CHINESE_LOCALE.equals(requestBody.getLocale())) {
            SuspictionHandgoodsStatisticsWordView.setCurrentLocale(Locale.CHINESE);
        } else {
            SuspictionHandgoodsStatisticsWordView.setCurrentLocale(Locale.ENGLISH);
        }
        InputStream inputStream = SuspictionHandgoodsStatisticsWordView.buildWordDocument(exportList); //make inputstream of data to be exported

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=suspicionGoodsStatistics.docx");//set filename

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
    private TreeMap<Integer, TreeMap<String, Long>> getExportList(TreeMap<Integer, TreeMap<String, Long>> detailedStatistics, boolean isAll, String idList) {

        TreeMap<Integer, TreeMap<String, Long>> exportList = new TreeMap<>();

        if (isAll == false) {
            String[] splits = idList.split(","); //get ids from idList

            for (Map.Entry<Integer, TreeMap<String, Long>> entry : detailedStatistics.entrySet()) {

                TreeMap<String, Long> record = entry.getValue();

                boolean isExist = false;
                for (int j = 0; j < splits.length; j++) {
                    if (splits[j].equals(Long.toString(record.get("time")))) {//if specified id is contained idList
                        isExist = true;
                        break;
                    }
                }
                if (isExist == true) {//if exist
                    exportList.put(entry.getKey(), record);
                    if(exportList.size() >= Constants.MAX_EXPORT_NUMBER) {
                        break;
                    }
                }

            }

        } else {//if isAll is true
            for (Map.Entry<Integer, TreeMap<String, Long>> entry : detailedStatistics.entrySet()) {
                TreeMap<String, Long> record = entry.getValue();
                exportList.put(entry.getKey(), record);
                if(exportList.size() >= Constants.MAX_EXPORT_NUMBER) {
                    break;
                }
            }
        }

        return exportList;
    }

}
