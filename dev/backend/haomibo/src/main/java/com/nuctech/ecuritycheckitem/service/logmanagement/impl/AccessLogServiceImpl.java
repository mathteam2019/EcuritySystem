/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（AccessLogServiceImpl）
 * 文件名：	AccessLogServiceImpl.java
 * 描述：	AccessLogService implement
 * 作者名：	Choe
 * 日期：	2019/12/10
 */

package com.nuctech.ecuritycheckitem.service.logmanagement.impl;

import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.es.repositories.EsSysAccessLogRepository;
import com.nuctech.ecuritycheckitem.models.db.QSysAccessLog;
import com.nuctech.ecuritycheckitem.models.db.SerPlatformOtherParams;
import com.nuctech.ecuritycheckitem.models.db.SysAccessLog;
import com.nuctech.ecuritycheckitem.models.db.SysUser;
import com.nuctech.ecuritycheckitem.models.es.EsSysAccessLog;
import com.nuctech.ecuritycheckitem.models.reusables.CategoryUser;
import com.nuctech.ecuritycheckitem.repositories.SerPlatformOtherParamRepository;
import com.nuctech.ecuritycheckitem.repositories.SysAccessLogRepository;
import com.nuctech.ecuritycheckitem.security.AuthenticationFacade;
import com.nuctech.ecuritycheckitem.service.auth.AuthService;
import com.nuctech.ecuritycheckitem.service.logmanagement.AccessLogService;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import com.nuctech.ecuritycheckitem.utils.PageResult;
import com.nuctech.ecuritycheckitem.utils.Utils;
import com.querydsl.core.BooleanBuilder;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.DocValueFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class AccessLogServiceImpl implements AccessLogService {

    @Autowired
    SysAccessLogRepository originalSysAccessLogRepository;

//    @Autowired
//    EsSysAccessLogRepository sysAccessLogRepository;

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
     * @param operateAccount
     * @param operateStartTime
     * @param operateEndTime
     * @return
     */
    private BooleanBuilder getPredicatePre(String clientIp, String operateAccount, Date operateStartTime, Date operateEndTime) {
        QSysAccessLog builder = QSysAccessLog.sysAccessLog;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        if (!StringUtils.isEmpty(clientIp)) {
            predicate.and(builder.clientIp.contains(clientIp));
        }

        if (!StringUtils.isEmpty(operateAccount)) {
            predicate.and(builder.operateAccount.eq(operateAccount));
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



    public PageResult<SysAccessLog> getAccessLogListByFilterPre(String sortBy, String order, String clientIp, String operateAccount, Date operateStartTime,
                                                             Date operateEndTime, int currentPage, int perPage) {
        BooleanBuilder predicate = getPredicatePre(clientIp, operateAccount, operateStartTime, operateEndTime);
        PageRequest pageRequest = PageRequest.of(currentPage, perPage);
        if (StringUtils.isNotBlank(order) && StringUtils.isNotEmpty(sortBy)) {
            if (order.equals(Constants.SortOrder.ASC)) {
                pageRequest = PageRequest.of(currentPage, perPage, Sort.by(sortBy).ascending());
            }
            else {
                pageRequest = PageRequest.of(currentPage, perPage, Sort.by(sortBy).descending());
            }
        }
        long total = originalSysAccessLogRepository.count(predicate);
        List<SysAccessLog> data = originalSysAccessLogRepository.findAll(predicate, pageRequest).getContent();
        return new PageResult<>(total, data);
    }


    /**
     *
     * @param sortBy
     * @param order
     * @param clientIp
     * @param operateAccount
     * @param operateStartTime
     * @param operateEndTime
     * @param isAll
     * @param idList
     * @return
     */
    private List<SysAccessLog> getExportListPre(String sortBy, String order, String clientIp, String operateAccount, Date operateStartTime,
                                       Date operateEndTime, boolean isAll, String idList) {
        BooleanBuilder predicate = getPredicatePre(clientIp, operateAccount, operateStartTime, operateEndTime);
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
        predicate.and(QSysAccessLog.sysAccessLog.id.in(logIdList));
        Sort sort = null;
        if (StringUtils.isNotBlank(order) && StringUtils.isNotEmpty(sortBy)) {
            //sort = new Sort(Sort.Direction.ASC, new ArrayList<>(Arrays.asList(sortBy)));
            if (order.equals(Constants.SortOrder.DESC)) {
                //sort = new Sort(Sort.Direction.DESC, new ArrayList<>(Arrays.asList(sortBy)));
            }
        }
        List<SysAccessLog> logList;
        if(sort != null) {
            logList = StreamSupport
                    .stream(originalSysAccessLogRepository.findAll(predicate, sort).spliterator(), false)
                    .collect(Collectors.toList());
        } else {
            logList = StreamSupport
                    .stream(originalSysAccessLogRepository.findAll(predicate).spliterator(), false)
                    .collect(Collectors.toList());
        }
        List<SysAccessLog> answerList = new ArrayList<>();
        for(int i = 0; i < logList.size() && i < max_size; i ++) {
            answerList.add(logList.get(i));
        }

        return answerList;//getExportList(logList, isAll, idList);
    }

    /**
     * get predicate from filter parameters
     * @param clientIp
     * @param operateAccount
     * @param operateStartTime
     * @param operateEndTime
     * @return
     */
    private BoolQueryBuilder getPredicate(String clientIp, String operateAccount, Date operateStartTime, Date operateEndTime) {

        BoolQueryBuilder builder = QueryBuilders.boolQuery();

        if (!StringUtils.isEmpty(clientIp)) {
            builder.must(QueryBuilders.matchQuery("client_ip", clientIp));
        }

        if (!StringUtils.isEmpty(operateAccount)) {
            builder.must(QueryBuilders.matchQuery("operate_account", operateAccount));
        }

        if(operateStartTime != null) {
            builder.must(QueryBuilders.rangeQuery("operate_time").gte(operateStartTime.getTime()));
        }
        if(operateEndTime != null){
            builder.must(QueryBuilders.rangeQuery("operate_time").lte(operateEndTime.getTime()));
        }
        CategoryUser categoryUser = authService.getDataCategoryUserList();
        if(categoryUser.isAll() == false) {
            List<Long> userIdList = categoryUser.getUserIdList();
            TermsQueryBuilder createdTerms = QueryBuilders.termsQuery("createdby", userIdList);
            TermsQueryBuilder editedTerms = QueryBuilders.termsQuery("editedby", userIdList);
            BoolQueryBuilder builderUserId = new BoolQueryBuilder()
                    .minimumShouldMatch(1)
                    .should(createdTerms)
                    .should(editedTerms);

            builder.should(builderUserId);
        }


        return builder;
    }

    /**
     * extract access log export list
     * @param logList
     * @param isAll
     * @param idList
     * @return
     */
    private List<SysAccessLog> getExportList(List<SysAccessLog> logList, boolean isAll, String idList) {
        List<SysAccessLog> exportList = new ArrayList<>();
        Long max_size = 5000L;
        try {
            SerPlatformOtherParams serPlatformOtherParams = platformOtherParamRepository.findAll().get(0);
            max_size = serPlatformOtherParams.getLogMaxNumber();
        } catch(Exception ex) {}

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

    private EsSysAccessLog convertAccessLog(SysAccessLog accessLog) {
        return EsSysAccessLog.builder()
                .id(accessLog.getId())
                .operateId(accessLog.getOperateId())
                .operateAccount(accessLog.getOperateAccount())
                .operateResult(accessLog.getOperateResult())
                .clientIp(accessLog.getClientIp())
                .onlineTime(accessLog.getOnlineTime())
                .action(accessLog.getAction())
                .reasonCode(accessLog.getReasonCode())
                .operateTime(accessLog.getOperateTime())
                .createdBy(accessLog.getCreatedBy())
                .editBy(accessLog.getEditedBy())
                .build();
    }

    /**
     * get paginated and filtered access log list
     * @param clientIp
     * @param operateAccount
     * @param operateStartTime
     * @param operateEndTime
     * @param currentPage
     * @param perPage
     * @return
     */
    @Override
    public PageResult<EsSysAccessLog> getAccessLogListByFilter(String sortBy, String order, String clientIp, String operateAccount, Date operateStartTime,
                                                          Date operateEndTime, int currentPage, int perPage) {

//        BoolQueryBuilder predicate = getPredicate(clientIp, operateAccount, operateStartTime, operateEndTime);
//        PageRequest pageRequest = PageRequest.of(currentPage, perPage);
//        if (StringUtils.isNotBlank(order) && StringUtils.isNotEmpty(sortBy)) {
//            if (order.equals(Constants.SortOrder.ASC)) {
//                pageRequest = PageRequest.of(currentPage, perPage, Sort.by(sortBy).ascending());
//            }
//            else {
//                pageRequest = PageRequest.of(currentPage, perPage, Sort.by(sortBy).descending());
//            }
//        } else {
//            pageRequest = PageRequest.of(currentPage, perPage, Sort.by("id").ascending());
//        }
//        //Page<EsSysAccessLog> pageResult = sysAccessLogRepository.search(predicate, pageRequest);
//        long total = 0;//pageResult.getTotalElements();
//        List<EsSysAccessLog> data = new ArrayList<>();//pageResult.getContent();
        PageResult<SysAccessLog> preResult = getAccessLogListByFilterPre(sortBy, order, clientIp, operateAccount, operateStartTime, operateEndTime, currentPage, perPage);
        long total = preResult.getTotal();
        List<SysAccessLog> logResult = preResult.getDataList();
        List<EsSysAccessLog> data = new ArrayList<>();
        for(int i = 0; i < logResult.size(); i ++) {
            data.add(convertAccessLog(logResult.get(i)));
        }
        return new PageResult<>(total, data);
    }

    /**
     * get access loge export list
     * @param clientIp
     * @param operateAccount
     * @param operateStartTime
     * @param operateEndTime
     * @param isAll
     * @param idList
     * @return
     */
    @Override
    public List<EsSysAccessLog> getExportList(String sortBy, String order, String clientIp, String operateAccount, Date operateStartTime,
                                         Date operateEndTime, boolean isAll, String idList) {
//        BoolQueryBuilder predicate = getPredicate(clientIp, operateAccount, operateStartTime, operateEndTime);
//        Long max_size = 5000L;
//        try {
//            SerPlatformOtherParams serPlatformOtherParams = platformOtherParamRepository.findAll().get(0);
//            max_size = serPlatformOtherParams.getLogMaxNumber();
//        } catch(Exception ex) {}
//        String[] splits = idList.split(",");
//        List<Long> logIdList = new ArrayList<>();
//        for(String idStr: splits) {
//            logIdList.add(Long.valueOf(idStr));
//        }
//        predicate.must( QueryBuilders.termsQuery("id", logIdList));
//        Sort sort = null;
//        PageRequest pageRequest = PageRequest.of(0, Integer.MAX_VALUE);
//        if (StringUtils.isNotBlank(order) && StringUtils.isNotEmpty(sortBy)) {
//            if (order.equals(Constants.SortOrder.ASC)) {
//                pageRequest = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(sortBy).ascending());
//            }
//            else {
//                pageRequest = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(sortBy).descending());
//            }
//        } else {
//            pageRequest = PageRequest.of(0, Integer.MAX_VALUE, Sort.by("id").ascending());
//        }
//        List<EsSysAccessLog> logList = new ArrayList<>();//StreamSupport
////                .stream(sysAccessLogRepository.search(predicate, pageRequest).spliterator(), false)
////                .collect(Collectors.toList());
//
//        List<EsSysAccessLog> answerList = new ArrayList<>();
//        for(int i = 0; i < logList.size() && i < max_size; i ++) {
//            answerList.add(logList.get(i));
//        }
//        return answerList;
        List<SysAccessLog> preResult = getExportListPre(sortBy, order, clientIp, operateAccount, operateStartTime, operateEndTime, isAll, idList);
        List<EsSysAccessLog> data = new ArrayList<>();
        for(int i = 0; i < preResult.size(); i ++) {
            data.add(convertAccessLog(preResult.get(i)));
        }
        return data;

    }

    /**
     *
     * @param action: 操作
     * @param result: 操作结果
     * @param reason: 失败原因代码
     * @param onlineTime: 在线时长(秒)
     * @return
     */
    @Override
    public boolean saveAccessLog(SysUser user, String action, String result, String reason, Long onlineTime) {

        SysAccessLog accessLog = SysAccessLog.builder()
                .clientIp(utils.ipAddress)
                .action(action)
                .operateResult(result)
                .operateId(user.getUserId())
                .operateAccount(user.getUserAccount())
                .onlineTime(onlineTime)
                .reasonCode(reason)
                .operateTime(new Date())
                .createdBy(user.getUserId())
                .build();

        originalSysAccessLogRepository.save(accessLog);
        return true;
    }
}
