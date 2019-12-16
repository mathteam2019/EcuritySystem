/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/10/22
 * @CreatedBy Sandy.
 * @FileName AssignPermissionManagementController.java
 * @ModifyHistory
 */
package com.nuctech.ecuritycheckitem.controllers.permissionmanagement;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.enums.Role;
import com.nuctech.ecuritycheckitem.export.permissionmanagement.assignpermissionmanagement.*;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.*;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.reusables.FilteringAndPaginationResult;
import com.nuctech.ecuritycheckitem.service.permissionmanagement.AssignPermissionService;
import com.nuctech.ecuritycheckitem.service.permissionmanagement.UserService;
import com.nuctech.ecuritycheckitem.utils.PageResult;
import com.nuctech.ecuritycheckitem.validation.annotations.RoleId;
import com.nuctech.ecuritycheckitem.validation.annotations.UserDataRangeCategory;
import com.nuctech.ecuritycheckitem.validation.annotations.UserId;
import com.querydsl.core.BooleanBuilder;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Assign permission management controller.
 */
@RestController
@RequestMapping("/permission-management/assign-permission-management")
public class AssignPermissionManagementController extends BaseController {

    @Autowired
    AssignPermissionService assignPermissionService;

    @Autowired
    UserService userService;

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
     * User  generate request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class UserGenerateRequestBody {


        String idList;
        @NotNull
        Boolean isAll;

        UserGetByFilterAndPageRequestBody.Filter filter;
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
     * User Group generate request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class UserGroupGenerateRequestBody {
        String idList;
        @NotNull
        Boolean isAll;

        UserGroupGetByFilterAndPageRequestBody.Filter filter;
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

    /**
     * User assign role and data range request.
     */
    @PreAuthorize(Role.Authority.HAS_ASSIGN_USER_CREATE)
    @RequestMapping(value = "/user/assign-role-and-data-range", method = RequestMethod.POST)
    public Object userAssignRoleAndDataRange(
            @RequestBody @Valid UserAssignRoleAndDataRangeRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        if (assignPermissionService.userAssignRoleAndDataRange(requestBody.getUserId(), requestBody.getRoleIdList(), requestBody.getDataRangeCategory(), requestBody.getSelectedDataGroupId())) {
            SysUser sysUser = (SysUser) authenticationFacade.getAuthentication().getPrincipal();

            List<SysResource> permission = userService.getResourceList(sysUser.getUserId());

            MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, permission));


            SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();

            value.setFilters(filters);

            return value;
        }
        else {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }
    }


    /**
     * User group assign role and data range request.
     */
    @PreAuthorize(Role.Authority.HAS_ASSIGN_USER_GROUP_CREATE)
    @RequestMapping(value = "/user-group/assign-role-and-data-range", method = RequestMethod.POST)
    public Object userGroupAssignRoleAndDataRange(
            @RequestBody @Valid UserGroupAssignRoleAndDataRangeRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        if (assignPermissionService.userGroupAssignRoleAndDataRange(requestBody.getUserGroupId(), requestBody.getRoleIdList(), requestBody.getDataRangeCategory(), requestBody.getSelectedDataGroupId())) {
            SysUser sysUser = (SysUser) authenticationFacade.getAuthentication().getPrincipal();

            List<SysResource> permission = userService.getResourceList(sysUser.getUserId());

            MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, permission));


            SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();

            value.setFilters(filters);

