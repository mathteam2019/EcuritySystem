/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（PlatformOtherService）
 * 文件名：	PlatformOtherService.java
 * 描述：	PlatformOtherService interface
 * 作者名：	Choe
 * 日期：	2019/12/10
 */

package com.nuctech.ecuritycheckitem.service.settingmanagement;

import com.nuctech.ecuritycheckitem.models.db.SerPlatformOtherParams;

import java.util.List;

public interface PlatformOtherService {

    /**
     * find all platform other params
     * @return
     */
    List<SerPlatformOtherParams> findAll();

    /**
     * modify platform other params
     * @param serPlatformOtherParams
     */
    void modifyPlatform(SerPlatformOtherParams serPlatformOtherParams);
}
