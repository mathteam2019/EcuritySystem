/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（AssignPermissionServiceImpl）
 * 文件名：	AssignPermissionServiceImpl.java
 * 描述：	AssignPermissionService impl
 * 作者名：	Choe
 * 日期：	2019/12/10
 */

package com.nuctech.ecuritycheckitem.service.permissionmanagement.impl;

import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.models.db.SysUser;
import com.nuctech.ecuritycheckitem.models.db.QSysUser;
import com.nuctech.ecuritycheckitem.models.db.SysDataGroup;
import com.nuctech.ecuritycheckitem.models.db.QSysDataGroup;
import com.nuctech.ecuritycheckitem.models.db.QSysRoleUser;
import com.nuctech.ecuritycheckitem.models.db.SysRoleUser;
import com.nuctech.ecuritycheckitem.models.db.SysUserLookup;
import com.nuctech.ecuritycheckitem.models.db.QSysUserLookup;
import com.nuctech.ecuritycheckitem.models.db.QSysUserGroup;
import com.nuctech.ecuritycheckitem.models.db.SysUserGroup;
import com.nuctech.ecuritycheckitem.models.db.SysUserGroupRole;
import com.nuctech.ecuritycheckitem.models.db.QSysUserGroupRole;
import com.nuctech.ecuritycheckitem.models.db.SysUserGroupLookup;
import com.nuctech.ecuritycheckitem.models.db.QSysUserGroupLookup;
import com.nuctech.ecuritycheckitem.models.db.SysRole;
import com.nuctech.ecuritycheckitem.models.db.SysOrg;
import com.nuctech.ecuritycheckitem.models.db.QSysOrg;

import com.nuctech.ecuritycheckitem.repositories.SysUserRepository;
import com.nuctech.ecuritycheckitem.repositories.SysDataGroupRepository;
import com.nuctech.ecuritycheckitem.repositories.SysRoleUserRepository;
import com.nuctech.ecuritycheckitem.repositories.SysUserLookupRepository;
import com.nuctech.ecuritycheckitem.repositories.SysUserGroupRepository;
import com.nuctech.ecuritycheckitem.repositories.SysUserGroupRoleRepository;
import com.nuctech.ecuritycheckitem.repositories.SysUserGroupLookupRepository;
import com.nuctech.ecuritycheckitem.repositories.SysRoleRepository;
import com.nuctech.ecuritycheckitem.repositories.SysOrgRepository;

