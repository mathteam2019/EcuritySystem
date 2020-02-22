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
import com.nuctech.ecuritycheckitem.models.simplifieddb.QSerTaskSimplifiedForProcessTaskManagement;
import com.nuctech.ecuritycheckitem.models.simplifieddb.SerPlatformCheckParamsSimplifiedForTaskManagement;
import com.nuctech.ecuritycheckitem.models.simplifieddb.SerTaskSimplifiedForProcessTaskManagement;
import com.nuctech.ecuritycheckitem.repositories.SerPlatformCheckParamRepository;
import com.nuctech.ecuritycheckitem.repositories.SerTaskRepository;
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
        QSerTaskSimplifiedForProcessTaskManagement builder = QSerTaskSimplifiedForProcessTaskManagement.serTaskSimplifiedForProcessTaskManagement;

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
            Predicate scanUserName = builder.serScan.scanPointsman.userName.contains(userName)
                    .or(builder.serJudgeGraph.judgeUser.userName.contains(userName))
                    .or(builder.serJudgeGraph.judgeUser.userName.contains(userName));
            predicate.and(scanUserName);
        }
        if (startTime != null) {
            predicate.and(builder.createdTime.after(startTime));
        }
        if (endTime != null) {
            predicate.and(builder.createdTime.before(endTime));
        }
        CategoryUser categoryUser = authService.getDataCategoryUserList();
        if(categoryUser.isAll() == false) {
            predicate.and(builder.createdBy.in(categoryUser.getUserIdList()).or(builder.editedBy.in(categoryUser.getUserIdList())));
        }

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
    public PageResult<SerTaskSimplifiedForProcessTaskManagement> getProcessTaskByFilter(String taskNumber, Long modeId, String taskStatus, Long fieldId, String userName, Date startTime, Date endTime, String sortBy, String order, Integer currentPage, Integer perPage) {

        QSerTaskSimplifiedForProcessTaskManagement builder = QSerTaskSimplifiedForProcessTaskManagement.serTaskSimplifiedForProcessTaskManagement;
        BooleanBuilder predicate = getPredicate(taskNumber, modeId, taskStatus, fieldId, userName, startTime, endTime);
        predicate.and(builder.serScan.scanInvalid.eq(SerScan.Invalid.FALSE));
        predicate.and(builder.serCheckResultList.size().eq(0));

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
        }

        long total = serTaskRepository.count(predicate);
        List<SerTaskSimplifiedForProcessTaskManagement> data = serTaskRepository.findAll(predicate, pageRequest).getContent();

        return new PageResult<SerTaskSimplifiedForProcessTaskManagement>(total, data);

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
        predicate.and(builder.serCheckResultList.size().eq(0));

        Sort sort = null;
        if (StringUtils.isNotBlank(order) && StringUtils.isNotEmpty(sortBy)) {
            if(sortBy.equals("scanStartTime")) {
                sortBy = "serScan.scanStartTime";
            } else if(sortBy.equals("scanEndTime")) {
                sortBy = "serScan.scanEndTime";
            }
            //sort = new Sort(Sort.Direction.ASC, new ArrayList<>(Arrays.asList(sortBy)));
            if (order.equals(Constants.SortOrder.DESC)) {
                //sort = new Sort(Sort.Direction.DESC, new ArrayList<>(Arrays.asList(sortBy)));
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
    public List<SerTaskSimplifiedForProcessTaskManagement> getExportProcessTask(String taskNumber, Long modeId, String taskStatus, Long fieldId, String userName, Date startTime, Date endTime, String sortBy, String order, String idList) {

        QSerTaskSimplifiedForProcessTaskManagement builder = QSerTaskSimplifiedForProcessTaskManagement.serTaskSimplifiedForProcessTaskManagement;
        BooleanBuilder predicate = getPredicate(taskNumber, modeId, taskStatus, fieldId, userName, startTime, endTime);
        predicate.and(builder.serScan.scanInvalid.eq(SerScan.Invalid.FALSE));
        predicate.and(builder.serCheckResultList.size().eq(0));
        String[] splits = idList.split(",");
        List<Long> fieldIdList = new ArrayList<>();
        for(String idStr: splits) {
            fieldIdList.add(Long.valueOf(idStr));
        }
        predicate.and(QSerTaskSimplifiedForProcessTaskManagement.serTaskSimplifiedForProcessTaskManagement.taskId.in(fieldIdList));

        Sort sort = null;
        if (StringUtils.isNotBlank(order) && StringUtils.isNotEmpty(sortBy)) {
            if(sortBy.equals("scanStartTime")) {
                sortBy = "serScan.scanStartTime";
            } else if(sortBy.equals("scanEndTime")) {
                sortBy = "serScan.scanEndTime";
            }
            //sort = new Sort(Sort.Direction.ASC, new ArrayList<>(Arrays.asList(sortBy)));
            if (order.equals(Constants.SortOrder.DESC)) {
                //sort = new Sort(Sort.Direction.DESC, new ArrayList<>(Arrays.asList(sortBy)));
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
    public PageResult<SerTaskSimplifiedForProcessTaskManagement> getInvalidTaskByFilter(String taskNumber, Long modeId, String taskStatus, Long fieldId, String userName, Date startTime, Date endTime, String sortBy, String order, Integer currentPage, Integer perPage) {

        QSerTaskSimplifiedForProcessTaskManagement builder = QSerTaskSimplifiedForProcessTaskManagement.serTaskSimplifiedForProcessTaskManagement;
        BooleanBuilder predicate = getPredicate(taskNumber, modeId, taskStatus, fieldId, userName, startTime, endTime);
        predicate.and(builder.serScan.scanInvalid.eq(SerScan.Invalid.TRUE));

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
        }

        long total = serTaskRepository.count(predicate);
        List<SerTaskSimplifiedForProcessTaskManagement> data = serTaskRepository.findAll(predicate, pageRequest).getContent();

        return new PageResult<SerTaskSimplifiedForProcessTaskManagement>(total, data);

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
            //sort = new Sort(Sort.Direction.ASC, new ArrayList<>(Arrays.asList(sortBy)));
            if (order.equals(Constants.SortOrder.DESC)) {
                //sort = new Sort(Sort.Direction.DESC, new ArrayList<>(Arrays.asList(sortBy)));
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
    public List<SerTaskSimplifiedForProcessTaskManagement> getExportInvalidTask(String taskNumber, Long modeId, String taskStatus, Long fieldId, String userName, Date startTime, Date endTime, String sortBy, String order, String idList) {

        QSerTaskSimplifiedForProcessTaskManagement builder = QSerTaskSimplifiedForProcessTaskManagement.serTaskSimplifiedForProcessTaskManagement;
        BooleanBuilder predicate = getPredicate(taskNumber, modeId, taskStatus, fieldId, userName, startTime, endTime);
        predicate.and(builder.serScan.scanInvalid.eq(SerScan.Invalid.TRUE));
        String[] splits = idList.split(",");
        List<Long> taskIdList = new ArrayList<>();
        for(String idStr: splits) {
            taskIdList.add(Long.valueOf(idStr));
        }
        predicate.and(QSerTaskSimplifiedForProcessTaskManagement.serTaskSimplifiedForProcessTaskManagement.taskId.in(taskIdList));

        Sort sort = null;

        if (StringUtils.isNotBlank(order) && StringUtils.isNotEmpty(sortBy)) {
            if(sortBy.equals("scanStartTime")) {
                sortBy = "serScan.scanStartTime";
            } else if(sortBy.equals("scanEndTime")) {
                sortBy = "serScan.scanEndTime";
            }
            //sort = new Sort(Sort.Direction.ASC, new ArrayList<>(Arrays.asList(sortBy)));
            if (order.equals(Constants.SortOrder.DESC)) {
                //sort = new Sort(Sort.Direction.DESC, new ArrayList<>(Arrays.asList(sortBy)));
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

}
