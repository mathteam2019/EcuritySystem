package com.nuctech.ecuritycheckitem.service.logmanagement;

import com.nuctech.ecuritycheckitem.controllers.logmanagement.devicelog.DeviceLogController;
import com.nuctech.ecuritycheckitem.models.db.QSerDevLog;
import com.nuctech.ecuritycheckitem.models.db.SerDevLog;
import com.nuctech.ecuritycheckitem.repositories.SerDevLogRepository;
import com.nuctech.ecuritycheckitem.utils.PageResult;
import com.querydsl.core.BooleanBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class DeviceLogServiceImpl implements DeviceLogService {
    @Autowired
    SerDevLogRepository serDevLogRepository;

    private BooleanBuilder getPredicate(String deviceType, String deviceName, String userName, Long category,
                                        Long level, Date operateStartTime, Date operateEndTime) {
        QSerDevLog builder = QSerDevLog.serDevLog;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        if (!StringUtils.isEmpty(deviceType)) {
            predicate.and(builder.device.deviceType.eq(deviceType));
        }

        if (!StringUtils.isEmpty(deviceName)) {
            predicate.and(builder.device.deviceName.contains(deviceName));
        }

        if (!StringUtils.isEmpty(userName)) {
            predicate.and(builder.user.userAccount.contains(userName));
        }

        if (category != null) {
            predicate.and(builder.category.eq(category));
        }

        if (level != null) {
            predicate.and(builder.level.eq(level));
        }

        if(operateStartTime != null) {
            predicate.and(builder.time.after(operateStartTime));

        }
        if(operateEndTime != null){
            predicate.and(builder.time.before(operateEndTime));
        }
        return predicate;
    }

    private List<SerDevLog> getExportList(List<SerDevLog> logList, boolean isAll, String idList) {
        List<SerDevLog> exportList = new ArrayList<>();
        if(isAll == false) {
            String[] splits = idList.split(",");
            for(int i = 0; i < logList.size(); i ++) {
                SerDevLog log = logList.get(i);
                boolean isExist = false;
                for(int j = 0; j < splits.length; j ++) {
                    if(splits[j].equals(log.getId().toString())) {
                        isExist = true;
                        break;
                    }
                }
                if(isExist == true) {
                    exportList.add(log);
                }
            }
        } else {
            exportList = logList;
        }
        return exportList;
    }

    @Override
    public PageResult<SerDevLog> getDeviceLogListByFilter(String deviceType, String deviceName, String userName, Long category, Long level, Date operateStartTime,
                                                          Date operateEndTime, int currentPage, int perPage) {

        BooleanBuilder predicate = getPredicate(deviceType, deviceName, userName, category, level, operateStartTime, operateEndTime);
        PageRequest pageRequest = PageRequest.of(currentPage, perPage);

        long total = serDevLogRepository.count(predicate);
        List<SerDevLog> data = serDevLogRepository.findAll(predicate, pageRequest).getContent();
        return new PageResult<>(total, data);
    }

    @Override
    public List<SerDevLog> getExportList(String deviceType, String deviceName, String userName, Long category, Long level, Date operateStartTime,
                                         Date operateEndTime, boolean isAll, String idList) {
        BooleanBuilder predicate = getPredicate(deviceType, deviceName, userName, category, level, operateStartTime, operateEndTime);
        List<SerDevLog> logList = StreamSupport
                .stream(serDevLogRepository.findAll(predicate).spliterator(), false)
                .collect(Collectors.toList());
        return getExportList(logList, isAll, idList);

    }
}
