package com.nuctech.ecuritycheckitem.controllers.taskmanagement.statisticsmanagement;

import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.controllers.taskmanagement.ProcessTaskController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.export.statisticsmanagement.PreviewStatisticsExcelView;
import com.nuctech.ecuritycheckitem.export.statisticsmanagement.PreviewStatisticsPdfView;
import com.nuctech.ecuritycheckitem.export.statisticsmanagement.ScanStatisticsExcelView;
import com.nuctech.ecuritycheckitem.export.statisticsmanagement.ScanStatisticsPdfView;
import com.nuctech.ecuritycheckitem.models.db.*;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.*;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
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
import javax.validation.constraints.NotNull;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.Math.round;

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


    @RequestMapping(value = "/preview", method = RequestMethod.POST)
    public Object previewStatisticsGet(
            @RequestBody @Valid StatisticsRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        TotalStatisticsResponse response = getPreviewStatistics(requestBody);

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, response));

        return value;

    }



    /**
     * Preview Statistics generate pdf file request.
     */
    @RequestMapping(value = "/preview/generate/print", method = RequestMethod.POST)
    public Object previewStatisticsPDFGenerateFile(@RequestBody @Valid StatisticsGenerateRequestBody requestBody,
                                                   BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        StatisticsRequestBody body = requestBody.getFilter();

        TreeMap<Long, TotalStatistics> totalStatistics = getPreviewStatistics(requestBody.getFilter()).getDetailedStatistics();

        TreeMap<Long, TotalStatistics> exportList = getExportList(totalStatistics, requestBody.getIsAll(), requestBody.getIdList());
        PreviewStatisticsPdfView.setResource(res);
        InputStream inputStream = PreviewStatisticsPdfView.buildPDFDocument(exportList);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=previewStatistics.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));
    }

    /**
     * Preview Statistics generate pdf file request.
     */
    @RequestMapping(value = "/preview/generate/export", method = RequestMethod.POST)
    public Object previewStatisticsGenerateExcelFile(@RequestBody @Valid StatisticsGenerateRequestBody requestBody,
                                                     BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        TreeMap<Long, TotalStatistics> totalStatistics = getPreviewStatistics(requestBody.getFilter()).getDetailedStatistics();

        TreeMap<Long, TotalStatistics> exportList = getExportList(totalStatistics, requestBody.getIsAll(), requestBody.getIdList());

        InputStream inputStream = PreviewStatisticsExcelView.buildExcelDocument(exportList);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=previewStatistics.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msexcel"))
                .body(new InputStreamResource(inputStream));
    }

    private TreeMap<Long, TotalStatistics> getExportList(TreeMap<Long, TotalStatistics> detailedStatistics, boolean isAll, String idList) {

        TreeMap<Long, TotalStatistics> exportList = new TreeMap<>();

        if (isAll == false) {
            String[] splits = idList.split(",");

            for (Map.Entry<Long, TotalStatistics> entry : detailedStatistics.entrySet()) {

                TotalStatistics record = entry.getValue();

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


    private String  getSelectQuery() {
        
        return "SELECT\n" +
                "\tq,\n" +
                "\tIFNULL(totalScan, 0),\n" + "\tIFNULL(validScan, 0),\n" + "\tIFNULL(invalidScan, 0),\n" + "\tIFNULL(passedScan, 0),\n" + "\tIFNULL(alarmScan, 0),\n" +
                "\tIFNULL(totalJudge, 0),\n" + "\tIFNULL(suspictionJudge, 0),\n" + "\tIFNULL(noSuspictionJudge, 0),\n" +
                "\tIFNULL(totalHand, 0),\n" + "\tIFNULL(seizureHand, 0),\n" + "\tIFNULL(noSeizureHand, 0) \n" +
                "\tFROM\n" + "\t(\n" +
                "\tSELECT\n" + "\t\tq \n" + "\tFROM\n" +
                "\t\t(\n" +
                "\t\tSELECT DISTINCT \n" +
                "\t\t:scanGroupBy AS q \n" +
                "\t\tFROM\n" +
                "\t\t\tser_scan s UNION\n" +
                "\t\tSELECT DISTINCT \n" +
                "\t\t:judgeGroupBy AS q \n" +
                "\t\tFROM\n" +
                "\t\t\tser_judge_graph j UNION\n" +
                "\t\tSELECT DISTINCT \n" +
                "\t\t:handGroupBy AS q \n" +
                "\t\tFROM\n" +
                "\t\t\tser_hand_examination h \n" +
                "\t\t) AS t00 \n" +
                "\t) AS t0 ";
    }
    
    private String getJoinQuery() {

        return getScanJoinQuery() + getJudgeJoinQuery() + getHandJoinQuery();

    }

    private String getScanJoinQuery() {

        return "LEFT JOIN (\n" +
                "\tSELECT\n" +
                "\t\tcount( SCAN_ID ) AS totalScan,\n" +
                "\t\tsum( IF ( SCAN_INVALID LIKE '" + SerScan.Invalid.TRUE + "', 1, 0 ) ) AS validScan,\n" +
                "\t\tsum( IF ( SCAN_INVALID LIKE '" + SerScan.Invalid.FALSE + "', 1, 0 ) ) AS invalidScan,\n" +
                "\t\tsum( IF ( SCAN_ATR_RESULT LIKE '" + SerScan.ATRResult.TRUE + "', 1, 0 ) ) AS passedScan,\n" +
                "\t\tsum( IF ( SCAN_FOOT_ALARM LIKE '" + SerScan.FootAlarm.TRUE + "', 1, 0 ) ) AS alarmScan,\n" +
                "\t\t:scanGroupBy AS q1 \n" +
                "\tFROM\n" +
                "\t\tser_scan \n" +
                "\t:where\t" +
                "\tGROUP BY\n" +
                "\t\tq1 \n" +
                "\t) AS t1 ON t0.q = t1.q1\t";
    }

    private String getJudgeJoinQuery() {

        return "LEFT JOIN (\n" +
                "\tSELECT\n" +
                "\t\tcount( judge_id ) AS totalJudge,\n" +
                "\t\tsum( IF ( JUDGE_RESULT LIKE '" + SerJudgeGraph.Result.TRUE + "', 1, 0 ) ) AS suspictionJudge,\n" +
                "\t\tsum( IF ( JUDGE_RESULT LIKE '" + SerJudgeGraph.Result.FALSE + "', 1, 0 ) ) AS noSuspictionJudge,\n" +
                "\t\t:judgeGroupBy AS q2 \n" +
                "\tFROM\n" +
                "\t\tser_judge_graph \n" +
                "\t:where\t" +
                "\tGROUP BY\n" +
                "\t\tq2 \n" +
                "\t) AS t2 ON t0.q = t2.q2\t";
    }

    private String getHandJoinQuery() {

        return "LEFT JOIN (\n" +
                "\tSELECT\n" +
                "\t\tcount( HAND_EXAMINATION_ID ) AS totalHand,\n" +
                "\t\tsum( IF ( HAND_RESULT LIKE '" + SerHandExamination.Result.TRUE + "', 1, 0 ) ) AS seizureHand,\n" +
                "\t\tsum( IF ( HAND_RESULT LIKE '" + SerHandExamination.Result.FALSE + "', 1, 0 ) ) AS noSeizureHand,\n" +
                "\t\t:handGroupBy AS q3 \n" +
                "\tFROM\n" +
                "\t\tser_hand_examination \n" +
                "\t:where\t" +
                "\tGROUP BY\n" +
                "\t\tq3 \n" +
                "\t) AS t3 ON t0.q = t3.q3\t";
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

    private TotalStatistics getTotalStatistics(String query) {

        String temp = query.replace(":scanGroupBy", "1");
        temp = temp.replace(":handGroupBy", "1");
        temp = temp.replace(":judgeGroupBy", "1");

        Query jpaQuery = entityManager.createNativeQuery(temp);
        List<Object> resultTotal = jpaQuery.getResultList();

        TotalStatistics record = new TotalStatistics();
        for (int i = 0; i < resultTotal.size(); i++) {
            Object[] item = (Object[]) resultTotal.get(i);
            record = initModelFromObject(item);
        }

        return record;
    }

    private TotalStatistics initModelFromObject(Object[] item) {

        TotalStatistics record = new TotalStatistics();

        try {
            record.setTime(Integer.parseInt(item[0].toString()));
            ScanStatistics scanStat = new ScanStatistics();
            JudgeStatisticsModelForPreview judgeStat = new JudgeStatisticsModelForPreview();
            HandExaminationStatisticsForPreview handStat = new HandExaminationStatisticsForPreview();

            scanStat.setTotalScan(Long.parseLong(item[1].toString()));
            scanStat.setValidScan(Long.parseLong(item[2].toString()));
            scanStat.setInvalidScan(Long.parseLong(item[3].toString()));
            scanStat.setPassedScan(Long.parseLong(item[4].toString()));
            scanStat.setAlarmScan(Long.parseLong(item[5].toString()));
            judgeStat.setTotalJudge(Long.parseLong(item[6].toString()));
            judgeStat.setSuspictionJudge(Long.parseLong(item[7].toString()));
            judgeStat.setNoSuspictionJudge(Long.parseLong(item[8].toString()));
            handStat.setTotalHandExamination(Long.parseLong(item[9].toString()));
            handStat.setSeizureHandExamination(Long.parseLong(item[10].toString()));
            handStat.setNoSeizureHandExamination(Long.parseLong(item[11].toString()));
            scanStat.setValidScanRate(0);
            scanStat.setInvalidScanRate(0);
            scanStat.setPassedScanRate(0);
            scanStat.setAlarmScanRate(0);
            judgeStat.setSuspictionJudgeRate(0);
            judgeStat.setNoSuspictionJudgeRate(0);
            handStat.setSeizureHandExaminationRate(0);
            handStat.setNoSeizureHandExaminationRate(0);
            if (scanStat.getTotalScan() > 0) {
                scanStat.setValidScanRate(scanStat.getValidScan() * 100 / (double)scanStat.getTotalScan());
                scanStat.setInvalidScanRate(scanStat.getInvalidScan() * 100 / (double)scanStat.getTotalScan());
                scanStat.setPassedScanRate(scanStat.getPassedScan() * 100 / (double)scanStat.getTotalScan());
                scanStat.setAlarmScanRate(scanStat.getAlarmScan() * 100 / (double)scanStat.getTotalScan());
            }
            if (judgeStat.getTotalJudge() > 0 ) {
                judgeStat.setSuspictionJudgeRate(judgeStat.getSuspictionJudge() * 100 / (double)judgeStat.getTotalJudge());
                judgeStat.setNoSuspictionJudgeRate(judgeStat.getNoSuspictionJudge() * 100 / (double)judgeStat.getTotalJudge());
            }
            if (handStat.getTotalHandExamination() > 0) {
                handStat.setSeizureHandExaminationRate(handStat.getSeizureHandExamination() * 100 / (double)handStat.getTotalHandExamination());
                handStat.setNoSeizureHandExaminationRate(handStat.getNoSeizureHandExamination() * 100 / (double)handStat.getTotalHandExamination());
            }
            record.setScanStatistics(scanStat);
            record.setJudgeStatistics(judgeStat);
            record.setHandExaminationStatistics(handStat);

        } catch (Exception e) {
        }

        return record;
    }

    private TreeMap<Long, TotalStatistics> getDetailedStatistics(String query, StatisticsRequestBody requestBody) {

        String groupBy = "hour";
        if (requestBody.getFilter().getStatWidth() != null && !requestBody.getFilter().getStatWidth().isEmpty()) {
            groupBy = requestBody.getFilter().getStatWidth();
        }

        String temp = query;
        temp = temp.replace(":scanGroupBy", groupBy + "(SCAN_START_TIME)");
        temp = temp.replace(":judgeGroupBy", groupBy + "(JUDGE_START_TIME)");
        temp = temp.replace(":handGroupBy", groupBy + "(HAND_START_TIME)");

        Query jpaQuery = entityManager.createNativeQuery(temp);
        List<Object> result = jpaQuery.getResultList();
        TreeMap<Long, TotalStatistics> data = new TreeMap<>();

        Integer keyValueMin = 0, keyValueMax = -1;
        List<Integer> keyValues = getKeyValuesforStatistics(requestBody);
        try {
            keyValueMin = keyValues.get(0);
            keyValueMax = keyValues.get(1);
        } catch (Exception e) { }


        for (Integer i = keyValueMin; i <= keyValueMax; i++) {
            TotalStatistics item = new TotalStatistics();
            item.setScanStatistics(new ScanStatistics());
            item.setJudgeStatistics(new JudgeStatisticsModelForPreview());
            item.setHandExaminationStatistics(new HandExaminationStatisticsForPreview());
            item.setTime(i);
            data.put((long)i, item);
        }

        for (int i = 0; i < result.size(); i++) {

            Object[] item = (Object[]) result.get(i);
            TotalStatistics record = initModelFromObject(item);
            data.put(record.getTime(), record);
        }

        return  data;
    }

    public List<Integer> getKeyValuesforStatistics(StatisticsRequestBody requestBody) {

        Integer keyValueMin = 1, keyValueMax = 0;
        if (requestBody.getFilter().getStatWidth() != null && !requestBody.getFilter().getStatWidth().isEmpty()) {
            switch (requestBody.getFilter().getStatWidth()) {
                case ProcessTaskController.StatisticWidth.HOUR:
                    keyValueMin = 0;
                    keyValueMax = 23;
                    break;
                case ProcessTaskController.StatisticWidth.DAY:
                    keyValueMax = 31;
                    break;
                case ProcessTaskController.StatisticWidth.WEEK:
                    keyValueMax = 5;
                    break;
                case ProcessTaskController.StatisticWidth.MONTH:
                    keyValueMax = 12;
                    break;
                case ProcessTaskController.StatisticWidth.QUARTER:
                    keyValueMax = 4;
                    break;
                case ProcessTaskController.StatisticWidth.YEAR:
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

    private Map<String, Object> getPaginatedList(TreeMap<Long, TotalStatistics> sorted, StatisticsRequestBody requestBody) {
        Map<String, Object> result = new HashMap<>();
        TreeMap<Long, TotalStatistics> detailedStatistics = new TreeMap<>();

        Integer keyValueMin = 0, keyValueMax = -1;
        List<Integer> keyValues = getKeyValuesforStatistics(requestBody);
        try {
            keyValueMin = keyValues.get(0);
            keyValueMax = keyValues.get(1);
        } catch (Exception e) { }

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
                detailedStatistics.put((long)i, sorted.get(i));
            }
        }

        result.put("list", detailedStatistics);
        return result;
    }

    private String makeQuery() {

        return getSelectQuery() + getJoinQuery();

    }

    private TotalStatisticsResponse getPreviewStatistics(StatisticsRequestBody requestBody) {

        StringBuilder whereBuilder = new StringBuilder();



        TotalStatisticsResponse response = new TotalStatisticsResponse();
        List<String> whereCause = getWhereCause(requestBody);
        if (!whereCause.isEmpty()) {
            whereBuilder.append(" where " + StringUtils.join(whereCause, " and "));
        }

        StringBuilder queryBuilder = new StringBuilder();
        //.... Get Total Statistics
        queryBuilder.append(makeQuery().replace(":where", whereBuilder.toString()));
        String strOriginalQuery = queryBuilder.toString();



        TotalStatistics totalStatistics = getTotalStatistics(strOriginalQuery);
        response.setTotalStatistics(totalStatistics);

        //.... Get Detailed Statistics
        TreeMap<Long, TotalStatistics> detailedStatistics = getDetailedStatistics(strOriginalQuery, requestBody);

        try {
            Map<String, Object> paginatedResult = getPaginatedList(detailedStatistics, requestBody);
            response.setFrom(Long.parseLong(paginatedResult.get("from").toString()));
            response.setTo(Long.parseLong(paginatedResult.get("to").toString()));
            response.setDetailedStatistics((TreeMap<Long, TotalStatistics>)getPaginatedList(detailedStatistics, requestBody).get("list"));
        }
        catch (Exception e) {
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
            } catch (Exception e) { }
        }

        return response;

    }
}
