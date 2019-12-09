package com.nuctech.ecuritycheckitem.service;

import com.nuctech.ecuritycheckitem.controllers.taskmanagement.TaskManagementController;
import com.nuctech.ecuritycheckitem.models.db.QSerTask;
import com.nuctech.ecuritycheckitem.models.db.SerScan;
import com.nuctech.ecuritycheckitem.models.db.SerTask;
import com.nuctech.ecuritycheckitem.repositories.SerTaskRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    SerTaskRepository serTaskRepository;

    @Override
    public Map<String, Object> getFilterTaskList(TaskManagementController.TaskGetByFilterAndPageRequestBody.Filter filter, Integer currentPage, Integer perPage) {

        Map<String, Object> result = new HashMap<>();
        QSerTask builder = QSerTask.serTask;
        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        if (filter != null) {
            predicate.and(builder.serScan.scanInvalid.eq(SerScan.Invalid.TRUE));

            if (filter.getTaskNumber() != null && !filter.getTaskNumber().isEmpty()) {
                predicate.and(builder.taskNumber.contains(filter.getTaskNumber()));
            }
            if (filter.getMode() != null) {
                predicate.and(builder.workFlow.workMode.modeId.eq(filter.getMode()));
            }
            if (filter.getStatus() != null && !filter.getStatus().isEmpty()) {
                predicate.and(builder.taskStatus.eq(filter.getStatus()));
            }
            if (filter.getFieldId() != null) {
                predicate.and(builder.fieldId.eq(filter.getFieldId()));
            }
            if (filter.getUserName() != null && !filter.getUserName().isEmpty()) {
                Predicate scanUserName = builder.serScan.scanPointsman.userName.contains(filter.getUserName())
                        .or(builder.serJudgeGraph.judgeUser.userName.contains(filter.getUserName()))
                        .or(builder.serJudgeGraph.judgeUser.userName.contains(filter.getUserName()));
                predicate.and(scanUserName);
            }
            if (filter.getStartTime() != null) {
                predicate.and(builder.createdTime.after(filter.getStartTime()));
            }
            if (filter.getEndTime() != null) {
                predicate.and(builder.createdTime.before(filter.getEndTime()));
            }
        }


        List<SerTask> data = new ArrayList<>();
        long total = serTaskRepository.count(predicate);

        if (currentPage != null && perPage != null) {
            PageRequest pageRequest = PageRequest.of(currentPage, perPage);
            data = serTaskRepository.findAll(predicate, pageRequest).getContent();
        }
        else {
            data = StreamSupport
                    .stream(serTaskRepository.findAll(predicate).spliterator(), false)
                    .collect(Collectors.toList());
        }

        result.put("total", total);
        result.put("data", data);

        return result;

    }

}
