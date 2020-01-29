/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（ScanParamService）
 * 文件名：	ScanParamService.java
 * 描述：	ScanParamService interface
 * 作者名：	Choe
 * 日期：	2019/12/10
 */

package com.nuctech.ecuritycheckitem.service.settingmanagement;

import com.nuctech.ecuritycheckitem.models.db.SerScanParam;
import com.nuctech.ecuritycheckitem.utils.PageResult;

import java.util.List;

public interface ScanParamService {

    /**
     * get scan param by id
     * @param paramId
     * @return
     */
    SerScanParam getById(Long paramId);

    /**
     * update Status of scan param
     * @param paramId
     * @return
     */
    void updateStatus(Long paramId, String status);

    /**
     * get paginated and filtered list of scan param list
     * @param deviceName
     * @param status
     * @param currentPage
     * @param perPage
     * @return
     */
    PageResult<SerScanParam> getScanParamListByFilter(String sortBy, String order, String deviceName, String status, Integer currentPage, Integer perPage);

    /**
     * get filtered list of scan param list
     * @param deviceName
     * @param status
     * @return
     */
    List<SerScanParam> getAllWithFilter(String deviceName, String status);

    /**
     * get all ser scan params
     * @return
     */
    List<SerScanParam> getAllWithoutFilter();

    /**
     * edit scan param
     * @param paramDeviceIdList
     * @param serScanParamNew
     * @return
     */
    boolean modifyScanParam(List<Long> paramDeviceIdList, SerScanParam serScanParamNew);

}
