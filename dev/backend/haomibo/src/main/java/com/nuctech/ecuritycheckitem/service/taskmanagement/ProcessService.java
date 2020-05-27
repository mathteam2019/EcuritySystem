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
import com.nuctech.ecuritycheckitem.models.simplifieddb.HistorySimplifiedForProcessTableManagement;
import com.nuctech.ecuritycheckitem.models.simplifieddb.ProcessSimplifiedForImageManagement;
import com.nuctech.ecuritycheckitem.models.simplifieddb.SerTaskSimplifiedForProcessTableManagement;
import com.nuctech.ecuritycheckitem.models.simplifieddb.SerTaskSimplifiedForProcessTaskManagement;
import com.nuctech.ecuritycheckitem.utils.PageResult;

import java.util.Date;
import java.util.List;

public interface ProcessService {


    /**
     * get paginated list of process task with filter
     * @param taskNumber
     * @param modeId
     * @param taskStatus
     * @param fieldId
     * @param userName
     * @param startTime
     * @param endTime
     * @param currentPage
     * @param perPage
     * @return
     */
    PageResult<HistorySimplifiedForProcessTableManagement> getProcessTaskByFilter(String taskNumber, Long modeId, String taskStatus, Long fieldId, String userName, Date startTime, Date endTime, String sortBy, String order, Integer currentPage, Integer perPage);


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
    List<HistorySimplifiedForProcessTableManagement> getExportProcessTask(String taskNumber, Long modeId, String taskStatus, Long fieldId, String userName, Date startTime, Date endTime, String sortBy, String order, boolean isAll, String idList);

    /**
     *
     * @param sortBy
     * @param order
     * @param idList
     * @return
     */
    public List<ProcessSimplifiedForImageManagement> getExportProcessImage(String taskNumber, Long modeId, String taskStatus, Long fieldId, String userName, Date startTime, Date endTime, String sortBy, String order, boolean isAll, String idList);

    /**
     * get detailed information of a process task with ID
     * @param taskId
     * @return
     */
    SerTaskSimplifiedForProcessTaskManagement getOne(Long taskId);


}
