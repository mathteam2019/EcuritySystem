/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（AuditLogServiceImpl）
 * 文件名：	AuditLogServiceImpl.java
 * 描述：	AuditLogService implement
 * 作者名：	Choe
 * 日期：	2019/12/10
 */

package com.nuctech.ecuritycheckitem.service.logmanagement.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.models.db.*;
import com.nuctech.ecuritycheckitem.models.reusables.CategoryUser;
import com.nuctech.ecuritycheckitem.repositories.SerPlatformOtherParamRepository;
import com.nuctech.ecuritycheckitem.repositories.SysAuditLogDetailRepository;
import com.nuctech.ecuritycheckitem.repositories.SysAuditLogRepository;
import com.nuctech.ecuritycheckitem.security.AuthenticationFacade;
import com.nuctech.ecuritycheckitem.service.auth.AuthService;
import com.nuctech.ecuritycheckitem.service.logmanagement.AuditLogService;
import com.nuctech.ecuritycheckitem.utils.PageResult;
import com.nuctech.ecuritycheckitem.utils.Utils;
import com.querydsl.core.BooleanBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class AuditLogServiceImpl implements AuditLogService {

    @Autowired
    SysAuditLogRepository sysAuditLogRepository;

    @Autowired
    SysAuditLogDetailRepository sysAuditLogDetailRepository;

    @Autowired
    SerPlatformOtherParamRepository platformOtherParamRepository;

    @Autowired
    AuthenticationFacade authenticationFacade;

    @Autowired
    AuthService authService;

    @Autowired
    private Utils utils;

    /**
     * get predicate from filter parameters
     * @param clientIp
     * @param operateResult
     * @param operateObject
     * @param operateStartTime
     * @param operateEndTime
     * @return
     */
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
        CategoryUser categoryUser = authService.getDataCategoryUserList();
        if(categoryUser.isAll() == false) {
            List<Long> userIdList = categoryUser.getUserIdList();
            predicate.and(builder.createdBy.in(userIdList).or(builder.editedBy.in(userIdList)));
        }
        return predicate;
    }

    /**
     * extract export audit log list
     * @param logList
     * @param isAll
     * @param idList
     * @return
     */
    private List<SysAuditLog> getExportList(List<SysAuditLog> logList, boolean isAll, String idList) {
        List<SysAuditLog> exportList = new ArrayList<>();
        Long max_size = 5000L;
        try {
            SerPlatformOtherParams serPlatformOtherParams = platformOtherParamRepository.findAll().get(0);
            max_size = serPlatformOtherParams.getLogMaxNumber();
        } catch(Exception ex) {}
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
     * get paginated and filtered audit log list
     * @param clientIp,
     * @param operateResult
     * @param operateObject
     * @param operateStartTime
     * @param operateEndTime
     * @param currentPage
     * @param perPage
     * @return
     */
    @Override
    public PageResult<SysAuditLog> getAuditLogListByFilter(String sortBy, String order, String clientIp, String operateResult, String operateObject, Date operateStartTime,
                                                    Date operateEndTime, int currentPage, int perPage) {
        BooleanBuilder predicate = getPredicate(clientIp, operateResult, operateObject, operateStartTime, operateEndTime);
        PageRequest pageRequest = PageRequest.of(currentPage, perPage);
        if (StringUtils.isNotBlank(order) && StringUtils.isNotEmpty(sortBy)) {
            if (order.equals(Constants.SortOrder.ASC)) {
                pageRequest = PageRequest.of(currentPage, perPage, Sort.by(sortBy).ascending());
            }
            else {
                pageRequest = PageRequest.of(currentPage, perPage, Sort.by(sortBy).descending());
            }
        }
        long total = sysAuditLogRepository.count(predicate);
        List<SysAuditLog> data = sysAuditLogRepository.findAll(predicate, pageRequest).getContent();
        return new PageResult<>(total, data);
    }

    /**
     * get audit log export list
     * @param clientIp
     * @param operateResult
     * @param operateObject
     * @param operateStartTime
     * @param operateEndTime
     * @param isAll
     * @param idList
     * @return
     */
    @Override
    public List<SysAuditLog> getExportList(String sortBy, String order, String clientIp, String operateResult, String operateObject, Date operateStartTime,
                                    Date operateEndTime, boolean isAll, String idList) {
        BooleanBuilder predicate = getPredicate(clientIp, operateResult, operateObject, operateStartTime, operateEndTime);
        String[] splits = idList.split(",");
        Long max_size = 5000L;
        try {
            SerPlatformOtherParams serPlatformOtherParams = platformOtherParamRepository.findAll().get(0);
            max_size = serPlatformOtherParams.getLogMaxNumber();
        } catch(Exception ex) {}
        List<Long> logIdList = new ArrayList<>();
        for(String idStr: splits) {
            logIdList.add(Long.valueOf(idStr));
        }
        predicate.and(QSysAuditLog.sysAuditLog.id.in(logIdList));
        Sort sort = null;
        if (StringUtils.isNotBlank(order) && StringUtils.isNotEmpty(sortBy)) {
            sort = Sort.by(sortBy).ascending();
            if (order.equals(Constants.SortOrder.DESC)) {
                sort = Sort.by(sortBy).descending();
            }
        } else {
            sort = Sort.by("id").ascending();
        }
        List<SysAuditLog> logList;
        if(sort != null) {
            logList = StreamSupport
                    .stream(sysAuditLogRepository.findAll(predicate, sort).spliterator(), false)
                    .collect(Collectors.toList());
        } else {
            logList = StreamSupport
                    .stream(sysAuditLogRepository.findAll(predicate).spliterator(), false)
                    .collect(Collectors.toList());
        }
        List<SysAuditLog> answerList = new ArrayList<>();
        for(int i = 0; i < logList.size() && i < max_size; i ++) {
            answerList.add(logList.get(i));
        }

        return answerList;//getExportList(logList, isAll, idList);
    }


    /**
     *
     * @param action: 操作
     * @param result: 操作结果
     * @param content: 操作内容
     * @param reason: 失败原因代码
     * @param onlineTime: 在线时长(秒)
     * @return
     */
    @Override
    public boolean saveAudioLog(String action, String result, String content, String fieldName, String reason, String object, Long onlineTime, boolean isSuccess, String valueBefore, String valueAfter) {
        SysUser user = (SysUser) authenticationFacade.getAuthentication().getPrincipal();
        if(isSuccess) {
            result = "1";
        } else {
            result = "0";
        }
        SysAuditLog auditLog = SysAuditLog.builder()
                .clientIp(utils.ipAddress)
                .action(action)
                .operateResult(result)
                .reasonCode(reason)
                .operateContent(content)
                .onlineTime(onlineTime)
                .operateAccount(user.getUserAccount())
                .operatorId(user.getUserId())
                .operateTime(new Date())
                .operateObject(fieldName)
                .build();
        auditLog.addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
        sysAuditLogRepository.save(auditLog);
        if(isSuccess) {
            ObjectMapper objectMapper = new ObjectMapper();

            try {
                Map<String, Object> mapBefore = objectMapper.readValue(valueBefore, HashMap.class);
                Map<String, Object> mapAfter = objectMapper.readValue(valueAfter, HashMap.class);
                Map<String, Object> mapDifferentBefore = new HashMap<>();
                Map<String, Object> mapDifferentAfter = new HashMap<>();
                Set<String> keys = mapBefore.keySet();
                Set<String> keysAfter = mapAfter.keySet();
                for(String key: keysAfter) {
                    if(!keys.contains(key)) {
                        keys.add(key);
                    }
                }
                for(String key: keys) {
                    Object beforeValue = mapBefore.get(key);
                    Object afterValue = mapAfter.get(key);
                    if(beforeValue == null) {
                        if(afterValue == null) {
                            continue;
                        }
                    } else if(beforeValue.equals(afterValue)){
                        continue;
                    }
                    mapDifferentBefore.put(key, beforeValue);
                    mapDifferentAfter.put(key, afterValue);
                }
                String strDifferentBefore = objectMapper.writeValueAsString(mapDifferentBefore);
                String strDifferentAfter = objectMapper.writeValueAsString(mapDifferentAfter);

                SysAuditLogDetail auditLogDetail = SysAuditLogDetail.builder()
                        .auditLogId(auditLog.getId())
                        .fieldName(object)
                        .valueBefore(strDifferentBefore)
                        .valueAfter(strDifferentAfter)
                        .build();
                auditLogDetail.addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
                sysAuditLogDetailRepository.save(auditLogDetail);
            } catch(Exception ex) {
                ex.printStackTrace();

            }
        }
        return true;
    }

    /**
     *
     * @param action: 操作
     * @param result: 操作结果
     * @param content: 操作内容
     * @param reason: 失败原因代码
     * @param onlineTime: 在线时长(秒)
     * @return
     */
    //@Override
    public boolean saveAudioLog(String action, String result, String content, String reason, String object, Long onlineTime) {
        SysUser user = (SysUser) authenticationFacade.getAuthentication().getPrincipal();
        SysAuditLog auditLog = SysAuditLog.builder()
                .clientIp(utils.ipAddress)
                .action(action)
                .operateResult(result)
                .reasonCode(reason)
                .operateContent(content)
                .onlineTime(onlineTime)
                .operateAccount(user.getUserAccount())
                .operatorId(user.getUserId())
                .operateTime(new Date())
                .operateObject(object)
                .build();
        auditLog.addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
        sysAuditLogRepository.save(auditLog);
        return true;
    }
}
