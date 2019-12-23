package com.nuctech.ecuritycheckitem.service.taskmanagement;

import com.nuctech.ecuritycheckitem.models.db.SerTask;
import com.nuctech.ecuritycheckitem.utils.PageResult;

import java.util.Date;
import java.util.List;

public interface TaskService {

    /**
     * get paginated list of invalid task with filter
     * @param taskNumber
     * @param mode
     * @param taskStatus
     * @param fieldId
     * @param userName
     * @param startTime
     * @param endTime
     * @param currentPage
     * @param perPage
     * @return
     */
    PageResult<SerTask> getInvalidTaskByFilter(String taskNumber, Long mode, String taskStatus, Long fieldId, String userName, Date startTime, Date endTime, String sortBy, String order, Integer currentPage, Integer perPage);

    /**
     * get all listof invalid task with filter
     * @param taskNumber
     * @param modeId
     * @param taskStatus
     * @param fieldId
     * @param userName
     * @param startTime
     * @param endTime
     * @return
     */
    List<SerTask> getInvalidTaskAll(String taskNumber, Long modeId, String taskStatus, Long fieldId, String userName, Date startTime, Date endTime);

    /**
     * get paginated list of process task with filter
     * @param taskNumber
     * @param mode
     * @param taskStatus
     * @param fieldId
     * @param userName
     * @param startTime
     * @param endTime
     * @param currentPage
     * @param perPage
     * @return
     */
    PageResult<SerTask> getProcessTaskByFilter(String taskNumber, Long mode, String taskStatus, Long fieldId, String userName, Date startTime, Date endTime, String sortBy, String order, Integer currentPage, Integer perPage);

    /**
     * get All list of process task with filter
     * @param taskNumber
     * @param modeId
     * @param taskStatus
     * @param fieldId
     * @param userName
     * @param startTime
     * @param endTime
     * @return
     */
    List<SerTask> getProcessTaskAll(String taskNumber, Long modeId, String taskStatus, Long fieldId, String userName, Date startTime, Date endTime);

    /**
     * get detailed information of a process task with ID
     * @param taskId
     * @return
     */
    SerTask getOne(Long taskId);


}