            return value;
        }
        else {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

    }


    /**
     * Role get all request.
     */
    @RequestMapping(value = "/role/get-all", method = RequestMethod.POST)
    public Object roleGetAll() {

        // Get all role list from DB.
        List<SysRole> sysRoleList = assignPermissionService.roleGetAll();

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
        List<SysUserGroup> sysUserGroupList = assignPermissionService.userGroupGetAll();

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

        Integer currentPage = requestBody.getCurrentPage();
        Integer perPage = requestBody.getPerPage();
        currentPage --;
        PageResult<SysUser> result = assignPermissionService.userGetByFilterAndPage(
                requestBody.getFilter().getUserName(),
                requestBody.getFilter().getOrgId(),
                requestBody.getFilter().getRoleName(),
                currentPage,
                perPage);

        long total = result.getTotal();
        List<SysUser> data = result.getDataList();

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
     * User generate file request.
     */
    @PreAuthorize(Role.Authority.HAS_ASSIGN_USER_EXPORT)
    @RequestMapping(value = "/user/xlsx", method = RequestMethod.POST)
    public Object userGenerateExcelFile(@RequestBody @Valid UserGenerateRequestBody requestBody,
                                           BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }


        //get all user list
        List<SysUser> userList = assignPermissionService.userGetByFilter(
                requestBody.getFilter().getUserName(),
                requestBody.getFilter().getOrgId(),
                requestBody.getFilter().getRoleName());

        List<SysUser> exportList = getExportList(userList, requestBody.getIsAll(), requestBody.getIdList());
        setDictionary();
        InputStream inputStream = AssignUserExcelView.buildExcelDocument(exportList);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=assign-user.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msexcel"))
                .body(new InputStreamResource(inputStream));
    }


    /**
     * User generate file request.
     */

    @RequestMapping(value = "/user/docx", method = RequestMethod.POST)
    public Object userGenerateWordFile(@RequestBody @Valid UserGenerateRequestBody requestBody,
                                        BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        //get all user list
        List<SysUser> userList = assignPermissionService.userGetByFilter(
                requestBody.getFilter().getUserName(),
                requestBody.getFilter().getOrgId(),
                requestBody.getFilter().getRoleName());

        List<SysUser> exportList = getExportList(userList, requestBody.getIsAll(), requestBody.getIdList());
        setDictionary();
        InputStream inputStream = AssignUserWordView.buildWordDocument(exportList);



        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=assign-user.docx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msword"))
                .body(new InputStreamResource(inputStream));
    }

    /**
     * User generate file request.
     */
    @PreAuthorize(Role.Authority.HAS_ASSIGN_USER_PRINT)
    @RequestMapping(value = "/user/pdf", method = RequestMethod.POST)
    public Object userGeneratePDFFile(@RequestBody @Valid UserGenerateRequestBody requestBody,
                                   BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        //get all user list
        List<SysUser> userList = assignPermissionService.userGetByFilter(
                requestBody.getFilter().getUserName(),
                requestBody.getFilter().getOrgId(),
                requestBody.getFilter().getRoleName());

        List<SysUser> exportList = getExportList(userList, requestBody.getIsAll(), requestBody.getIdList());
        AssignUserPdfView.setResource(res);
        setDictionary();
        InputStream inputStream = AssignUserPdfView.buildPDFDocument(exportList);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=assign-user.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));
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

        Integer currentPage = requestBody.getCurrentPage();
        Integer perPage = requestBody.getPerPage();
        currentPage --;
        PageResult<SysUserGroup> result = assignPermissionService.userGroupGetByFilterAndPage(
                requestBody.getFilter().getGroupName(),
                requestBody.getFilter().getUserName(),
                requestBody.getFilter().getRoleName(),
                currentPage,
                perPage);

        long total = result.getTotal();
        List<SysUserGroup> data = result.getDataList();

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

    private List<SysUserGroup> getUserGroupExportList(List<SysUserGroup> userGroupList, boolean isAll, String idList) {
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

    /**
     * User Group generate excel file request.
     */
    @PreAuthorize(Role.Authority.HAS_ASSIGN_USER_GROUP_EXPORT)
    @RequestMapping(value = "/user-group/xlsx", method = RequestMethod.POST)
    public Object userGroupGenerateExcelFile(@RequestBody @Valid UserGroupGenerateRequestBody requestBody,
                                   BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        //get all user group list
        List<SysUserGroup> userGroupList = assignPermissionService.userGroupGetByFilter(
                requestBody.getFilter().getGroupName(),
                requestBody.getFilter().getUserName(),
                requestBody.getFilter().getRoleName()
        );

        List<SysUserGroup> exportList = getUserGroupExportList(userGroupList, requestBody.getIsAll(), requestBody.getIdList());
        setDictionary();
        InputStream inputStream = AssignUserGroupExcelView.buildExcelDocument(exportList);



        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=assign-user-group.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msexcel"))
                .body(new InputStreamResource(inputStream));

    }

    /**
     * User Group generate word file request.
     */

    @RequestMapping(value = "/user-group/docx", method = RequestMethod.POST)
    public Object userGroupGenerateWordFile(@RequestBody @Valid UserGroupGenerateRequestBody requestBody,
                                             BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        //get all user group list
        List<SysUserGroup> userGroupList = assignPermissionService.userGroupGetByFilter(
                requestBody.getFilter().getGroupName(),
                requestBody.getFilter().getUserName(),
                requestBody.getFilter().getRoleName()
        );


        List<SysUserGroup> exportList = getUserGroupExportList(userGroupList, requestBody.getIsAll(), requestBody.getIdList());
        setDictionary();
        InputStream inputStream = AssignUserGroupWordView.buildWordDocument(exportList);



        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=assign-user-group.docx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msword"))
                .body(new InputStreamResource(inputStream));

    }

    /**
     * User Group generate pdf file request.
     */
    @PreAuthorize(Role.Authority.HAS_ASSIGN_USER_GROUP_PRINT)
    @RequestMapping(value = "/user-group/pdf", method = RequestMethod.POST)
    public Object userGroupGeneratePDFFile(@RequestBody @Valid UserGroupGenerateRequestBody requestBody,
                                             BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        //get all user group list
        List<SysUserGroup> userGroupList = assignPermissionService.userGroupGetByFilter(
                requestBody.getFilter().getGroupName(),
                requestBody.getFilter().getUserName(),
                requestBody.getFilter().getRoleName()
        );

        List<SysUserGroup> exportList = getUserGroupExportList(userGroupList, requestBody.getIsAll(), requestBody.getIdList());
        AssignUserGroupPdfView.setResource(res);
        setDictionary();
        InputStream inputStream = AssignUserGroupPdfView.buildPDFDocument(exportList);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=assign-user-group.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));

    }


}
