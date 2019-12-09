package com.nuctech.ecuritycheckitem.service;

import com.nuctech.ecuritycheckitem.controllers.taskmanagement.TaskManagementController;
import java.util.Map;

public interface TaskService {
    Map<String, Object> getFilterTaskList(TaskManagementController.TaskGetByFilterAndPageRequestBody.Filter filter, Integer currentPage, Integer perPage);
}
