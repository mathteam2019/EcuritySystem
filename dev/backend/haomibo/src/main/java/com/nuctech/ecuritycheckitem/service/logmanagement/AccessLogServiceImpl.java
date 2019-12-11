package com.nuctech.ecuritycheckitem.service.logmanagement;

import com.nuctech.ecuritycheckitem.controllers.logmanagement.operatinglog.AccessLogController;
import com.nuctech.ecuritycheckitem.models.db.QSerDevLog;
import com.nuctech.ecuritycheckitem.models.db.QSysAccessLog;
import com.nuctech.ecuritycheckitem.models.db.SerDevLog;
import com.nuctech.ecuritycheckitem.models.db.SysAccessLog;
import com.nuctech.ecuritycheckitem.repositories.SysAccessLogRepository;
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
public class AccessLogServiceImpl implements AccessLogService{

    @Autowired
    SysAccessLogRepository sysAccessLogRepository;

    private BooleanBuilder getPredicate(String clientIp, String operateAccount, Date operateStartTime, Date operateEndTime) {
        QSysAccessLog builder = QSysAccessLog.sysAccessLog;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        if (!StringUtils.isEmpty(clientIp)) {
            predicate.and(builder.clientIp.contains(clientIp));
        }

        if (!StringUtils.isEmpty(operateAccount)) {
            predicate.and(builder.operateAccount.contains(operateAccount));
        }

        if(operateStartTime != null) {
            predicate.and(builder.operateTime.after(operateStartTime));

        }
        if(operateEndTime != null){
            predicate.and(builder.operateTime.before(operateEndTime));
        }


        return predicate;
    }

    private List<SysAccessLog> getExportList(List<SysAccessLog> logList, boolean isAll, String idList) {
        List<SysAccessLog> exportList = new ArrayList<>();
        if(isAll == false) {
            String[] splits = idList.split(",");
            for(int i = 0; i < logList.size(); i ++) {
                SysAccessLog log = logList.get(i);
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
    public PageResult<SysAccessLog> getAccessLogListByFilter(String clientIp, String operateAccount, Date operateStartTime,
                                                          Date operateEndTime, int currentPage, int perPage) {

        BooleanBuilder predicate = getPredicate(clientIp, operateAccount, operateStartTime, operateEndTime);
        PageRequest pageRequest = PageRequest.of(currentPage, perPage);

        long total = sysAccessLogRepository.count(predicate);
        List<SysAccessLog> data = sysAccessLogRepository.findAll(predicate, pageRequest).getContent();
        return new PageResult<>(total, data);
    }

    @Override
    public List<SysAccessLog> getExportList(String clientIp, String operateAccount, Date operateStartTime,
                                         Date operateEndTime, boolean isAll, String idList) {
        BooleanBuilder predicate = getPredicate(clientIp, operateAccount, operateStartTime, operateEndTime);
        List<SysAccessLog> logList = StreamSupport
                .stream(sysAccessLogRepository.findAll(predicate).spliterator(), false)
                .collect(Collectors.toList());
        return getExportList(logList, isAll, idList);

    }
}
