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

import com.nuctech.ecuritycheckitem.models.reusables.CategoryUser;
import com.nuctech.ecuritycheckitem.repositories.*;

import com.nuctech.ecuritycheckitem.security.AuthenticationFacade;
import com.nuctech.ecuritycheckitem.service.auth.AuthService;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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

    @Autowired
    SysUserLookupRepository sysUserLookupRepository;

    @Autowired
    SysUserGroupLookupRepository sysUserGroupLookupRepository;

    @Autowired
    SysDataGroupRepository sysDataGroupRepository;

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
    public int checkPendingUser(SysUser user, Integer count) {

        Integer passwordLimit = 7;

        List<SerPlatformOtherParams> serPlatformOtherParams = serPlatformOtherParamRepository.findAll();

        if(serPlatformOtherParams != null && serPlatformOtherParams.size() > 0) {

            Long loginNumber = serPlatformOtherParams.get(0).getLoginNumber();
            if(loginNumber != null) {
                passwordLimit = loginNumber.intValue();
            }
        }
        if(count != null) {
            if(count == passwordLimit - 1) {
                return 1;
            } else if(count >= passwordLimit) {
                user.setStatus(SysUser.Status.PENDING);
                sysUserRepository.save(user);
                return 2;
            }
        }
        return 0;
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
            SysUserGroupSimple userGroupList = allGroupUser.get(i).getUserGroupSimple();
            if(userGroupList == null) {
                continue;
            }
            userGroupList.getRoles().forEach(sysRole -> {
                availableSysResourceListPre.addAll(sysRole.getResources());
            });
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

    @Override
    public CategoryUser getDataCategoryUserList() {
        SysUser sysUser = (SysUser) authenticationFacade.getAuthentication().getPrincipal();
        sysUser = sysUserRepository.findOne(QSysUser.sysUser.userId.eq(sysUser.getUserId())).get();
        QSysUserGroupUserDetail builder = QSysUserGroupUserDetail.sysUserGroupUserDetail;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        predicate.and(builder.userId.eq(sysUser.getUserId()));

        List<SysUserGroupUserDetail> allGroupUser = StreamSupport
                .stream(sysUserGroupUserDetailRepository.findAll(predicate).spliterator(), false)
                .collect(Collectors.toList());
        String levelUser = sysUser.getDataRangeCategory();
        List<Long> relateUserList = new ArrayList<>();
        List<Long> relateDataGroupIdList = new ArrayList<>();
        if(levelUser.equals(SysUser.DataRangeCategory.SPECIFIED.getValue())) {
            levelUser = "";
            List<SysUserLookup> lookupList = StreamSupport
                    .stream(sysUserLookupRepository.findAll(QSysUserLookup.sysUserLookup.userId.eq(sysUser.getUserId())).spliterator(), false)
                    .collect(Collectors.toList());
            for(int i = 0; i < lookupList.size(); i ++) {
                relateDataGroupIdList.add(lookupList.get(i).getDataGroupId());
            }
        }

        String levelGroup = "";
        List<Long> groupIdForDataList = new ArrayList<>();
        List<Long> groupIdList = new ArrayList<>();
        for(int i = 0; i < allGroupUser.size(); i ++) {
            SysUserGroupSimple userGroup = allGroupUser.get(i).getUserGroupSimple();
            if(userGroup != null) {
                if(userGroup.getDataRangeCategory().equals(SysUserGroup.DataRangeCategory.SPECIFIED.getValue())) {
                    groupIdForDataList.add(userGroup.getUserGroupId());
                } else {
                    if(userGroup.getDataRangeCategory().equals(SysUserGroup.DataRangeCategory.ALL.getValue())) {
                        levelGroup = userGroup.getDataRangeCategory();
                        break;
                    }
                    if(userGroup.getDataRangeCategory().equals(SysUserGroup.DataRangeCategory.GROUP.getValue())) {
                        groupIdList.add(userGroup.getUserGroupId());
                    }
                }
            }
        }


        CategoryUser categoryUser = new CategoryUser();
        if(levelUser.equals(SysUser.DataRangeCategory.ALL.getValue()) || levelGroup.equals(SysUserGroup.DataRangeCategory.ALL.getValue())) {
            categoryUser.setAll(true);
            return categoryUser;
        }
        categoryUser.setAll(false);

        if(groupIdList.size() > 0) {
            List<SysUserGroupUserDetail> userGroupUserList = StreamSupport
                    .stream(sysUserGroupUserDetailRepository.findAll(QSysUserGroupUserDetail.sysUserGroupUserDetail.userGroupId.in(groupIdList)).spliterator(), false)
                    .collect(Collectors.toList());
            for(int i = 0; i < userGroupUserList.size(); i ++) {
                relateUserList.add(userGroupUserList.get(i).getUserId());
            }
        }

        if(groupIdForDataList.size() > 0) {
            List<SysUserGroupLookup> userGroupLookupList = StreamSupport
                    .stream(sysUserGroupLookupRepository.findAll(QSysUserGroupLookup.sysUserGroupLookup.userGroupId.in(groupIdForDataList)).spliterator(), false)
                    .collect(Collectors.toList());
            for(int i = 0; i < userGroupLookupList.size(); i ++) {
                relateDataGroupIdList.add(userGroupLookupList.get(i).getDataGroupId());
            }
        }
        if(relateDataGroupIdList.size() > 0) {
            List<SysDataGroup> dataGroupList = StreamSupport
                    .stream(sysDataGroupRepository.findAll(QSysDataGroup.sysDataGroup.dataGroupId.in(relateDataGroupIdList)).spliterator(), false)
                    .collect(Collectors.toList());
            for(int i = 0; i < dataGroupList.size(); i ++) {
                SysDataGroup dataGroup = dataGroupList.get(i);
                for(SysUser user: dataGroup.getUsers()) {
                    relateUserList.add(user.getUserId());
                }
            }
        }
        List<Long> relateOrgIdList = new ArrayList<>();
        if(levelUser.equals(SysUser.DataRangeCategory.ORG.getValue())) {
            relateOrgIdList.add(sysUser.getOrg().getOrgId());
        } else if(levelUser.equals(SysUser.DataRangeCategory.ORG_DESC.getValue())) {
            SysOrg parentOrg = sysUser.getOrg();
            Queue<SysOrg> queue = new LinkedList<>();
            queue.add(parentOrg);
            while(!queue.isEmpty()) {
                SysOrg head = queue.remove();
                relateOrgIdList.add(head.getOrgId());
                for(SysOrg child: head.getChildren()) {
                    queue.add(child);
                }
            }
        } else {
            relateUserList.add(sysUser.getUserId());
        }

        if(relateOrgIdList.size() > 0) {
            List<SysUser> userList = StreamSupport
                    .stream(sysUserRepository.findAll(QSysUser.sysUser.orgId.in(relateOrgIdList)).spliterator(), false)
                    .collect(Collectors.toList());
            for(SysUser relateUser: userList) {
                relateUserList.add(relateUser.getUserId());
            }
        }
        List<Long> answerUserIdList = new ArrayList<>();
        for(Long relateUserId: relateUserList) {
            boolean isExist = false;
            for(Long checkUserId: answerUserIdList) {
                if(checkUserId == relateUserId) {
                    isExist = true;
                    break;
                }
            }
            if(isExist == false) {
                answerUserIdList.add(relateUserId);
            }
        }
        categoryUser.setUserIdList(answerUserIdList);
        return categoryUser;
    }

}
