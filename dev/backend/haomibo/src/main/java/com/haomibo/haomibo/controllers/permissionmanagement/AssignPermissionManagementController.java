package com.haomibo.haomibo.controllers.permissionmanagement;

import com.haomibo.haomibo.controllers.BaseController;
import com.haomibo.haomibo.enums.ResponseMessage;
import com.haomibo.haomibo.models.db.*;
import com.haomibo.haomibo.models.response.CommonResponseBody;
import com.haomibo.haomibo.validation.annotations.RoleId;
import com.haomibo.haomibo.validation.annotations.UserDataRangeCategory;
import com.haomibo.haomibo.validation.annotations.UserId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Assign permission management controller.
 */
@RestController
@RequestMapping("/permission-management/assign-permission-management")
public class AssignPermissionManagementController extends BaseController {

    /**
     * Request body for assigning role and data range to user.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class UserAssignRoleAndDataRangeRequestBody {

        @NotNull
        @UserId
        Long userId;

        @NotNull
        List<@RoleId Long> roleIdList;

        @NotNull
        @UserDataRangeCategory
        String dataRangeCategory;

        long selectedDataGroupId;

    }

    /**
     * Request body for assigning role and data range to user group.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class UserGroupAssignRoleAndDataRangeRequestBody {

        @NotNull
        @UserId
        Long userGroupId;

        @NotNull
        List<@RoleId Long> roleIdList;

        @NotNull
        @UserDataRangeCategory
        String dataRangeCategory;

        long selectedDataGroupId;

    }

    /**
     * User assign role and data range request.
     */
    @RequestMapping(value = "/user/assign-role-and-data-range", method = RequestMethod.POST)
    public Object userAssignRoleAndDataRange(
            @RequestBody @Valid UserAssignRoleAndDataRangeRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // Get user.
        SysUser sysUser = sysUserRepository.findOne(QSysUser.sysUser.userId.eq(requestBody.getUserId())).orElse(null);
        if (sysUser == null) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // If data range category is SPECIFIED and data group id is invalid, this is invalid request.
        if (SysUser.DataRangeCategory.SPECIFIED.getValue().equals(requestBody.getDataRangeCategory())) {
            long selectedDataGroupId = requestBody.getSelectedDataGroupId();
            SysDataGroup sysDataGroup = sysDataGroupRepository.findOne(QSysDataGroup.sysDataGroup.dataGroupId.eq(selectedDataGroupId)).orElse(null);
            if (sysDataGroup == null) {
                return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
            }
        }

        // Delete assigned roles for user first.
        sysRoleUserRepository.deleteAll(sysRoleUserRepository.findAll(QSysRoleUser.sysRoleUser.userId.eq(sysUser.getUserId())));

        // Generate role relation list.
        List<SysRoleUser> sysRoleUserList = requestBody
                .getRoleIdList()
                .stream()
                .map(roleId ->
                        SysRoleUser
                                .builder()
                                .roleId(roleId)
                                .userId(sysUser.getUserId())
                                .build())
                .collect(Collectors.toList());

        // Save role relations for user.
        sysRoleUserRepository.saveAll(sysRoleUserList);

        // Set and save data range category for user.
        sysUser.setDataRangeCategory(requestBody.getDataRangeCategory());

        sysUserRepository.save(sysUser);

        // Delete all specifically assigned data group from user-dataGroup relation table.
        sysUserLookupRepository.deleteAll(sysUserLookupRepository.findAll(QSysUserLookup.sysUserLookup.userId.eq(sysUser.getUserId())));

        if (SysUser.DataRangeCategory.SPECIFIED.getValue().equals(requestBody.getDataRangeCategory())) {
            // If data range category is SPECIFIED, need to save data group id too.
            sysUserLookupRepository.save(SysUserLookup.builder().userId(sysUser.getUserId()).dataGroupId(requestBody.getSelectedDataGroupId()).build());
        }

        return new CommonResponseBody(ResponseMessage.OK);
    }


    /**
     * User group assign role and data range request.
     */
    @RequestMapping(value = "/user-group/assign-role-and-data-range", method = RequestMethod.POST)
    public Object userGroupAssignRoleAndDataRange(
            @RequestBody @Valid UserGroupAssignRoleAndDataRangeRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // Get user group.
        SysUserGroup sysUserGroup = sysUserGroupRepository.findOne(QSysUserGroup.sysUserGroup.userGroupId.eq(requestBody.getUserGroupId())).orElse(null);
        if (sysUserGroup == null) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // If data range category is SPECIFIED and data group id is invalid, this is invalid request.
        if (SysUserGroup.DataRangeCategory.SPECIFIED.getValue().equals(requestBody.getDataRangeCategory())) {
            long selectedDataGroupId = requestBody.getSelectedDataGroupId();
            SysDataGroup sysDataGroup = sysDataGroupRepository.findOne(QSysDataGroup.sysDataGroup.dataGroupId.eq(selectedDataGroupId)).orElse(null);
            if (sysDataGroup == null) {
                return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
            }
        }

        // Delete assigned roles for user group first.
        sysUserGroupRoleRepository.deleteAll(sysUserGroupRoleRepository.findAll(QSysUserGroupRole.sysUserGroupRole.userGroupId.eq(sysUserGroup.getUserGroupId())));

        // Generate role relation list.
        List<SysUserGroupRole> sysUserGroupRoleList = requestBody
                .getRoleIdList()
                .stream()
                .map(roleId ->
                        SysUserGroupRole
                                .builder()
                                .roleId(roleId)
                                .userGroupId(sysUserGroup.getUserGroupId())
                                .build())
                .collect(Collectors.toList());

        // Save role relations for user group.
        sysUserGroupRoleRepository.saveAll(sysUserGroupRoleList);

        // Set and save data range category for user.
        sysUserGroup.setDataRangeCategory(requestBody.getDataRangeCategory());

        sysUserGroupRepository.save(sysUserGroup);

        // Delete all specifically assigned data group from userGroup-dataGroup relation table.
        sysUserGroupLookupRepository.deleteAll(sysUserGroupLookupRepository.findAll(QSysUserGroupLookup.sysUserGroupLookup.userGroupId.eq(sysUserGroup.getUserGroupId())));

        if (SysUserGroup.DataRangeCategory.SPECIFIED.getValue().equals(requestBody.getDataRangeCategory())) {
            // If data range category is SPECIFIED, need to save data group id too.
            sysUserGroupLookupRepository.save(SysUserGroupLookup.builder().userGroupId(sysUserGroup.getUserGroupId()).dataGroupId(requestBody.getSelectedDataGroupId()).build());
        }

        return new CommonResponseBody(ResponseMessage.OK);
    }

}
