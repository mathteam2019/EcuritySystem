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
import com.nuctech.ecuritycheckitem.models.db.QSysField;
import com.nuctech.ecuritycheckitem.models.db.SerPlatformCheckParams;
import com.nuctech.ecuritycheckitem.models.db.SerScan;
import com.nuctech.ecuritycheckitem.models.reusables.CategoryUser;
import com.nuctech.ecuritycheckitem.models.simplifieddb.*;
import com.nuctech.ecuritycheckitem.repositories.SerPlatformCheckParamRepository;
import com.nuctech.ecuritycheckitem.repositories.SerTaskRepository;
import com.nuctech.ecuritycheckitem.repositories.SerTaskTableRepository;
import com.nuctech.ecuritycheckitem.service.auth.AuthService;
import com.nuctech.ecuritycheckitem.service.taskmanagement.TaskService;
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
public class TaskServiceImpl implements TaskService {

    @Autowired
    SerTaskRepository serTaskRepository;

    @Autowired
    SerTaskTableRepository serTaskTableRepository;

    @Autowired
    SerPlatformCheckParamRepository serPlatformCheckParamRepository;

    @Autowired
    AuthService authService;

    /**
     * Get filter condition
     *
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
        QSerTaskSimplifiedForProcessTableManagement builder = QSerTaskSimplifiedForProcessTableManagement.serTaskSimplifiedForProcessTableManagement;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());


        if (taskNumber != null) {
            predicate.and(builder.taskNumber.contains(taskNumber));
        }
        if (modeId != null) {
            predicate.and(builder.workFlow.workMode.modeId.eq(modeId));
        }
        if (taskStatus != null && !taskStatus.isEmpty()) {
            predicate.and(builder.taskStatus.eq(taskStatus));
        }
        if (fieldId != null) {
            predicate.and(builder.fieldId.eq(fieldId));
        }
        if (userName != null && !userName.isEmpty()) {
            Predicate scanUserName = builder.serScan.scanPointsman.userName.contains(userName);
            predicate.and(scanUserName);
        }
        if (startTime != null) {
            predicate.and(builder.serScan.scanStartTime.after(startTime));
        }
        if (endTime != null) {
            predicate.and(builder.serScan.scanStartTime.before(endTime));
        }


//        CategoryUser categoryUser = authService.getDataCategoryUserList();
//        if(categoryUser.isAll() == false) {
//            predicate.and(builder.createdBy.in(categoryUser.getUserIdList()).or(builder.editedBy.in(categoryUser.getUserIdList())));
//        }

        return predicate;
    }

    /**
     * Get paginated and filtered records of process task
     *
     * @param taskNumber  : task number
     * @param modeId      : workmode id
     * @param taskStatus  : task status
     * @param fieldId     : scene id
     * @param userName    : user name
     * @param startTime   : start time
     * @param endTime     : end time
     * @param currentPage : current page no
     * @param perPage     : record count per page
     * @return
     */
    @Override
    public PageResult<SerTaskSimplifiedForProcessTableManagement> getProcessTaskByFilter(String taskNumber, Long modeId, String taskStatus, Long fieldId, String userName, Date startTime, Date endTime, String sortBy, String order, Integer currentPage, Integer perPage) {

        QSerTaskSimplifiedForProcessTableManagement builder = QSerTaskSimplifiedForProcessTableManagement.serTaskSimplifiedForProcessTableManagement;
        BooleanBuilder predicate = getPredicate(taskNumber, modeId, taskStatus, fieldId, userName, startTime, endTime);
        predicate.and(builder.scanInvalid.eq(SerScan.Invalid.FALSE));
        predicate.and(builder.taskStatus.ne(HistorySimplifiedForHistoryTableManagement.TaskStatusType.ALL));
        //predicate.and(builder.serCheckResult.checkResultId.isNull());

        PageRequest pageRequest = PageRequest.of(currentPage, perPage);
        if (StringUtils.isNotBlank(order) && StringUtils.isNotEmpty(sortBy)) {
            if(sortBy.equals("scanStartTime")) {
                sortBy = "serScan.scanStartTime";
            } else if(sortBy.equals("scanEndTime")) {
                sortBy = "serScan.scanEndTime";
            }
            if (order.equals(Constants.SortOrder.ASC)) {
                pageRequest = PageRequest.of(currentPage, perPage, Sort.by(sortBy).ascending());
            } else {
                pageRequest = PageRequest.of(currentPage, perPage, Sort.by(sortBy).descending());
            }
        } else {
            pageRequest = PageRequest.of(currentPage, perPage, Sort.by("taskId").descending());
        }
        Date start = new Date();

        long total = serTaskTableRepository.count(predicate);
        Date end = new Date();
        long diff = end.getTime() - start.getTime();
        List<SerTaskSimplifiedForProcessTableManagement> data = serTaskTableRepository.findAll(predicate, pageRequest).getContent();

        end = new Date();
        long diff1 = end.getTime() - start.getTime();

        return new PageResult<SerTaskSimplifiedForProcessTableManagement>(total, data);

    }

