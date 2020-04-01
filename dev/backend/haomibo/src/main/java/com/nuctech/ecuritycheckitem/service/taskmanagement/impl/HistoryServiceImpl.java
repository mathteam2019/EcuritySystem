/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（历史任务 service 1.0)
 * 文件名：	HistoryServiceImpl.java
 * 描述：	Service to get invalid task data from database using models and repositories
 * 作者名：	Tiny
 * 日期：	2019/12/20
 *
 */

package com.nuctech.ecuritycheckitem.service.taskmanagement.impl;

import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.models.db.QSysField;
import com.nuctech.ecuritycheckitem.models.db.SerPlatformCheckParams;
import com.nuctech.ecuritycheckitem.models.reusables.CategoryUser;
import com.nuctech.ecuritycheckitem.models.simplifieddb.*;
import com.nuctech.ecuritycheckitem.repositories.HistoryImageRepository;
import com.nuctech.ecuritycheckitem.repositories.HistoryRepository;
import com.nuctech.ecuritycheckitem.repositories.HistoryTableRepository;
import com.nuctech.ecuritycheckitem.repositories.SerPlatformCheckParamRepository;
import com.nuctech.ecuritycheckitem.service.auth.AuthService;
import com.nuctech.ecuritycheckitem.service.taskmanagement.HistoryService;
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
public class HistoryServiceImpl implements HistoryService {

    @Autowired
    HistoryRepository historyRepository;

    @Autowired
    HistoryImageRepository historyImageRepository;

    @Autowired
    HistoryTableRepository historyTableRepository;

    @Autowired
    AuthService authService;

