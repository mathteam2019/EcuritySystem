/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（TaskServiceImpl)
 * 文件名：	TaskServiceImpl.java
 * 描述：	Service to get task data from database using models and repositories
 * 作者名：	Tiny
 * 日期：	2019/12/20
 *
 */

package com.nuctech.ecuritycheckitem.service.taskmanagement.impl;

import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.models.db.SerPlatformCheckParams;
import com.nuctech.ecuritycheckitem.models.db.SerScan;
import com.nuctech.ecuritycheckitem.models.simplifieddb.*;
import com.nuctech.ecuritycheckitem.repositories.*;
import com.nuctech.ecuritycheckitem.service.auth.AuthService;
import com.nuctech.ecuritycheckitem.service.taskmanagement.ProcessService;
import com.nuctech.ecuritycheckitem.utils.PageResult;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ProcessServiceImpl implements ProcessService {

    @Autowired
    SerTaskRepository serTaskRepository;

    @Autowired
    SerTaskTableRepository serTaskTableRepository;

    @Autowired
    SerPlatformCheckParamRepository serPlatformCheckParamRepository;

    @Autowired
    HistoryTableProcessRepository historyTableProcessRepository;

    @Autowired
    ProcessImageRepository processImageRepository;

    @Autowired
    AuthService authService;

    /**
     * Get filter condition from input parameters
     * @param taskNumber
     * @param modeId
     * @param taskStatus
     * @param fieldId
     * @param userName
     * @param startTime
     * @param endTime
     * @return
     */
    private BooleanBuilder getPredicateProcess(String taskNumber, Long modeId, String taskStatus, Long fieldId, String userName, Date startTime, Date endTime) {

        QHistorySimplifiedForProcessTableManagement builder = QHistorySimplifiedForProcessTableManagement.historySimplifiedForProcessTableManagement;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        if (!StringUtils.isEmpty(taskNumber)) { //if task number is input
            predicate.and(builder.taskNumber.contains(taskNumber));
        }

        if (!StringUtils.isEmpty(taskStatus)) { //if task number is input
            predicate.and(builder.taskStatus.eq(taskStatus));
        }
        if (modeId != null) { //if mode id is input
            predicate.and(builder.modeId.eq(modeId));
        }

        if (fieldId != null) { //if field id is input
            predicate.and(builder.fieldId.eq(fieldId));
        }
        if (!StringUtils.isEmpty(userName)) { //if username is input
            Predicate scanUserName = builder.scanPointsManName.contains(userName);
            predicate.and(scanUserName);
        }
        if (startTime != null) { //if start time is input
            predicate.and(builder.scanStartTime.after(startTime));
        }
        if (endTime != null) { //if end time is input
            predicate.and(builder.scanStartTime.before(endTime));
        }




        //predicate.and(builder.serCheckResultList.isNotEmpty());
//        CategoryUser categoryUser = authService.getDataCategoryUserList();
//        if(categoryUser.isAll() == false) {
//            predicate.and(builder.createdBy.in(categoryUser.getUserIdList()).or(builder.editedBy.in(categoryUser.getUserIdList())));
//        }
        //predicate.and(builder.serCheckResult.checkResultId.isNotNull());
        return predicate;
    }

    /**
     * Get paginated and filtered records of HistorySimplifiedForHistoryTaskManagement task
     * @param taskNumber : task number
     * @param modeId : workmode id
     * @param taskStatus : task status
     * @param fieldId : scene id
     * @param userName : user name
     * @param startTime : start time
     * @param endTime : end time
     * @param currentPage : current page no
     * @param perPage : record count per page
     * @return
     */
    @Override
    public PageResult<HistorySimplifiedForProcessTableManagement> getProcessTaskByFilter(String taskNumber, Long modeId, String taskStatus, Long fieldId, String userName, Date startTime, Date endTime, String sortBy, String order, Integer currentPage, Integer perPage) {
        Date start = new Date();
        QHistorySimplifiedForProcessTableManagement builder = QHistorySimplifiedForProcessTableManagement.historySimplifiedForProcessTableManagement;
        BooleanBuilder predicate = getPredicateProcess(taskNumber, modeId, taskStatus, fieldId, userName, startTime, endTime); //get predicate from input parameters
//        predicate.and((builder.taskStatus.eq(HistorySimplifiedForHistoryTableManagement.TaskStatusType.ASSIGN))
//                        .or(builder.taskStatus.eq(HistorySimplifiedForHistoryTableManagement.TaskStatusType.SECURITY))
//                        .or(builder.taskStatus.eq(HistorySimplifiedForHistoryTableManagement.TaskStatusType.JUDGE))
//                        .or(builder.taskStatus.eq(HistorySimplifiedForHistoryTableManagement.TaskStatusType.HAND)));
//        predicate.and(builder.scanInvalid.eq(HistorySimplifiedForHistoryTableManagement.InvalidType.FALSE));

        PageRequest pageRequest = PageRequest.of(currentPage, perPage);
        if (StringUtils.isNotBlank(order) && StringUtils.isNotEmpty(sortBy)) {

            if (order.equals(Constants.SortOrder.ASC)) {
                pageRequest = PageRequest.of(currentPage, perPage, Sort.by(sortBy).ascending());
            }
            else {
                pageRequest = PageRequest.of(currentPage, perPage, Sort.by(sortBy).descending());
            }
        } else {
            pageRequest = PageRequest.of(currentPage, perPage, Sort.by("scanStartTime").descending());
        }


        long total = historyTableProcessRepository.count(predicate); //get total count from database using repsitory
        Date end = new Date();
        long diff = end.getTime() - start.getTime();
        List<HistorySimplifiedForProcessTableManagement> data = historyTableProcessRepository.findAll(predicate, pageRequest).getContent(); //get list of data from database using repository
        end = new Date();
        long diff1 = end.getTime() - start.getTime();
        return new PageResult<>(total, data);
    }

    @Override
    public List<ProcessSimplifiedForImageManagement> getExportProcessImage(String sortBy, String order, String idList) {

        QProcessSimplifiedForImageManagement builder = QProcessSimplifiedForImageManagement.processSimplifiedForImageManagement;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());
        String[] splits = idList.split(",");
        List<Long> historyIdList = new ArrayList<>();
        for(String idStr: splits) {
            historyIdList.add(Long.valueOf(idStr));
        }
        predicate.and(builder.taskId.in(historyIdList));
        Sort sort = null;
        if (StringUtils.isNotBlank(order) && StringUtils.isNotEmpty(sortBy)) {

            sort = Sort.by(sortBy).ascending();
            if (order.equals(Constants.SortOrder.DESC)) {
                sort = Sort.by(sortBy).descending();
            }
        } else {
            sort = Sort.by("scanStartTime").descending();
        }

        List<ProcessSimplifiedForImageManagement> data = new ArrayList<>();
        if (sort != null) {
            data = StreamSupport
                    .stream(processImageRepository.findAll(predicate, sort).spliterator(), false)
                    .collect(Collectors.toList());
        }
        else {
            data = StreamSupport
                    .stream(processImageRepository.findAll(predicate).spliterator(), false)
                    .collect(Collectors.toList());
        }

        return data;
    }

    @Override
    public List<HistorySimplifiedForProcessTableManagement> getExportProcessTask(String taskNumber, Long modeId, String taskStatus, Long fieldId, String userName, Date startTime, Date endTime, String sortBy, String order, String idList) {

        BooleanBuilder predicate = getPredicateProcess(taskNumber, modeId, taskStatus, fieldId, userName, startTime, endTime); //get filter from input parameters
        String[] splits = idList.split(",");
        List<Long> taskIdList = new ArrayList<>();
        for(String idStr: splits) {
            taskIdList.add(Long.valueOf(idStr));
        }
        predicate.and(QHistorySimplifiedForProcessTableManagement.historySimplifiedForProcessTableManagement.taskId.in(taskIdList));
        Sort sort = null;
        if (StringUtils.isNotBlank(order) && StringUtils.isNotEmpty(sortBy)) {
            sort = Sort.by(sortBy).ascending();
            if (order.equals(Constants.SortOrder.DESC)) {
                sort = Sort.by(sortBy).descending();
            }
        } else {
            sort = Sort.by("scanStartTime").descending();
        }

        List<HistorySimplifiedForProcessTableManagement> data = new ArrayList<>();
        if (sort != null) {
            data = StreamSupport
                    .stream(historyTableProcessRepository.findAll(predicate, sort).spliterator(), false)
                    .collect(Collectors.toList());
        }
        else {
            data = StreamSupport
                    .stream(historyTableProcessRepository.findAll(predicate).spliterator(), false)
                    .collect(Collectors.toList());
        }

        return data;
    }

    /**
     * get detailed info of an invalid task
     *
     * @param taskId
     * @return
     */
    @Override
    public SerTaskSimplifiedForProcessTaskManagement getOne(Long taskId) {

        Date startTime = new Date();
        QSerTaskSimplifiedForProcessTaskManagement builder = QSerTaskSimplifiedForProcessTaskManagement.serTaskSimplifiedForProcessTaskManagement;
        Optional<SerTaskSimplifiedForProcessTaskManagement> data = serTaskRepository.findOne(builder.taskId.eq(taskId));
        if (!data.isPresent()) {
            return null;
        }

        SerPlatformCheckParamsSimplifiedForTaskManagement platformCheckParams = new SerPlatformCheckParamsSimplifiedForTaskManagement();
        List<SerPlatformCheckParams> serPlatformCheckParams = serPlatformCheckParamRepository.findAll();
        if (!serPlatformCheckParams.isEmpty()) {

            SerPlatformCheckParams checkParams = serPlatformCheckParams.get(0);
            platformCheckParams.setScanRecogniseColour(checkParams.getScanRecogniseColour());
            platformCheckParams.setJudgeRecogniseColour(checkParams.getJudgeRecogniseColour());
            platformCheckParams.setHandRecogniseColour(checkParams.getHandRecogniseColour());
            platformCheckParams.setDisplayDeleteSuspicionColour(checkParams.getDisplayDeleteSuspicionColour());
            platformCheckParams.setDisplayDeleteSuspicion(checkParams.getDisplayDeleteSuspicion());
        }

        SerTaskSimplifiedForProcessTaskManagement serTask = data.get();
        serTask.setPlatFormCheckParams(platformCheckParams);
        Date endTime = new Date();
        long diff = endTime.getTime() - startTime.getTime();
        return data.get();
    }







}
