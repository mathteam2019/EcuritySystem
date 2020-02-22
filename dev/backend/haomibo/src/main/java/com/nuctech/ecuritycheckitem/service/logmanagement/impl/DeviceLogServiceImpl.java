/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（DeviceLogServiceImpl）
 * 文件名：	DeviceLogServiceImpl.java
 * 描述：	DeviceLogService implement
 * 作者名：	Choe
 * 日期：	2019/12/10
 */

package com.nuctech.ecuritycheckitem.service.logmanagement.impl;

import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.models.db.QSerDevLog;
import com.nuctech.ecuritycheckitem.models.db.SerDevLog;
import com.nuctech.ecuritycheckitem.models.db.SerPlatformOtherParams;
import com.nuctech.ecuritycheckitem.models.reusables.CategoryUser;
import com.nuctech.ecuritycheckitem.repositories.SerDevLogRepository;
import com.nuctech.ecuritycheckitem.repositories.SerPlatformOtherParamRepository;
import com.nuctech.ecuritycheckitem.service.auth.AuthService;
import com.nuctech.ecuritycheckitem.service.logmanagement.DeviceLogService;
import com.nuctech.ecuritycheckitem.utils.PageResult;
import com.querydsl.core.BooleanBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class DeviceLogServiceImpl implements DeviceLogService {
    @Autowired
    SerDevLogRepository serDevLogRepository;

    @Autowired
    AuthService authService;

    @Autowired
    SerPlatformOtherParamRepository platformOtherParamRepository;

    /**
     * get predicate from filter parameters
     * @param deviceType
     * @param deviceName
     * @param userName
     * @param category
     * @param level
     * @param operateStartTime
     * @param operateEndTime
     * @return
     */
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
            predicate.and(builder.loginName.contains(userName));
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
        CategoryUser categoryUser = authService.getDataCategoryUserList();
        if(categoryUser.isAll() == false) {
            List<Long> userIdList = categoryUser.getUserIdList();
            predicate.and(builder.createdBy.in(userIdList).or(builder.editedBy.in(userIdList)));
        }
        return predicate;
    }

    /**
     * extract export list
     * @param logList
     * @param isAll
     * @param idList
     * @return
     */
    private List<SerDevLog> getExportList(List<SerDevLog> logList, boolean isAll, String idList) {
        List<SerDevLog> exportList = new ArrayList<>();
        Long max_size = 5000L;
        try {
            SerPlatformOtherParams serPlatformOtherParams = platformOtherParamRepository.findAll().get(0);
            max_size = serPlatformOtherParams.getLogMaxNumber();
        } catch(Exception ex) {}
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
                    if(exportList.size() >= max_size) {
                        break;
                    }
                }
            }
        } else {
            for(int i = 0; i < logList.size() && i < max_size; i ++) {
                exportList.add(logList.get(i));
            }
        }
        return exportList;
    }

    /**
     * get paginated and filtered dev log list
     * @param deviceType
     * @param deviceName
     * @param userName
     * @param category
     * @param level
     * @param operateStartTime
     * @param operateEndTime
     * @param currentPage
     * @param perPage
     * @return
     */
    @Override
    public PageResult<SerDevLog> getDeviceLogListByFilter(String sortBy, String order, String deviceType, String deviceName, String userName, Long category, Long level, Date operateStartTime,
                                                          Date operateEndTime, int currentPage, int perPage) {

        BooleanBuilder predicate = getPredicate(deviceType, deviceName, userName, category, level, operateStartTime, operateEndTime);
        PageRequest pageRequest = PageRequest.of(currentPage, perPage);
        if (StringUtils.isNotBlank(order) && StringUtils.isNotEmpty(sortBy)) {
            if(!sortBy.equals("time")) {
                sortBy = "device.deviceSerial";
            }
            if (order.equals(Constants.SortOrder.ASC)) {
                pageRequest = PageRequest.of(currentPage, perPage, Sort.by(sortBy).ascending());
            }
            else {
                pageRequest = PageRequest.of(currentPage, perPage, Sort.by(sortBy).descending());
            }
        }

        long total = serDevLogRepository.count(predicate);
        List<SerDevLog> data = serDevLogRepository.findAll(predicate, pageRequest).getContent();
        return new PageResult<>(total, data);
    }

    /**
     * get dev log export list
     * @param deviceType
     * @param deviceName
     * @param userName
     * @param category
     * @param level
     * @param operateStartTime
     * @param operateEndTime
     * @param isAll
     * @param idList
     * @return
     */
    @Override
    public List<SerDevLog> getExportList(String sortBy, String order, String deviceType, String deviceName, String userName, Long category, Long level, Date operateStartTime,
                                         Date operateEndTime, boolean isAll, String idList) {
        BooleanBuilder predicate = getPredicate(deviceType, deviceName, userName, category, level, operateStartTime, operateEndTime);
        String[] splits = idList.split(",");
        List<Long> logIdList = new ArrayList<>();
        for(String idStr: splits) {
            logIdList.add(Long.valueOf(idStr));
        }
        predicate.and(QSerDevLog.serDevLog.id.in(logIdList));
        Sort sort = null;
        if (StringUtils.isNotBlank(order) && StringUtils.isNotEmpty(sortBy)) {
            if(!sortBy.equals("time")) {
                sortBy = "device.deviceSerial";
            }
            //sort = new Sort(Sort.Direction.ASC, new ArrayList<>(Arrays.asList(sortBy)));
            if (order.equals(Constants.SortOrder.DESC)) {
                //sort = new Sort(Sort.Direction.DESC, new ArrayList<>(Arrays.asList(sortBy)));
            }
        }
        List<SerDevLog> logList;
        if(sort != null) {
            logList = StreamSupport
                    .stream(serDevLogRepository.findAll(predicate, sort).spliterator(), false)
                    .collect(Collectors.toList());
        } else {
            logList = StreamSupport
                    .stream(serDevLogRepository.findAll(predicate).spliterator(), false)
                    .collect(Collectors.toList());
        }

        return logList;//getExportList(logList, isAll, idList);

    }
}
