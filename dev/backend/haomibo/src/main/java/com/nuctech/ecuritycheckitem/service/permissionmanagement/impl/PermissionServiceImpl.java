package com.nuctech.ecuritycheckitem.service.permissionmanagement.impl;

import com.google.common.collect.Lists;
import com.nuctech.ecuritycheckitem.controllers.permissionmanagement.PermissionControlController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.models.db.*;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.repositories.*;
import com.nuctech.ecuritycheckitem.security.AuthenticationFacade;
import com.nuctech.ecuritycheckitem.service.permissionmanagement.PermissionService;
import com.nuctech.ecuritycheckitem.utils.PageResult;
import com.querydsl.core.BooleanBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    @Override
    @Transactional
    public boolean createRole(SysRole role, List<Long> resourceIdList) {
        role.addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
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

        if (hasInvalidResource) {
            return false;
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
                                .addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal())
                )
                .collect(Collectors.toList());
        sysRoleResourceRepository.saveAll(relationList);
        return false;
    }



    private BooleanBuilder getRolePredicate(String roleName) {
        QSysRole builder = QSysRole.sysRole;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        if (!StringUtils.isEmpty(roleName)) {
            predicate.and(builder.roleName.contains(roleName));
        }

        return predicate;
    }

    @Override
    public PageResult<SysRole> getRoleListByPage(String roleName, int currentPage, int perPage) {
        BooleanBuilder predicate = getRolePredicate(roleName);

        PageRequest pageRequest = PageRequest.of(currentPage, perPage);

        long total = sysRoleRepository.count(predicate);
        List<SysRole> data = sysRoleRepository.findAll(predicate, pageRequest).getContent();
        return new PageResult<>(total, data);
    }

    private List<SysRole> getRoleExportList(List<SysRole> roleList, boolean isAll, String idList) {
        List<SysRole> exportList = new ArrayList<>();
        if(isAll == false) {
            String[] splits = idList.split(",");
            for(int i = 0; i < roleList.size(); i ++) {
                SysRole role = roleList.get(i);
                boolean isExist = false;
                for(int j = 0; j < splits.length; j ++) {
                    if(splits[j].equals(role.getRoleId().toString())) {
                        isExist = true;
                        break;
                    }
                }
                if(isExist == true) {
                    exportList.add(role);
                }
            }
        } else {
            exportList = roleList;
        }
        return exportList;
    }

    @Override
    public List<SysRole> getExportListByFilter(String roleName, boolean isAll, String idList) {
        BooleanBuilder predicate = getRolePredicate(roleName);


        //get all role list
        List<SysRole> roleList = StreamSupport
                .stream(sysRoleRepository.findAll(predicate).spliterator(), false)
                .collect(Collectors.toList());


        List<SysRole> exportList = getRoleExportList(roleList, isAll, idList);
        return exportList;
    }

    @Override
    public boolean checkRoleExist(long roleId) {
        return sysRoleRepository.exists(QSysRole.
                sysRole.roleId.eq(roleId));
    }

    @Override
    @Transactional
    public boolean modifyRole(long roleId, List<Long> resourceIdList) {
        SysRole sysRole = sysRoleRepository.findOne(QSysRole.sysRole.roleId.eq(roleId)).orElse(null);


        // Get valid resource list from request resource id list.
        List<SysResource> sysResourceList = Lists.newArrayList(sysResourceRepository.findAll(QSysResource.sysResource.resourceId.in(resourceIdList)));

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

        if (hasInvalidResource) {
            return false;
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
                                .addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal())
                )
                .collect(Collectors.toList());

        sysRoleResourceRepository.saveAll(relationList);

        // Add edited info.
        sysRole.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
        sysRoleRepository.save(sysRole);
        return true;
    }


    @Override
    public boolean checkResourceExist(long roleId) {
        return sysRoleResourceRepository.exists(QSysRoleResource.
                sysRoleResource.roleId.eq(roleId));
    }

    @Override
    public boolean checkUserExist(long roleId) {
        return sysRoleUserRepository.exists(QSysRoleUser.sysRoleUser.roleId.eq(roleId));
    }

    @Override
    public boolean checkUserGroupExist(long roleId) {
        return sysUserGroupRoleRepository.exists(QSysUserGroupRole.sysUserGroupRole.roleId.eq(roleId));
    }

    @Override
    @Transactional
    public void removeRole(long roleId) {
        sysRoleRepository.delete(
                SysRole
                        .builder()
                        .roleId(roleId)
                        .build()
        );
        return ;
    }

    @Override
    @Transactional
    public boolean createDataGroup(SysDataGroup dataGroup, List<Long> userIdList) {
        dataGroup.addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
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
                                .addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal())
                )
                .collect(Collectors.toList());

        // Save.
        sysDataGroupUserRepository.saveAll(relationList);
        return true;
    }

    private BooleanBuilder getDataGroupPredicate(String dataGroupName) {
        QSysDataGroup builder = QSysDataGroup.sysDataGroup;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        if (!StringUtils.isEmpty(dataGroupName)) {
            predicate.and(builder.dataGroupName.contains(dataGroupName));
        }

        return predicate;
    }

    @Override
    public PageResult<SysDataGroup> getDataGroupListByPage(String dataGroupName, int currentPage, int perPage) {
        BooleanBuilder predicate = getDataGroupPredicate(dataGroupName);

        PageRequest pageRequest = PageRequest.of(currentPage, perPage);

        long total = sysDataGroupRepository.count(predicate);
        List<SysDataGroup> data = sysDataGroupRepository.findAll(predicate, pageRequest).getContent();
        return new PageResult<>(total, data);
    }

    private List<SysDataGroup> getDataGroupExportList(List<SysDataGroup> dataGroupList, boolean isAll, String idList) {
        List<SysDataGroup> exportList = new ArrayList<>();
        if(isAll == false) {
            String[] splits = idList.split(",");
            for(int i = 0; i < dataGroupList.size(); i ++) {
                SysDataGroup dataGroup = dataGroupList.get(i);
                boolean isExist = false;
                for(int j = 0; j < splits.length; j ++) {
                    if(splits[j].equals(dataGroup.getDataGroupId().toString())) {
                        isExist = true;
                        break;
                    }
                }
                if(isExist == true) {
                    exportList.add(dataGroup);
                }
            }
        } else {
            exportList = dataGroupList;
        }
        return exportList;
    }

    @Override
    public List<SysDataGroup> getExportGroupListByFilter(String dataGroupName, boolean isAll, String idList) {
        BooleanBuilder predicate = getDataGroupPredicate(dataGroupName);


        //get all data group list
        List<SysDataGroup> dataGroupList = StreamSupport
                .stream(sysDataGroupRepository.findAll(predicate).spliterator(), false)
                .collect(Collectors.toList());

        List<SysDataGroup> exportList = getDataGroupExportList(dataGroupList, isAll, idList);
        return exportList;
    }

    @Override
    public List<SysResource> findAllResource() {
        return sysResourceRepository.findAll();
    }

    @Override
    public List<SysDataGroup> findAllDataGroup() {
        return sysDataGroupRepository.findAll();
    }

    @Override
    @Transactional
    public boolean modifyDataGroup(long dataGroupId, List<Long> userIdList) {
        Optional<SysDataGroup> optionalSysDataGroup = sysDataGroupRepository.findOne(QSysDataGroup.sysDataGroup.dataGroupId.eq(dataGroupId));

        SysDataGroup sysDataGroup = optionalSysDataGroup.get();

        sysDataGroupUserRepository.deleteAll(sysDataGroupUserRepository.findAll(QSysDataGroupUser.sysDataGroupUser.dataGroupId.eq(sysDataGroup.getDataGroupId())));

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
                                .addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal())
                )
                .collect(Collectors.toList());

        // Save.
        sysDataGroupUserRepository.saveAll(relationList);

        // Add edited info.
        sysDataGroup.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
        sysDataGroupRepository.save(sysDataGroup);
        return true;
    }

    @Override
    public boolean checkDataGroupExist(long dataGroupId) {
        return sysDataGroupRepository.exists(QSysDataGroup.sysDataGroup.dataGroupId.eq(dataGroupId));
    }

    @Override
    public boolean checkGroupChildExist(long dataGroupId) {
        return sysDataGroupUserRepository.exists(QSysDataGroupUser.sysDataGroupUser.dataGroupId.eq(dataGroupId));
    }

    @Override
    public boolean checkUserLookUpExist(long dataGroupId) {
        return sysUserLookupRepository.exists(QSysUserLookup.sysUserLookup.dataGroupId.eq(dataGroupId));
    }

    @Override
    public boolean checkDataGroupLookupExist(long dataGroupId) {
        return sysUserGroupLookupRepository.exists(QSysUserGroupLookup.sysUserGroupLookup.dataGroupId.eq(dataGroupId));
    }

    @Override
    @Transactional
    public boolean removeDataGroup(long dataGroupId) {
        sysDataGroupRepository.delete(
                SysDataGroup
                        .builder()
                        .dataGroupId(dataGroupId)
                        .build()
        );
        return true;
    }
}
