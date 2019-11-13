package com.haomibo.haomibo.controllers.permissionmanagement;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.haomibo.haomibo.controllers.BaseController;
import com.haomibo.haomibo.enums.ResponseMessage;
import com.haomibo.haomibo.jsonfilter.ModelJsonFilters;
import com.haomibo.haomibo.models.db.*;
import com.haomibo.haomibo.models.response.CommonResponseBody;
import com.haomibo.haomibo.models.reusables.FilteringAndPaginationResult;
import com.haomibo.haomibo.validation.annotations.RoleId;
import com.haomibo.haomibo.validation.annotations.UserDataRangeCategory;
import com.haomibo.haomibo.validation.annotations.UserId;
import com.querydsl.core.BooleanBuilder;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Min;
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
     * User with role datatable request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class UserGetByFilterAndPageRequestBody {

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        static class Filter {
            String userName;

            Long orgId;

            String roleName;


        }

        @NotNull
        @Min(1)
        int currentPage;

        @NotNull
        int perPage;

        Filter filter;

    }


    /**
     * User group with role datatable request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class UserGroupGetByFilterAndPageRequestBody {

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        static class Filter {
            String groupName;
            String userName;
            String roleName;
        }

        @NotNull
        @Min(1)
        int currentPage;

        @NotNull
        int perPage;

        Filter filter;

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
        sysUser.setDataRangeCategory(requestBody.getDataRangeCategory());

        sysUserRepository.save(sysUser);

        // Delete all specifically assigned data group from user-dataGroup relation table.
        sysUserLookupRepository.deleteAll(sysUserLookupRepository.findAll(QSysUserLookup.sysUserLookup.userId.eq(sysUser.getUserId())));

        if (SysUser.DataRangeCategory.SPECIFIED.getValue().equals(requestBody.getDataRangeCategory())) {
            // If data range category is SPECIFIED, need to save data group id too.
            sysUserLookupRepository.save(
                    (SysUserLookup) SysUserLookup
                            .builder()
                            .userId(sysUser.getUserId())
                            .dataGroupId(requestBody.getSelectedDataGroupId())
                            .build()
                            .addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal())
            );
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
        sysUserGroup.setDataRangeCategory(requestBody.getDataRangeCategory());

        sysUserGroupRepository.save(sysUserGroup);

        // Delete all specifically assigned data group from userGroup-dataGroup relation table.
        sysUserGroupLookupRepository.deleteAll(sysUserGroupLookupRepository.findAll(QSysUserGroupLookup.sysUserGroupLookup.userGroupId.eq(sysUserGroup.getUserGroupId())));

        if (SysUserGroup.DataRangeCategory.SPECIFIED.getValue().equals(requestBody.getDataRangeCategory())) {
            // If data range category is SPECIFIED, need to save data group id too.
            sysUserGroupLookupRepository.save(
                    (SysUserGroupLookup) SysUserGroupLookup
                            .builder()
                            .userGroupId(sysUserGroup.getUserGroupId())
                            .dataGroupId(requestBody.getSelectedDataGroupId())
                            .build()
                            .addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal())
            );
        }

        return new CommonResponseBody(ResponseMessage.OK);
    }


    /**
     * Role get all request.
     */
    @RequestMapping(value = "/role/get-all", method = RequestMethod.POST)
    public Object roleGetAll() {

        // Get all role list from DB.
        List<SysRole> sysRoleList = sysRoleRepository.findAll();

        // Set filter for response json.

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, sysRoleList));

        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();

        filters.addFilter(ModelJsonFilters.FILTER_SYS_ROLE, SimpleBeanPropertyFilter.serializeAllExcept("resources"));

        value.setFilters(filters);

        return value;
    }


    /**
     * Role get all request.
     */
    @RequestMapping(value = "/user-group/get-all", method = RequestMethod.POST)
    public Object userGroupGetAll() {

        // Get all sys user group list from DB.
        List<SysUserGroup> sysUserGroupList = sysUserGroupRepository.findAll();

        // Set filter for response json.

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, sysUserGroupList));

        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();

        filters
                .addFilter(ModelJsonFilters.FILTER_SYS_USER, SimpleBeanPropertyFilter.serializeAllExcept("org", "roles", "dataGroups"))
                .addFilter(ModelJsonFilters.FILTER_SYS_ROLE, SimpleBeanPropertyFilter.serializeAllExcept("resources"));

        value.setFilters(filters);

        return value;
    }

    /**
     * User with role datatable request.
     */
    @RequestMapping(value = "/user/get-by-filter-and-page", method = RequestMethod.POST)
    public Object userGetByFilterAndPage(
            @RequestBody @Valid UserGetByFilterAndPageRequestBody requestBody,
            BindingResult bindingResult) {


        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // Build query.
        QSysUser builder = QSysUser.sysUser;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        UserGetByFilterAndPageRequestBody.Filter filter = requestBody.getFilter();

        if (filter != null) {
            if (!StringUtils.isEmpty(filter.getUserName())) {
                predicate.and(builder.userName.contains(filter.getUserName()));
            }
            if (!StringUtils.isEmpty(filter.getRoleName())) {
                predicate.and(builder.roles.any().roleName.contains(filter.getRoleName()));
            }

            if (filter.getOrgId() != null) {

                // Build query if the user's org is under the org.
                sysOrgRepository.findOne(QSysOrg.sysOrg.orgId.eq(filter.getOrgId())).ifPresent(parentSysOrg -> {

                    List<SysOrg> parentOrgList = parentSysOrg.generateChildrenList();
                    List<Long> parentOrgIdList = parentOrgList.stream().map(SysOrg::getOrgId).collect(Collectors.toList());

                    predicate.and(builder.orgId.in(parentOrgIdList));

                });
            }
        }

        // Pagination.

        int currentPage = requestBody.getCurrentPage() - 1; // On server side, page is calculated from 0.
        int perPage = requestBody.getPerPage();

        PageRequest pageRequest = PageRequest.of(currentPage, perPage);

        long total = sysUserRepository.count(predicate);
        List<SysUser> data = sysUserRepository.findAll(predicate, pageRequest).getContent();

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(
                ResponseMessage.OK,
                FilteringAndPaginationResult
                        .builder()
                        .total(total)
                        .perPage(perPage)
                        .currentPage(currentPage + 1)
                        .lastPage((int) Math.ceil(((double) total) / perPage))
                        .from(perPage * currentPage + 1)
                        .to(perPage * currentPage + data.size())
                        .data(data)
                        .build()));


        // Set filters.
        FilterProvider filters = ModelJsonFilters
                .getDefaultFilters()
                .addFilter(
                        ModelJsonFilters.FILTER_SYS_ORG,
                        SimpleBeanPropertyFilter.serializeAllExcept("parent", "children", "users"))
                .addFilter(
                        ModelJsonFilters.FILTER_SYS_ROLE,
                        SimpleBeanPropertyFilter.serializeAllExcept("resources"))
                .addFilter(
                        ModelJsonFilters.FILTER_SYS_DATA_GROUP,
                        SimpleBeanPropertyFilter.serializeAllExcept("users"));

        value.setFilters(filters);

        return value;
    }


    /**
     * User group with role datatable request.
     */
    @RequestMapping(value = "/user-group/get-by-filter-and-page", method = RequestMethod.POST)
    public Object userGroupGetByFilterAndPage(
            @RequestBody @Valid UserGroupGetByFilterAndPageRequestBody requestBody,
            BindingResult bindingResult) {


        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // Build query.
        QSysUserGroup builder = QSysUserGroup.sysUserGroup;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        UserGroupGetByFilterAndPageRequestBody.Filter filter = requestBody.getFilter();

        if (filter != null) {
            if (!StringUtils.isEmpty(filter.getGroupName())) {
                predicate.and(builder.groupName.contains(filter.getGroupName()));
            }
            if (!StringUtils.isEmpty(filter.getUserName())) {
                predicate.and(builder.users.any().userName.contains(filter.getUserName()));
            }
            if (!StringUtils.isEmpty(filter.getRoleName())) {
                predicate.and(builder.roles.any().roleName.contains(filter.getRoleName()));
            }

        }

        // Pagination.
        int currentPage = requestBody.getCurrentPage() - 1; // On server side, page is calculated from 0.
        int perPage = requestBody.getPerPage();

        PageRequest pageRequest = PageRequest.of(currentPage, perPage);

        long total = sysUserGroupRepository.count(predicate);
        List<SysUserGroup> data = sysUserGroupRepository.findAll(predicate, pageRequest).getContent();

        // Set filters.
        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(
                ResponseMessage.OK,
                FilteringAndPaginationResult
                        .builder()
                        .total(total)
                        .perPage(perPage)
                        .currentPage(currentPage + 1)
                        .lastPage((int) Math.ceil(((double) total) / perPage))
                        .from(perPage * currentPage + 1)
                        .to(perPage * currentPage + data.size())
                        .data(data)
                        .build()));

        FilterProvider filters = ModelJsonFilters
                .getDefaultFilters()
                .addFilter(
                        ModelJsonFilters.FILTER_SYS_USER,
                        SimpleBeanPropertyFilter.serializeAllExcept("org", "roles", "dataGroups"))
                .addFilter(
                        ModelJsonFilters.FILTER_SYS_ROLE,
                        SimpleBeanPropertyFilter.serializeAllExcept("resources"))
                .addFilter(
                        ModelJsonFilters.FILTER_SYS_DATA_GROUP,
                        SimpleBeanPropertyFilter.serializeAllExcept("users"));

        value.setFilters(filters);

        return value;
    }


}