    @Autowired
    SerPlatformCheckParamRepository serPlatformCheckParamRepository;
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
    private BooleanBuilder getPredicate(String taskNumber, Long modeId, String taskStatus, Long fieldId, String userName, Date startTime, Date endTime) {

        QHistorySimplifiedForHistoryTableManagement builder = QHistorySimplifiedForHistoryTableManagement.historySimplifiedForHistoryTableManagement;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        if (taskNumber != null) { //if task number is input
            predicate.and(builder.task.taskNumber.contains(taskNumber));
        }
        if (modeId != null) { //if mode id is input
            predicate.and(builder.workMode.modeId.eq(modeId));
        }

        if (fieldId != null) { //if field id is input
            predicate.and(builder.task.field.fieldId.eq(fieldId));
        }
        if (userName != null && !userName.isEmpty()) { //if username is input
            Predicate scanUserName = builder.scanPointsman.userName.contains(userName);
            predicate.and(scanUserName);
        }
        if (startTime != null) { //if start time is input
            predicate.and(builder.scanStartTime.after(startTime));
        }
        if (endTime != null) { //if end time is input
            predicate.and(builder.scanStartTime.before(endTime));
        }

        predicate.and(builder.serCheckResultList.isNotEmpty());
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
    public PageResult<HistorySimplifiedForHistoryTableManagement> getHistoryTaskByFilter(String taskNumber, Long modeId, String taskStatus, Long fieldId, String userName, Date startTime, Date endTime, String sortBy, String order, Integer currentPage, Integer perPage) {
        Date start = new Date();
        QHistorySimplifiedForHistoryTaskManagement builder = QHistorySimplifiedForHistoryTaskManagement.historySimplifiedForHistoryTaskManagement;
        BooleanBuilder predicate = getPredicate(taskNumber, modeId, taskStatus, fieldId, userName, startTime, endTime); //get predicate from input parameters

        PageRequest pageRequest = PageRequest.of(currentPage, perPage);
        if (StringUtils.isNotBlank(order) && StringUtils.isNotEmpty(sortBy)) {
            if(sortBy.equals("taskNumber")) {
                sortBy = "task.taskNumber";
            }
            if (order.equals(Constants.SortOrder.ASC)) {
                pageRequest = PageRequest.of(currentPage, perPage, Sort.by(sortBy).ascending());
            }
            else {
                pageRequest = PageRequest.of(currentPage, perPage, Sort.by(sortBy).descending());
            }
        } else {
            pageRequest = PageRequest.of(currentPage, perPage, Sort.by("scanStartTime").descending());
        }


        long total = historyTableRepository.count(predicate); //get total count from database using repsitory
        Date end = new Date();
        long diff = end.getTime() - start.getTime();
        List<HistorySimplifiedForHistoryTableManagement> data = historyTableRepository.findAll(predicate, pageRequest).getContent(); //get list of data from database using repository
        end = new Date();
        long diff1 = end.getTime() - start.getTime();
        return new PageResult<>(total, data);

    }

    /**
     * get all filtered records of HistorySimplifiedForHistoryTaskManagement task
     * @param taskNumber : task number
     * @param modeId : workmode id
     * @param taskStatus : task status
     * @param fieldId : scene id
     * @param userName : user name
     * @param startTime : start time
     * @param endTime : end time
     * @return
     */
    @Override
    public List<HistorySimplifiedForHistoryTableManagement> getHistoryTaskAll(String taskNumber, Long modeId, String taskStatus, Long fieldId, String userName, Date startTime, Date endTime, String sortBy, String order) {

        BooleanBuilder predicate = getPredicate(taskNumber, modeId, taskStatus, fieldId, userName, startTime, endTime); //get filter from input parameters

        Sort sort = null;
        if (StringUtils.isNotBlank(order) && StringUtils.isNotEmpty(sortBy)) {
            if(sortBy.equals("taskNumber")) {
                sortBy = "task.taskNumber";
            }

            sort = Sort.by(sortBy).ascending();
            if (order.equals(Constants.SortOrder.DESC)) {
                sort = Sort.by(sortBy).descending();
            }
        }

        List<HistorySimplifiedForHistoryTableManagement> data = new ArrayList<>();
        if (sort != null) {
            data = StreamSupport
                    .stream(historyTableRepository.findAll(predicate, sort).spliterator(), false)
                    .collect(Collectors.toList());
        }
        else {
            data = StreamSupport
                    .stream(historyTableRepository.findAll(predicate).spliterator(), false)
                    .collect(Collectors.toList());
        }

        return data;
    }

    @Override
    public List<HistorySimplifiedForHistoryTableManagement> getExportHistoryTask(String taskNumber, Long modeId, String taskStatus, Long fieldId, String userName, Date startTime, Date endTime, String sortBy, String order, String idList) {

        BooleanBuilder predicate = getPredicate(taskNumber, modeId, taskStatus, fieldId, userName, startTime, endTime); //get filter from input parameters
        String[] splits = idList.split(",");
        List<Long> historyIdList = new ArrayList<>();
        for(String idStr: splits) {
            historyIdList.add(Long.valueOf(idStr));
        }
        predicate.and(QHistorySimplifiedForHistoryTableManagement.historySimplifiedForHistoryTableManagement.historyId.in(historyIdList));
        Sort sort = null;
        if (StringUtils.isNotBlank(order) && StringUtils.isNotEmpty(sortBy)) {
            if(sortBy.equals("taskNumber")) {
                sortBy = "task.taskNumber";
            }

            sort = Sort.by(sortBy).ascending();
            if (order.equals(Constants.SortOrder.DESC)) {
                sort = Sort.by(sortBy).descending();
            }
        } else {
            sort = Sort.by("scanStartTime").descending();
        }

        List<HistorySimplifiedForHistoryTableManagement> data = new ArrayList<>();
        if (sort != null) {
            data = StreamSupport
                    .stream(historyTableRepository.findAll(predicate, sort).spliterator(), false)
                    .collect(Collectors.toList());
        }
        else {
            data = StreamSupport
                    .stream(historyTableRepository.findAll(predicate).spliterator(), false)
                    .collect(Collectors.toList());
        }

        return data;
    }


    @Override
    public List<HistorySimplifiedForHistoryImageManagement> getExportHistoryImage(String taskNumber, Long modeId, String taskStatus, Long fieldId, String userName, Date startTime, Date endTime, String sortBy, String order, String idList) {

        QHistorySimplifiedForHistoryImageManagement builder = QHistorySimplifiedForHistoryImageManagement.historySimplifiedForHistoryImageManagement;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());
        String[] splits = idList.split(",");
        List<Long> historyIdList = new ArrayList<>();
        for(String idStr: splits) {
            historyIdList.add(Long.valueOf(idStr));
        }
        predicate.and(builder.historyId.in(historyIdList));
        Sort sort = null;
        if (StringUtils.isNotBlank(order) && StringUtils.isNotEmpty(sortBy)) {
            if(sortBy.equals("taskNumber")) {
                sortBy = "task.taskNumber";
            }

            sort = Sort.by(sortBy).ascending();
            if (order.equals(Constants.SortOrder.DESC)) {
                sort = Sort.by(sortBy).descending();
            }
        } else {
            sort = Sort.by("scanStartTime").descending();
        }

        List<HistorySimplifiedForHistoryImageManagement> data = new ArrayList<>();
        if (sort != null) {
            data = StreamSupport
                    .stream(historyImageRepository.findAll(predicate, sort).spliterator(), false)
                    .collect(Collectors.toList());
        }
        else {
            data = StreamSupport
                    .stream(historyImageRepository.findAll(predicate).spliterator(), false)
                    .collect(Collectors.toList());
        }

        return data;
    }

    /**
     * Get one HistorySimplifiedForHistoryTaskManagement information
     * @param taskId : id of a HistorySimplifiedForHistoryTaskManagement task
     * @return
     */
    @Override
    public HistorySimplifiedForHistoryTaskManagement getOne(Long taskId) {
        QHistorySimplifiedForHistoryTaskManagement builder = QHistorySimplifiedForHistoryTaskManagement.historySimplifiedForHistoryTaskManagement;
        Optional<HistorySimplifiedForHistoryTaskManagement> data = historyRepository.findOne(builder.historyId.eq(taskId)); //get a HistorySimplifiedForHistoryTaskManagement record from database using repository
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

        HistorySimplifiedForHistoryTaskManagement history = data.get();
        history.setPlatFormCheckParams(platformCheckParams);
        return history;
    }


}
