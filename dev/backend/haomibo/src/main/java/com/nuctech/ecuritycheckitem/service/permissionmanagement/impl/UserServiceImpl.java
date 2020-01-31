/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（UserServiceImpl）
 * 文件名：	UserServiceImpl.java
 * 描述：	UserService implement
 * 作者名：	Choe
 * 日期：	2019/12/10
 */

package com.nuctech.ecuritycheckitem.service.permissionmanagement.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.models.db.*;

import com.nuctech.ecuritycheckitem.repositories.*;

import com.nuctech.ecuritycheckitem.security.AuthenticationFacade;
import com.nuctech.ecuritycheckitem.service.auth.AuthService;
import com.nuctech.ecuritycheckitem.service.logmanagement.AuditLogService;
import com.nuctech.ecuritycheckitem.service.permissionmanagement.UserService;
import com.nuctech.ecuritycheckitem.utils.PageResult;
import com.nuctech.ecuritycheckitem.utils.Utils;
import com.querydsl.core.BooleanBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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

    @Autowired
    SysUserGroupRoleRepository sysUserGroupRoleRepository;

    @Autowired
    SysDataGroupUserRepository sysDataGroupUserRepository;

    @Autowired
    SysRoleUserRepository sysRoleUserRepository;

    @Autowired
    SysUserLookupRepository sysUserLookupRepository;

    @Autowired
    AuthService authService;

    @Autowired
    AuditLogService auditLogService;

    @Autowired
    public MessageSource messageSource;

    public static Locale currentLocale = Locale.ENGLISH;

    public String getJsonFromUser(SysUser user) {
        SysUser newUser = SysUser.builder()
                .userId(user.getUserId())
                .orgId(user.getOrgId())
                .userName(user.getUserName())
                .userAccount(user.getUserAccount())
                .password(user.getPassword())
                .dataRangeCategory(user.getDataRangeCategory())
                .userNumber(user.getUserNumber())
                .gender(user.getGender())
                .identityCard(user.getIdentityCard())
                .post(user.getPost())
                .education(user.getEducation())
                .degree(user.getDegree())
                .email(user.getEmail())
                .mobile(user.getMobile())
                .address(user.getAddress())
                .category(user.getDataRangeCategory())
                .status(user.getStatus())
                .portrait(user.getPortrait())
                .taskId(user.getTaskId())
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        String answer = "";
        try {
            answer = objectMapper.writeValueAsString(newUser);
        } catch(Exception ex) {
        }
        return answer;
    }

    public String getJsonFromUserGroup(SysUserGroup userGroup) {
        SysUserGroup newRole = SysUserGroup.builder()
                .userGroupId(userGroup.getUserGroupId())
                .orgId(userGroup.getOrgId())
                .dataRangeCategory(userGroup.getDataRangeCategory())
                .groupNumber(userGroup.getGroupNumber())
                .groupName(userGroup.getDataRangeCategory())
                .groupFlag(userGroup.getGroupFlag())
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        String answer = "";
        try {
            answer = objectMapper.writeValueAsString(newRole);
        } catch(Exception ex) {
        }
        return answer;
    }

    /**
     * check if user exists
     * @param userId
     * @return
     */
    @Override
    public boolean checkUserExist(long userId) {
        return sysUserRepository.exists(QSysUser.sysUser.userId.eq(userId));
    }

    /**
     * check if org exists
     * @param orgId
     * @return
     */
    @Override
    public boolean checkOrgExist(long orgId) {
        return sysOrgRepository.exists(QSysOrg.sysOrg.orgId.eq(orgId));
    }

    /**
     * check if account exists
     * @param userAccount
     * @param userId
     * @return
     */
    @Override
    public boolean checkAccountExist(String userAccount, Long userId) {
        if (userId == null) {
            return sysUserRepository.exists(QSysUser.sysUser.userAccount.eq(userAccount));
        }
        return sysUserRepository.exists(QSysUser.sysUser.userAccount.eq(userAccount).and(QSysUser.sysUser.userId.ne(userId)));
    }

    /**
     * check if account exists
     * @param userNumber
     * @param userId
     * @return
     */
    @Override
    public boolean checkNumberExist(String userNumber, Long userId) {
        if (userId == null) {
            return sysUserRepository.exists(QSysUser.sysUser.userNumber.eq(userNumber));
        }
        return sysUserRepository.exists(QSysUser.sysUser.userNumber.eq(userNumber).and(QSysUser.sysUser.userId.ne(userId)));
    }

    /**
     * check if email exists
     * @param email
     * @param userId
     * @return
     */
    @Override
    public boolean checkEmailExist(String email, Long userId) {
        if (userId == null) {
            return sysUserRepository.exists(QSysUser.sysUser.email.eq(email));
        }
        return sysUserRepository.exists(QSysUser.sysUser.email.eq(email).and(QSysUser.sysUser.userId.ne(userId)));
    }

    /**
     * check if group name exists
     * @param groupName
     * @param groupId
     * @return
     */
    @Override
    public boolean checkGroupNameExist(String groupName, Long groupId) {
        if (groupId == null) {
            return sysUserGroupRepository.exists(QSysUserGroup.sysUserGroup.groupName.eq(groupName));
        }
        return sysUserGroupRepository.exists(QSysUserGroup.sysUserGroup.groupName.eq(groupName)
                .and(QSysUser.sysUser.userId.ne(groupId)));
    }

    /**
     * check if group number exists
     * @param groupNumber
     * @param groupId
     * @return
     */
    @Override
    public boolean checkGroupNumberExist(String groupNumber, Long groupId) {
        if (groupId == null) {
            return sysUserGroupRepository.exists(QSysUserGroup.sysUserGroup.groupNumber.eq(groupNumber));
        }
        return sysUserGroupRepository.exists(QSysUserGroup.sysUserGroup.groupNumber.eq(groupNumber)
                .and(QSysUser.sysUser.userId.ne(groupId)));
    }

    /**
     * check if mobile exists
     * @param mobile
     * @param userId
     * @return
     */
    @Override
    public boolean checkMobileExist(String mobile, Long userId) {
        if (userId == null) {
            return sysUserRepository.exists(QSysUser.sysUser.mobile.eq(mobile));
        }
        return sysUserRepository.exists(QSysUser.sysUser.mobile.eq(mobile).and(QSysUser.sysUser.userId.ne(userId)));
    }

    /**
     * Check user group exist which contain user
     * @param userId
     * @return
     */
    public boolean checkParentUserGroupExist(Long userId) {
        return sysUserGroupUserRepository.exists(QSysUserGroupUser.sysUserGroupUser.userId.eq(userId));
    }

    /**
     * Check data group exist which contain user
     * @param userId
     * @return
     */
    public boolean checkParentDataGroupExist(Long userId) {
        return sysDataGroupUserRepository.exists(QSysDataGroupUser.sysDataGroupUser.userId.eq(userId));
    }

    /**
     * Check user have role
     * @param userId
     * @return
     */
    public boolean checkRoleExist(Long userId) {
        return sysRoleUserRepository.exists(QSysRoleUser.sysRoleUser.userId.eq(userId));
    }

    /**
     * create new user
     * @param user
     * @param portraitFile
     * @return
     */
    @Override
    @Transactional
    public boolean createUser(SysUser user, MultipartFile portraitFile) {
        // Process portrait file.
        String fileName = utils.saveImageFile(portraitFile);
        user.setPortrait(fileName);


        // Add created info.
        user.addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());

        sysUserRepository.save(user);
        String valueAfter = getJsonFromUser(user);
        auditLogService.saveAudioLog(messageSource.getMessage("Create", null, currentLocale), messageSource.getMessage("Success", null, currentLocale),
                "", messageSource.getMessage("User", null, currentLocale), "", user.getUserId().toString(), null, true, "", valueAfter);

        return true;
    }

    /**
     * edit user
     * @param user
     * @param portraitFile
     * @return
     */
    @Override
    @Transactional
    public boolean modifyUser(SysUser user, MultipartFile portraitFile) {
        Optional<SysUser> optionalOldSysUser = sysUserRepository.findOne(QSysUser.sysUser.userId.eq(user.getUserId()));
        SysUser oldSysUser = optionalOldSysUser.get();
        String valueBefore = getJsonFromUser(oldSysUser);
        // Don't modify password.
        //user.setPassword(oldSysUser.getPassword());

        // Don't modify portrait if uploaded file is not found.
        user.setPortrait(oldSysUser.getPortrait());

        //Don't modify created by and created time
        user.setCreatedBy(oldSysUser.getCreatedBy());
        user.setCreatedTime(oldSysUser.getCreatedTime());
        user.setStatus(oldSysUser.getStatus());

        // Process user portrait file.
        String fileName = utils.saveImageFile(portraitFile);
        if (!fileName.equals("")) {
            user.setPortrait(fileName);
        }

        // Add edited info.
        user.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());

        sysUserRepository.save(user);
        String valueAfter = getJsonFromUser(user);
        auditLogService.saveAudioLog(messageSource.getMessage("Modify", null, currentLocale), messageSource.getMessage("Success", null, currentLocale),
                "", messageSource.getMessage("User", null, currentLocale), "", user.getUserId().toString(), null, true, valueBefore, valueAfter);

        return true;
    }

    /**
     * get prediate from filer parameters
     * @param userName
     * @param status
     * @param gender
     * @param orgId
     * @return
     */
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

    /**
     * get paginated and filtered user list
     * @param userName
     * @param status
     * @param gender
     * @param orgId
     * @param currentPage
     * @param perPage
     * @return
     */
    @Override
    public PageResult<SysUser> getUserListByPage(String sortBy, String order, String userName, String status, String gender, Long orgId, int currentPage, int perPage) {
        // Build query.

        BooleanBuilder predicate = getPredicate(userName, status, gender, orgId);


        PageRequest pageRequest = PageRequest.of(currentPage, perPage);
        if (StringUtils.isNotBlank(order) && StringUtils.isNotEmpty(sortBy)) {
            if (order.equals(Constants.SortOrder.ASC)) {
                pageRequest = PageRequest.of(currentPage, perPage, Sort.by(sortBy).ascending());
            }
            else {
                pageRequest = PageRequest.of(currentPage, perPage, Sort.by(sortBy).descending());
            }
        }

        long total = sysUserRepository.count(predicate);
        List<SysUser> data = sysUserRepository.findAll(predicate, pageRequest).getContent();
        return new PageResult<>(total, data);
    }

    /**
     * extract export list
     * @param userList
     * @param isAll
     * @param idList
     * @return
     */
    private List<SysUser> getExportList(List<SysUser> userList, boolean isAll, String idList) {
        List<SysUser> exportList = new ArrayList<>();
        if (isAll == false) {
            String[] splits = idList.split(",");
            for (int i = 0; i < userList.size(); i++) {
                SysUser user = userList.get(i);
                boolean isExist = false;
                for (int j = 0; j < splits.length; j++) {
                    if (splits[j].equals(user.getUserId().toString())) {
                        isExist = true;
                        break;
                    }
                }
                if (isExist == true) {
                    exportList.add(user);
                    if(exportList.size() >= Constants.MAX_EXPORT_NUMBER) {
                        break;
                    }
                }
            }
        } else {
            for(int i = 0; i < userList.size() && i < Constants.MAX_EXPORT_NUMBER; i ++) {
                exportList.add(userList.get(i));
            }
        }
        return exportList;
    }

    /**
     * get filtered export user list
     * @param userName
     * @param status
     * @param gender
     * @param orgId
     * @param isAll
     * @param idList
     * @return
     */
    @Override
    public List<SysUser> getExportUserListByPage(String sortBy, String order, String userName, String status, String gender, Long orgId, boolean isAll, String idList) {
        BooleanBuilder predicate = getPredicate(userName, status, gender, orgId);
        Sort sort = null;
        if (StringUtils.isNotBlank(order) && StringUtils.isNotEmpty(sortBy)) {
            sort = new Sort(Sort.Direction.ASC, sortBy);
            if (order.equals(Constants.SortOrder.DESC)) {
                sort = new Sort(Sort.Direction.DESC, sortBy);
            }
        }
        //get all user list
        List<SysUser> userList;
        if(sort != null) {
            userList = StreamSupport
                    .stream(sysUserRepository.findAll(predicate, sort).spliterator(), false)
                    .collect(Collectors.toList());
        } else {
            userList = StreamSupport
                    .stream(sysUserRepository.findAll(predicate).spliterator(), false)
                    .collect(Collectors.toList());
        }



        List<SysUser> exportList = getExportList(userList, isAll, idList);
        return exportList;
    }

    /**
     * update user status
     * @param userId
     * @param status
     * @return
     */
    @Override
    @Transactional
    public boolean updateStatus(long userId, String status) {
        Optional<SysUser> optionalSysUser = sysUserRepository.findOne(QSysUser.sysUser.userId.eq(userId));

        SysUser sysUser = optionalSysUser.get();
        String valueBefore = getJsonFromUser(sysUser);

        sysUser.setStatus(status);

        // Add edited info.
        sysUser.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());

        sysUserRepository.save(sysUser);
        String valueAfter = getJsonFromUser(sysUser);
        auditLogService.saveAudioLog(messageSource.getMessage("UpdateStatus", null, currentLocale), messageSource.getMessage("Success", null, currentLocale),
                "", messageSource.getMessage("User", null, currentLocale), "", sysUser.getUserId().toString(), null, true, valueBefore, valueAfter);
        return true;
    }

    /**
     * modify user password
     * @param userId
     * @param password
     * @return
     */
    public boolean modifyPassword(long userId, String password) {
        Optional<SysUser> optionalSysUser = sysUserRepository.findOne(QSysUser.sysUser.userId.eq(userId));

        SysUser sysUser = optionalSysUser.get();
        if(sysUser.getStatus().equals(SysUser.Status.PENDING)) {
            String valueBefore = getJsonFromUser(sysUser);
            sysUser.setStatus(SysUser.Status.ACTIVE);
            sysUser.setPassword(password);
            sysUser.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());

            sysUserRepository.save(sysUser);
            String valueAfter = getJsonFromUser(sysUser);
            auditLogService.saveAudioLog(messageSource.getMessage("ModifyPassword", null, currentLocale), messageSource.getMessage("Success", null, currentLocale),
                    "", messageSource.getMessage("User", null, currentLocale), "", sysUser.getUserId().toString(), null, true, valueBefore, valueAfter);
            return true;
        }
        return false;

    }

    /**
     * find all users
     * @return
     */
    @Override
    public List<SysUser> findAllUser() {
        QSysUser builder = QSysUser.sysUser;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        String[] list = {SysUser.Status.ACTIVE, SysUser.Status.PENDING};
        predicate.and(builder.status.in(list));

        return StreamSupport
                .stream(sysUserRepository.findAll(predicate).spliterator(), false)
                .collect(Collectors.toList());
    }

    /**
     * create new user group
     * @param userGroup
     * @param userIdList
     * @return
     */
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
        String valueAfter = getJsonFromUserGroup(userGroup);
        auditLogService.saveAudioLog(messageSource.getMessage("Create", null, currentLocale), messageSource.getMessage("Success", null, currentLocale),
                "", messageSource.getMessage("UserGroup", null, currentLocale), "", userGroup.getUserGroupId().toString(), null, true, "", valueAfter);
        return true;
    }

    /**
     * get usergroup predicate with groupname
     * @param groupName
     * @return
     */
    private BooleanBuilder getUserGroupPredicate(String groupName) {
        // Build query.
        QSysUserGroup builder = QSysUserGroup.sysUserGroup;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());


        if (!StringUtils.isEmpty(groupName)) {
            predicate.and(builder.groupName.contains(groupName));
        }

        return predicate;
    }

    /**
     * get pagniated and filtered user group list
     * @param groupName
     * @param currentPage
     * @param perPage
     * @return
     */
    @Override
    public PageResult<SysUserGroup> getUserGroupListByPage(String sortBy, String order, String groupName, int currentPage, int perPage) {
        BooleanBuilder predicate = getUserGroupPredicate(groupName);

        PageRequest pageRequest = PageRequest.of(currentPage, perPage);
        if (StringUtils.isNotBlank(order) && StringUtils.isNotEmpty(sortBy)) {
            if (order.equals(Constants.SortOrder.ASC)) {
                pageRequest = PageRequest.of(currentPage, perPage, Sort.by(sortBy).ascending());
            }
            else {
                pageRequest = PageRequest.of(currentPage, perPage, Sort.by(sortBy).descending());
            }
        }
        long total = sysUserGroupRepository.count(predicate);
        List<SysUserGroup> data = sysUserGroupRepository.findAll(predicate, pageRequest).getContent();
        return new PageResult<>(total, data);
    }

    /**
     * extract usergroup export list
     * @param userGroupList
     * @param isAll
     * @param idList
     * @return
     */
    private List<SysUserGroup> getExportUserGroupList(List<SysUserGroup> userGroupList, boolean isAll, String idList) {
        List<SysUserGroup> exportList = new ArrayList<>();
        if (isAll == false) {
            String[] splits = idList.split(",");
            for (int i = 0; i < userGroupList.size(); i++) {
                SysUserGroup userGroup = userGroupList.get(i);
                boolean isExist = false;
                for (int j = 0; j < splits.length; j++) {
                    if (splits[j].equals(userGroup.getUserGroupId().toString())) {
                        isExist = true;
                        break;
                    }
                }
                if (isExist == true) {
                    exportList.add(userGroup);
                    if(exportList.size() >= Constants.MAX_EXPORT_NUMBER) {
                        break;
                    }
                }
            }
        } else {
            for(int i = 0; i < userGroupList.size() && i < Constants.MAX_EXPORT_NUMBER; i ++) {
                exportList.add(userGroupList.get(i));
            }
        }
        return exportList;
    }

    /**
     * get export usergroup list
     * @param groupName
     * @param isAll
     * @param idList
     * @return
     */
    @Override
    public List<SysUserGroup> getExportUserGroupListByPage(String sortBy, String order, String groupName, boolean isAll, String idList) {
        BooleanBuilder predicate = getUserGroupPredicate(groupName);
        Sort sort = null;
        if (StringUtils.isNotBlank(order) && StringUtils.isNotEmpty(sortBy)) {
            sort = new Sort(Sort.Direction.ASC, sortBy);
            if (order.equals(Constants.SortOrder.DESC)) {
                sort = new Sort(Sort.Direction.DESC, sortBy);
            }
        }
        //get all user group list;
        List<SysUserGroup> userGroupList;
        if(sort != null) {
            userGroupList = StreamSupport
                    .stream(sysUserGroupRepository.findAll(predicate, sort).spliterator(), false)
                    .collect(Collectors.toList());
        } else {
            userGroupList = StreamSupport
                    .stream(sysUserGroupRepository.findAll(predicate).spliterator(), false)
                    .collect(Collectors.toList());
        }


        List<SysUserGroup> exportList = getExportUserGroupList(userGroupList, isAll, idList);
        return exportList;
    }

    /**
     * check if usergroup exists
     * @param userGroupId
     * @return
     */
    @Override
    public boolean checkUserGroupExist(long userGroupId) {
        return sysUserGroupRepository.exists(QSysUserGroup.sysUserGroup.userGroupId.eq(userGroupId));
    }

    /**
     * check if usergroupuser exists
     * @param userGroupId
     * @return
     */
    @Override
    public boolean checkUserGroupUserExist(long userGroupId) {
        return sysUserGroupUserRepository.exists(QSysUserGroupUser.sysUserGroupUser.userGroupId.eq(userGroupId));
    }

    /**
     * check if userGroupRole exists
     * @param userGroupId
     * @return
     */
    @Override
    public boolean checkUserGroupRoleExist(long userGroupId) {
        return sysUserGroupRoleRepository.exists(QSysUserGroupRole.sysUserGroupRole.userGroupId.eq(userGroupId));
    }

    /**
     * edit user group
     * @param userGroupId
     * @param userIdList
     * @return
     */
    @Override
    @Transactional
    public boolean modifyUserGroup(long userGroupId, List<Long> userIdList) {
        Optional<SysUserGroup> optionalSysUserGroup = sysUserGroupRepository.findOne(QSysUserGroup.sysUserGroup.userGroupId.eq(userGroupId));

        SysUserGroup sysUserGroup = optionalSysUserGroup.get();
        String valueBefore = getJsonFromUserGroup(sysUserGroup);

        // Delete all existing relation.
        Iterable<SysUserGroupUser> userGroupList = sysUserGroupUserRepository.findAll(QSysUserGroupUser.sysUserGroupUser.userGroupId.eq(sysUserGroup.getUserGroupId()));
        List<SysUserGroupUser> listTest = StreamSupport.stream(userGroupList.spliterator(), false).collect(Collectors.toList());
        Collectors.toList();
        sysUserGroupUserRepository.deleteAll(userGroupList);

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
        String valueAfter = getJsonFromUserGroup(sysUserGroup);
        auditLogService.saveAudioLog(messageSource.getMessage("Modify", null, currentLocale), messageSource.getMessage("Success", null, currentLocale),
                "", messageSource.getMessage("UserGroup", null, currentLocale), "", sysUserGroup.getUserGroupId().toString(), null, true, valueBefore, valueAfter);
        return true;
    }

    /**
     * remove usergroup
     * @param userGroupId
     * @return
     */
    @Override
    @Transactional
    public boolean removeUserGroup(long userGroupId) {
        Optional<SysUserGroup> optionalSysUserGroup = sysUserGroupRepository.findOne(QSysUserGroup.sysUserGroup.userGroupId.eq(userGroupId));

        SysUserGroup sysUserGroup = optionalSysUserGroup.get();
        String valueBefore = getJsonFromUserGroup(sysUserGroup);
        sysUserGroupRepository.delete(
                SysUserGroup
                        .builder()
                        .userGroupId(userGroupId)
                        .build()
        );
        auditLogService.saveAudioLog(messageSource.getMessage("Delete", null, currentLocale), messageSource.getMessage("Success", null, currentLocale),
                "", messageSource.getMessage("UserGroup", null, currentLocale), "", sysUserGroup.getUserGroupId().toString(), null, true, valueBefore, "");
        return true;
    }

    /**
     * get resoure list with specified user id
     * @param userId
     * @return
     */
    @Override
    public List<SysResource> getResourceList(long userId) {
        Optional<SysUser> optionalSysUser = sysUserRepository.findOne(QSysUser.sysUser.userId.eq(userId));

        if (!optionalSysUser.isPresent()) {
            return null;
        }

        SysUser sysUser = optionalSysUser.get();

        // Get all available resources for user.
        List<SysResource> availableSysResourceList = authService.getAvailableSysResourceList(sysUser);

        return availableSysResourceList;
    }
}
