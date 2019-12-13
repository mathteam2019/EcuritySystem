package com.nuctech.ecuritycheckitem.service.taskmanagement;

import com.nuctech.ecuritycheckitem.models.db.*;
import com.nuctech.ecuritycheckitem.models.db.QHistory;
import com.nuctech.ecuritycheckitem.repositories.HistoryRepository;
import com.nuctech.ecuritycheckitem.repositories.SerTaskRepository;
import com.nuctech.ecuritycheckitem.utils.PageResult;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class HistoryServiceImpl implements HistoryService {

    @Autowired
    HistoryRepository historyRepository;

    private BooleanBuilder getPredicate(String taskNumber, Long modeId, String taskStatus, Long fieldId, String userName, Date startTime, Date endTime) {

        QHistory builder = QHistory.history;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        if (taskNumber != null) {
            predicate.and(builder.task.taskNumber.contains(taskNumber));
        }
        if (modeId != null) {
            predicate.and(builder.mode.eq(modeId));
        }
        if (taskStatus != null && !taskStatus.isEmpty()) {
            predicate.and(builder.task.taskStatus.eq(taskStatus));
        }
        if (fieldId != null) {
            predicate.and(builder.task.workflowId.eq(fieldId));
        }
        if (userName != null && !userName.isEmpty()) {
            Predicate scanUserName = builder.scanPointsman.userName.contains(userName)
                    .or(builder.judgeUser.userName.contains(userName))
                    .or(builder.judgeUser.userName.contains(userName));
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

    @Override
    public PageResult<History> getHistoryTaskByFilter(String taskNumber, Long modeId, String taskStatus, Long fieldId, String userName, Date startTime, Date endTime, Integer currentPage, Integer perPage) {

        QHistory builder = QHistory.history;
        BooleanBuilder predicate = getPredicate(taskNumber, modeId, taskStatus, fieldId, userName, startTime, endTime);

        PageRequest pageRequest = PageRequest.of(currentPage, perPage);

        long total = historyRepository.count(predicate);
        List<History> data = historyRepository.findAll(predicate, pageRequest).getContent();

        return new PageResult<History>(total, data);

    }

    @Override
    public List<History> getHistoryTaskAll(String taskNumber, Long modeId, String taskStatus, Long fieldId, String userName, Date startTime, Date endTime) {

        QHistory builder = QHistory.history;
        BooleanBuilder predicate = getPredicate(taskNumber, modeId, taskStatus, fieldId, userName, startTime, endTime);

        List<History> data = StreamSupport
                .stream(historyRepository.findAll(predicate).spliterator(), false)
                .collect(Collectors.toList());

        return data;
    }

    @Override
    public History getOne(Long taskId) {

        QHistory builder = QHistory.history;
        Optional<History> data = historyRepository.findOne(builder.historyId.eq(taskId));
        if (!data.isPresent()) {
            return null;
        }

        return data.get();
    }


}
