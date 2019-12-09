/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/12/10
 * @CreatedBy Choe.
 * @FileName AuthServiceImpl.java
 * @ModifyHistory
 */
package com.nuctech.ecuritycheckitem.service;

import com.nuctech.ecuritycheckitem.models.db.QSysUser;
import com.nuctech.ecuritycheckitem.models.db.SysUser;
import com.nuctech.ecuritycheckitem.repositories.SysUserRepository;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service implement for user authentication.
 */
@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    SysUserRepository sysUserRepository;

    @Override
    public SysUser getSysUserByEmail(String email) {
        // Find user by his email address.
        QSysUser qSysUser = QSysUser.sysUser;
        Predicate predicate = qSysUser.email.eq(email);

        Optional<SysUser> optionalSysUser = sysUserRepository.findOne(predicate);

        if (!optionalSysUser.isPresent()) {
            return null;
        }
        return optionalSysUser.get();
    }
}
