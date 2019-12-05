/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/20
 * @CreatedBy Choe.
 * @FileName DeviceStatusController.java
 * @ModifyHistory
 */

package com.nuctech.ecuritycheckitem.controllers.devicemanagement;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.*;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.reusables.FilteringAndPaginationResult;
import com.querydsl.core.BooleanBuilder;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/device-management/condition-monitoring")
public class DeviceStatusController extends BaseController {

    /**
     * Device status datatable request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class DeviceStatusGetByFilterAndPageRequestBody {

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        static class Filter {
            Long fieldId;
            Long categoryId;
            String deviceName;
        }

        @NotNull
        @Min(1)
        int currentPage;

        @NotNull
        int perPage;

        Filter filter;
    }

    private SerDeviceStatus.MonitorRecord getRecordList(long deviceId, int deviceTrafficSetting) {
        Date curDate = new Date();
        long times = curDate.getTime();
        long unitMiliSecond = deviceTrafficSetting * 60 * 1000;
        long lastDateTime = (times / unitMiliSecond + 1) * unitMiliSecond;
        long startDateTime = lastDateTime - unitMiliSecond * 10;
        Date[] rangeDate = new Date[20];
        int[] countArray = new int[20];
        for(int i = 0; i <= 10; i ++) {
            rangeDate[i] = new Date(startDateTime + unitMiliSecond * i);
            countArray[i] = 0;
        }
        QSerScan builder = QSerScan.serScan;
        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());
        //predicate.and(builder.scanStartTime.after(rangeDate[0]));
        predicate.and(builder.scanDeviceId.eq(deviceId));
        List<SerScan> scanDataList = StreamSupport
                .stream(serScanRepository.findAll(predicate).spliterator(), false)
                .collect(Collectors.toList());

        if(scanDataList != null) {
            for(int i = 0; i < scanDataList.size(); i ++) {
                SerScan scan = scanDataList.get(i);
                Date scanStartTime = scan.getScanStartTime();
                for(int j = 0; j < 10; j ++) {
                    if((scanStartTime.after(rangeDate[j]) || scanStartTime.equals(rangeDate[j])) && scanStartTime.before(rangeDate[j + 1])) {
                        countArray[j] ++;
                    }
                }
            }
        }

        SerDeviceStatus.MonitorRecord result = new SerDeviceStatus.MonitorRecord();
        List<String> timeList = new ArrayList<>();
        List<Integer> countList = new ArrayList<>();

        for(int i = 0; i < 10; i ++) {
            SerDeviceStatus.MonitorRecord record = new SerDeviceStatus.MonitorRecord();
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
            String strTime = formatter.format(rangeDate[i + 1]);
            timeList.add(strTime);
            countList.add(countArray[i]);
        }
        result.setTimeList(timeList);
        result.setCountList(countList);






        return result;
    }



    /**
     * Device datatable data.
     */
    @RequestMapping(value = "/get-by-filter-and-page", method = RequestMethod.POST)
    public Object deviceGetByFilterAndPage(
            @RequestBody @Valid DeviceStatusGetByFilterAndPageRequestBody requestBody,
            BindingResult bindingResult) {


        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }


        QSerDeviceStatus builder = QSerDeviceStatus.serDeviceStatus;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        DeviceStatusGetByFilterAndPageRequestBody.Filter filter = requestBody.getFilter();
        if (filter != null) {
            if (filter.getFieldId() != null) {
                predicate.and(builder.device.fieldId.eq(filter.getFieldId()));
            }
            if (!StringUtils.isEmpty(filter.getDeviceName())) {
                predicate.and(builder.device.deviceName.contains(filter.getDeviceName()));
            }
        }



        int currentPage = requestBody.getCurrentPage() - 1; // On server side, page is calculated from 0.
        int perPage = requestBody.getPerPage();

        int startIndex = perPage * currentPage;
        int endIndex = perPage * (currentPage + 1);

        long total = 0;
        List<SerDeviceStatus> allData = StreamSupport
                .stream(serDeviceStatusRepository.findAll(predicate).spliterator(), false)
                .collect(Collectors.toList());
        List<SerDeviceStatus> data = new ArrayList<>();


        if(filter != null && filter.getCategoryId() != null) {

            for(int i = 0; i < allData.size(); i ++) {
                SerDeviceStatus deviceStatusData = allData.get(i);
                try {
                    if(deviceStatusData.getDevice().getArchive().getArchiveTemplate().getDeviceCategory().getCategoryId() == filter.getCategoryId()) {
                        if(total >= startIndex && total < endIndex) {
                            data.add(deviceStatusData);
                        }
                        total ++;

                    }
                } catch(Exception ex) {
                }
            }
        } else {
            for(int i = 0; i < allData.size(); i ++) {
                SerDeviceStatus deviceStatusData = allData.get(i);
                if(i >= startIndex && i < endIndex) {
                    data.add(deviceStatusData);
                }
            }
            total = allData.size();
        }

        int deviceTrafficSetting = 10;
        int deviceTrafficMiddle = 30;
        int deviceTrafficHigh = 80;
        int storageAlarm = 0;
        List<SerPlatformOtherParams> paramList = serPlatformOtherParamRepository.findAll();
        if(paramList != null && paramList.size() > 0) {
            deviceTrafficSetting = paramList.get(0).getDeviceTrafficSettings();
            deviceTrafficMiddle = paramList.get(0).getDeviceTrafficMiddle();
            deviceTrafficHigh = paramList.get(0).getDeviceTrafficHigh();
            storageAlarm = paramList.get(0).getStorageAlarm();
        }

        for(int i = 0; i < data.size(); i ++) {
            SerDeviceStatus deviceStatus = data.get(i);
            deviceStatus.setDeviceTrafficHigh(deviceTrafficHigh);
            deviceStatus.setDeviceTrafficMiddle(deviceTrafficMiddle);
            deviceStatus.setStorageAlarm(storageAlarm);
            deviceStatus.setRecord(getRecordList(deviceStatus.getDeviceId(), deviceTrafficSetting));
        }



        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(
                ResponseMessage.OK,
                FilteringAndPaginationResult
                        .builder()
                        .total(total)
                        .perPage(perPage)
                        .currentPage(currentPage + 1)
                        .lastPage((int) Math.ceil(((double) total) / perPage))
                        .from(perPage * currentPage + 1)
                        .to(perPage * currentPage + data.size())
                        .data(data)
                        .build()));

        // Set filters.

        FilterProvider filters = ModelJsonFilters
                .getDefaultFilters()
                .addFilter(ModelJsonFilters.FILTER_SYS_DEVICE, SimpleBeanPropertyFilter.serializeAllExcept("deviceConfig", "scanParam"))
                .addFilter(ModelJsonFilters.FILTER_SYS_FIELD, SimpleBeanPropertyFilter.serializeAllExcept("parent"))
                .addFilter(ModelJsonFilters.FILTER_SER_ARCHIVE_TEMPLATE, SimpleBeanPropertyFilter.serializeAllExcept("archiveIndicatorsList"))
                .addFilter(ModelJsonFilters.FILTER_SER_ARCHIVES, SimpleBeanPropertyFilter.serializeAllExcept("archiveValueList"))
                .addFilter(ModelJsonFilters.FILTER_SYS_DEVICE_CATEGORY, SimpleBeanPropertyFilter.serializeAllExcept("parent"));


        value.setFilters(filters);

        return value;
    }


}