import com.nuctech.ecuritycheckitem.security.AuthenticationFacade;
import com.nuctech.ecuritycheckitem.service.permissionmanagement.AssignPermissionService;
import com.nuctech.ecuritycheckitem.utils.PageResult;
import com.querydsl.core.BooleanBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class AssignPermissionServiceImpl implements AssignPermissionService {

    @Autowired
    SysUserRepository sysUserRepository;

    @Autowired
    SysDataGroupRepository sysDataGroupRepository;

    @Autowired
    SysRoleUserRepository sysRoleUserRepository;

    @Autowired
    SysUserLookupRepository sysUserLookupRepository;

    @Autowired
    AuthenticationFacade authenticationFacade;

    @Autowired
    SysUserGroupRepository sysUserGroupRepository;

    @Autowired
    SysUserGroupRoleRepository sysUserGroupRoleRepository;

    @Autowired
    SysUserGroupLookupRepository sysUserGroupLookupRepository;

    @Autowired
    SysRoleRepository sysRoleRepository;

    @Autowired
    SysOrgRepository sysOrgRepository;

    /**
     * Check user role assigned
     * @param userId
     * @return
     */
    public boolean checkUserAssignRole(Long userId) {
        SysUser sysUser = sysUserRepository.findOne(QSysUser.sysUser.userId.eq(userId)).orElse(null);
        if (sysUser == null) {
            return false;
        }
        boolean isExist = sysRoleUserRepository.exists(QSysRoleUser.sysRoleUser.userId.eq(sysUser.getUserId()));
        return isExist;
    }

    /**
     * Assign user role and data range
     *
     * @param userId
     * @param roleIdList
     * @param dataRangeCategory
     * @param selectedDataGroupId
     * @return
     */
    public boolean userAssignRoleAndDataRange(Long userId, List<Long> roleIdList, String dataRangeCategory, Long selectedDataGroupId) {

        SysUser sysUser = sysUserRepository.findOne(QSysUser.sysUser.userId.eq(userId)).orElse(null);
        if (sysUser == null) {
            return false;
        }

        // If data range category is SPECIFIED and data group id is invalid, this is invalid request.
        if (SysUser.DataRangeCategory.SPECIFIED.getValue().equals(dataRangeCategory)) {
            SysDataGroup sysDataGroup = sysDataGroupRepository.findOne(QSysDataGroup.sysDataGroup.dataGroupId.eq(selectedDataGroupId)).orElse(null);
            if (sysDataGroup == null) {
                return false;
            }
        }

        // Delete assigned roles for user first.
        sysRoleUserRepository.deleteAll(sysRoleUserRepository.findAll(QSysRoleUser.sysRoleUser.userId.eq(sysUser.getUserId())));

        // Generate role relation list.
        List<SysRoleUser> sysRoleUserList = roleIdList
                .stream()
                .map(
                        roleId -> (SysRoleUser) SysRoleUser
                                .builder()
                                .roleId(roleId)
                                .userId(sysUser.getUserId())
                                .build()
                                .addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal())
                )
                .collect(Collectors.toList());

        // Save role relations for user.
        sysRoleUserRepository.saveAll(sysRoleUserList);

        // Set and save data range category for user.
        sysUser.setDataRangeCategory(dataRangeCategory);

        sysUserRepository.save(sysUser);

        // Delete all specifically assigned data group from user-dataGroup relation table.
        sysUserLookupRepository.deleteAll(sysUserLookupRepository.findAll(QSysUserLookup.sysUserLookup.userId.eq(sysUser.getUserId())));

        if (SysUser.DataRangeCategory.SPECIFIED.getValue().equals(dataRangeCategory)) {
            // If data range category is SPECIFIED, need to save data group id too.
            sysUserLookupRepository.save(
                    (SysUserLookup) SysUserLookup
                            .builder()
                            .userId(sysUser.getUserId())
                            .dataGroupId(selectedDataGroupId)
                            .build()
                            .addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal())
            );
        }

        return true;

    }

    /**
     * Check user group role assigned
     * @param userGroupId
     * @return
     */
    public boolean checkUserGroupAssignRole(Long userGroupId) {
        SysUserGroup sysUserGroup = sysUserGroupRepository.findOne(QSysUserGroup.sysUserGroup.userGroupId.eq(userGroupId)).orElse(null);
        if (sysUserGroup == null) {
            return false;
        }
        boolean isExist = sysUserGroupRoleRepository.exists(QSysUserGroupRole.sysUserGroupRole.userGroupId.eq(sysUserGroup.getUserGroupId()));
        return isExist;
    }

    /**
     * Assign role and datagroup for user group
     *
     * @param userGroupId
     * @param roleIdList
     * @param dataRangeCategory
     * @param selectedDataGroupId
     * @return
     */
    public boolean userGroupAssignRoleAndDataRange(Long userGroupId, List<Long> roleIdList, String dataRangeCategory, Long selectedDataGroupId) {

        // Get user group.
        SysUserGroup sysUserGroup = sysUserGroupRepository.findOne(QSysUserGroup.sysUserGroup.userGroupId.eq(userGroupId)).orElse(null);
        if (sysUserGroup == null) {
            return false;
        }

        // If data range category is SPECIFIED and data group id is invalid, this is invalid request.
        if (SysUserGroup.DataRangeCategory.SPECIFIED.getValue().equals(dataRangeCategory)) {
            SysDataGroup sysDataGroup = sysDataGroupRepository.findOne(QSysDataGroup.sysDataGroup.dataGroupId.eq(selectedDataGroupId)).orElse(null);
            if (sysDataGroup == null) {
                return false;
            }
        }

        // Delete assigned roles for user group first.
        sysUserGroupRoleRepository.deleteAll(sysUserGroupRoleRepository.findAll(QSysUserGroupRole.sysUserGroupRole.userGroupId.eq(sysUserGroup.getUserGroupId())));

        // Generate role relation list.
        List<SysUserGroupRole> sysUserGroupRoleList = roleIdList
                .stream()
                .map(roleId ->
                        (SysUserGroupRole) SysUserGroupRole
                                .builder()
                                .roleId(roleId)
                                .userGroupId(sysUserGroup.getUserGroupId())
                                .build()
                                .addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal())
                )
                .collect(Collectors.toList());

        // Save role relations for user group.
        sysUserGroupRoleRepository.saveAll(sysUserGroupRoleList);

        // Set and save data range category for user.
        sysUserGroup.setDataRangeCategory(dataRangeCategory);

        sysUserGroupRepository.save(sysUserGroup);

        // Delete all specifically assigned data group from userGroup-dataGroup relation table.
        sysUserGroupLookupRepository.deleteAll(sysUserGroupLookupRepository.findAll(QSysUserGroupLookup.sysUserGroupLookup.userGroupId.eq(sysUserGroup.getUserGroupId())));

        if (SysUserGroup.DataRangeCategory.SPECIFIED.getValue().equals(dataRangeCategory)) {
            // If data range category is SPECIFIED, need to save data group id too.
            sysUserGroupLookupRepository.save(
                    (SysUserGroupLookup) SysUserGroupLookup
                            .builder()
                            .userGroupId(sysUserGroup.getUserGroupId())
                            .dataGroupId(selectedDataGroupId)
                            .build()
                            .addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal())
            );
        }

        return true;

    }

    /**
     * Get all roles
     *
     * @return
     */
    public List<SysRole> roleGetAll() {
        return sysRoleRepository.findAll();
    }

    /**
     * Get all UserGroup
     *
     * @return
     */
    public List<SysUserGroup> userGroupGetAll() {
        return sysUserGroupRepository.findAll();
    }

    /**
     * Get paginated user list by filter
     *
     * @param userName
     * @param orgId
     * @param roleName
     * @param currentPage
     * @param perPage
     * @return
     */
    public PageResult<SysUser> userGetByFilterAndPage(String sortBy, String order, String userName, Long orgId, String roleName, String dataRangeCategory, Integer currentPage, Integer perPage) {

        BooleanBuilder predicate = getPredicate(userName, orgId, roleName, dataRangeCategory);

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

        return new PageResult<SysUser>(total, data);

    }

    /**
     * Get user list by filter
     *
     * @param userName
     * @param orgId
     * @param roleName
     * @return
     */
    public List<SysUser> userGetByFilter(String sortBy, String order, String userName, Long orgId, String roleName, String dataRangeCategory) {
        BooleanBuilder predicate = getPredicate(userName, orgId, roleName, dataRangeCategory);
        Sort sort = null;
        if (StringUtils.isNotBlank(order) && StringUtils.isNotEmpty(sortBy)) {
            sort = new Sort(Sort.Direction.ASC, sortBy);
            if (order.equals(Constants.SortOrder.DESC)) {
                sort = new Sort(Sort.Direction.DESC, sortBy);
            }
        }
        if(sort != null) {
            return StreamSupport
                    .stream(sysUserRepository.findAll(predicate, sort).spliterator(), false)
                    .collect(Collectors.toList());
        }
        return StreamSupport
                .stream(sysUserRepository.findAll(predicate).spliterator(), false)
                .collect(Collectors.toList());
    }

    /**
     * Get paginated user group by filter and page
     *
     * @param groupName
     * @param userName
     * @param roleName
     * @param currentPage
     * @param perPage
     * @return
     */
    public PageResult<SysUserGroup> userGroupGetByFilterAndPage(String sortBy, String order, String groupName, String userName, String roleName, String dataRangeCategory,
                                                                Integer currentPage, Integer perPage) {

        BooleanBuilder predicate = getUserGroupPredicate(groupName, userName, roleName, dataRangeCategory);
        PageRequest pageRequest = PageRequest.of(currentPage, perPage);

        long total = sysUserGroupRepository.count(predicate);
        List<SysUserGroup> data = sysUserGroupRepository.findAll(predicate, pageRequest).getContent();

        return new PageResult<SysUserGroup>(total, data);

    }

    /**
     * Get user group by filter
     *
     * @param groupName
     * @param userName
     * @param roleName
     * @return
     */
    public List<SysUserGroup> userGroupGetByFilter(String sortBy, String order, String groupName, String userName, String roleName, String dataRangeCategory) {
        BooleanBuilder predicate = getUserGroupPredicate(groupName, userName, roleName, dataRangeCategory);

        return StreamSupport
                .stream(sysUserGroupRepository.findAll(predicate).spliterator(), false)
                .collect(Collectors.toList());
    }

    /**
     * Get predicate for user filter
     *
     * @param userName
     * @param orgId
     * @param roleName
     * @return
     */
    private BooleanBuilder getPredicate(String userName, Long orgId, String roleName, String dataRangeCategory) {
        QSysUser builder = QSysUser.sysUser;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());


        if (!StringUtils.isEmpty(userName)) {
            predicate.and(builder.userName.contains(userName));
        }
        if (!StringUtils.isEmpty(roleName)) {
            predicate.and(builder.roles.any().roleName.contains(roleName));
        }

        predicate.and(builder.roles.size().ne(0));

        if (!StringUtils.isEmpty(dataRangeCategory)) {
            predicate.and(builder.dataRangeCategory.eq(dataRangeCategory));
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
     * Get predicate for usergroup filter
     *
     * @param groupName
     * @param userName
     * @param roleName
     * @return
     */
    private BooleanBuilder getUserGroupPredicate(String groupName, String userName, String roleName, String dataRangeCategory) {

        QSysUserGroup builder = QSysUserGroup.sysUserGroup;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());


        if (!StringUtils.isEmpty(groupName)) {
            predicate.and(builder.groupName.contains(groupName));
        }
        if (!StringUtils.isEmpty(userName)) {
            predicate.and(builder.users.any().userName.contains(userName));
        }
        if (!StringUtils.isEmpty(roleName)) {
            predicate.and(builder.roles.any().roleName.contains(roleName));
        }
        predicate.and(builder.roles.size().ne(0));

        if (!StringUtils.isEmpty(dataRangeCategory)) {
            predicate.and(builder.dataRangeCategory.eq(dataRangeCategory));
        }

        return predicate;
    }

}
