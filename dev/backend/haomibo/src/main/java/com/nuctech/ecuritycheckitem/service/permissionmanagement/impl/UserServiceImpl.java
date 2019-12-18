package com.nuctech.ecuritycheckitem.service.permissionmanagement.impl;

import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.controllers.permissionmanagement.UserManagementController;
import com.nuctech.ecuritycheckitem.models.db.*;
import com.nuctech.ecuritycheckitem.repositories.SysOrgRepository;
import com.nuctech.ecuritycheckitem.repositories.SysUserGroupRepository;
import com.nuctech.ecuritycheckitem.repositories.SysUserGroupUserRepository;
import com.nuctech.ecuritycheckitem.repositories.SysUserRepository;
import com.nuctech.ecuritycheckitem.security.AuthenticationFacade;
import com.nuctech.ecuritycheckitem.service.permissionmanagement.UserService;
import com.nuctech.ecuritycheckitem.utils.PageResult;
import com.nuctech.ecuritycheckitem.utils.Utils;
import com.querydsl.core.BooleanBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    SysOrgRepository sysOrgRepository;

    @Autowired
    SysUserRepository sysUserRepository;

    @Autowired
    Utils utils;

    @Autowired
    AuthenticationFacade authenticationFacade;

    @Autowired
    SysUserGroupRepository sysUserGroupRepository;

    @Autowired
    SysUserGroupUserRepository sysUserGroupUserRepository;

    @Override
    public boolean checkUserExist(long userId) {
        return sysUserRepository.exists(QSysUser.sysUser.userId.eq(userId));
    }

    @Override
    public boolean checkOrgExist(long orgId) {
        return sysOrgRepository.exists(QSysOrg.sysOrg.orgId.eq(orgId));
    }

    @Override
    public boolean checkAccountExist(String userAccount, Long userId) {
        if(userId == null) {
            return sysUserRepository.exists(QSysUser.sysUser.userAccount.eq(userAccount));
        }
        return sysUserRepository.exists(QSysUser.sysUser.userAccount.eq(userAccount).and(QSysUser.sysUser.userId.ne(userId)));
    }

    @Override
    public boolean checkEmailExist(String email, Long userId) {
        if(userId == null) {
            return sysUserRepository.exists(QSysUser.sysUser.email.eq(email));
        }
        return sysUserRepository.exists(QSysUser.sysUser.email.eq(email).and(QSysUser.sysUser.userId.ne(userId)));
    }

    @Override
    public boolean checkGroupNameExist(String groupName, Long groupId) {
        if(groupId == null) {
            return sysUserGroupRepository.exists(QSysUserGroup.sysUserGroup.groupName.eq(groupName));
        }
        return sysUserGroupRepository.exists(QSysUserGroup.sysUserGroup.groupName.eq(groupName)
                .and(QSysUser.sysUser.userId.ne(groupId)));
    }

    @Override
    public boolean checkGroupNumberExist(String groupNumber, Long groupId) {
        if(groupId == null) {
            return sysUserGroupRepository.exists(QSysUserGroup.sysUserGroup.groupNumber.eq(groupNumber));
        }
        return sysUserGroupRepository.exists(QSysUserGroup.sysUserGroup.groupNumber.eq(groupNumber)
                .and(QSysUser.sysUser.userId.ne(groupId)));
    }

    @Override
    public boolean checkMobileExist(String mobile, Long userId) {
        if(userId == null) {
            return sysUserRepository.exists(QSysUser.sysUser.mobile.eq(mobile));
        }
        return sysUserRepository.exists(QSysUser.sysUser.mobile.eq(mobile).and(QSysUser.sysUser.userId.ne(userId)));
    }

    @Override
    @Transactional
    public boolean createUser(SysUser user, MultipartFile portraitFile) {
        // Process portrait file.
        String fileName = utils.saveImageFile(portraitFile);
        user.setPortrait(fileName);


        // Add created info.
        user.addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());

        sysUserRepository.save(user);
        return true;
    }

    @Override
    @Transactional
    public boolean modifyUser(SysUser user, MultipartFile portraitFile) {
        Optional<SysUser> optionalOldSysUser = sysUserRepository.findOne(QSysUser.sysUser.userId.eq(user.getUserId()));
        SysUser oldSysUser = optionalOldSysUser.get();

        // Don't modify password.
        user.setPassword(oldSysUser.getPassword());

        // Don't modify portrait if uploaded file is not found.
        user.setPortrait(oldSysUser.getPortrait());

        //Don't modify created by and created time
        user.setCreatedBy(oldSysUser.getCreatedBy());
        user.setCreatedTime(oldSysUser.getCreatedTime());

        // Process user portrait file.
        String fileName = utils.saveImageFile(portraitFile);
        if(!fileName.equals("")) {
            user.setPortrait(fileName);
        }

        // Add edited info.
        user.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());

        sysUserRepository.save(user);
        return true;
    }

    private BooleanBuilder getPredicate(String userName, String status, String gender, Long orgId) {
        QSysUser builder = QSysUser.sysUser;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        if (!StringUtils.isEmpty(userName)) {
            predicate.and(builder.userName.contains(userName));
        }
        if (!StringUtils.isEmpty(status)) {
            predicate.and(builder.status.eq(status));
        }
        if (!StringUtils.isEmpty(gender)) {
            predicate.and(builder.gender.eq(gender));
        }
        if (orgId != null) {

            // Build query if the user's org is under the org.
            sysOrgRepository.findOne(QSysOrg.sysOrg.orgId.eq(orgId)).ifPresent(parentSysOrg -> {

                List<SysOrg> parentOrgList = parentSysOrg.generateChildrenList();
                List<Long> parentOrgIdList = parentOrgList.stream().map(SysOrg::getOrgId).collect(Collectors.toList());

                predicate.and(builder.orgId.in(parentOrgIdList));
            });
        }

        return predicate;
    }

    @Override
    public PageResult<SysUser> getUserListByPage(String userName, String status, String gender, Long orgId, int currentPage, int perPage) {
        // Build query.

        BooleanBuilder predicate = getPredicate(userName, status, gender, orgId);


        PageRequest pageRequest = PageRequest.of(currentPage, perPage);

        long total = sysUserRepository.count(predicate);
        List<SysUser> data = sysUserRepository.findAll(predicate, pageRequest).getContent();
        return new PageResult<>(total, data);
    }

    private List<SysUser> getExportList(List<SysUser> userList, boolean isAll, String idList) {
        List<SysUser> exportList = new ArrayList<>();
        if(isAll == false) {
            String[] splits = idList.split(",");
            for(int i = 0; i < userList.size(); i ++) {
                SysUser user = userList.get(i);
                boolean isExist = false;
                for(int j = 0; j < splits.length; j ++) {
                    if(splits[j].equals(user.getUserId().toString())) {
                        isExist = true;
                        break;
                    }
                }
                if(isExist == true) {
                    exportList.add(user);
                }
            }
        } else {
            exportList = userList;
        }
        return exportList;
    }

    @Override
    public List<SysUser> getExportUserListByPage(String userName, String status, String gender, Long orgId, boolean isAll, String idList) {
        BooleanBuilder predicate = getPredicate(userName, status, gender, orgId);
        //get all user list
        List<SysUser> userList = StreamSupport
                .stream(sysUserRepository.findAll(predicate).spliterator(), false)
                .collect(Collectors.toList());


        List<SysUser> exportList = getExportList(userList, isAll, idList);
        return exportList;
    }

    @Override
    @Transactional
    public boolean updateStatus(long userId, String status) {
        Optional<SysUser> optionalSysUser = sysUserRepository.findOne(QSysUser.sysUser.userId.eq(userId));

        SysUser sysUser = optionalSysUser.get();

        sysUser.setStatus(status);

        // Add edited info.
        sysUser.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());

        sysUserRepository.save(sysUser);
        return true;
    }

    @Override
    public List<SysUser> findAllUser() {
        QSysUser builder = QSysUser.sysUser;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        predicate.and(builder.status.eq(SysUser.Status.ACTIVE));

        return StreamSupport
                .stream(sysUserRepository.findAll(predicate).spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean createUserGroup(SysUserGroup userGroup, List<Long> userIdList) {
        sysUserGroupRepository.save(userGroup);
        List<SysUserGroupUser> relationList = StreamSupport.stream(
                sysUserRepository.findAll(QSysUser.sysUser.userId.in(userIdList)).spliterator(),
                false)
                .map(sysUser -> (SysUserGroupUser) SysUserGroupUser
                        .builder()
                        .userGroupId(userGroup.getUserGroupId())
                        .userId(sysUser.getUserId())
                        .build()
                        .addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal())
                )
                .collect(Collectors.toList());

        // Save.
        sysUserGroupUserRepository.saveAll(relationList);
        return true;
    }

    private BooleanBuilder getUserGroupPredicate(String groupName) {
        // Build query.
        QSysUserGroup builder = QSysUserGroup.sysUserGroup;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());


        if (!StringUtils.isEmpty(groupName)) {
            predicate.and(builder.groupName.contains(groupName));
        }

        return predicate;
    }

    @Override
    public PageResult<SysUserGroup> getUserGroupListByPage(String groupName, int currentPage, int perPage){
        BooleanBuilder predicate = getUserGroupPredicate(groupName);

        PageRequest pageRequest = PageRequest.of(currentPage, perPage);

        long total = sysUserGroupRepository.count(predicate);
        List<SysUserGroup> data = sysUserGroupRepository.findAll(predicate, pageRequest).getContent();
        return new PageResult<>(total, data);
    }

    private List<SysUserGroup> getExportUserGroupList(List<SysUserGroup> userGroupList, boolean isAll, String idList) {
        List<SysUserGroup> exportList = new ArrayList<>();
        if(isAll == false) {
            String[] splits = idList.split(",");
            for(int i = 0; i < userGroupList.size(); i ++) {
                SysUserGroup userGroup = userGroupList.get(i);
                boolean isExist = false;
                for(int j = 0; j < splits.length; j ++) {
                    if(splits[j].equals(userGroup.getUserGroupId().toString())) {
                        isExist = true;
                        break;
                    }
                }
                if(isExist == true) {
                    exportList.add(userGroup);
                }
            }
        } else {
            exportList = userGroupList;
        }
        return exportList;
    }

    @Override
    public List<SysUserGroup> getExportUserGroupListByPage(String groupName, boolean isAll, String idList) {
        BooleanBuilder predicate = getUserGroupPredicate(groupName);
        //get all user group list
        List<SysUserGroup> userGroupList = StreamSupport
                .stream(sysUserGroupRepository.findAll(predicate).spliterator(), false)
                .collect(Collectors.toList());

        List<SysUserGroup> exportList = getExportUserGroupList(userGroupList, isAll,idList);
        return exportList;
    }

    @Override
    public boolean checkUserGroupExist(long userGroupId) {
        return sysUserGroupRepository.exists(QSysUserGroup.sysUserGroup.userGroupId.eq(userGroupId));
    }

    @Override
    public boolean checkUserGroupUserExist(long userGroupId) {
        return sysUserGroupUserRepository.exists(QSysUserGroupUser.sysUserGroupUser.userGroupId.eq(userGroupId));
    }

    @Override
    @Transactional
    public  boolean modifyUserGroup(long userGroupId, List<Long> userIdList) {
        Optional<SysUserGroup> optionalSysUserGroup = sysUserGroupRepository.findOne(QSysUserGroup.sysUserGroup.userGroupId.eq(userGroupId));

        SysUserGroup sysUserGroup = optionalSysUserGroup.get();

        // Delete all existing relation.
        sysUserGroupUserRepository.deleteAll(sysUserGroupUserRepository.findAll(QSysUserGroupUser.sysUserGroupUser.userGroupId.eq(sysUserGroup.getUserGroupId())));

        // Build relation array which are valid only.
        List<SysUserGroupUser> relationList = StreamSupport.stream(
                sysUserRepository.findAll(QSysUser.sysUser.userId.in(userIdList)).spliterator(),
                false)
                .map(sysUser -> (SysUserGroupUser) SysUserGroupUser
                        .builder()
                        .userGroupId(sysUserGroup.getUserGroupId())
                        .userId(sysUser.getUserId())
                        .build()
                        .addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal())
                )
                .collect(Collectors.toList());

        // Save.
        sysUserGroupUserRepository.saveAll(relationList);

        // Add edited info.
        sysUserGroup.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
        sysUserGroupRepository.save(sysUserGroup);
        return true;
    }

    @Override
    @Transactional
    public  boolean removeUserGroup(long userGroupId) {
        sysUserGroupRepository.delete(
                SysUserGroup
                        .builder()
                        .userGroupId(userGroupId)
                        .build()
        );
        return true;
    }

    @Override
    public List<SysResource> getResourceList(long userId) {
        Optional<SysUser> optionalSysUser = sysUserRepository.findOne(QSysUser.sysUser.userId.eq(userId));

        if (!optionalSysUser.isPresent()) {
            return null;
        }

        SysUser sysUser = optionalSysUser.get();

        // Get all available resources for user.
        List<SysResource> availableSysResourceList = new ArrayList<>();
        sysUser.getRoles().forEach(sysRole -> {
            availableSysResourceList.addAll(sysRole.getResources());
        });
        return availableSysResourceList;
    }
}
