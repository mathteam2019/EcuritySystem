/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（AuthService）
 * 文件名：	AuthService.java
 * 描述：	Service interface for user authentication.
 * 作者名：	Choe
 * 日期：	2019/12/10
 */

package com.nuctech.ecuritycheckitem.service.auth;

import com.nuctech.ecuritycheckitem.models.db.SysDeviceDictionaryData;
import com.nuctech.ecuritycheckitem.models.db.SysDictionaryData;
import com.nuctech.ecuritycheckitem.models.db.SysResource;
import com.nuctech.ecuritycheckitem.models.db.SysUser;
import com.nuctech.ecuritycheckitem.models.reusables.CategoryUser;

import java.util.List;

public interface AuthService {

    /**
     * get user from user account
     * @param userAccount
     * @return
     */
    SysUser getSysUserByUserAccount(String userAccount);

    /**
     * modify password
     * @param userId
     * @param password
     * @return
     */
    boolean modifyPassword(Long userId, String password);

    /**
     * find all dictionary data
     * @return
     */
    List<SysDictionaryData> findAllDictionary();

    /**
     * find all device dictionary data
     * @return
     */
    List<SysDeviceDictionaryData> findAllDeviceDictionary();

    /**
     * check pending user
     * @param user
     * @param count
     */
    int checkPendingUser(SysUser user, Integer count);

    /**
     * get all resource for user
     * @param sysUser
     * @return
     */
    List<SysResource> getAvailableSysResourceList(SysUser sysUser);


    void uploadCategoryUserListRedis();

    CategoryUser getDataCategoryUserList();
}
