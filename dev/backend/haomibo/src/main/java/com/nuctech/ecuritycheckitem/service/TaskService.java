package com.nuctech.ecuritycheckitem.service;

import com.nuctech.ecuritycheckitem.controllers.taskmanagement.InvalidTaskController;
import com.nuctech.ecuritycheckitem.controllers.taskmanagement.ProcessTaskController;
import java.util.Map;

public interface TaskService {
    Map<String, Object> getFilterProcessTask(ProcessTaskController.TaskGetByFilterAndPageRequestBody.Filter filter, Integer currentPage, Integer perPage);
    Map<String, Object> getFilterInvalidTask(InvalidTaskController.TaskGetByFilterAndPageRequestBody.Filter filter, Integer currentPage, Integer perPage);
}
