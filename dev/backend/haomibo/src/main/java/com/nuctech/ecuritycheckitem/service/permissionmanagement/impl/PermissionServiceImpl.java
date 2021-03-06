/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（PermissionServiceImpl）
 * 文件名：	PermissionServiceImpl.java
 * 描述：	PermissionService impl
 * 作者名：	Choe
 * 日期：	2019/12/10
 */

package com.nuctech.ecuritycheckitem.service.permissionmanagement.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.google.common.collect.Lists;

import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.*;


import com.nuctech.ecuritycheckitem.models.reusables.CategoryUser;
import com.nuctech.ecuritycheckitem.repositories.SysRoleRepository;
import com.nuctech.ecuritycheckitem.repositories.SysResourceRepository;
import com.nuctech.ecuritycheckitem.repositories.SysRoleResourceRepository;
import com.nuctech.ecuritycheckitem.repositories.SysRoleUserRepository;
import com.nuctech.ecuritycheckitem.repositories.SysUserGroupRoleRepository;
import com.nuctech.ecuritycheckitem.repositories.SysDataGroupRepository;
import com.nuctech.ecuritycheckitem.repositories.SysUserRepository;
import com.nuctech.ecuritycheckitem.repositories.SysDataGroupUserRepository;
import com.nuctech.ecuritycheckitem.repositories.SysUserGroupLookupRepository;
import com.nuctech.ecuritycheckitem.repositories.SysUserLookupRepository;


