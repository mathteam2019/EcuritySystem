package com.nuctech.ecuritycheckitem.controllers.taskmanagement.statisticsmanagement;

import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.controllers.taskmanagement.ProcessTaskController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.export.statisticsmanagement.ScanStatisticsExcelView;
import com.nuctech.ecuritycheckitem.export.statisticsmanagement.ScanStatisticsPdfView;
import com.nuctech.ecuritycheckitem.export.statisticsmanagement.ScanStatisticsWordView;
import com.nuctech.ecuritycheckitem.models.db.SerScan;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.ScanStatistics;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.ScanStatisticsResponse;
import com.sun.istack.NotNull;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
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

import javax.persistence.Query;
import javax.validation.Valid;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/task/statistics/")
public class ScanStatisticsController extends BaseController {

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

            Long fieldId;
            Long deviceId;
            Long userCategory;
            String userName;
            @DateTimeFormat(style = Constants.DATETIME_FORMAT)
            Date startTime;
            @DateTimeFormat(style = Constants.DATETIME_FORMAT)
            Date endTime;
            String statWidth;

        }

        Integer currentPage;

        Integer perPage;

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

        String idList;
        @NotNull
        Boolean isAll;

        StatisticsRequestBody filter;
    }

    @RequestMapping(value = "/scan", method = RequestMethod.POST)
    public Object scanStatisticsGet(
            @RequestBody @Valid StatisticsRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        //get Scan statistics
        ScanStatisticsResponse scanStatistics = new ScanStatisticsResponse();

        scanStatistics = scanStatisticsService.getStatistics(
                requestBody.getFilter().getFieldId(),
                requestBody.getFilter().getDeviceId(),
                requestBody.getFilter().getUserCategory(),
                requestBody.getFilter().getUserName(),
                requestBody.getFilter().getStartTime(),
                requestBody.getFilter().getEndTime(),
                requestBody.getFilter().getStatWidth(),
                requestBody.getCurrentPage(),
                requestBody.getPerPage());

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, scanStatistics));

        return value;

    }

    /**
     * Scan Statistics generate excel file request.
     */
    @RequestMapping(value = "/scan/generate/export", method = RequestMethod.POST)
    public Object scanStatisticsGenerateExcelFile(@RequestBody @Valid StatisticsGenerateRequestBody requestBody,
                                                  BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        TreeMap<Integer, ScanStatistics> totalStatistics = scanStatisticsService.getStatistics(
                requestBody.getFilter().getFilter().getFieldId(),
                requestBody.getFilter().getFilter().getDeviceId(),
                requestBody.getFilter().getFilter().getUserCategory(),
                requestBody.getFilter().getFilter().getUserName(),
                requestBody.getFilter().getFilter().getStartTime(),
                requestBody.getFilter().getFilter().getEndTime(),
                requestBody.getFilter().getFilter().getStatWidth(),
                null,
                null).getDetailedStatistics();

        TreeMap<Integer, ScanStatistics> exportList = getExportList(totalStatistics, requestBody.getIsAll(), requestBody.getIdList());

        InputStream inputStream = ScanStatisticsExcelView.buildExcelDocument(exportList);


        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=scanStatistics.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msexcel"))
                .body(new InputStreamResource(inputStream));
    }

    /**
     * Scan Statistics generate word file request.
     */
    @RequestMapping(value = "/scan/generate/word", method = RequestMethod.POST)
    public Object scanStatisticsGenerateWordFile(@RequestBody @Valid StatisticsGenerateRequestBody requestBody,
                                                  BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        TreeMap<Integer, ScanStatistics> totalStatistics = scanStatisticsService.getStatistics(
                requestBody.getFilter().getFilter().getFieldId(),
                requestBody.getFilter().getFilter().getDeviceId(),
                requestBody.getFilter().getFilter().getUserCategory(),
                requestBody.getFilter().getFilter().getUserName(),
                requestBody.getFilter().getFilter().getStartTime(),
                requestBody.getFilter().getFilter().getEndTime(),
                requestBody.getFilter().getFilter().getStatWidth(),
                null,
                null).getDetailedStatistics();

        TreeMap<Integer, ScanStatistics> exportList = getExportList(totalStatistics, requestBody.getIsAll(), requestBody.getIdList());

        InputStream inputStream = ScanStatisticsWordView.buildWordDocument(exportList);


        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=scanStatistics.docx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msexcel"))
                .body(new InputStreamResource(inputStream));
    }

    /**
     * Scan Statistics generate pdf file request.
     */
    @RequestMapping(value = "/scan/generate/print", method = RequestMethod.POST)
    public Object scanStatisticsPDFGenerateFile(@RequestBody @Valid StatisticsGenerateRequestBody requestBody,
                                                BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        TreeMap<Integer, ScanStatistics> totalStatistics = scanStatisticsService.getStatistics(
                requestBody.getFilter().getFilter().getFieldId(),
                requestBody.getFilter().getFilter().getDeviceId(),
                requestBody.getFilter().getFilter().getUserCategory(),
                requestBody.getFilter().getFilter().getUserName(),
                requestBody.getFilter().getFilter().getStartTime(),
                requestBody.getFilter().getFilter().getEndTime(),
                requestBody.getFilter().getFilter().getStatWidth(),
                null,
                null).getDetailedStatistics();

        TreeMap<Integer, ScanStatistics> exportList = getExportList(totalStatistics, requestBody.getIsAll(), requestBody.getIdList());

        ScanStatisticsPdfView.setResource(res);
        InputStream inputStream = ScanStatisticsPdfView.buildPDFDocument(exportList);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=scanStatistics.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));
    }

    private TreeMap<Integer, ScanStatistics> getExportList(TreeMap<Integer, ScanStatistics> detailedStatistics, boolean isAll, String idList) {

        TreeMap<Integer, ScanStatistics> exportList = new TreeMap<>();

        if (isAll == false) {
            String[] splits = idList.split(",");

            for (Map.Entry<Integer, ScanStatistics> entry : detailedStatistics.entrySet()) {

                ScanStatistics record = entry.getValue();

                boolean isExist = false;
                for (int j = 0; j < splits.length; j++) {
                    if (splits[j].equals(Long.toString(record.getTime()))) {
                        isExist = true;
                        break;
                    }
                }
                if (isExist == true) {
                    exportList.put(entry.getKey(), record);
                }

            }

        } else {
            exportList = detailedStatistics;
        }

        return exportList;
    }


    private String getSelectQuery(String groupBy) {
        return "SELECT " +
                groupBy +
                "\n (s.SCAN_START_TIME)," +
                "\tcount( s.SCAN_ID ) AS total,\n" +
                "\tsum( IF ( s.scan_invalid LIKE '" + SerScan.Invalid.TRUE + "', 1, 0 ) ) AS valid,\n" +
                "\tsum( IF ( s.scan_invalid LIKE '" + SerScan.Invalid.FALSE + "', 1, 0 ) ) AS invalid,\n" +
                "\tsum( IF ( s.scan_atr_result LIKE '" + SerScan.ATRResult.TRUE + "', 1, 0 ) ) AS passed,\n" +
                "\tsum( IF ( s.scan_foot_alarm LIKE '" + SerScan.FootAlarm.TRUE + "', 1, 0 ) ) AS alarm\n";

    }

    private String getJoinQuery() {

        return "\tser_scan s\n" +
                "\tLEFT JOIN ser_task t ON s.task_id = t.task_id\n" +
                "\tLEFT JOIN sys_user u ON s.SCAN_POINTSMAN_ID = u.user_id\n";

    }

    private List<String> getWhereCause(StatisticsRequestBody requestBody) {
        List<String> whereCause = new ArrayList<String>();

        if (requestBody.getFilter().getFieldId() != null) {
            whereCause.add("t.SCENE = " + requestBody.getFilter().getFieldId());
        }
        if (requestBody.getFilter().getDeviceId() != null) {
            whereCause.add("s.SCAN_DEVICE_ID = " + requestBody.getFilter().getDeviceId());
        }
        if (requestBody.getFilter().getUserName() != null && !requestBody.getFilter().getUserName().isEmpty()) {
            whereCause.add("u.USER_NAME like '%" + requestBody.getFilter().getUserName() + "%' ");
        }
        if (requestBody.getFilter().getStartTime() != null) {
            Date date = requestBody.getFilter().getStartTime();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String strDate = dateFormat.format(date);
            whereCause.add("s.SCAN_START_TIME >= '" + strDate + "'");
        }
        if (requestBody.getFilter().getEndTime() != null) {
            Date date = requestBody.getFilter().getEndTime();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String strDate = dateFormat.format(date);
            whereCause.add("s.SCAN_END_TIME <= '" + strDate + "'");
        }
        return whereCause;
    }

    private ScanStatistics getTotalStatistics(String query) {

        Query jpaQuery = entityManager.createNativeQuery(query);
        List<Object> resultTotal = jpaQuery.getResultList();

        ScanStatistics record = new ScanStatistics();
        for (int i = 0; i < resultTotal.size(); i++) {
            Object[] item = (Object[]) resultTotal.get(i);
            record = initModelFromObject(item);
        }

        return record;
    }

    private ScanStatistics initModelFromObject(Object[] item) {

        ScanStatistics record = new ScanStatistics();

        try {
            record.setTime(Integer.parseInt(item[0].toString()));
            record.setTotalScan(Long.parseLong(item[1].toString()));
            record.setValidScan(Long.parseLong(item[2].toString()));
            record.setInvalidScan(Long.parseLong(item[3].toString()));
            record.setPassedScan(Long.parseLong(item[4].toString()));
            record.setAlarmScan(Long.parseLong(item[5].toString()));
            record.setValidScanRate(0);
            record.setInvalidScanRate(0);
            record.setPassedScanRate(0);
            record.setAlarmScanRate(0);
            if (record.getTotalScan() > 0) {
                record.setValidScanRate(record.getValidScan() * 100 / (double) record.getTotalScan());
                record.setInvalidScanRate(record.getInvalidScan() * 100 / (double) record.getTotalScan());
                record.setPassedScanRate(record.getPassedScan() * 100 / (double) record.getTotalScan());
                record.setAlarmScanRate(record.getAlarmScan() * 100 / (double) record.getTotalScan());
            }
        } catch (Exception e) {
        }

        return record;
    }

    private TreeMap<Integer, ScanStatistics> getDetailedStatistics(String query, StatisticsRequestBody requestBody) {
        Query jpaQuery = entityManager.createNativeQuery(query);
        List<Object> result = jpaQuery.getResultList();
        TreeMap<Integer, ScanStatistics> data = new TreeMap<>();

        Integer keyValueMin = 0, keyValueMax = -1;
        List<Integer> keyValues = getKeyValuesforStatistics(requestBody);
        try {
            keyValueMin = keyValues.get(0);
            keyValueMax = keyValues.get(1);
        } catch (Exception e) {
        }


        for (Integer i = keyValueMin; i <= keyValueMax; i++) {
            ScanStatistics item = new ScanStatistics();
            item.setTime(i);
            data.put(i, item);
        }

        for (int i = 0; i < result.size(); i++) {

            Object[] item = (Object[]) result.get(i);
            ScanStatistics record = initModelFromObject(item);
            data.put((int) record.getTime(), record);
        }

        return data;
    }

    private ScanStatisticsResponse getScanStatistics(StatisticsRequestBody requestBody) {

        StringBuilder queryBuilder = new StringBuilder();

        String groupBy = "hour";
        if (requestBody.getFilter().getStatWidth() != null && !requestBody.getFilter().getStatWidth().isEmpty()) {
            groupBy = requestBody.getFilter().getStatWidth();
        }

        ScanStatisticsResponse response = new ScanStatisticsResponse();

        //.... Get Total Statistics
        queryBuilder.append(getSelectQuery(groupBy) + "\tFROM\n" + getJoinQuery());
        List<String> whereCause = getWhereCause(requestBody);
        if (!whereCause.isEmpty()) {
            queryBuilder.append(" where " + StringUtils.join(whereCause, " and "));
        }
        ScanStatistics totalStatistics = getTotalStatistics(queryBuilder.toString());
        response.setTotalStatistics(totalStatistics);

        //.... Get Detailed Statistics
        queryBuilder.append(" GROUP BY  " + groupBy + "(s.SCAN_START_TIME)");
        TreeMap<Integer, ScanStatistics> detailedStatistics = getDetailedStatistics(queryBuilder.toString(), requestBody);

        try {
            Map<String, Object> paginatedResult = getPaginatedList(detailedStatistics, requestBody);
            response.setFrom(Long.parseLong(paginatedResult.get("from").toString()));
            response.setTo(Long.parseLong(paginatedResult.get("to").toString()));
            response.setDetailedStatistics((TreeMap<Integer, ScanStatistics>) getPaginatedList(detailedStatistics, requestBody).get("list"));
        } catch (Exception e) {
            response.setDetailedStatistics(detailedStatistics);
        }

        if (requestBody.getPerPage() != null && requestBody.getCurrentPage() != null) {
            response.setPer_page(requestBody.getPerPage());
            response.setCurrent_page(requestBody.getCurrentPage());
            try {
                response.setTotal(detailedStatistics.size());
                if (response.getTotal() % response.getPer_page() == 0) {
                    response.setLast_page(response.getTotal() / response.getPer_page());
                } else {
                    response.setLast_page(response.getTotal() / response.getPer_page() + 1);
                }
            } catch (Exception e) {
            }
        }

        return response;

    }

    private Map<String, Object> getPaginatedList(TreeMap<Integer, ScanStatistics> sorted, StatisticsRequestBody requestBody) {
        Map<String, Object> result = new HashMap<>();
        TreeMap<Integer, ScanStatistics> detailedStatistics = new TreeMap<>();

        Integer keyValueMin = 0, keyValueMax = -1;
        List<Integer> keyValues = getKeyValuesforStatistics(requestBody);
        try {
            keyValueMin = keyValues.get(0);
            keyValueMax = keyValues.get(1);
        } catch (Exception e) {
        }

        if (requestBody.getCurrentPage() != null && requestBody.getCurrentPage() != null && requestBody.getCurrentPage() > 0 && requestBody.getPerPage() > 0) {
            Integer from, to;
            from = (requestBody.getCurrentPage() - 1) * requestBody.getPerPage() + keyValueMin;
            to = requestBody.getCurrentPage() * requestBody.getPerPage() - 1 + keyValueMin;

            if (from < keyValueMin) {
                from = keyValueMin;
            }

            if (to > keyValueMax) {
                to = keyValueMax;
            }

            result.put("from", from - keyValueMin + 1);
            result.put("to", to - keyValueMin + 1);

            for (Integer i = from; i <= to; i++) {
                detailedStatistics.put(i, sorted.get(i));
            }
        }

        result.put("list", detailedStatistics);
        return result;
    }

    public List<Integer> getKeyValuesforStatistics(StatisticsRequestBody requestBody) {

        Integer keyValueMin = 1, keyValueMax = 0;
        if (requestBody.getFilter().getStatWidth() != null && !requestBody.getFilter().getStatWidth().isEmpty()) {
            switch (requestBody.getFilter().getStatWidth()) {
                case Constants.StatisticWidth.HOUR:
                    keyValueMin = 0;
                    keyValueMax = 23;
                    break;
                case Constants.StatisticWidth.DAY:
                    keyValueMax = 31;
                    break;
                case Constants.StatisticWidth.WEEK:
                    keyValueMax = 5;
                    break;
                case Constants.StatisticWidth.MONTH:
                    keyValueMax = 12;
                    break;
                case Constants.StatisticWidth.QUARTER:
                    keyValueMax = 4;
                    break;
                case Constants.StatisticWidth.YEAR:
                    Map<String, Integer> availableYearRage = getAvailableYearRange(requestBody);
                    keyValueMax = availableYearRage.get("max");
                    keyValueMin = availableYearRage.get("min");
                    break;
                default:
                    keyValueMin = 0;
                    keyValueMax = -1;
                    break;
            }
        }

        List<Integer> result = new ArrayList<>();
        result.add(keyValueMin);
        result.add(keyValueMax);

        return result;
    }

    private Map<String, Integer> getAvailableYearRange(StatisticsRequestBody requestBody) {

        Integer keyValueMin = 0, keyValueMax = 0;

        Integer yearMax = serScanRepository.findMaxYear();
        Integer yearMin = 1970;
        Calendar calendar = Calendar.getInstance();
        if (requestBody.getFilter().getStartTime() != null) {
            calendar.setTime(requestBody.getFilter().getStartTime());
            keyValueMin = calendar.get(Calendar.YEAR);
        } else {
            keyValueMin = Calendar.getInstance().get(Calendar.YEAR) - 10 + 1;
        }
        if (requestBody.getFilter().getEndTime() != null) {
            calendar.setTime(requestBody.getFilter().getEndTime());
            keyValueMax = calendar.get(Calendar.YEAR);
        } else {
            keyValueMax = Calendar.getInstance().get(Calendar.YEAR);
        }
        if (keyValueMin < yearMin) {
            keyValueMin = yearMin;
        }
        if (keyValueMax > yearMax) {
            keyValueMax = yearMax;
        }

        Map<String, Integer> result = new HashMap<String, Integer>();

        result.put("min", keyValueMin);
        result.put("max", keyValueMax);

        return result;
    }


}
