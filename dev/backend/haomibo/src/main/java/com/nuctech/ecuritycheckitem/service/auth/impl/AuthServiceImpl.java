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


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.models.db.*;

import com.nuctech.ecuritycheckitem.models.reusables.CategoryUser;
import com.nuctech.ecuritycheckitem.models.simplifieddb.*;
import com.nuctech.ecuritycheckitem.repositories.*;

import com.nuctech.ecuritycheckitem.security.AuthenticationFacade;
import com.nuctech.ecuritycheckitem.service.auth.AuthService;
import com.nuctech.ecuritycheckitem.utils.RedisUtil;
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
    SysUserSimpleRepository sysUserSimpleRepository;

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

    @Autowired
    SysOrgRepository sysOrgRepository;

    @Autowired
    SysRoleResourceRepository sysRoleResourceRepository;

    @Autowired
    SysUserGroupDataRangeRepository sysUserGroupUserCategoryRepository;

    @Autowired
    SysUserGroupUserRepository sysUserGroupUserRepository;

    @Autowired
    SysUserGroupDataRangeRepository sysUserGroupDataRangeRepository;

    @Autowired
    private RedisUtil redisUtil;


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
        user.addEditedInfo((Long) authenticationFacade.getAuthentication().getPrincipal());
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
     * @param
     */
    @Override
    public int checkPendingUser(SysUser user) {

        Integer passwordLimit = 7;
        Integer failCount = user.getFailCount();
        if(failCount == null) {
            failCount = 0;
        }
        failCount ++;
        user.setFailCount(failCount);

        List<SerPlatformOtherParams> serPlatformOtherParams = serPlatformOtherParamRepository.findAll();

        if(serPlatformOtherParams != null && serPlatformOtherParams.size() > 0) {

            Long loginNumber = serPlatformOtherParams.get(0).getLoginNumber();
            if(loginNumber != null) {
                passwordLimit = loginNumber.intValue();
            }
        }

        int answer = 0;
        if(passwordLimit > 0) {
            if(failCount == passwordLimit - 1) {
                answer = 1;
            } else if(failCount >= passwordLimit) {
                user.setStatus(SysUser.Status.PENDING);
                answer = 2;

            }
        }
        sysUserRepository.save(user);
        return answer;
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
        List<Long> roleIdList = new ArrayList<>();
        sysUser.getRoles().forEach(sysRole -> {
            roleIdList.add(sysRole.getRoleId());
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
                roleIdList.add(sysRole.getRoleId());
            });
        }



        List<SysRoleResource> roleResourceList = Lists.newArrayList(sysRoleResourceRepository.findAll(QSysRoleResource.sysRoleResource.roleId.in(roleIdList)));

        List<Long> resourceIdList = new ArrayList<>();
        roleResourceList.forEach(roleResource -> {
            if(!resourceIdList.contains(roleResource.getResourceId())) {
                resourceIdList.add(roleResource.getResourceId());
            }
        });

        List<SysResource> userAvalialbleResource = Lists.newArrayList(sysResourceRepository.findAll(QSysResource.sysResource.resourceId.in(resourceIdList)));
        availableSysResourceListPre.addAll(userAvalialbleResource);


        List<SysResource> availableSysResourceList = new ArrayList<>();
        for(int i = 0; i < availableSysResourceListPre.size(); i ++) {
            boolean isExist = false;
            for(int j = 0; j < availableSysResourceList.size(); j ++) {
                if(availableSysResourceList.get(j).getResourceId().equals(availableSysResourceListPre.get(i).getResourceId())) {
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

    private CategoryUser getDataCategoryUserListPre() {
        Date startTime = new Date();
        Long userId = (Long) authenticationFacade.getAuthentication().getPrincipal();
        SysUserSimplifiedOnlyHasName sysUserSimple = sysUserSimpleRepository.findOne(QSysUserSimplifiedOnlyHasName.sysUserSimplifiedOnlyHasName.userId.eq(userId)).get();
        Date endTime = new Date();
        long userTime = endTime.getTime() - startTime.getTime();
        QSysUserGroupUser builder = QSysUserGroupUser.sysUserGroupUser;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        predicate.and(builder.userId.eq(sysUserSimple.getUserId()));
        CategoryUser categoryUser = new CategoryUser();


        String levelUser = sysUserSimple.getDataRangeCategory();
        if(levelUser.equals(SysUser.DataRangeCategory.ALL.getValue())) {
            categoryUser.setAll(true);
            return categoryUser;
        }

        List<SysUserGroupUser> sysUserGroupUserList = StreamSupport
                .stream(sysUserGroupUserRepository.findAll(predicate).spliterator(), false)
                .collect(Collectors.toList());

        List<Long> groupIdListPre = new ArrayList<>();
        for(SysUserGroupUser sysUserGroupUser: sysUserGroupUserList) {
            groupIdListPre.add(sysUserGroupUser.getUserGroupId());
        }
        predicate = new BooleanBuilder();
        QSysUserGroupDataRange builderRange = QSysUserGroupDataRange.sysUserGroupDataRange;
        predicate.and(builderRange.userGroupId.in(groupIdListPre));
        List<SysUserGroupDataRange> sysUserGroupList = StreamSupport
                .stream(sysUserGroupDataRangeRepository.findAll(predicate).spliterator(), false)
                .collect(Collectors.toList());


        endTime = new Date();
        long userGroupTime = endTime.getTime() - startTime.getTime();
        List<Long> relateUserList = new ArrayList<>();
        List<Long> relateDataGroupIdList = new ArrayList<>();
        if(levelUser.equals(SysUser.DataRangeCategory.SPECIFIED.getValue())) {
            levelUser = "";
            List<SysUserLookup> lookupList = StreamSupport
                    .stream(sysUserLookupRepository.findAll(QSysUserLookup.sysUserLookup.userId.eq(userId)).spliterator(), false)
                    .collect(Collectors.toList());
            for(int i = 0; i < lookupList.size(); i ++) {
                relateDataGroupIdList.add(lookupList.get(i).getDataGroupId());
            }
        }
        endTime = new Date();
        long relateTime = endTime.getTime() - startTime.getTime();

        String levelGroup = "";
        List<Long> groupIdForDataList = new ArrayList<>();
        List<Long> groupIdList = new ArrayList<>();
        for(int i = 0; i < sysUserGroupList.size(); i ++) {
            SysUserGroupDataRange userGroup = sysUserGroupList.get(i);
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

        endTime = new Date();
        long groupTime = endTime.getTime() - startTime.getTime();
        if(levelGroup.equals(SysUserGroup.DataRangeCategory.ALL.getValue())) {
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
        endTime = new Date();
        long groupDataTime = endTime.getTime() - startTime.getTime();

        if(groupIdForDataList.size() > 0) {
            List<SysUserGroupLookup> userGroupLookupList = StreamSupport
                    .stream(sysUserGroupLookupRepository.findAll(QSysUserGroupLookup.sysUserGroupLookup.userGroupId.in(groupIdForDataList)).spliterator(), false)
                    .collect(Collectors.toList());
            for(int i = 0; i < userGroupLookupList.size(); i ++) {
                relateDataGroupIdList.add(userGroupLookupList.get(i).getDataGroupId());
            }
        }
        endTime = new Date();
        long dataGroupTime = endTime.getTime() - startTime.getTime();
        if(relateDataGroupIdList.size() > 0) {
            List<SysDataGroup> dataGroupList = StreamSupport
                    .stream(sysDataGroupRepository.findAll(QSysDataGroup.sysDataGroup.dataGroupId.in(relateDataGroupIdList)).spliterator(), false)
                    .collect(Collectors.toList());
            for(int i = 0; i < dataGroupList.size(); i ++) {
                SysDataGroup dataGroup = dataGroupList.get(i);
                for(SysUserSimplifiedOnlyHasName user: dataGroup.getUsers()) {
                    relateUserList.add(user.getUserId());
                }
            }
        }
        endTime = new Date();
        long dataGroupUserTime = endTime.getTime() - startTime.getTime();
        List<Long> relateOrgIdList = new ArrayList<>();
        if(levelUser.equals(SysUser.DataRangeCategory.ORG.getValue())) {
            relateOrgIdList.add(sysUserSimple.getOrgId());
        } else if(levelUser.equals(SysUser.DataRangeCategory.ORG_DESC.getValue())) {
            SysOrg parentOrg = null;
            List<SysOrg> allOrg = sysOrgRepository.findAll();
            for(SysOrg org: allOrg){
                if(org.getOrgId().equals(sysUserSimple.getOrgId())) {
                    parentOrg = org;
                }
            }
            Queue<SysOrg> queue = new LinkedList<>();

            queue.add(parentOrg);

            while (!queue.isEmpty()) {
                SysOrg head = queue.remove();
                relateOrgIdList.add(head.getOrgId());
                for(int i = 0; i < allOrg.size(); i ++) {
                    if(allOrg.get(i).getParentOrgId().equals(head.getOrgId())) {
                        queue.add(allOrg.get(i));
                    }
                }
            }
            for(int i = 0; i < allOrg.size(); i ++) {
                relateOrgIdList.add(allOrg.get(i).getOrgId());
            }
        } else {
            relateUserList.add(userId);
        }
        endTime = new Date();
        long orgTime = endTime.getTime() - startTime.getTime();

        if(relateOrgIdList.size() > 0) {
            List<SysUserSimplifiedOnlyHasName> userList = StreamSupport
                    .stream(sysUserSimpleRepository.findAll(QSysUserSimplifiedOnlyHasName.sysUserSimplifiedOnlyHasName.orgId.in(relateOrgIdList)).spliterator(), false)
                    .collect(Collectors.toList());
            endTime = new Date();
            long orgUserListTime = endTime.getTime() - startTime.getTime();
            for(SysUserSimplifiedOnlyHasName relateUser: userList) {
                relateUserList.add(relateUser.getUserId());
            }
        }
        endTime = new Date();
        long orgUserTime = endTime.getTime() - startTime.getTime();
        List<Long> answerUserIdList = new ArrayList<>();
        for(Long relateUserId: relateUserList) {
            boolean isExist = false;
            for(Long checkUserId: answerUserIdList) {
                if(checkUserId.equals(relateUserId)) {
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

    @Override
    public void uploadCategoryUserListRedis() {
        CategoryUser categoryUser = getDataCategoryUserListPre();
        Long userId = (Long) authenticationFacade.getAuthentication().getPrincipal();
        ObjectMapper objectMapper = new ObjectMapper();
        String categoryUserStr = "";
        try {
            categoryUserStr = objectMapper.writeValueAsString(categoryUser);
            redisUtil.set(Constants.REDIS_CATEGORY_USER_INFO + "_" + userId,
                    categoryUserStr, Integer.MAX_VALUE);
        } catch (Exception ex) {

        }

    }

    @Override
    public CategoryUser getDataCategoryUserList() {
//        SysUser sysUser = (Long) authenticationFacade.getAuthentication().getPrincipal();
//        Long userId = sysUser.getUserId();
//        try {
//            String categoryUserStr = redisUtil.get(Constants.REDIS_CATEGORY_USER_INFO + "_" + userId);
//            ObjectMapper objectMapper = new ObjectMapper();
//            CategoryUser categoryUser = objectMapper.readValue(categoryUserStr, CategoryUser.class);
//            return categoryUser;
//        } catch (Exception ex) {
//        }
//        return null;
        return getDataCategoryUserListPre();
    }

    @Override
    public SysUser getUserById(Long userId) {
        return sysUserRepository.findOne(QSysUser.sysUser.userId.eq(userId)).get();
    }

}
