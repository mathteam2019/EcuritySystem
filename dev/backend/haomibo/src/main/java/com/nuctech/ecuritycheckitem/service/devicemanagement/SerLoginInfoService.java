/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（SerLoginInfoService）
 * 文件名：	SerLoginInfoService.java
 * 描述：	SerLoginInfoService interface
 * 作者名：	Choe
 * 日期：	2019/12/10
 */

package com.nuctech.ecuritycheckitem.service.devicemanagement;

import com.nuctech.ecuritycheckitem.models.db.SerDeviceStatus;
import com.nuctech.ecuritycheckitem.models.db.SerLoginInfo;
import com.nuctech.ecuritycheckitem.utils.PageResult;

public interface SerLoginInfoService {

    /**
     * get paginated and filtered device status
     * @param deviceId
     * @return
     */
    SerLoginInfo findLatestLoginInfo(Long deviceId);
}
