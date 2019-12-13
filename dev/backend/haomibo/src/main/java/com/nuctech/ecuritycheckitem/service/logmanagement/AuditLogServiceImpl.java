package com.nuctech.ecuritycheckitem.service.logmanagement;

import com.nuctech.ecuritycheckitem.models.db.QSysAuditLog;
import com.nuctech.ecuritycheckitem.models.db.SysAuditLog;
import com.nuctech.ecuritycheckitem.repositories.SysAuditLogRepository;
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
public class AuditLogServiceImpl implements AuditLogService{

    @Autowired
    SysAuditLogRepository sysAuditLogRepository;

    private BooleanBuilder getPredicate(String clientIp, String operateResult, String operateObject, Date operateStartTime, Date operateEndTime) {
        QSysAuditLog builder = QSysAuditLog.sysAuditLog;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        if (!StringUtils.isEmpty(clientIp)) {
            predicate.and(builder.clientIp.contains(clientIp));
        }

        if (!StringUtils.isEmpty(operateResult)) {
            predicate.and(builder.operateResult.eq(operateResult));
        }

        if (!StringUtils.isEmpty(operateObject)) {
            predicate.and(builder.operateObject.contains(operateObject));
        }


        if(operateStartTime != null) {
            predicate.and(builder.operateTime.after(operateStartTime));

        }
        if(operateEndTime != null){
            predicate.and(builder.operateTime.before(operateEndTime));
        }
        return predicate;
    }

    private List<SysAuditLog> getExportList(List<SysAuditLog> logList, boolean isAll, String idList) {
        List<SysAuditLog> exportList = new ArrayList<>();
        if(isAll == false) {
            String[] splits = idList.split(",");
            for(int i = 0; i < logList.size(); i ++) {
                SysAuditLog log = logList.get(i);
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
    public PageResult<SysAuditLog> getAuditLogListByFilter(String clientIp, String operateResult, String operateObject, Date operateStartTime,
                                                    Date operateEndTime, int currentPage, int perPage) {
        BooleanBuilder predicate = getPredicate(clientIp, operateResult, operateObject, operateStartTime, operateEndTime);
        PageRequest pageRequest = PageRequest.of(currentPage, perPage);

        long total = sysAuditLogRepository.count(predicate);
        List<SysAuditLog> data = sysAuditLogRepository.findAll(predicate, pageRequest).getContent();
        return new PageResult<>(total, data);
    }

    @Override
    public List<SysAuditLog> getExportList(String clientIp, String operateResult, String operateObject, Date operateStartTime,
                                    Date operateEndTime, boolean isAll, String idList) {
        BooleanBuilder predicate = getPredicate(clientIp, operateResult, operateObject, operateStartTime, operateEndTime);
        List<SysAuditLog> logList = StreamSupport
                .stream(sysAuditLogRepository.findAll(predicate).spliterator(), false)
                .collect(Collectors.toList());
        return getExportList(logList, isAll, idList);
    }
}