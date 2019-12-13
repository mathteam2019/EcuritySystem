package com.nuctech.ecuritycheckitem.controllers.taskmanagement.statisticsmanagement;

import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.controllers.taskmanagement.ProcessTaskController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.export.statisticsmanagement.UserOrDeviceStatisticsExcelView;
import com.nuctech.ecuritycheckitem.export.statisticsmanagement.UserOrDeviceStatisticsPdfView;
import com.nuctech.ecuritycheckitem.models.db.*;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.*;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import org.apache.commons.collections4.IterableUtils;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/task/statistics/")
public class StatisticsbyUserOrDeviceController extends BaseController {

    /**
     * Statistics By User RequestBody
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class StatisticsByUserRequestBody {

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        static class Filter {

            Long modeId;
            String userName;
            @DateTimeFormat(style = Constants.DATETIME_FORMAT)
            Date startTime;
            @DateTimeFormat(style = Constants.DATETIME_FORMAT)
            Date endTime;

        }

        Integer currentPage;
        Integer perPage;

        StatisticsByUserRequestBody.Filter filter;

    }

    /**
     * Statistics By Device RequestBody
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class StatisticsByDeviceRequestBody {

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        static class Filter {

            Long deviceCategoryId;
            Long deviceId;
            @DateTimeFormat(style = Constants.DATETIME_FORMAT)
            Date startTime;
            @DateTimeFormat(style = Constants.DATETIME_FORMAT)
            Date endTime;

        }

        Integer currentPage;
        Integer perPage;
        StatisticsByDeviceRequestBody.Filter filter;

    }


    /**
     * preview statistics generate request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class StatisticsByDeviceGenerateRequestBody {

        String idList;
        @NotNull
        Boolean isAll;

        StatisticsByDeviceRequestBody filter;
    }

    /**
     * preview statistics generate request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class StatisticsByUserGenerateRequestBody {

        String idList;
        @NotNull
        Boolean isAll;

        StatisticsByUserRequestBody filter;
    }

    private BooleanBuilder getScanPredicateByUser(StatisticsByUserRequestBody requestBody) {
        QSerScan scanbuilder = QSerScan.serScan;
        BooleanBuilder predicateScan = new BooleanBuilder(scanbuilder.isNotNull());

        StatisticsByUserRequestBody.Filter filter = requestBody.getFilter();
        predicateScan.and(scanbuilder.scanPointsman.isNotNull());
        if (filter != null) {
            if (filter.getUserName() != null && !filter.getUserName().isEmpty()) {
                predicateScan.and(scanbuilder.scanPointsman.userName.contains(filter.getUserName()));
            }
            if (filter.getStartTime() != null) {
                predicateScan.and(scanbuilder.scanStartTime.after(filter.getStartTime()));
            }
            if (filter.getEndTime() != null) {
                predicateScan.and(scanbuilder.scanEndTime.before(filter.getEndTime()));
            }
        }

        return predicateScan;
    }

    private BooleanBuilder getJudgePredicateByUser(StatisticsByUserRequestBody requestBody) {

        QSerJudgeGraph judgeBuilder = QSerJudgeGraph.serJudgeGraph;
        BooleanBuilder predicateJudge = new BooleanBuilder(judgeBuilder.isNotNull());
        predicateJudge.and(judgeBuilder.judgeUser.isNotNull());
        StatisticsByUserRequestBody.Filter filter = requestBody.getFilter();

        if (filter != null) {

            if (filter.getUserName() != null && !filter.getUserName().isEmpty()) {
                predicateJudge.and(judgeBuilder.judgeUser.userName.contains(filter.getUserName()));
            }
            if (filter.getStartTime() != null) {
                predicateJudge.and(judgeBuilder.judgeStartTime.after(filter.getStartTime()));
            }
            if (filter.getEndTime() != null) {
                predicateJudge.and(judgeBuilder.judgeEndTime.before(filter.getEndTime()));
            }
        }

        return predicateJudge;
    }

    private BooleanBuilder getHandPredicateByUser(StatisticsByUserRequestBody requestBody) {
        QSerHandExamination handBuilder = QSerHandExamination.serHandExamination;
        BooleanBuilder predicateHand = new BooleanBuilder(handBuilder.isNotNull());

        predicateHand.and(handBuilder.handUser.isNotNull());
        StatisticsByUserRequestBody.Filter filter = requestBody.getFilter();

        if (filter != null) {
            if (filter.getUserName() != null && !filter.getUserName().isEmpty()) {
                predicateHand.and(handBuilder.handUser.userName.contains(filter.getUserName()));
            }
            if (filter.getStartTime() != null) {
                predicateHand.and(handBuilder.handEndTime.after(filter.getStartTime()));
            }

            if (filter.getEndTime() != null) {
                predicateHand.and(handBuilder.handEndTime.before(filter.getEndTime()));
            }
        }

        return predicateHand;
    }

    private TotalStatisticsResponse getStatisticsByUser(StatisticsByUserRequestBody requestBody) {

        BooleanBuilder predicateScan = getScanPredicateByUser(requestBody);
        BooleanBuilder predicateJudge = getJudgePredicateByUser(requestBody);
        BooleanBuilder predicateHand = getHandPredicateByUser(requestBody);

        Map<Long, List<SerScan>> scans = IterableUtils.toList(serScanRepository.findAll(predicateScan)).stream().collect(Collectors.groupingBy(SerScan::getScanPointsmanId, Collectors.toList()));
        Map<Long, List<SerJudgeGraph>> judges = IterableUtils.toList(serJudgeGraphRepository.findAll(predicateJudge)).stream().collect(Collectors.groupingBy(SerJudgeGraph::getJudgeUserId, Collectors.toList()));
        Map<Long, List<SerHandExamination>> handExaminations = IterableUtils.toList(serHandExaminationRepository.findAll(predicateHand)).stream().collect(Collectors.groupingBy(SerHandExamination::getHandUserId, Collectors.toList()));

        TreeMap<Long, TotalStatistics> listTotalStatistics = new TreeMap<Long, TotalStatistics>();

        for (Map.Entry<Long, List<SerScan>> entry : scans.entrySet()) {
            Long userId = entry.getKey();
            List<SerScan> listScans = entry.getValue();

            TotalStatistics totalStat = new TotalStatistics();
            ScanStatistics scanStat = new ScanStatistics();

            long validScan = 0;
            long invalidScan = 0;
            double workingSeconds = 0;

            String strName = "";

            boolean userNullFlag = false;

            for (int i = 0; i < listScans.size(); i++) {

                if (listScans.get(i).getScanPointsman() == null) {
                    userNullFlag = true;
                    break;
                }

                workingSeconds += (listScans.get(i).getScanEndTime().getTime() - listScans.get(i).getScanStartTime().getTime()) / 1000.0;

                try {

                    strName = listScans.get(i).getScanPointsman().getUserName();

                } catch (Exception e) {

                }

                if (listScans.get(i).getScanInvalid().equals(SerScan.Invalid.TRUE)) {

                    validScan++;

                }

                if (listScans.get(i).getScanInvalid().equals(SerScan.Invalid.FALSE)) {

                    invalidScan++;

                }
            }

            if (userNullFlag) {
                continue;
            }
            scanStat.setValidScan(validScan);
            scanStat.setInvalidScan(invalidScan);
            scanStat.setTotalScan(listScans.size());
            scanStat.setWorkingSeconds(workingSeconds);

            if (listScans.size() > 0) {
                scanStat.setValidScanRate(scanStat.getValidScan() * 100 / (double) scanStat.getTotalScan());
                scanStat.setInvalidScanRate(scanStat.getInvalidScan() * 100 / (double) scanStat.getTotalScan());
            }

            totalStat.setName(strName);
            totalStat.setScanStatistics(scanStat);

            listTotalStatistics.put(userId, totalStat);

        }

        for (Map.Entry<Long, List<SerJudgeGraph>> entry : judges.entrySet()) {

            Long userId = entry.getKey();
            List<SerJudgeGraph> listJudge = entry.getValue();

            TotalStatistics totalStat = new TotalStatistics();
            JudgeStatisticsModelForPreview judgeStat = new JudgeStatisticsModelForPreview();

            long suspiction = 0;
            long noSuspiction = 0;
            double workingSeconds = 0;
            String strName = "";

            boolean userNullFlag = false;

            for (int i = 0; i < listJudge.size(); i++) {

                if (listJudge.get(i).getJudgeUser() == null) {
                    userNullFlag = true;
                    break;
                }

                workingSeconds += (listJudge.get(i).getJudgeEndTime().getTime() - listJudge.get(i).getJudgeStartTime().getTime()) / 1000.0;
                try {
                    strName = listJudge.get(i).getJudgeUser().getUserName();
                } catch (Exception e) {

                }

                if (listJudge.get(i).getJudgeResult().equals(SerJudgeGraph.Result.TRUE)) {

                    suspiction++;

                }

                if (listJudge.get(i).getJudgeResult().equals(SerJudgeGraph.Result.TRUE)) {

                    noSuspiction++;

                }
            }

            if (userNullFlag) {
                continue;
            }

            judgeStat.setSuspictionJudge(suspiction);
            judgeStat.setNoSuspictionJudge(noSuspiction);
            judgeStat.setTotalJudge(listJudge.size());
            judgeStat.setWorkingSeconds(workingSeconds);

            if (listJudge.size() > 0) {
                judgeStat.setSuspictionJudgeRate(judgeStat.getSuspictionJudge() * 100 / (double) judgeStat.getTotalJudge());
                judgeStat.setNoSuspictionJudgeRate(judgeStat.getNoSuspictionJudge() * 100 / (double) judgeStat.getTotalJudge());
            }

            if (listTotalStatistics.containsKey(userId)) {

                listTotalStatistics.get(userId).setJudgeStatistics(judgeStat);

            } else {

                totalStat.setJudgeStatistics(judgeStat);
                totalStat.setName(strName);
                listTotalStatistics.put(userId, totalStat);

            }

        }

        for (Map.Entry<Long, List<SerHandExamination>> entry : handExaminations.entrySet()) {

            Long userId = entry.getKey();
            List<SerHandExamination> listHand = entry.getValue();

            TotalStatistics totalStat = new TotalStatistics();
            HandExaminationStatisticsForPreview handStat = new HandExaminationStatisticsForPreview();

            long seizure = 0;
            long noSeizure = 0;
            double workingSeconds = 0;
            String strName = "";

            boolean userNullFlag = false;

            for (int i = 0; i < listHand.size(); i++) {

                if (listHand.get(i).getHandUser() == null) {
                    userNullFlag = true;
                    break;
                }

                workingSeconds += (listHand.get(i).getHandEndTime().getTime() - listHand.get(i).getHandStartTime().getTime()) / 1000.0;

                try {
                    strName = listHand.get(i).getHandUser().getUserName();
                } catch (Exception e) {

                }

                if (listHand.get(i).getHandResult().equals(SerHandExamination.Result.TRUE)) {

                    seizure++;

                }

                if (listHand.get(i).getHandResult().equals(SerHandExamination.Result.TRUE)) {

                    noSeizure++;

                }
            }

            if (userNullFlag) {
                continue;
            }

            handStat.setSeizureHandExamination(seizure);
            handStat.setNoSeizureHandExamination(noSeizure);
            handStat.setWorkingSeconds(workingSeconds);
            handStat.setTotalHandExamination(listHand.size());

            if (listHand.size() > 0) {
                handStat.setSeizureHandExaminationRate(seizure * 100 / (double) handStat.getTotalHandExamination());
                handStat.setNoSeizureHandExaminationRate(noSeizure * 100 / (double) handStat.getTotalHandExamination());
            }

            if (listTotalStatistics.containsKey(userId)) {

                listTotalStatistics.get(userId).setHandExaminationStatistics(handStat);


            } else {

                totalStat.setHandExaminationStatistics(handStat);
                totalStat.setName(strName);
                listTotalStatistics.put(userId, totalStat);

            }

        }

        Map<String, Object> paginatedList = getPaginatedList(listTotalStatistics, requestBody.getCurrentPage(), requestBody.getPerPage());
        TreeMap<Long, TotalStatistics> subList = null;
        try {
            subList = (TreeMap<Long, TotalStatistics>) paginatedList.get("list");
        } catch (Exception e) {

        }

        TotalStatisticsResponse response = new TotalStatisticsResponse();

        if (subList == null) {
            response.setDetailedStatistics(listTotalStatistics);
        } else {
            response.setDetailedStatistics(subList);
            response.setTotal(listTotalStatistics.keySet().size());
            response.setFrom(Long.parseLong(paginatedList.get("from").toString()));
            response.setTo(Long.parseLong(paginatedList.get("to").toString()));
            response.setLast_page(Long.parseLong(paginatedList.get("lastpage").toString()));
            response.setCurrent_page(requestBody.getCurrentPage());
            response.setPer_page(requestBody.getPerPage());
        }


        return response;

    }

    private TotalStatisticsResponse getStatisticsByDevice(StatisticsByDeviceRequestBody requestBody) {

        QSerScan scanbuilder = QSerScan.serScan;
        QSerJudgeGraph judgeBuilder = QSerJudgeGraph.serJudgeGraph;
        QSerHandExamination handBuilder = QSerHandExamination.serHandExamination;

        BooleanBuilder predicateScan = new BooleanBuilder(scanbuilder.isNotNull());
        BooleanBuilder predicateJudge = new BooleanBuilder(judgeBuilder.isNotNull());
        BooleanBuilder predicateHand = new BooleanBuilder(handBuilder.isNotNull());

        predicateScan.and(scanbuilder.scanDevice.isNotNull());
        predicateJudge.and(judgeBuilder.judgeDevice.isNotNull());
        predicateHand.and(handBuilder.handDevice.isNotNull());

        StatisticsByDeviceRequestBody.Filter filter = requestBody.getFilter();

        if (filter != null) {

            if (filter.getDeviceId() != null) {

                predicateScan.and(scanbuilder.scanDeviceId.eq(filter.getDeviceId()));
                predicateJudge.and(judgeBuilder.judgeDeviceId.eq(filter.getDeviceId()));
                predicateHand.and(handBuilder.handDeviceId.eq(filter.getDeviceId()));

            }
            if (filter.getStartTime() != null) {

                predicateScan.and(scanbuilder.scanStartTime.after(filter.getStartTime()));
                predicateJudge.and(judgeBuilder.judgeStartTime.after(filter.getStartTime()));
                predicateHand.and(handBuilder.handStartTime.after(filter.getStartTime()));

            }

            if (filter.getEndTime() != null) {

                predicateScan.and(scanbuilder.scanEndTime.before(filter.getEndTime()));
                predicateJudge.and(judgeBuilder.judgeEndTime.before(filter.getEndTime()));
                predicateHand.and(handBuilder.handEndTime.before(filter.getEndTime()));

            }

        }

        Map<Long, List<SerScan>> scans = IterableUtils.toList(serScanRepository.findAll(predicateScan)).stream().collect(Collectors.groupingBy(SerScan::getScanDeviceId, Collectors.toList()));
        Map<Long, List<SerJudgeGraph>> judges = IterableUtils.toList(serJudgeGraphRepository.findAll(predicateJudge)).stream().collect(Collectors.groupingBy(SerJudgeGraph::getJudgeDeviceId, Collectors.toList()));
        Map<Long, List<SerHandExamination>> handExaminations = IterableUtils.toList(serHandExaminationRepository.findAll(predicateHand)).stream().collect(Collectors.groupingBy(SerHandExamination::getHandDeviceId, Collectors.toList()));

        TreeMap<Long, TotalStatistics> listTotalStatistics = new TreeMap<Long, TotalStatistics>();

        for (Map.Entry<Long, List<SerScan>> entry : scans.entrySet()) {

            Long deviceId = entry.getKey();
            List<SerScan> listScans = entry.getValue();

            TotalStatistics totalStat = new TotalStatistics();
            ScanStatistics scanStat = new ScanStatistics();

            long validScan = 0;
            long invalidScan = 0;
            double workingSeconds = 0;
            String strName = "";

            boolean deviceNullFlag = false;

            for (int i = 0; i < listScans.size(); i++) {

                if (listScans.get(i).getScanDevice() == null) {
                    deviceNullFlag = true;
                    break;
                }

                workingSeconds += (listScans.get(i).getScanEndTime().getTime() - listScans.get(i).getScanStartTime().getTime()) / 1000.0;

                try {
                    strName = listScans.get(i).getScanDevice().getDeviceName();
                } catch (Exception e) {

                }

                if (listScans.get(i).getScanInvalid().equals(SerScan.Invalid.TRUE)) {

                    validScan++;

                }

                if (listScans.get(i).getScanInvalid().equals(SerScan.Invalid.FALSE)) {

                    invalidScan++;

                }
            }

            if (deviceNullFlag) {
                continue;
            }

            scanStat.setValidScan(validScan);
            scanStat.setInvalidScan(invalidScan);
            scanStat.setTotalScan(listScans.size());
            scanStat.setWorkingSeconds(workingSeconds);

            if (listScans.size() > 0) {
                scanStat.setValidScanRate(scanStat.getValidScan() * 100 / (double) scanStat.getTotalScan());
                scanStat.setInvalidScanRate(scanStat.getInvalidScan() * 100 / (double) scanStat.getTotalScan());
            }
            totalStat.setName(strName);
            totalStat.setScanStatistics(scanStat);
            listTotalStatistics.put(deviceId, totalStat);

        }

        for (Map.Entry<Long, List<SerJudgeGraph>> entry : judges.entrySet()) {

            Long deviceId = entry.getKey();
            List<SerJudgeGraph> listJudge = entry.getValue();

            TotalStatistics totalStat = new TotalStatistics();
            JudgeStatisticsModelForPreview judgeStat = new JudgeStatisticsModelForPreview();

            long suspiction = 0;
            long noSuspiction = 0;
            long workingSeconds = 0;

            String strName = "";

            boolean deviceNullFlag = false;

            for (int i = 0; i < listJudge.size(); i++) {

                if (listJudge.get(i).getJudgeDevice() == null) {
                    deviceNullFlag = true;
                    break;
                }

                workingSeconds += (listJudge.get(i).getJudgeEndTime().getTime() - listJudge.get(i).getJudgeStartTime().getTime()) / 1000.0;
                try {
                    strName = listJudge.get(i).getJudgeDevice().getDeviceName();
                } catch (Exception e) {

                }


                if (listJudge.get(i).getJudgeResult().equals(SerJudgeGraph.Result.TRUE)) {

                    suspiction++;

                }

                if (listJudge.get(i).getJudgeResult().equals(SerJudgeGraph.Result.TRUE)) {

                    noSuspiction++;

                }
            }

            if (deviceNullFlag) {
                continue;
            }

            judgeStat.setSuspictionJudge(suspiction);
            judgeStat.setNoSuspictionJudge(noSuspiction);
            judgeStat.setTotalJudge(listJudge.size());
            judgeStat.setWorkingSeconds(workingSeconds);

            if (listJudge.size() > 0) {
                judgeStat.setSuspictionJudgeRate(judgeStat.getSuspictionJudge() * 100 / (double) judgeStat.getTotalJudge());
                judgeStat.setNoSuspictionJudgeRate(judgeStat.getNoSuspictionJudge() * 100 / (double) judgeStat.getTotalJudge());
            }

            if (listTotalStatistics.containsKey(deviceId)) {

                listTotalStatistics.get(deviceId).setJudgeStatistics(judgeStat);

            } else {

                totalStat.setJudgeStatistics(judgeStat);
                totalStat.setName(strName);
                listTotalStatistics.put(deviceId, totalStat);

            }

        }

        for (Map.Entry<Long, List<SerHandExamination>> entry : handExaminations.entrySet()) {

            Long deviceId = entry.getKey();
            List<SerHandExamination> listHand = entry.getValue();

            TotalStatistics totalStat = new TotalStatistics();
            HandExaminationStatisticsForPreview handStat = new HandExaminationStatisticsForPreview();

            long seizure = 0;
            long noSeizure = 0;
            long workingSeconds = 0;
            String strName = "";

            boolean deviceNullFlag = false;

            for (int i = 0; i < listHand.size(); i++) {

                if (listHand.get(i).getHandDevice() == null) {
                    deviceNullFlag = true;
                    break;
                }

                workingSeconds += (listHand.get(i).getHandEndTime().getTime() - listHand.get(i).getHandStartTime().getTime()) / 1000.0;
                try {
                    strName = listHand.get(i).getHandDevice().getDeviceName();
                } catch (Exception e) {

                }


                if (listHand.get(i).getHandResult().equals(SerHandExamination.Result.TRUE)) {

                    seizure++;

                }

                if (listHand.get(i).getHandResult().equals(SerHandExamination.Result.TRUE)) {

                    noSeizure++;

                }
            }

            if (deviceNullFlag) {
                continue;
            }

            handStat.setSeizureHandExamination(seizure);
            handStat.setNoSeizureHandExamination(noSeizure);
            handStat.setTotalHandExamination(listHand.size());
            handStat.setWorkingSeconds(workingSeconds);

            if (listHand.size() > 0) {
                handStat.setSeizureHandExaminationRate(seizure * 100 / (double) handStat.getTotalHandExamination());
                handStat.setNoSeizureHandExaminationRate(noSeizure * 100 / (double) handStat.getTotalHandExamination());
            }

            if (listTotalStatistics.containsKey(deviceId)) {

                listTotalStatistics.get(deviceId).setHandExaminationStatistics(handStat);

            } else {

                totalStat.setHandExaminationStatistics(handStat);
                totalStat.setName(strName);
                listTotalStatistics.put(deviceId, totalStat);

            }

        }

        Map<String, Object> paginatedList = getPaginatedList(listTotalStatistics, requestBody.getCurrentPage(), requestBody.getPerPage());
        TreeMap<Long, TotalStatistics> subList = null;
        try {
            subList = (TreeMap<Long, TotalStatistics>) paginatedList.get("list");
        } catch (Exception e) {

        }

        TotalStatisticsResponse response = new TotalStatisticsResponse();

        if (subList == null) {
            response.setDetailedStatistics(listTotalStatistics);
        } else {
            response.setDetailedStatistics(subList);
            response.setTotal(listTotalStatistics.keySet().size());
            response.setFrom(Long.parseLong(paginatedList.get("from").toString()));
            response.setTo(Long.parseLong(paginatedList.get("to").toString()));
            response.setLast_page(Long.parseLong(paginatedList.get("lastpage").toString()));
            response.setCurrent_page(requestBody.getCurrentPage());
            response.setPer_page(requestBody.getPerPage());
        }

        return response;

    }

    Map<String, Object> getPaginatedList(TreeMap<Long, TotalStatistics> list, Integer currentPage, Integer perPage) {

        HashMap<String, Object> paginationResult = new HashMap<String, Object>();
        TreeMap<Long, TotalStatistics> subList = new TreeMap<>();

        if (currentPage == null || perPage == null) {
            return null;
        }

        if (currentPage < 0 || perPage < 0) {
            return null;
        }

        Integer from = (currentPage - 1) * perPage + 1;
        Integer to = (currentPage) * perPage;

        if (from > list.size()) {
            return null;
        } else if (to > list.size()) {
            to = list.size();
        }

        paginationResult.put("from", from);
        paginationResult.put("to", to);
        paginationResult.put("total", list.size());

        if (list.size() % perPage == 0) {
            paginationResult.put("lastpage", list.size() / perPage);
        } else {
            paginationResult.put("lastpage", list.size() / perPage + 1);
        }

        int index = 0;
        for (Map.Entry<Long, TotalStatistics> entry : list.entrySet()) {
            if (index >= from - 1 && index <= to - 1) {
                subList.put(entry.getKey(), entry.getValue());
            }
            index++;
        }

        paginationResult.put("list", subList);

        return paginationResult;
    }


    @RequestMapping(value = "/get-statistics-filter-by-user", method = RequestMethod.POST)
    public Object getStatisticsByUserSummary(
            @RequestBody @Valid StatisticsByUserRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        //TotalStatisticsResponse response = getStatisticsByUser(requestBody);
        TotalStatisticsResponse response = userStatisticsService.getStatistics(
                requestBody.getFilter().getModeId(),
                requestBody.getFilter().getUserName(),
                requestBody.getFilter().getStartTime(),
                requestBody.getFilter().getEndTime(),
                requestBody.getCurrentPage(),
                requestBody.getPerPage());

        return new CommonResponseBody(ResponseMessage.OK, response);

    }

    @RequestMapping(value = "/get-statistics-filter-by-device", method = RequestMethod.POST)
    public Object getStatisticsByDeviceSummary(
            @RequestBody @Valid StatisticsByDeviceRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        TotalStatisticsResponse response = getStatisticsByDevice(requestBody);

        return new CommonResponseBody(ResponseMessage.OK, response);

    }

    @RequestMapping(value = "/get-by-user-sum", method = RequestMethod.POST)
    public Object statisticsByUserGet(
            @RequestBody @Valid StatisticsByUserRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        long totalSecs = 0;
        long scanSecs = 0;
        long judgeSecs = 0;
        long handSecs = 0;

        scanSecs = getWorkingSecondsByUser(requestBody, ProcessTaskController.TableType.SER_SCAN);
        judgeSecs = getWorkingSecondsByUser(requestBody, ProcessTaskController.TableType.SER_JUDGE_GRAPH);
        handSecs = getWorkingSecondsByUser(requestBody, ProcessTaskController.TableType.SER_HAND_EXAMINATION);

        totalSecs = scanSecs + judgeSecs + handSecs;

        StatisticsByUserResponse response = new StatisticsByUserResponse();
        response.setTotalSeconds(totalSecs);
        response.setScanSeconds(scanSecs);
        response.setJudgeSeconds(judgeSecs);
        response.setHandSeconds(handSecs);

        return new CommonResponseBody(ResponseMessage.OK, response);

    }

    @RequestMapping(value = "/get-by-device-sum", method = RequestMethod.POST)
    public Object statisticsByDeviceGet(
            @RequestBody @Valid StatisticsByDeviceRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        long totalSecs = 0;
        long scanSecs = 0;
        long judgeSecs = 0;
        long handSecs = 0;

        scanSecs = getWorkingSecondsByDevice(requestBody, ProcessTaskController.TableType.SER_SCAN);
        judgeSecs = getWorkingSecondsByDevice(requestBody, ProcessTaskController.TableType.SER_JUDGE_GRAPH);
        handSecs = getWorkingSecondsByDevice(requestBody, ProcessTaskController.TableType.SER_HAND_EXAMINATION);

        totalSecs = scanSecs + judgeSecs + handSecs;

        StatisticsByUserResponse response = new StatisticsByUserResponse();
        response.setTotalSeconds(totalSecs);
        response.setScanSeconds(scanSecs);
        response.setJudgeSeconds(judgeSecs);
        response.setHandSeconds(handSecs);

        return new CommonResponseBody(ResponseMessage.OK, response);

    }

    /**
     * Evaluate Statistics generate pdf file request.
     */
    @RequestMapping(value = "/userstatistics/generate/print", method = RequestMethod.POST)
    public Object userStatisticsPDFGenerateFile(@RequestBody @Valid StatisticsByUserGenerateRequestBody requestBody,
                                                BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        TreeMap<Long, TotalStatistics> totalStatistics = getStatisticsByUser(requestBody.getFilter()).getDetailedStatistics();

        TreeMap<Long, TotalStatistics> exportList = getExportList(totalStatistics, requestBody.getIsAll(), requestBody.getIdList());
        UserOrDeviceStatisticsPdfView.setResource(res);
        InputStream inputStream = UserOrDeviceStatisticsPdfView.buildPDFDocument(exportList, true);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=statisticsbyuser.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));
    }

    /**
     * EvaluateJudge Statistics generate pdf file request.
     */
    @RequestMapping(value = "/userstatistics/generate/export", method = RequestMethod.POST)
    public Object userStatisticsGenerateExcelFile(@RequestBody @Valid StatisticsByUserGenerateRequestBody requestBody,
                                                  BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        TreeMap<Long, TotalStatistics> userStatistics = getStatisticsByUser(requestBody.getFilter()).getDetailedStatistics();

        TreeMap<Long, TotalStatistics> exportList = getExportList(userStatistics, requestBody.getIsAll(), requestBody.getIdList());

        InputStream inputStream = UserOrDeviceStatisticsExcelView.buildExcelDocument(exportList, true);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=statisticsByUser.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msexcel"))
                .body(new InputStreamResource(inputStream));
    }

    /**
     * Evaluate Statistics generate pdf file request.
     */
    @RequestMapping(value = "/devicestatistics/generate/print", method = RequestMethod.POST)
    public Object deviceStatisticsPDFGenerateFile(@RequestBody @Valid StatisticsByDeviceGenerateRequestBody requestBody,
                                                  BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        TreeMap<Long, TotalStatistics> totalStatistics = getStatisticsByDevice(requestBody.getFilter()).getDetailedStatistics();

        TreeMap<Long, TotalStatistics> exportList = getExportList(totalStatistics, requestBody.getIsAll(), requestBody.getIdList());
        UserOrDeviceStatisticsPdfView.setResource(res);
        InputStream inputStream = UserOrDeviceStatisticsPdfView.buildPDFDocument(exportList, false);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=statisticsbydevice.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));
    }

    /**
     * EvaluateJudge Statistics generate pdf file request.
     */
    @RequestMapping(value = "/devicestatistics/generate/export", method = RequestMethod.POST)
    public Object userStatisticsGenerateExcelFile(@RequestBody @Valid StatisticsByDeviceGenerateRequestBody requestBody,
                                                  BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        TreeMap<Long, TotalStatistics> deviceStatistics = getStatisticsByDevice(requestBody.getFilter()).getDetailedStatistics();

        TreeMap<Long, TotalStatistics> exportList = getExportList(deviceStatistics, requestBody.getIsAll(), requestBody.getIdList());

        InputStream inputStream = UserOrDeviceStatisticsExcelView.buildExcelDocument(exportList, false);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=statisticsByDevice.xlsx");

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

    public long getWorkingSecondsByUser(StatisticsByUserRequestBody requestBody, ProcessTaskController.TableType tbType) {

        QSerTask serTask = QSerTask.serTask;
        BooleanBuilder predicate = new BooleanBuilder(serTask.isNotNull());
        StatisticsByUserRequestBody.Filter filter = requestBody.getFilter();

        if (filter != null) {
            if (filter.getModeId() != null) {
                predicate.and(serTask.workFlow.workMode.modeId.eq(filter.getModeId()));
            }
            if (filter.getUserName() != null && !filter.getUserName().isEmpty()) {
                if (tbType == ProcessTaskController.TableType.SER_SCAN) {
                    Predicate scanUserName = serTask.serScan.scanPointsman.userName.contains(filter.getUserName());
                    predicate.and(scanUserName);
                } else if (tbType == ProcessTaskController.TableType.SER_JUDGE_GRAPH) {
                    Predicate judgeUserName = serTask.serJudgeGraph.judgeUser.userName.contains(filter.getUserName());
                    predicate.and(judgeUserName);
                } else if (tbType == ProcessTaskController.TableType.SER_HAND_EXAMINATION) {
                    Predicate handUserName = serTask.serHandExamination.handUser.userName.contains(filter.getUserName());
                    predicate.and(handUserName);
                }
            }
            if (filter.getStartTime() != null) {
                if (tbType == ProcessTaskController.TableType.SER_SCAN) {
                    predicate.and(serTask.serScan.scanStartTime.after(filter.getStartTime()));
                } else if (tbType == ProcessTaskController.TableType.SER_JUDGE_GRAPH) {
                    predicate.and(serTask.serJudgeGraph.judgeStartTime.after(filter.getStartTime()));
                } else {
                    predicate.and(serTask.serHandExamination.handStartTime.after(filter.getStartTime()));
                }
            }
            if (filter.getEndTime() != null) {
                if (tbType == ProcessTaskController.TableType.SER_SCAN) {
                    predicate.and(serTask.serScan.scanEndTime.before(filter.getEndTime()));
                } else if (tbType == ProcessTaskController.TableType.SER_JUDGE_GRAPH) {
                    predicate.and(serTask.serJudgeGraph.judgeEndTime.before(filter.getEndTime()));
                } else {
                    predicate.and(serTask.serHandExamination.handEndTime.before(filter.getEndTime()));
                }
            }
        }

        Iterable<SerTask> listTasks = serTaskRespository.findAll(predicate);

        long workingSeconds = 0;


        for (SerTask item : listTasks) {

            if (tbType == ProcessTaskController.TableType.SER_SCAN) {

                try {
                    workingSeconds += (item.getSerScan().getScanEndTime().getTime() - item.getSerScan().getScanStartTime().getTime()) / 1000;
                } catch (Exception e) {

                }

            } else if (tbType == ProcessTaskController.TableType.SER_JUDGE_GRAPH) {

                try {
                    workingSeconds += (item.getSerJudgeGraph().getJudgeEndTime().getTime() - item.getSerJudgeGraph().getJudgeStartTime().getTime()) / 1000;
                } catch (Exception e) {

                }

            } else {
                try {
                    workingSeconds += (item.getSerHandExamination().getHandEndTime().getTime() - item.getSerHandExamination().getHandStartTime().getTime()) / 1000;
                } catch (Exception e) {

                }

            }


        }

        return workingSeconds;

    }

    public long getWorkingSecondsByDevice(StatisticsByDeviceRequestBody requestBody, ProcessTaskController.TableType tbType) {

        QSerTask serTask = QSerTask.serTask;

        BooleanBuilder predicate = new BooleanBuilder(serTask.isNotNull());

        StatisticsByDeviceRequestBody.Filter filter = requestBody.getFilter();

        if (filter != null) {

            if (filter.getDeviceCategoryId() != null) {

                if (tbType == ProcessTaskController.TableType.SER_SCAN) {

                    Predicate scanDeviceCategory = serTask.serScan.scanDevice.categoryId.eq(filter.getDeviceCategoryId());
                    predicate.and(scanDeviceCategory);

                } else if (tbType == ProcessTaskController.TableType.SER_JUDGE_GRAPH) {

                    Predicate judgeDeviceCategory = serTask.serJudgeGraph.judgeDevice.categoryId.eq(filter.getDeviceCategoryId());
                    predicate.and(judgeDeviceCategory);

                } else if (tbType == ProcessTaskController.TableType.SER_HAND_EXAMINATION) {

                    Predicate handDeviceCategory = serTask.serHandExamination.handDevice.categoryId.eq(filter.getDeviceCategoryId());
                    predicate.and(handDeviceCategory);

                }
            }
            if (filter.getDeviceId() != null) {

                if (tbType == ProcessTaskController.TableType.SER_SCAN) {

                    Predicate scanDeviceId = serTask.serScan.scanDevice.deviceId.eq(filter.getDeviceId());
                    predicate.and(scanDeviceId);

                } else if (tbType == ProcessTaskController.TableType.SER_JUDGE_GRAPH) {

                    Predicate judgeDeviceId = serTask.serJudgeGraph.judgeDevice.deviceId.eq(filter.getDeviceId());
                    predicate.and(judgeDeviceId);

                } else if (tbType == ProcessTaskController.TableType.SER_HAND_EXAMINATION) {

                    Predicate handDeviceId = serTask.serHandExamination.handDevice.deviceId.eq(filter.getDeviceId());
                    predicate.and(handDeviceId);

                }

            }

            if (filter.getStartTime() != null) {

                if (tbType == ProcessTaskController.TableType.SER_SCAN) {

                    predicate.and(serTask.serScan.scanStartTime.after(filter.getStartTime()));

                } else if (tbType == ProcessTaskController.TableType.SER_JUDGE_GRAPH) {

                    predicate.and(serTask.serJudgeGraph.judgeStartTime.after(filter.getStartTime()));

                } else {

                    predicate.and(serTask.serHandExamination.handStartTime.after(filter.getStartTime()));

                }

            }

            if (filter.getEndTime() != null) {

                if (tbType == ProcessTaskController.TableType.SER_SCAN) {

                    predicate.and(serTask.serScan.scanEndTime.before(filter.getEndTime()));

                } else if (tbType == ProcessTaskController.TableType.SER_JUDGE_GRAPH) {

                    predicate.and(serTask.serJudgeGraph.judgeEndTime.before(filter.getEndTime()));

                } else {

                    predicate.and(serTask.serHandExamination.handEndTime.before(filter.getEndTime()));

                }

            }
        }

        Iterable<SerTask> listTasks = serTaskRespository.findAll(predicate);

        long workingSeconds = 0;

        for (SerTask item : listTasks) {

            if (tbType == ProcessTaskController.TableType.SER_SCAN) {

                try {
                    workingSeconds += (item.getSerScan().getScanEndTime().getTime() - item.getSerScan().getScanStartTime().getTime()) / 1000;
                } catch (Exception e) {

                }

            } else if (tbType == ProcessTaskController.TableType.SER_JUDGE_GRAPH) {

                try {
                    workingSeconds += (item.getSerJudgeGraph().getJudgeEndTime().getTime() - item.getSerJudgeGraph().getJudgeStartTime().getTime()) / 1000;
                } catch (Exception e) {

                }

            } else {

                try {
                    workingSeconds += (item.getSerHandExamination().getHandEndTime().getTime() - item.getSerHandExamination().getHandStartTime().getTime()) / 1000;
                } catch (Exception e) {

                }

            }

        }

        return workingSeconds;

    }

}