    /**
     * get all filtered records of process task
     *
     * @param taskNumber : task number
     * @param modeId     : workmode id
     * @param taskStatus : task status
     * @param fieldId    : scene id
     * @param userName   : user name
     * @param startTime  : start time
     * @param endTime    : end time
     * @return
     */
    @Override
    public List<SerTaskSimplifiedForProcessTaskManagement> getProcessTaskAll(String taskNumber, Long modeId, String taskStatus, Long fieldId, String userName, Date startTime, Date endTime, String sortBy, String order) {

        QSerTaskSimplifiedForProcessTaskManagement builder = QSerTaskSimplifiedForProcessTaskManagement.serTaskSimplifiedForProcessTaskManagement;
        BooleanBuilder predicate = getPredicate(taskNumber, modeId, taskStatus, fieldId, userName, startTime, endTime);
        predicate.and(builder.serScan.scanInvalid.eq(SerScan.Invalid.FALSE));
        predicate.and(builder.serCheckResultList.isEmpty());

        Sort sort = null;
        if (StringUtils.isNotBlank(order) && StringUtils.isNotEmpty(sortBy)) {
            if(sortBy.equals("scanStartTime")) {
                sortBy = "serScan.scanStartTime";
            } else if(sortBy.equals("scanEndTime")) {
                sortBy = "serScan.scanEndTime";
            }
            sort = Sort.by(sortBy).ascending();
            if (order.equals(Constants.SortOrder.DESC)) {
                sort = Sort.by(sortBy).descending();
            }
        }

        List<SerTaskSimplifiedForProcessTaskManagement> data = new ArrayList<>();
        if (sort != null) {
            data = StreamSupport
                    .stream(serTaskRepository.findAll(predicate, sort).spliterator(), false)
                    .collect(Collectors.toList());
        }
        else {
            data = StreamSupport
                    .stream(serTaskRepository.findAll(predicate).spliterator(), false)
                    .collect(Collectors.toList());
        }

        return data;
    }

