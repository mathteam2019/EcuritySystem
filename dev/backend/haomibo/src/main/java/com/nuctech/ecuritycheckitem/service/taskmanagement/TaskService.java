/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（TaskService)
 * 文件名：	TaskService.java
 * 描述：	Service interface to get task data from database using models and repositories
 * 作者名：	Tiny
 * 日期：	2019/12/20
 *
 */

package com.nuctech.ecuritycheckitem.service.taskmanagement;

import com.nuctech.ecuritycheckitem.models.db.SerTask;
import com.nuctech.ecuritycheckitem.models.simplifieddb.SerTaskSimplifiedForProcessTaskManagement;
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
    PageResult<SerTaskSimplifiedForProcessTaskManagement> getInvalidTaskByFilter(String taskNumber, Long mode, String taskStatus, Long fieldId, String userName, Date startTime, Date endTime, String sortBy, String order, Integer currentPage, Integer perPage);

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
    List<SerTaskSimplifiedForProcessTaskManagement> getExportInvalidTask(String taskNumber, Long modeId, String taskStatus, Long fieldId, String userName, Date startTime, Date endTime, String sortBy, String order, String idList);


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
    List<SerTaskSimplifiedForProcessTaskManagement> getInvalidTaskAll(String taskNumber, Long modeId, String taskStatus, Long fieldId, String userName, Date startTime, Date endTime, String sortBy, String order);

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
    PageResult<SerTaskSimplifiedForProcessTaskManagement> getProcessTaskByFilter(String taskNumber, Long mode, String taskStatus, Long fieldId, String userName, Date startTime, Date endTime, String sortBy, String order, Integer currentPage, Integer perPage);

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
    List<SerTaskSimplifiedForProcessTaskManagement> getProcessTaskAll(String taskNumber, Long modeId, String taskStatus, Long fieldId, String userName, Date startTime, Date endTime, String sortBy, String order);

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
    List<SerTaskSimplifiedForProcessTaskManagement> getExportProcessTask(String taskNumber, Long modeId, String taskStatus, Long fieldId, String userName, Date startTime, Date endTime, String sortBy, String order, String idList);


    /**
     * get detailed information of a process task with ID
     * @param taskId
     * @return
     */
    SerTaskSimplifiedForProcessTaskManagement getOne(Long taskId);


}