import com.nuctech.ecuritycheckitem.security.AuthenticationFacade;
import com.nuctech.ecuritycheckitem.service.auth.AuthService;
import com.nuctech.ecuritycheckitem.service.logmanagement.AuditLogService;
import com.nuctech.ecuritycheckitem.service.permissionmanagement.PermissionService;
import com.nuctech.ecuritycheckitem.utils.PageResult;
import com.querydsl.core.BooleanBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class PermissionServiceImpl implements PermissionService {
    @Autowired
    SysRoleRepository sysRoleRepository;

    @Autowired
    SysResourceRepository sysResourceRepository;

    @Autowired
    AuthenticationFacade authenticationFacade;

    @Autowired
    SysRoleResourceRepository sysRoleResourceRepository;

    @Autowired
    SysRoleUserRepository sysRoleUserRepository;

    @Autowired
    SysUserGroupRoleRepository sysUserGroupRoleRepository;

    @Autowired
    SysDataGroupRepository sysDataGroupRepository;

    @Autowired
    SysUserRepository sysUserRepository;

    @Autowired
    SysDataGroupUserRepository sysDataGroupUserRepository;

    @Autowired
    SysUserGroupLookupRepository sysUserGroupLookupRepository;

    @Autowired
    SysUserLookupRepository sysUserLookupRepository;

    @Autowired
    AuditLogService auditLogService;

    @Autowired
    AuthService authService;

    @Autowired
    public MessageSource messageSource;

    public static Locale currentLocale = Locale.CHINESE;

    public static String defaultRoleSort = "roleNumber";

    public static String defaultDataGroiupSort = "dataGroupNumber";

    public String getJsonFromRole(SysRole role) {
        SysRole newRole = SysRole.builder()
                .roleId(role.getRoleId())
                .orgId(role.getOrgId())
                .roleNumber(role.getRoleNumber())
                .roleName(role.getRoleName())
                .roleFlag(role.getRoleFlag())
                .status(role.getStatus())
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        String answer = "";
        try {
            SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();
            answer = objectMapper.writer(filters).writeValueAsString(newRole);
        } catch(Exception ex) {
        }
        return answer;
    }

    public String getJsonFromDataGroup(SysDataGroup dataGroup) {
        SysDataGroup newRole = SysDataGroup.builder()
                .dataGroupId(dataGroup.getDataGroupId())
                .orgId(dataGroup.getOrgId())
                .dataGroupNumber(dataGroup.getDataGroupNumber())
                .dataGroupName(dataGroup.getDataGroupName())
                .dataGroupFlag(dataGroup.getDataGroupFlag())
                .status(dataGroup.getStatus())
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        String answer = "";
        try {
            SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();
            answer = objectMapper.writer(filters).writeValueAsString(newRole);
        } catch(Exception ex) {
        }
        return answer;
    }

    /**
     * create role
     * @param role
     * @param resourceIdList
     * @return
     */
    @Override
    @Transactional
    public boolean createRole(SysRole role, List<Long> resourceIdList) {
        role.addCreatedInfo((Long) authenticationFacade.getAuthentication().getPrincipal());
        SysRole sysRole = sysRoleRepository.save(role);

        // Get resource Id list from request.


        // Get valid resource list from request resource id list.
        List<SysResource> sysResourceList = StreamSupport
                .stream(sysResourceRepository.findAll(
                        QSysResource.sysResource.resourceId.in(resourceIdList)
                        ).spliterator(),
                        false
                )
                .collect(Collectors.toList());

        if (sysResourceList.size() == 0) {
            sysRoleResourceRepository.deleteAll(sysRoleResourceRepository.findAll(QSysRoleResource.sysRoleResource.roleId.eq(sysRole.getRoleId())));
            return true;
        }

        // The category will be gained from the first resource.
        String category = sysResourceList.get(0).getResourceCategory();

        // if category value is neither 'admin' nor 'user', this is invalid request.
        if (!(SysResource.Category.ADMIN.equals(category) || SysResource.Category.USER.equals(category))) {
            return false;
        }

        // Check if all the resource has same category.
        boolean hasInvalidResource = false;
        for (SysResource iterator : sysResourceList) {
            if (!category.equals(iterator.getResourceCategory())) {
                hasInvalidResource = true;
                break;
            }
        }

        // Delete all relations.
        sysRoleResourceRepository.deleteAll(sysRoleResourceRepository.findAll(QSysRoleResource.sysRoleResource.roleId.eq(sysRole.getRoleId())));

        // Save relation.
        List<SysRoleResource> relationList = sysResourceList.stream()
                .map(
                        sysResource -> (SysRoleResource) SysRoleResource
                                .builder()
                                .roleId(sysRole.getRoleId())
                                .resourceId(sysResource.getResourceId())
                                .build()
                                .addCreatedInfo((Long) authenticationFacade.getAuthentication().getPrincipal())
                )
                .collect(Collectors.toList());
        sysRoleResourceRepository.saveAll(relationList);
        String valueAfter = getJsonFromRole(sysRole);
        auditLogService.saveAudioLog(messageSource.getMessage("Create", null, currentLocale), messageSource.getMessage("Success", null, currentLocale),
                "", messageSource.getMessage("Role", null, currentLocale), "", sysRole.getRoleId().toString(), null, true, "", valueAfter);

        return true;
    }

    /**
     * get role predicate
     * @param roleName
     * @param resourceName
     * @return
     */
    private BooleanBuilder getRolePredicate(String roleName, String resourceName, String locale) {
        QSysRole builder = QSysRole.sysRole;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        if (!StringUtils.isEmpty(roleName)) {
            predicate.and(builder.roleName.contains(roleName));
        }
        if (!StringUtils.isEmpty(resourceName)) {
            if(Constants.ENGLISH_LOCALE.equals(locale)) {
                predicate.and(builder.resources.any().resourceCaptionEnglish.contains(resourceName));
            } else {
                predicate.and(builder.resources.any().resourceCaption.contains(resourceName));
            }
        }
        CategoryUser categoryUser = authService.getDataCategoryUserList();
        if(categoryUser.isAll() == false) {
            List<Long> userIdList = categoryUser.getUserIdList();
            predicate.and(builder.createdBy.in(userIdList).or(builder.editedBy.in(userIdList)));
        }

        return predicate;
    }



    /**
     * get filtered and paginated role list
     * @param roleName
     * @param resourceName
     * @param currentPage
     * @param perPage
     * @return
     */
    @Override
    public PageResult<SysRole> getRoleListByPage(String sortBy, String order, String roleName, String resourceName, int currentPage, int perPage, String locale) {
        BooleanBuilder predicate = getRolePredicate(roleName, resourceName, locale);

        PageRequest pageRequest = PageRequest.of(currentPage, perPage);
        if (StringUtils.isNotBlank(order) && StringUtils.isNotEmpty(sortBy)) {
            if (order.equals(Constants.SortOrder.ASC)) {
                pageRequest = PageRequest.of(currentPage, perPage, Sort.by(sortBy).ascending());
            }
            else {
                pageRequest = PageRequest.of(currentPage, perPage, Sort.by(sortBy).descending());
            }
        } else {
            pageRequest = PageRequest.of(currentPage, perPage, Sort.by(defaultRoleSort).ascending());
        }
        long total = sysRoleRepository.count(predicate);
        List<SysRole> data = sysRoleRepository.findAll(predicate, pageRequest).getContent();
        return new PageResult<>(total, data);
    }

    /**
     * extract export list
     * @param roleList
     * @param isAll
     * @param idList
     * @return
     */
    private List<SysRole> getRoleExportList(List<SysRole> roleList, boolean isAll, String idList) {
        List<SysRole> exportList = new ArrayList<>();
        if (isAll == false) {
            String[] splits = idList.split(",");
            for (int i = 0; i < roleList.size(); i++) {
                SysRole role = roleList.get(i);
                boolean isExist = false;
                for (int j = 0; j < splits.length; j++) {
                    if (splits[j].equals(role.getRoleId().toString())) {
                        isExist = true;
                        break;
                    }
                }
                if (isExist == true) {
                    exportList.add(role);
                    if(exportList.size() >= Constants.MAX_EXPORT_NUMBER) {
                        break;
                    }
                }
            }
        } else {
            for(int i = 0; i < roleList.size() && i < Constants.MAX_EXPORT_NUMBER; i ++) {
                exportList.add(roleList.get(i));
            }
        }
        return exportList;
    }

    /**
     * get export role list
     * @param roleName
     * @param resourceName
     * @param isAll
     * @param idList
     * @return
     */
    @Override
    public List<SysRole> getExportListByFilter(String sortBy, String order, String roleName, String resourceName, boolean isAll, String idList, String locale) {
        BooleanBuilder predicate = getRolePredicate(roleName, resourceName, locale);
        if(isAll == false) {
            String[] splits = idList.split(",");
            List<Long> roleIdList = new ArrayList<>();
            for(String idStr: splits) {
                roleIdList.add(Long.valueOf(idStr));
            }
            predicate.and(QSysRole.sysRole.roleId.in(roleIdList));
        }
        Sort sort = null;
        if (StringUtils.isNotBlank(order) && StringUtils.isNotEmpty(sortBy)) {
            sort = Sort.by(sortBy).ascending();
            if (order.equals(Constants.SortOrder.DESC)) {
                sort = Sort.by(sortBy).descending();
            }
        } else {
            sort = Sort.by(defaultRoleSort).ascending();
        }

        //get all role list
        List<SysRole> roleList;
        if(sort != null) {
            roleList = StreamSupport
                    .stream(sysRoleRepository.findAll(predicate, sort).spliterator(), false)
                    .collect(Collectors.toList());
        } else {
            roleList = StreamSupport
                    .stream(sysRoleRepository.findAll(predicate).spliterator(), false)
                    .collect(Collectors.toList());
        }


        //List<SysRole> exportList = getRoleExportList(roleList, isAll, idList);
        return roleList;
    }

    /**
     * check if role exists
     * @param roleId
     * @return
     */
    @Override
    public boolean checkRoleExist(long roleId) {
        return sysRoleRepository.exists(QSysRole.
                sysRole.roleId.eq(roleId));
    }

    /**
     * edit role
     * @param roleId
     * @param roleName
     * @param resourceIdList
     * @return
     */
    @Override
    @Transactional
    public boolean modifyRole(long roleId, String roleName, List<Long> resourceIdList) {
        SysRole sysRole = sysRoleRepository.findOne(QSysRole.sysRole.roleId.eq(roleId)).orElse(null);
        String valueBefore = getJsonFromRole(sysRole);


        // Get valid resource list from request resource id list.

        List<SysRoleResource> roleResourceList = Lists.newArrayList(sysRoleResourceRepository.findAll(QSysRoleResource.sysRoleResource.roleId.eq(sysRole.getRoleId())));

        List<SysRoleResource> removeList = new ArrayList<>();
        List<Long> remainList = new ArrayList<>();
        for(SysRoleResource roleResource: roleResourceList) {
            if(resourceIdList.contains(roleResource.getResourceId())) {
                remainList.add(roleResource.getResourceId());
            } else {
                removeList.add(roleResource);
            }
        }
        // Delete all relations.
        sysRoleResourceRepository.deleteAll(removeList);


        // Save relation.
        List<SysRoleResource> relationList = new ArrayList<>();
        for(Long resourceId: resourceIdList) {
            if(remainList.contains(resourceId)) {
                continue;
            }
            SysRoleResource roleResource = SysRoleResource.builder()
                    .resourceId(resourceId)
                    .roleId(roleId)
                    .build();
            roleResource.addCreatedInfo((Long) authenticationFacade.getAuthentication().getPrincipal());
            relationList.add(roleResource);
        }

        sysRoleResourceRepository.saveAll(relationList);
        // Add edited info.
        sysRole.setRoleName(roleName);
        sysRole.addEditedInfo((Long) authenticationFacade.getAuthentication().getPrincipal());
        sysRoleRepository.save(sysRole);
        String valueAfter = getJsonFromRole(sysRole);
        auditLogService.saveAudioLog(messageSource.getMessage("Modify", null, currentLocale), messageSource.getMessage("Success", null, currentLocale),
                "", messageSource.getMessage("Role", null, currentLocale), "", sysRole.getRoleId().toString(), null, true, valueBefore, valueAfter);
        return true;
    }


    /**
     * check if resource exists
     * @param roleId
     * @return
     */
    @Override
    public boolean checkResourceExist(long roleId) {
        return sysRoleResourceRepository.exists(QSysRoleResource.
                sysRoleResource.roleId.eq(roleId));
    }

    /**
     * check if user exists
     * @param roleId
     * @return
     */
    @Override
    public boolean checkUserExist(long roleId) {
        return sysRoleUserRepository.exists(QSysRoleUser.sysRoleUser.roleId.eq(roleId));
    }

    /**
     * check if usergroup exists
     * @param roleId
     * @return
     */
    @Override
    public boolean checkUserGroupExist(long roleId) {
        return sysUserGroupRoleRepository.exists(QSysUserGroupRole.sysUserGroupRole.roleId.eq(roleId));
    }

    /**
     * remove role
     * @param roleId
     */
    @Override
    @Transactional
    public void removeRole(long roleId) {
        SysRole sysRole = sysRoleRepository.findOne(QSysRole.sysRole.roleId.eq(roleId)).orElse(null);
        String valueBefore = getJsonFromRole(sysRole);
        sysRoleRepository.delete(
                SysRole
                        .builder()
                        .roleId(roleId)
                        .build()
        );
        auditLogService.saveAudioLog(messageSource.getMessage("Delete", null, currentLocale), messageSource.getMessage("Success", null, currentLocale),
                "", messageSource.getMessage("Role", null, currentLocale), "", sysRole.getRoleId().toString(), null, true, valueBefore, "");

        return;
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
            return sysDataGroupRepository.exists(QSysDataGroup.sysDataGroup.dataGroupName.eq(groupName));
        }
        return sysDataGroupRepository.exists(QSysDataGroup.sysDataGroup.dataGroupName.eq(groupName)
                .and(QSysDataGroup.sysDataGroup.dataGroupId.ne(groupId)));

    }

    /**
     * check if group number exists
     * @param groupNumber
     * @return
     */
    @Override
    public boolean checkGroupNumberExist(String groupNumber) {
        return sysDataGroupRepository.exists(QSysDataGroup.sysDataGroup.dataGroupNumber.eq(groupNumber));
    }

    /**
     * check if role name exists
     * @param roleName
     * @param roleId
     * @return
     */
    @Override
    public boolean checkRoleNameExist(String roleName, Long roleId) {
        if (roleId == null) {
            return sysRoleRepository.exists(QSysRole.sysRole.roleName.eq(roleName));
        }
        return sysRoleRepository.exists(QSysRole.sysRole.roleName.eq(roleName)
                .and(QSysRole.sysRole.roleId.ne(roleId)));
    }

    /**
     * create new role in case of role number exists
     * @param roleNumber
     * @param roleId
     * @return
     */
    @Override
    public boolean checkRoleNumberExist(String roleNumber, Long roleId) {
        if (roleId == null) {
            return sysRoleRepository.exists(QSysRole.sysRole.roleNumber.eq(roleNumber));
        }
        return sysRoleRepository.exists(QSysRole.sysRole.roleNumber.eq(roleNumber)
                .and(QSysRole.sysRole.roleId.ne(roleId)));
    }

    /**
     * create data group
     * @param dataGroup
     * @param userIdList
     * @return
     */
    @Override
    @Transactional
    public boolean createDataGroup(SysDataGroup dataGroup, List<Long> userIdList) {
        dataGroup.addCreatedInfo((Long) authenticationFacade.getAuthentication().getPrincipal());
        SysDataGroup sysDataGroup = sysDataGroupRepository.save(dataGroup);


        // Generate relation list with valid UserIds which are filtered by comparing to database.
        List<SysDataGroupUser> relationList = StreamSupport.stream(
                sysUserRepository.findAll(QSysUser.sysUser.userId.in(userIdList)).spliterator(),
                false
        )
                .map(
                        sysUser -> (SysDataGroupUser) SysDataGroupUser
                                .builder()
                                .dataGroupId(sysDataGroup.getDataGroupId())
                                .userId(sysUser.getUserId())
                                .build()
                                .addCreatedInfo((Long) authenticationFacade.getAuthentication().getPrincipal())
                )
                .collect(Collectors.toList());

        // Save.
        sysDataGroupUserRepository.saveAll(relationList);
        String valueAfter = getJsonFromDataGroup(sysDataGroup);
        auditLogService.saveAudioLog(messageSource.getMessage("Create", null, currentLocale), messageSource.getMessage("Success", null, currentLocale),
                "", messageSource.getMessage("DataGroup", null, currentLocale), "", dataGroup.getDataGroupId().toString(), null, true, "", valueAfter);

        return true;
    }

    /**
     * get data group predicate with name filter
     * @param dataGroupName
     * @return
     */
    private BooleanBuilder getDataGroupPredicate(String dataGroupName, String userName) {
        QSysDataGroup builder = QSysDataGroup.sysDataGroup;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        if (!StringUtils.isEmpty(dataGroupName)) {
            predicate.and(builder.dataGroupName.contains(dataGroupName));
        }

        if (!StringUtils.isEmpty(userName)) {
            predicate.and(builder.users.any().userName.contains(userName));
        }
        CategoryUser categoryUser = authService.getDataCategoryUserList();
        if(categoryUser.isAll() == false) {
            List<Long> userIdList = categoryUser.getUserIdList();
            predicate.and(builder.createdBy.in(userIdList).or(builder.editedBy.in(userIdList)));
        }

        return predicate;
    }

    /**
     * get paginated and filtered list of data group
     * @param dataGroupName
     * @param currentPage
     * @param perPage
     * @return
     */
    @Override
    public PageResult<SysDataGroup> getDataGroupListByPage(String sortBy, String order, String dataGroupName, String userName, int currentPage, int perPage) {
        BooleanBuilder predicate = getDataGroupPredicate(dataGroupName, userName);

        PageRequest pageRequest = PageRequest.of(currentPage, perPage);
        if (StringUtils.isNotBlank(order) && StringUtils.isNotEmpty(sortBy)) {
            if (order.equals(Constants.SortOrder.ASC)) {
                pageRequest = PageRequest.of(currentPage, perPage, Sort.by(sortBy).ascending());
            }
            else {
                pageRequest = PageRequest.of(currentPage, perPage, Sort.by(sortBy).descending());
            }
        } else {
            pageRequest = PageRequest.of(currentPage, perPage, Sort.by(defaultDataGroiupSort).ascending());
        }

        long total = sysDataGroupRepository.count(predicate);
        List<SysDataGroup> data = sysDataGroupRepository.findAll(predicate, pageRequest).getContent();
        return new PageResult<>(total, data);
    }

    /**
     * extract export data group list
     * @param dataGroupList
     * @param isAll
     * @param idList
     * @return
     */
    private List<SysDataGroup> getDataGroupExportList(List<SysDataGroup> dataGroupList, boolean isAll, String idList) {
        List<SysDataGroup> exportList = new ArrayList<>();
        if (isAll == false) {
            String[] splits = idList.split(",");
            for (int i = 0; i < dataGroupList.size(); i++) {
                SysDataGroup dataGroup = dataGroupList.get(i);
                boolean isExist = false;
                for (int j = 0; j < splits.length; j++) {
                    if (splits[j].equals(dataGroup.getDataGroupId().toString())) {
                        isExist = true;
                        break;
                    }
                }
                if (isExist == true) {
                    exportList.add(dataGroup);
                }
            }
        } else {
            for(int i = 0; i < dataGroupList.size(); i ++) {
                exportList.add(dataGroupList.get(i));
            }
        }
        return exportList;
    }

    /**
     * get filtered export data group list
     * @param dataGroupName
     * @param isAll
     * @param idList
     * @return
     */
    @Override
    public List<SysDataGroup> getExportGroupListByFilter(String sortBy, String order, String dataGroupName, String userName, boolean isAll, String idList) {
        BooleanBuilder predicate = getDataGroupPredicate(dataGroupName, userName);
        if(isAll == false) {
            String[] splits = idList.split(",");
            List<Long> dataGroupIdList = new ArrayList<>();
            for(String idStr: splits) {
                dataGroupIdList.add(Long.valueOf(idStr));
            }
            predicate.and(QSysDataGroup.sysDataGroup.dataGroupId.in(dataGroupIdList));
        }

        Sort sort = null;
        if (StringUtils.isNotBlank(order) && StringUtils.isNotEmpty(sortBy)) {
            sort = Sort.by(sortBy).ascending();
            if (order.equals(Constants.SortOrder.DESC)) {
                sort = Sort.by(sortBy).descending();
            }
        } else {
            sort = Sort.by(defaultDataGroiupSort).ascending();
        }
        //get all data group list
        List<SysDataGroup> dataGroupList;
        if(sort != null) {
            dataGroupList = StreamSupport
                    .stream(sysDataGroupRepository.findAll(predicate, sort).spliterator(), false)
                    .collect(Collectors.toList());
        } else {
            dataGroupList = StreamSupport
                    .stream(sysDataGroupRepository.findAll(predicate).spliterator(), false)
                    .collect(Collectors.toList());
        }


        //List<SysDataGroup> exportList = getDataGroupExportList(dataGroupList, isAll, idList);
        return dataGroupList;
    }

    /**
     * find all resources
     * @return
     */
    @Override
    public List<SysResource> findAllResource() {
        return sysResourceRepository.findAll();
    }

    /**
     * find all data groups
     * @return
     */
    @Override
    public List<SysDataGroup> findAllDataGroup() {
        return sysDataGroupRepository.findAll();
    }

    /**
     * edit data group
     * @param dataGroupId
     * @param dataGroupName
     * @param userIdList
     * @return
     */
    @Override
    @Transactional
    public boolean modifyDataGroup(long dataGroupId, String dataGroupName, List<Long> userIdList) {
        Optional<SysDataGroup> optionalSysDataGroup = sysDataGroupRepository.findOne(QSysDataGroup.sysDataGroup.dataGroupId.eq(dataGroupId));

        SysDataGroup sysDataGroup = optionalSysDataGroup.get();
        String valueBefore = getJsonFromDataGroup(sysDataGroup);

        List<SysDataGroupUser> dataGroupUserList = Lists.newArrayList(
                sysDataGroupUserRepository.findAll(QSysDataGroupUser.sysDataGroupUser.dataGroupId.eq(sysDataGroup.getDataGroupId())));
        List<SysDataGroupUser> removeUserList = new ArrayList<>();
        List<Long> remainList = new ArrayList<>();
        for(SysDataGroupUser dataGroupUser: dataGroupUserList) {
            if(userIdList.contains(dataGroupUser.getUserId())) {
                remainList.add(dataGroupUser.getUserId());
            } else {
                removeUserList.add(dataGroupUser);
            }
        }
        sysDataGroupUserRepository.deleteAll(removeUserList);

        // Generate relation list with valid UserIds which are filtered by comparing to database.
        List<SysDataGroupUser> relationList = new ArrayList<>();
        for(Long userId: userIdList) {
            if(!remainList.contains(userId)) {
                SysDataGroupUser dataGroupUser = SysDataGroupUser.builder()
                        .dataGroupId(dataGroupId)
                        .userId(userId)
                        .build();
                dataGroupUser.addCreatedInfo((Long) authenticationFacade.getAuthentication().getPrincipal());
                relationList.add(dataGroupUser);
            }
        }

        // Save.
        sysDataGroupUserRepository.saveAll(relationList);

        // Add edited info.
        sysDataGroup.setDataGroupName(dataGroupName);
        sysDataGroup.addEditedInfo((Long) authenticationFacade.getAuthentication().getPrincipal());
        sysDataGroupRepository.save(sysDataGroup);
        String valueAfter = getJsonFromDataGroup(sysDataGroup);
        auditLogService.saveAudioLog(messageSource.getMessage("Modify", null, currentLocale), messageSource.getMessage("Success", null, currentLocale),
                "", messageSource.getMessage("DataGroup", null, currentLocale), "", sysDataGroup.getDataGroupId().toString(), null, true, valueBefore, valueAfter);
        return true;
    }

    /**
     * check if data group exists
     * @param dataGroupId
     * @return
     */
    @Override
    public boolean checkDataGroupExist(long dataGroupId) {
        return sysDataGroupRepository.exists(QSysDataGroup.sysDataGroup.dataGroupId.eq(dataGroupId));
    }

    /**
     * check if child data group exists
     * @param dataGroupId
     * @return
     */
    @Override
    public boolean checkGroupChildExist(long dataGroupId) {
        return sysDataGroupUserRepository.exists(QSysDataGroupUser.sysDataGroupUser.dataGroupId.eq(dataGroupId));
    }

    /**
     * check if user loopup exists
     * @param dataGroupId
     * @return
     */
    @Override
    public boolean checkUserLookUpExist(long dataGroupId) {
        return sysUserLookupRepository.exists(QSysUserLookup.sysUserLookup.dataGroupId.eq(dataGroupId));
    }

    /**
     * check if datagroup look up exists
     * @param dataGroupId
     * @return
     */
    @Override
    public boolean checkDataGroupLookupExist(long dataGroupId) {
        return sysUserGroupLookupRepository.exists(QSysUserGroupLookup.sysUserGroupLookup.dataGroupId.eq(dataGroupId));
    }

    /**
     * remove data group
     * @param dataGroupId
     * @return
     */
    @Override
    @Transactional
    public boolean removeDataGroup(long dataGroupId) {
        Optional<SysDataGroup> optionalSysDataGroup = sysDataGroupRepository.findOne(QSysDataGroup.sysDataGroup.dataGroupId.eq(dataGroupId));

        SysDataGroup sysDataGroup = optionalSysDataGroup.get();
        String valueBefore = getJsonFromDataGroup(sysDataGroup);
        sysDataGroupRepository.delete(
                SysDataGroup
                        .builder()
                        .dataGroupId(dataGroupId)
                        .build()
        );
        auditLogService.saveAudioLog(messageSource.getMessage("Delete", null, currentLocale), messageSource.getMessage("Success", null, currentLocale),
                "", messageSource.getMessage("DataGroup", null, currentLocale), "", sysDataGroup.getDataGroupId().toString(), null, true, valueBefore, "");

        return true;
    }
}