    /**
     * get all filtered records of process task
     *
     * @param taskNumber : task number
     * @param modeId     : workmode id
     * @param taskStatus : task status
     * @param fieldId    : scene id
     * @param userName   : user name
     * @param startTime  : start time
     * @param endTime    : end time
     * @return
     */
    @Override
    public List<SerTaskSimplifiedForProcessTableManagement> getExportProcessTask(String taskNumber, Long modeId, String taskStatus, Long fieldId, String userName, Date startTime, Date endTime, String sortBy, String order, String idList) {

        QSerTaskSimplifiedForProcessTableManagement builder = QSerTaskSimplifiedForProcessTableManagement.serTaskSimplifiedForProcessTableManagement;
        BooleanBuilder predicate = getPredicate(taskNumber, modeId, taskStatus, fieldId, userName, startTime, endTime);
        predicate.and(builder.scanInvalid.eq(SerScan.Invalid.FALSE));
        predicate.and(builder.taskStatus.ne(HistorySimplifiedForHistoryTableManagement.TaskStatusType.ALL));
        //predicate.and(builder.serCheckResult.checkResultId.isNull());
        //predicate.and(builder.serCheckResultList.size().eq(0));
        String[] splits = idList.split(",");
        List<Long> fieldIdList = new ArrayList<>();
        for(String idStr: splits) {
            fieldIdList.add(Long.valueOf(idStr));
        }
        predicate.and(QSerTaskSimplifiedForProcessTableManagement.serTaskSimplifiedForProcessTableManagement.taskId.in(fieldIdList));

        Sort sort = null;
        if (StringUtils.isNotBlank(order) && StringUtils.isNotEmpty(sortBy)) {
            if(sortBy.equals("scanStartTime")) {
                sortBy = "serScan.scanStartTime";
            } else if(sortBy.equals("scanEndTime")) {
                sortBy = "serScan.scanEndTime";
            }
            sort = Sort.by(sortBy).ascending();
            if (order.equals(Constants.SortOrder.DESC)) {
                sort = Sort.by(sortBy).descending();
            }
        } else {
            sort = Sort.by("serScan.scanStartTime").descending();
        }

        List<SerTaskSimplifiedForProcessTableManagement> data = new ArrayList<>();
        if (sort != null) {
            data = StreamSupport
                    .stream(serTaskTableRepository.findAll(predicate, sort).spliterator(), false)
                    .collect(Collectors.toList());
        }
        else {
            data = StreamSupport
                    .stream(serTaskTableRepository.findAll(predicate).spliterator(), false)
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
        return data.get();
    }

    /**
     * Get paginated and filtered records of invalid task
     *
     * @param taskNumber  : task number
     * @param modeId      : workmode id
     * @param taskStatus  : task status
     * @param fieldId     : scene id
     * @param userName    : user name
     * @param startTime   : start time
     * @param endTime     : end time
     * @param currentPage : current page no
     * @param perPage     : record count per page
     * @return
     */
    @Override
    public PageResult<SerTaskSimplifiedForProcessTableManagement> getInvalidTaskByFilter(String taskNumber, Long modeId, String taskStatus, Long fieldId, String userName, Date startTime, Date endTime, String sortBy, String order, Integer currentPage, Integer perPage) {

        QSerTaskSimplifiedForProcessTableManagement builder = QSerTaskSimplifiedForProcessTableManagement.serTaskSimplifiedForProcessTableManagement;
        BooleanBuilder predicate = getPredicate(taskNumber, modeId, taskStatus, fieldId, userName, startTime, endTime);
        predicate.and(builder.scanInvalid.eq(SerScan.Invalid.TRUE));

        PageRequest pageRequest = PageRequest.of(currentPage, perPage);
        if (StringUtils.isNotBlank(order) && StringUtils.isNotEmpty(sortBy)) {
            if(sortBy.equals("scanStartTime")) {
                sortBy = "serScan.scanStartTime";
            } else if(sortBy.equals("scanEndTime")) {
                sortBy = "serScan.scanEndTime";
            }
            if (order.equals(Constants.SortOrder.ASC)) {
                pageRequest = PageRequest.of(currentPage, perPage, Sort.by(sortBy).ascending());
            } else {
                pageRequest = PageRequest.of(currentPage, perPage, Sort.by(sortBy).descending());
            }
        } else {
            pageRequest = PageRequest.of(currentPage, perPage, Sort.by("taskId").descending());
        }

        Date startDate = new Date();
        long total = serTaskTableRepository.count(predicate);
        Date endDate = new Date();
        long diff = endDate.getTime() - startDate.getTime();
        List<SerTaskSimplifiedForProcessTableManagement> data = serTaskTableRepository.findAll(predicate, pageRequest).getContent();
        endDate = new Date();
        long diff1 = endDate.getTime() - startDate.getTime();
        return new PageResult<SerTaskSimplifiedForProcessTableManagement>(total, data);

    }

    /**
     * get all filtered records of invalid task
     *
     * @param taskNumber : task number
     * @param modeId     : workmode id
     * @param taskStatus : task status
     * @param fieldId    : scene id
     * @param userName   : user name
     * @param startTime  : start time
     * @param endTime    : end time
     * @return
     */
    @Override
    public List<SerTaskSimplifiedForProcessTaskManagement> getInvalidTaskAll(String taskNumber, Long modeId, String taskStatus, Long fieldId, String userName, Date startTime, Date endTime, String sortBy, String order) {

        QSerTaskSimplifiedForProcessTaskManagement builder = QSerTaskSimplifiedForProcessTaskManagement.serTaskSimplifiedForProcessTaskManagement;
        BooleanBuilder predicate = getPredicate(taskNumber, modeId, taskStatus, fieldId, userName, startTime, endTime);
        predicate.and(builder.serScan.scanInvalid.eq(SerScan.Invalid.TRUE));

        Sort sort = null;

        if (StringUtils.isNotBlank(order) && StringUtils.isNotEmpty(sortBy)) {
            if(sortBy.equals("scanStartTime")) {
                sortBy = "serScan.scanStartTime";
            } else if(sortBy.equals("scanEndTime")) {
                sortBy = "serScan.scanEndTime";
            }
            sort = Sort.by(sortBy).ascending();
            if (order.equals(Constants.SortOrder.DESC)) {
                sort = Sort.by(sortBy).descending();
            }
        } else {
            sort = Sort.by("taskId").descending();
        }

        List<SerTaskSimplifiedForProcessTaskManagement> data = new ArrayList<>();
        if (sort != null) {
            data = StreamSupport
                    .stream(serTaskRepository.findAll(predicate, sort).spliterator(), false)
                    .collect(Collectors.toList());
        }
        else {
            data = StreamSupport
                    .stream(serTaskRepository.findAll(predicate).spliterator(), false)
                    .collect(Collectors.toList());
        }

        return data;
    }

    /**
     * get all filtered records of invalid task
     *
     * @param taskNumber : task number
     * @param modeId     : workmode id
     * @param taskStatus : task status
     * @param fieldId    : scene id
     * @param userName   : user name
     * @param startTime  : start time
     * @param endTime    : end time
     * @return
     */
    @Override
    public List<SerTaskSimplifiedForProcessTableManagement> getExportInvalidTask(String taskNumber, Long modeId, String taskStatus, Long fieldId, String userName, Date startTime, Date endTime, String sortBy, String order, String idList) {

        QSerTaskSimplifiedForProcessTableManagement builder = QSerTaskSimplifiedForProcessTableManagement.serTaskSimplifiedForProcessTableManagement;
        BooleanBuilder predicate = getPredicate(taskNumber, modeId, taskStatus, fieldId, userName, startTime, endTime);
        predicate.and(builder.scanInvalid.eq(SerScan.Invalid.TRUE));
        String[] splits = idList.split(",");
        List<Long> taskIdList = new ArrayList<>();
        for(String idStr: splits) {
            taskIdList.add(Long.valueOf(idStr));
        }
        predicate.and(QSerTaskSimplifiedForProcessTableManagement.serTaskSimplifiedForProcessTableManagement.taskId.in(taskIdList));

        Sort sort = null;

        if (StringUtils.isNotBlank(order) && StringUtils.isNotEmpty(sortBy)) {
            if(sortBy.equals("scanStartTime")) {
                sortBy = "serScan.scanStartTime";
            } else if(sortBy.equals("scanEndTime")) {
                sortBy = "serScan.scanEndTime";
            }
            sort = Sort.by(sortBy).ascending();
            if (order.equals(Constants.SortOrder.DESC)) {
                sort = Sort.by(sortBy).descending();
            }
        } else {
            sort = Sort.by("taskId").descending();
        }

        List<SerTaskSimplifiedForProcessTableManagement> data = new ArrayList<>();
        if (sort != null) {
            data = StreamSupport
                    .stream(serTaskTableRepository.findAll(predicate, sort).spliterator(), false)
                    .collect(Collectors.toList());
        }
        else {
            data = StreamSupport
                    .stream(serTaskTableRepository.findAll(predicate).spliterator(), false)
                    .collect(Collectors.toList());
        }

        return data;
    }

}
