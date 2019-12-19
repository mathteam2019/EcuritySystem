/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/12/10
 * @CreatedBy Choe.
 * @FileName AuthServiceImpl.java
 * @ModifyHistory
 */
package com.nuctech.ecuritycheckitem.service.auth.impl;

import com.nuctech.ecuritycheckitem.models.db.*;
import com.nuctech.ecuritycheckitem.repositories.*;
import com.nuctech.ecuritycheckitem.security.AuthenticationFacade;
import com.nuctech.ecuritycheckitem.service.auth.AuthService;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service implement for user authentication.
 */
@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    SysUserRepository sysUserRepository;

    @Autowired
    SysDeviceDictionaryDataRepository sysDeviceDictionaryDataRepository;

    @Autowired
    SysDictionaryDataRepository sysDictionaryDataRepository;

    @Autowired
    SerPlatformOtherParamRepository serPlatformOtherParamRepository;

    @Autowired
    AuthenticationFacade authenticationFacade;


    @Override
    public SysUser getSysUserByUserAccount(String userAccount) {
        // Find user by his user account
        QSysUser qSysUser = QSysUser.sysUser;
        Predicate predicate = qSysUser.userAccount.eq(userAccount);

        Optional<SysUser> optionalSysUser = sysUserRepository.findOne(predicate);

        if (!optionalSysUser.isPresent()) {
            return null;
        }
        return optionalSysUser.get();
    }


    @Override
    @Transactional
    public boolean modifyPassword(Long userId, String password) {
        QSysUser qSysUser = QSysUser.sysUser;
        Predicate predicate = qSysUser.userId.eq(userId);

        Optional<SysUser> optionalSysUser = sysUserRepository.findOne(predicate);

        if (!optionalSysUser.isPresent()) {
            return false;
        }
        SysUser user = optionalSysUser.get();
        user.setPassword(password);
        sysUserRepository.save(user);
        return true;
    }

    @Override
    public List<SysDictionaryData> findAllDictionary() {
        return sysDictionaryDataRepository.findAll();
    }

    @Override
    public List<SysDeviceDictionaryData> findAllDeviceDictionary() {
        return sysDeviceDictionaryDataRepository.findAll();
    }

    @Override
    public void checkPendingUser(SysUser user, Integer count) {
        Integer passwordLimit = 7;
        List<SerPlatformOtherParams> serPlatformOtherParams = serPlatformOtherParamRepository.findAll();
        if(serPlatformOtherParams != null && serPlatformOtherParams.size() > 0) {
            Long loginNumber = serPlatformOtherParams.get(0).getLoginNumber();
            if(loginNumber != null) {
                passwordLimit = loginNumber.intValue();
            }
        }
        if(count != null && count >= passwordLimit) {
            user.setStatus(SysUser.Status.PENDING);
            user.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
            sysUserRepository.save(user);

        }
    }

}
