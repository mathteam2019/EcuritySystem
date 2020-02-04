/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（DeviceStatusServiceImpl）
 * 文件名：	DeviceStatusServiceImpl.java
 * 描述：	DeviceStatusService implement
 * 作者名：	Choe
 * 日期：	2019/12/10
 */

package com.nuctech.ecuritycheckitem.service.devicemanagement.impl;

import com.nuctech.ecuritycheckitem.models.db.SerScanSimple;
import com.nuctech.ecuritycheckitem.models.db.SerDeviceStatus;
import com.nuctech.ecuritycheckitem.models.db.QSerDeviceStatus;
import com.nuctech.ecuritycheckitem.models.db.SerScanParamSimple;
import com.nuctech.ecuritycheckitem.models.db.SerPlatformOtherParams;

import com.nuctech.ecuritycheckitem.models.reusables.CategoryUser;
import com.nuctech.ecuritycheckitem.repositories.SerDeviceStatusRepository;
import com.nuctech.ecuritycheckitem.repositories.SerPlatformOtherParamRepository;
import com.nuctech.ecuritycheckitem.repositories.SerScanParamRepository;
import com.nuctech.ecuritycheckitem.repositories.SerScanRepository;
import com.nuctech.ecuritycheckitem.service.auth.AuthService;
import com.nuctech.ecuritycheckitem.service.devicemanagement.DeviceStatusService;
import com.nuctech.ecuritycheckitem.utils.PageResult;
import com.querydsl.core.BooleanBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class DeviceStatusServiceImpl implements DeviceStatusService {

    @Autowired
    SerScanRepository serScanRepository;

    @Autowired
    SerDeviceStatusRepository serDeviceStatusRepository;

    @Autowired
    SerPlatformOtherParamRepository serPlatformOtherParamRepository;

    @Autowired
    SerScanParamRepository serScanParamRepository;

    @Autowired
    AuthService authService;

    /**
     * get record list
     * @param devicueStatus
     * @param deviceTrafficSetting
     * @return
     */
    private SerDeviceStatus.MonitorRecord getRecordList(SerDeviceStatus devicueStatus, int deviceTrafficSetting) {
        Date curDate = new Date();
        long times = curDate.getTime();
        long unitMiliSecond = deviceTrafficSetting * 60 * 1000;
        long lastDateTime = (times / unitMiliSecond + 1) * unitMiliSecond;
        long startDateTime = lastDateTime - unitMiliSecond * 10;
        Date[] rangeDate = new Date[20];
        int[] countArray = new int[20];
        for (int i = 0; i <= 10; i++) {
            rangeDate[i] = new Date(startDateTime + unitMiliSecond * i);
            countArray[i] = 0;
        }
        List<SerScanSimple> scanDataList = devicueStatus.getScanList();


        if (scanDataList != null) {
            for (int i = 0; i < scanDataList.size(); i++) {
                SerScanSimple scan = scanDataList.get(i);
                Date scanStartTime = scan.getScanStartTime();
                for (int j = 0; j < 10; j++) {
                    if ((scanStartTime.after(rangeDate[j]) || scanStartTime.equals(rangeDate[j])) && scanStartTime.before(rangeDate[j + 1])) {
                        countArray[j]++;
                    }
                }
            }
        }

        SerDeviceStatus.MonitorRecord result = new SerDeviceStatus.MonitorRecord();
        List<String> timeList = new ArrayList<>();
        List<Integer> countList = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            SerDeviceStatus.MonitorRecord record = new SerDeviceStatus.MonitorRecord();
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
            String strTime = formatter.format(rangeDate[i + 1]);
            timeList.add(strTime);
            countList.add(countArray[i] * 10);
        }
        result.setTimeList(timeList);
        result.setCountList(countList);
        return result;
    }

    /**
     * get paginated and filtered device status
     * @param fieldId
     * @param deviceName
     * @param categoryId
     * @param currentPage
     * @param perPage
     * @return
     */
    @Override
    public PageResult<SerDeviceStatus> getDeviceStatusByFilter(Long fieldId, String deviceName, Long categoryId, int currentPage, int perPage) {
        QSerDeviceStatus builder = QSerDeviceStatus.serDeviceStatus;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        if (fieldId != null) {
            predicate.and(builder.device.fieldId.eq(fieldId));
        }
        if (!StringUtils.isEmpty(deviceName)) {
            predicate.and(builder.device.deviceName.contains(deviceName));
        }


        int startIndex = perPage * currentPage;
        int endIndex = perPage * (currentPage + 1);

        long total = 0;
        List<SerDeviceStatus> allData = StreamSupport
                .stream(serDeviceStatusRepository.findAll(predicate).spliterator(), false)
                .collect(Collectors.toList());
        List<SerDeviceStatus> data = new ArrayList<>();

        if (categoryId != null) {
            for (int i = 0; i < allData.size(); i++) {
                SerDeviceStatus deviceStatusData = allData.get(i);
                try {
                    if (deviceStatusData.getDevice().getArchive().getArchiveTemplate().getDeviceCategory().getCategoryId() == categoryId) {
                        if (total >= startIndex && total < endIndex) {
                            data.add(deviceStatusData);
                        }
                        total++;
                    }
                } catch (Exception ex) {
                }
            }
        } else {
            for (int i = 0; i < allData.size(); i++) {
                SerDeviceStatus deviceStatusData = allData.get(i);
                if (i >= startIndex && i < endIndex) {
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
        if (paramList != null && paramList.size() > 0) {
            deviceTrafficSetting = paramList.get(0).getDeviceTrafficSettings();
            deviceTrafficMiddle = paramList.get(0).getDeviceTrafficMiddle();
            deviceTrafficHigh = paramList.get(0).getDeviceTrafficHigh();
        }

        for (int i = 0; i < data.size(); i++) {
            SerDeviceStatus deviceStatus = data.get(i);
            deviceStatus.setDeviceTrafficHigh(deviceTrafficHigh);
            deviceStatus.setDeviceTrafficMiddle(deviceTrafficMiddle);
            deviceStatus.setRecord(getRecordList(deviceStatus, deviceTrafficSetting));

            List<SerScanParamSimple> serScanParamList = deviceStatus.getSerScanParamList();
            if (serScanParamList != null && serScanParamList.size() > 0) {
                try {
                    String[] splitDiskSpace = deviceStatus.getDiskSpace().split("/");
                    int currentSpace = Integer.parseInt(splitDiskSpace[0]);
                    int totalSpace = Integer.parseInt(splitDiskSpace[1]);
                    Integer deviceStorageAlarm = serScanParamList.get(0).getDeviceStorageAlarm();
                    Integer deviceStorageAlarmPercent = serScanParamList.get(0).getDeviceStorageAlarmPercent();
                    if (deviceStorageAlarm != null && currentSpace > deviceStorageAlarm) {
                        storageAlarm = 1;
                    } else if (deviceStorageAlarmPercent != null && currentSpace * 100 > totalSpace * deviceStorageAlarmPercent) {
                        storageAlarm = 2;
                    }
                } catch (Exception ex) {

                }

                deviceStatus.setDeviceStorageAlarm(storageAlarm);
            }
        }
        return new PageResult<>(total, data);
    }
}
