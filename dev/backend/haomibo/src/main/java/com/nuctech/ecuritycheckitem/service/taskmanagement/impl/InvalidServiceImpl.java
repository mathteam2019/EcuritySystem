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
import com.nuctech.ecuritycheckitem.models.db.SerPlatformCheckParams;
import com.nuctech.ecuritycheckitem.models.reusables.CategoryUser;
import com.nuctech.ecuritycheckitem.models.simplifieddb.*;
import com.nuctech.ecuritycheckitem.repositories.*;
import com.nuctech.ecuritycheckitem.service.auth.AuthService;
import com.nuctech.ecuritycheckitem.service.taskmanagement.HistoryService;
import com.nuctech.ecuritycheckitem.service.taskmanagement.InvalidService;
import com.nuctech.ecuritycheckitem.utils.PageResult;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class InvalidServiceImpl implements InvalidService {



    @Autowired
    HistoryRepository historyRepository;

    @Autowired
    HistoryTableProcessRepository historyTableProcessRepository;

    @Autowired
    HistoryTableInvalidRepository historyTableInvalidRepository;

    @Autowired
    HistoryImageRepository historyImageRepository;

    @Autowired
    HistoryTableRepository historyTableRepository;

    @Autowired
    InvalidImageRepository invalidImageRepository;

    @Autowired
    SerTaskInvalidRepository serTaskInvalidRepository;

    @Autowired
    AuthService authService;

    @Autowired
    SerPlatformCheckParamRepository serPlatformCheckParamRepository;
    private QHistorySimplifiedForHistoryTableManagement builder;




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
    private BooleanBuilder getInvalidPredicate(String taskNumber, Long modeId, String taskStatus, Long fieldId, String userName, Date startTime, Date endTime) {

        QHistorySimplifiedForInvalidTableManagement builder = QHistorySimplifiedForInvalidTableManagement.historySimplifiedForInvalidTableManagement;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        if (!StringUtils.isEmpty(taskNumber)) { //if task number is input
            predicate.and(builder.taskNumber.contains(taskNumber));
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
        CategoryUser categoryUser = authService.getDataCategoryUserList();
        if(categoryUser.isAll() == false) {
            predicate.and(builder.createdBy.in(categoryUser.getUserIdList()).or(builder.editedBy.in(categoryUser.getUserIdList())));
        }
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
    public PageResult<HistorySimplifiedForInvalidTableManagement> getInvalidTaskByFilter(String taskNumber, Long modeId, String taskStatus, Long fieldId, String userName, Date startTime, Date endTime, String sortBy, String order, Integer currentPage, Integer perPage) {
        Date start = new Date();
        QHistorySimplifiedForInvalidTableManagement builder = QHistorySimplifiedForInvalidTableManagement.historySimplifiedForInvalidTableManagement;
        BooleanBuilder predicate = getInvalidPredicate(taskNumber, modeId, taskStatus, fieldId, userName, startTime, endTime); //get predicate from input parameters
        //predicate.and(builder.scanInvalid.eq(HistorySimplifiedForHistoryTableManagement.InvalidType.TRUE));
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


        long total = historyTableInvalidRepository.count(predicate); //get total count from database using repsitory
        Date end = new Date();
        long diff = end.getTime() - start.getTime();
        List<HistorySimplifiedForInvalidTableManagement> data = historyTableInvalidRepository.findAll(predicate, pageRequest).getContent(); //get list of data from database using repository
        end = new Date();
        long diff1 = end.getTime() - start.getTime();
        return new PageResult<>(total, data);

    }





    @Override
    public List<HistorySimplifiedForInvalidTableManagement> getExportInvalidTask(String taskNumber, Long modeId, String taskStatus, Long fieldId, String userName, Date startTime, Date endTime, String sortBy, String order, boolean isAll, String idList) {

        BooleanBuilder predicate = getInvalidPredicate(taskNumber, modeId, taskStatus, fieldId, userName, startTime, endTime); //get filter from input parameters
        if(isAll == false) {
            String[] splits = idList.split(",");
            List<Long> taskIdList = new ArrayList<>();
            for(String idStr: splits) {
                taskIdList.add(Long.valueOf(idStr));
            }
            predicate.and(QHistorySimplifiedForInvalidTableManagement.historySimplifiedForInvalidTableManagement.taskId.in(taskIdList));
        }

        Long limit = Constants.MAX_EXPORT_NUMBER;
        int currentPage = 0;
        int perPage = limit.intValue();
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

        List<HistorySimplifiedForInvalidTableManagement> data = new ArrayList<>();
        data = StreamSupport
                .stream(historyTableInvalidRepository.findAll(predicate, pageRequest).spliterator(), false)
                .collect(Collectors.toList());

        return data;
    }


    @Override
    public List<InvalidSimplifiedForImageManagement> getExportInvalidImage(String taskNumber, Long modeId, String taskStatus, Long fieldId, String userName, Date startTime, Date endTime, String sortBy, String order, boolean isAll, String idList) {

        QInvalidSimplifiedForImageManagement builder = QInvalidSimplifiedForImageManagement.invalidSimplifiedForImageManagement;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        if (!StringUtils.isEmpty(taskNumber)) { //if task number is input
            predicate.and(builder.taskNumber.contains(taskNumber));
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
        CategoryUser categoryUser = authService.getDataCategoryUserList();
        if(categoryUser.isAll() == false) {
            predicate.and(builder.createdBy.in(categoryUser.getUserIdList()).or(builder.editedBy.in(categoryUser.getUserIdList())));
        }

        if(isAll == false) {
            String[] splits = idList.split(",");
            List<Long> taskIdList = new ArrayList<>();
            for(String idStr: splits) {
                taskIdList.add(Long.valueOf(idStr));
            }
            predicate.and(builder.taskId.in(taskIdList));
        }

        Long limit = Constants.MAX_EXPORT_NUMBER;
        int currentPage = 0;
        int perPage = limit.intValue();
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

        List<InvalidSimplifiedForImageManagement> data = new ArrayList<>();
        data = StreamSupport
                .stream(invalidImageRepository.findAll(predicate, pageRequest).spliterator(), false)
                .collect(Collectors.toList());

        return data;
    }

    /**
     * Get one HistorySimplifiedForHistoryTaskManagement information
     * @param taskId : id of a HistorySimplifiedForHistoryTaskManagement task
     * @return
     */
    @Override
    public SerTaskSimplifiedForInvalidTaskManagement getOne(Long taskId) {
        Date startTime = new Date();
        QSerTaskSimplifiedForInvalidTaskManagement builder = QSerTaskSimplifiedForInvalidTaskManagement.serTaskSimplifiedForInvalidTaskManagement;
        Optional<SerTaskSimplifiedForInvalidTaskManagement> data = serTaskInvalidRepository.findOne(builder.taskId.eq(taskId));
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

        SerTaskSimplifiedForInvalidTaskManagement serTask = data.get();
        serTask.setPlatFormCheckParams(platformCheckParams);
        Date endTime = new Date();
        long diff = endTime.getTime() - startTime.getTime();
        return data.get();
    }


}
