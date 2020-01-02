/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（AuthServiceImpl）
 * 文件名：	AuthServiceImpl.java
 * 描述：	Service implement for user authentication.
 * 作者名：	Choe
 * 日期：	2019/12/10
 */

package com.nuctech.ecuritycheckitem.service.auth.impl;


import com.nuctech.ecuritycheckitem.models.db.*;

import com.nuctech.ecuritycheckitem.repositories.*;

import com.nuctech.ecuritycheckitem.security.AuthenticationFacade;
import com.nuctech.ecuritycheckitem.service.auth.AuthService;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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

    @Autowired
    SysResourceRepository sysResourceRepository;

    @Autowired
    SysUserGroupUserDetailRepository sysUserGroupUserDetailRepository;

    /**
     * Find user by his user account
     * @param userAccount
     * @return
     */
    @Override
    public SysUser getSysUserByUserAccount(String userAccount) {

        QSysUser qSysUser = QSysUser.sysUser;
        Predicate predicate = qSysUser.userAccount.eq(userAccount);

        Optional<SysUser> optionalSysUser = sysUserRepository.findOne(predicate);

        if (!optionalSysUser.isPresent()) {
            return null;
        }
        return optionalSysUser.get();
    }

    /**
     * modify password
     * @param userId
     * @param password
     * @return
     */
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

    /**
     * find all dictionary data
     * @return
     */
    @Override
    public List<SysDictionaryData> findAllDictionary() {
        return sysDictionaryDataRepository.findAll();
    }

    /**
     * find all device dictionary data
     * @return
     */
    @Override
    public List<SysDeviceDictionaryData> findAllDeviceDictionary() {
        return sysDeviceDictionaryDataRepository.findAll();
    }

    /**
     * check pending user
     * @param user
     * @param count
     */
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
        passwordLimit --;
        if(count != null && count >= passwordLimit) {
            user.setStatus(SysUser.Status.PENDING);
            sysUserRepository.save(user);

        }
    }

    /**
     * get all available resource
     * @param sysUser
     * @return
     */
    @Override
    public List<SysResource> getAvailableSysResourceList(SysUser sysUser) {

        List<SysResource> allResources = sysResourceRepository.findAll();




        List<SysResource> availableSysResourceListPre = new ArrayList<>();
        sysUser.getRoles().forEach(sysRole -> {
            availableSysResourceListPre.addAll(sysRole.getResources());
        });

        QSysUserGroupUserDetail builder = QSysUserGroupUserDetail.sysUserGroupUserDetail;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        predicate.and(builder.userId.eq(sysUser.getUserId()));

        List<SysUserGroupUserDetail> allGroupUser = StreamSupport
                .stream(sysUserGroupUserDetailRepository.findAll(predicate).spliterator(), false)
                .collect(Collectors.toList());
        if(allGroupUser == null) {
            allGroupUser = new ArrayList<>();
        }
        for(int i = 0; i < allGroupUser.size(); i ++) {
            List<SysUserGroupSimple> userGroupList = allGroupUser.get(i).getUserGroupList();
            for(int j = 0; j < userGroupList.size(); j ++) {
                userGroupList.get(j).getRoles().forEach(sysRole -> {
                    availableSysResourceListPre.addAll(sysRole.getResources());
                });
            }
        }
        List<SysResource> availableSysResourceList = new ArrayList<>();
        for(int i = 0; i < availableSysResourceListPre.size(); i ++) {
            boolean isExist = false;
            for(int j = 0; j < availableSysResourceList.size(); j ++) {
                if(availableSysResourceList.get(j).getResourceId() == availableSysResourceListPre.get(i).getResourceId()) {
                    isExist = true;
                    break;
                }
            }
            if(!isExist) {
                availableSysResourceList.add(availableSysResourceListPre.get(i));
            }
        }

        int startIndex = 0;
        while(startIndex < availableSysResourceList.size()) {
            long parentId = availableSysResourceList.get(startIndex).getParentResourceId();
            if(parentId != 0) {

                boolean isExist = false;
                for(int i = 0; i < availableSysResourceList.size(); i ++) {
                    SysResource resource = availableSysResourceList.get(i);
                    if(resource.getResourceId() == parentId) {
                        isExist = true;
                        break;
                    }
                }
                if(!isExist) {
                    SysResource parent;
                    for(int i = 0; i < allResources.size(); i ++) {
                        SysResource resource = allResources.get(i);
                        if(resource.getResourceId() == parentId) {
                            parent = resource;
                            availableSysResourceList.add(parent);
                            break;
                        }
                    }
                }
            }
            startIndex ++;

        }
        return availableSysResourceList;
    }

}
