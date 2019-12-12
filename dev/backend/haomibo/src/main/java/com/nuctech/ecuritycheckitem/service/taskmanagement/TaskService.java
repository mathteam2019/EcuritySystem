package com.nuctech.ecuritycheckitem.service.taskmanagement;

import com.nuctech.ecuritycheckitem.controllers.taskmanagement.InvalidTaskController;
import com.nuctech.ecuritycheckitem.controllers.taskmanagement.ProcessTaskController;
import com.nuctech.ecuritycheckitem.models.db.SerTask;
import com.nuctech.ecuritycheckitem.utils.PageResult;

import java.util.Date;
import java.util.Map;

public interface TaskService {
    Map<String, Object> getFilterProcessTask(ProcessTaskController.TaskGetByFilterAndPageRequestBody.Filter filter, Integer currentPage, Integer perPage);
    Map<String, Object> getFilterInvalidTask(InvalidTaskController.TaskGetByFilterAndPageRequestBody.Filter filter, Integer currentPage, Integer perPage);

    PageResult<SerTask> getProcessTaskByFilter(String taskNumber, Long mode, String status, Long fieldId, String userName, Date startTime, Date endTime, int currentPage, int perPage);
}
