/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（DeviceConfigService）
 * 文件名：	DeviceConfigService.java
 * 描述：	DeviceConfigService interface
 * 作者名：	Choe
 * 日期：	2019/12/10
 */

package com.nuctech.ecuritycheckitem.service.devicemanagement;

import com.nuctech.ecuritycheckitem.models.db.SysDeviceConfig;
import com.nuctech.ecuritycheckitem.models.db.SysJudgeDevice;
import com.nuctech.ecuritycheckitem.models.db.SysManualDevice;
import com.nuctech.ecuritycheckitem.models.db.SysWorkMode;
import com.nuctech.ecuritycheckitem.models.redis.SerSecurityDeviceDetailModel;
import com.nuctech.ecuritycheckitem.utils.PageResult;

import java.util.List;

public interface DeviceConfigService {

    /**
     * find device config from id
     * @param configId
     * @return
     */
    SysDeviceConfig findConfigById(Long configId);

    /**
     * get filtered and paginated list of device config
     * @param deviceName
     * @param fieldId
     * @param categoryId
     * @param currentPage
     * @param perPage
     * @return
     */
    PageResult<SysDeviceConfig> findConfigByFilter(String sortBy, String order, String deviceName, Long fieldId, Long categoryId, Long mode, int currentPage, int perPage);

    /**
     * modify device config
     * @param sysDeviceConfig
     * @param manualDeviceIdList
     * @param judgeDeviceIdList
     * @param configDeviceIdList
     */
    void modifyDeviceConfig(SysDeviceConfig sysDeviceConfig, List<Long> manualDeviceIdList, List<Long> judgeDeviceIdList,
                            List<Long> configDeviceIdList);

    /**
     * Check device is offline or not.
     * @param deviceId
     * @return
     */
    boolean checkDeviceOnline(Long deviceId);

    /**
     * Check device have field or not
     * @param deviceId
     * @return
     */
    boolean checkDeviceHaveField(Long deviceId);

    /**
     * Change device config status
     * @param configId
     * @return
     */
    void updateStatusDeviceConfig(Long configId, String status);

    /**
     * remove device config
     * @param sysDeviceConfig
     */
    void removeDeviceConfig(SysDeviceConfig sysDeviceConfig);

    /**
     * find all device config except specified id
     * @param deviceId
     * @return
     */
    List<SysDeviceConfig> findAllDeviceConfigExceptId(Long deviceId);

    /**
     * get all work mode
     * @return
     */
    List<SysWorkMode> findAllWorkMode();

    /**
     * get all manual devices
     * @return
     */
    List<SysManualDevice> findAllManualDevice();

    /**
     * get all judge devices
     * @return
     */
    List<SysJudgeDevice> findAllJudgeDevice();

    /**
     * get all config information
     * @param deviceId
     * @return
     */
    SerSecurityDeviceDetailModel getSecurityInfoFromDatabase(Long deviceId);

}
