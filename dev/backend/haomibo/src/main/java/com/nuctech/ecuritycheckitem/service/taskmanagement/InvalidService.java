/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（历史任务 service interface 1.0)
 * 文件名：	HistoryService.java
 * 描述：	Service Interface to get history task data from database using models and repositories
 * 作者名：	Tiny
 * 日期：	2019/12/20
 *
 */

package com.nuctech.ecuritycheckitem.service.taskmanagement;

import com.nuctech.ecuritycheckitem.models.simplifieddb.*;
import com.nuctech.ecuritycheckitem.utils.PageResult;

import java.util.Date;
import java.util.List;

public interface InvalidService {





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
    PageResult<HistorySimplifiedForInvalidTableManagement> getInvalidTaskByFilter(String taskNumber, Long mode, String taskStatus, Long fieldId, String userName, Date startTime, Date endTime, String sortBy, String order, Integer currentPage, Integer perPage);







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
    List<HistorySimplifiedForInvalidTableManagement> getExportInvalidTask(String taskNumber, Long modeId, String taskStatus, Long fieldId, String userName, Date startTime, Date endTime, String sortBy, String order, boolean isAll, String idList);
    /**
     * get All history task with filter

     * @return
     */
    List<InvalidSimplifiedForImageManagement> getExportInvalidImage(String taskNumber, Long modeId, String taskStatus, Long fieldId, String userName, Date startTime, Date endTime, String sortBy, String order, boolean isAll, String idList);

    /**
     * Get one history task with id
     * @param taskId
     * @return
     */
    SerTaskSimplifiedForInvalidTaskManagement getOne(Long taskId);


}
