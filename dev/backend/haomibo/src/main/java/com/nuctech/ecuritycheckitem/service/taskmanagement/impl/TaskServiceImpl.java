package com.nuctech.ecuritycheckitem.service.taskmanagement.impl;

import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.models.db.QSerTask;
import com.nuctech.ecuritycheckitem.models.db.SerScan;
import com.nuctech.ecuritycheckitem.models.db.SerTask;
import com.nuctech.ecuritycheckitem.repositories.SerTaskRepository;
import com.nuctech.ecuritycheckitem.service.taskmanagement.TaskService;
import com.nuctech.ecuritycheckitem.utils.PageResult;
import com.nuctech.ecuritycheckitem.utils.Utils;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
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

    /**
     * Get filter condition
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
        QSerTask builder = QSerTask.serTask;

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

        return predicate;
    }

    /**
     * Get paginated and filtered records of process task
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
    public PageResult<SerTask> getProcessTaskByFilter(String taskNumber, Long modeId, String taskStatus, Long fieldId, String userName, Date startTime, Date endTime, String sortBy, String order, Integer currentPage, Integer perPage) {

        QSerTask builder = QSerTask.serTask;
        BooleanBuilder predicate = getPredicate(taskNumber, modeId, taskStatus, fieldId, userName, startTime, endTime);
        predicate.and(builder.serScan.scanInvalid.eq(SerScan.Invalid.FALSE));

        PageRequest pageRequest = PageRequest.of(currentPage, perPage);

        long total = serTaskRepository.count(predicate);
        List<SerTask> data = serTaskRepository.findAll(predicate, pageRequest).getContent();

        return new PageResult<SerTask>(total, data);

    }

    /**
     * get all filtered records of process task
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
    public List<SerTask> getProcessTaskAll(String taskNumber, Long modeId, String taskStatus, Long fieldId, String userName, Date startTime, Date endTime, String sortBy, String order) {

        QSerTask builder = QSerTask.serTask;
        BooleanBuilder predicate = getPredicate(taskNumber, modeId, taskStatus, fieldId, userName, startTime, endTime);
        predicate.and(builder.serScan.scanInvalid.eq(SerScan.Invalid.FALSE));

        List<SerTask> data = StreamSupport
                .stream(serTaskRepository.findAll(predicate).spliterator(), false)
                .collect(Collectors.toList());

        return data;
    }

    /**
     * get detailed info of an invalid task
     * @param taskId
     * @return
     */
    @Override
    public SerTask getOne(Long taskId) {

        QSerTask builder = QSerTask.serTask;
        Optional<SerTask> data = serTaskRepository.findOne(builder.taskId.eq(taskId));
        if (!data.isPresent()) {
            return null;
        }

        return data.get();
    }

    /**
     * Get paginated and filtered records of invalid task
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
    public PageResult<SerTask> getInvalidTaskByFilter(String taskNumber, Long modeId, String taskStatus, Long fieldId, String userName, Date startTime, Date endTime, String sortBy, String order,  Integer currentPage, Integer perPage) {

        QSerTask builder = QSerTask.serTask;
        BooleanBuilder predicate = getPredicate(taskNumber, modeId, taskStatus, fieldId, userName, startTime, endTime);
        predicate.and(builder.serScan.scanInvalid.eq(SerScan.Invalid.TRUE));


        PageRequest pageRequest = PageRequest.of(currentPage, perPage);

        long total = serTaskRepository.count(predicate);
        List<SerTask> data = serTaskRepository.findAll(predicate, pageRequest).getContent();

        return new PageResult<SerTask>(total, data);

    }

    /**
     * get all filtered records of invalid task
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
    public List<SerTask> getInvalidTaskAll(String taskNumber, Long modeId, String taskStatus, Long fieldId, String userName, Date startTime, Date endTime, String sortBy, String order) {

        QSerTask builder = QSerTask.serTask;
        BooleanBuilder predicate = getPredicate(taskNumber, modeId, taskStatus, fieldId, userName, startTime, endTime);
        predicate.and(builder.serScan.scanInvalid.eq(SerScan.Invalid.TRUE));

        List<SerTask> data = StreamSupport
                .stream(serTaskRepository.findAll(predicate).spliterator(), false)
                .collect(Collectors.toList());

        return data;
    }

}
