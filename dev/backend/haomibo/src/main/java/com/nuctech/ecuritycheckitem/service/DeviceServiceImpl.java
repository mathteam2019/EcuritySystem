package com.nuctech.ecuritycheckitem.service;

import com.nuctech.ecuritycheckitem.controllers.devicemanagement.DeviceControlController;
import com.nuctech.ecuritycheckitem.models.db.QSysDevice;
import com.nuctech.ecuritycheckitem.models.db.SysDevice;
import com.nuctech.ecuritycheckitem.repositories.SysDeviceRepository;
import com.querydsl.core.BooleanBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class DeviceServiceImpl implements DeviceService {

    @Autowired
    SysDeviceRepository sysDeviceRepository;

    @Override
    public List<SysDevice> getFilterDeviceList(DeviceControlController.DeviceGetByFilterAndPageRequestBody requestBody) {
        QSysDevice builder = QSysDevice.sysDevice;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        DeviceControlController.DeviceGetByFilterAndPageRequestBody.Filter filter = requestBody.getFilter();
        if (filter != null) {
            if (!StringUtils.isEmpty(filter.getArchivesName())) {
                predicate.and(builder.archive.archivesName.contains(filter.getArchivesName()));
            }
            if (!StringUtils.isEmpty(filter.getDeviceName())) {
                predicate.and(builder.deviceName.contains(filter.getDeviceName()));
            }
            if (!StringUtils.isEmpty(filter.getStatus())) {
                predicate.and(builder.status.eq(filter.getStatus()));
            }
            if(filter.getFieldId() != null) {
                predicate.and(builder.fieldId.eq(filter.getFieldId()));
            }



            /*
            * Todo
            *  Strange Category is null
            *if (filter.getCategoryId() != null) {
                predicate.and(builder.archive.archiveTemplate.category.categoryId.eq(filter.getCategoryId()));
            }
            * */

        }


        List<SysDevice> allData = StreamSupport
                .stream(sysDeviceRepository.findAll(predicate).spliterator(), false)
                .collect(Collectors.toList());
        return allData;

    }
}
