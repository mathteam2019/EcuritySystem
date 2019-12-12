/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/12/10
 * @CreatedBy Choe.
 * @FileName AuthService.java
 * @ModifyHistory
 */
package com.nuctech.ecuritycheckitem.service;

import com.nuctech.ecuritycheckitem.models.db.SysUser;

/**
 * Service interface for user authentication.
 */
public interface AuthService {
    SysUser getSysUserByUserAccount(String userAccount);
}
