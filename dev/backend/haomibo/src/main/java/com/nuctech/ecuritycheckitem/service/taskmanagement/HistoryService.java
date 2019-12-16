package com.nuctech.ecuritycheckitem.service.taskmanagement;

import com.nuctech.ecuritycheckitem.models.db.History;
import com.nuctech.ecuritycheckitem.utils.PageResult;

import java.util.Date;
import java.util.List;

public interface HistoryService {

    /**
     * get paginated list of history task
     * @param taskNumber : task number
     * @param mode : mode id
     * @param taskStatus : task status
     * @param fieldId : scene id
     * @param userName : user name
     * @param startTime :  start time
     * @param endTime : end time
     * @param currentPage : currentPage No
     * @param perPage : perPage count
     * @return
     */
    PageResult<History> getHistoryTaskByFilter(String taskNumber, Long mode, String taskStatus, Long fieldId, String userName, Date startTime, Date endTime, Integer currentPage, Integer perPage);

    /**
     * get All history task with filter
     * @param taskNumber : task number
     * @param modeId : mode id
     * @param taskStatus : task status
     * @param fieldId : scene id
     * @param userName : user name
     * @param startTime : start time
     * @param endTime : end time
     * @return
     */
    List<History> getHistoryTaskAll(String taskNumber, Long modeId, String taskStatus, Long fieldId, String userName, Date startTime, Date endTime);

    /**
     * Get one history task with id
     * @param taskId
     * @return
     */
    History getOne(Long taskId);


}
