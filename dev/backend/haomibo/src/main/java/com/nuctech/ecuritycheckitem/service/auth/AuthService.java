/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/12/10
 * @CreatedBy Choe.
 * @FileName AuthService.java
 * @ModifyHistory
 */
package com.nuctech.ecuritycheckitem.service.auth;

import com.nuctech.ecuritycheckitem.models.db.SysDeviceDictionaryData;
import com.nuctech.ecuritycheckitem.models.db.SysDictionaryData;
import com.nuctech.ecuritycheckitem.models.db.SysUser;

import java.util.List;

/**
 * Service interface for user authentication.
 */
public interface AuthService {
    SysUser getSysUserByUserAccount(String userAccount);

    boolean modifyPassword(Long userId, String password);

    List<SysDictionaryData> findAllDictionary();

    List<SysDeviceDictionaryData> findAllDeviceDictionary();
}
