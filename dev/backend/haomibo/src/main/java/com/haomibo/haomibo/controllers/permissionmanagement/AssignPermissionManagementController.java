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

        // Delete assigned roles for use first.
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

}
