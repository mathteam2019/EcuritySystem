/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/10/24
 * @CreatedBy Sandy.
 * @FileName PermissionControlController.java
 * @ModifyHistory
 */
package com.nuctech.ecuritycheckitem.controllers.permissionmanagement;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.google.common.collect.Lists;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.enums.Role;
import com.nuctech.ecuritycheckitem.export.permissionmanagement.permissioncontrol.DataGroupExcelView;
import com.nuctech.ecuritycheckitem.export.permissionmanagement.permissioncontrol.DataGroupPdfView;
import com.nuctech.ecuritycheckitem.export.permissionmanagement.permissioncontrol.RoleExcelView;
import com.nuctech.ecuritycheckitem.export.permissionmanagement.permissioncontrol.RolePdfView;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.*;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.reusables.FilteringAndPaginationResult;
import com.nuctech.ecuritycheckitem.validation.annotations.ResourceId;
import com.nuctech.ecuritycheckitem.validation.annotations.RoleId;
import com.querydsl.core.BooleanBuilder;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
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
import javax.validation.constraints.Pattern;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Permission control controller.
 */
@RestController
@RequestMapping("/permission-management/permission-control")
public class PermissionControlController extends BaseController {


    /**
     * Role create request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class RoleCreateRequestBody {

        @NotNull
        String roleNumber;

        @NotNull
        String roleName;

        @NotNull
        List<Long> resourceIdList;

        String note;

        SysRole convert2SysRole() {

            return SysRole
                    .builder()
                    .roleNumber(this.getRoleNumber())
                    .roleName(this.getRoleName())
                    .note(this.note)
                    .build();

        }
    }

    /**
     * Role modify request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class RoleModifyRequestBody {
        @NotNull
        @RoleId
        long roleId;

        @NotNull
        List<@ResourceId Long> resourceIdList;
    }


    /**
     * Role datatable request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class RoleGetByFilterAndPageRequestBody {

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        static class Filter {

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
     * Role  generate request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class RoleGenerateRequestBody {
        @NotNull
        String exportType;

        String idList;
        @NotNull
        Boolean isAll;

        RoleGetByFilterAndPageRequestBody.Filter filter;
    }


    /**
     * Role delete request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class RoleDeleteRequestBody {

        @NotNull
        long roleId;

    }

    /**
     * Data group datatable request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class DataGroupGetByFilterAndPageRequestBody {

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        static class Filter {

            String dataGroupName;
        }

        @NotNull
        @Min(1)
        int currentPage;

        @NotNull
        int perPage;

        Filter filter;

    }


    /**
     * Data Group  generate request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class DataGroupGenerateRequestBody {
        @NotNull
        String exportType;

        String idList;
        @NotNull
        Boolean isAll;

        DataGroupGetByFilterAndPageRequestBody.Filter filter;
    }


    /**
     * Data group create request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class DataGroupCreateRequestBody {

        @NotNull
        String dataGroupName;

        @NotNull
        String dataGroupNumber;

        @NotNull
        List<Long> userIdList;

        String note;

        SysDataGroup convert2SysDataGroup() {

            return SysDataGroup
                    .builder()
                    .dataGroupNumber(this.getDataGroupNumber())
                    .dataGroupName(this.getDataGroupName())
                    .note(this.note)
                    .build();

        }
    }


    /**
     * Data group modify request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class DataGroupModifyRequestBody {
        @NotNull
        long dataGroupId;

        @NotNull
        List<Long> userIdList;
    }

    /**
     * Data group delete request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class DataGroupDeleteRequestBody {

        @NotNull
        long dataGroupId;

    }


    /**
     * DataGroup get all request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class DataGroupGetAllRequestBody {

        static class GetAllType {
            static final String BARE = "bare";
            static final String WITH_USERS = "with_users";
        }

        @Pattern(regexp = GetAllType.BARE + "|" + GetAllType.WITH_USERS)
        String type = GetAllType.BARE;


    }

    /**
     * Role create request.
     */
    @PreAuthorize(Role.Authority.HAS_ROLE_CREATE)
    @RequestMapping(value = "/role/create", method = RequestMethod.POST)
    public Object roleCreate(
            @RequestBody @Valid RoleCreateRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // Create role with created info.
        SysRole sysRole = sysRoleRepository.save(
                (SysRole) requestBody
                        .convert2SysRole()
                        .addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal())
        );

        // Get resource Id list from request.
        List<Long> resourceIdList = requestBody.getResourceIdList();

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
            return new CommonResponseBody(ResponseMessage.OK);
        }

        // The category will be gained from the first resource.
        String category = sysResourceList.get(0).getResourceCategory();

        // if category value is neither 'admin' nor 'user', this is invalid request.
        if (!(SysResource.Category.ADMIN.equals(category) || SysResource.Category.USER.equals(category))) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
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
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
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


        return new CommonResponseBody(ResponseMessage.OK);
    }


    /**
     * Role datatable request.
     */
    @RequestMapping(value = "/role/get-by-filter-and-page", method = RequestMethod.POST)
    public Object roleGetByFilterAndPage(
            @RequestBody @Valid RoleGetByFilterAndPageRequestBody requestBody,
            BindingResult bindingResult) {


        if (bindingResult.hasErrors()) {
            return null;
        }

        QSysRole builder = QSysRole.sysRole;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        RoleGetByFilterAndPageRequestBody.Filter filter = requestBody.getFilter();

        if (filter != null) {
            if (!StringUtils.isEmpty(filter.getRoleName())) {
                predicate.and(builder.roleName.contains(filter.getRoleName()));
            }
        }

        int currentPage = requestBody.getCurrentPage() - 1; // On server side, page is calculated from 0.
        int perPage = requestBody.getPerPage();

        PageRequest pageRequest = PageRequest.of(currentPage, perPage);

        long total = sysRoleRepository.count(predicate);
        List<SysRole> data = sysRoleRepository.findAll(predicate, pageRequest).getContent();

        // Set filter.

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
                .getDefaultFilters();

        value.setFilters(filters);

        return value;
    }

    /**
     * Role generate file request.
     */
    @RequestMapping(value = "/role/export", method = RequestMethod.POST)
    public Object roleGenerateFile(@RequestBody @Valid RoleGenerateRequestBody requestBody,
                                   BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        QSysRole builder = QSysRole.sysRole;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        RoleGetByFilterAndPageRequestBody.Filter filter = requestBody.getFilter();

        if (filter != null) {
            if (!StringUtils.isEmpty(filter.getRoleName())) {
                predicate.and(builder.roleName.contains(filter.getRoleName()));
            }
        }


        //get all role list
        List<SysRole> roleList = StreamSupport
                .stream(sysRoleRepository.findAll(predicate).spliterator(), false)
                .collect(Collectors.toList());
        List<SysRole> exportList = new ArrayList<>();
        if(requestBody.getIsAll() == false) {
            String[] splits = requestBody.getIdList().split(",");
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

        if(requestBody.exportType.equals("excel")) {
            InputStream inputStream = RoleExcelView.buildExcelDocument(exportList);



            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=role.xlsx");

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentType(MediaType.valueOf("application/x-msexcel"))
                    .body(new InputStreamResource(inputStream));
        }
        InputStream inputStream = RolePdfView.buildPDFDocument(exportList);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=role.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));

    }

    /**
     * Role modify request.
     */
    @PreAuthorize(Role.Authority.HAS_ROLE_MODIFY)
    @RequestMapping(value = "/role/modify", method = RequestMethod.POST)
    public Object roleModify(
            @RequestBody @Valid RoleModifyRequestBody requestBody,
            BindingResult bindingResult) {


        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // Get role from database.
        SysRole sysRole = sysRoleRepository.findOne(QSysRole.sysRole.roleId.eq(requestBody.getRoleId())).orElse(null);

        if (sysRole == null) {
            // If role is not found, it's invalid parameter.
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        List<Long> resourceIdList = requestBody.getResourceIdList();

        // Get valid resource list from request resource id list.
        List<SysResource> sysResourceList = Lists.newArrayList(sysResourceRepository.findAll(QSysResource.sysResource.resourceId.in(resourceIdList)));

        if (sysResourceList.size() == 0) {
            sysRoleResourceRepository.deleteAll(sysRoleResourceRepository.findAll(QSysRoleResource.sysRoleResource.roleId.eq(sysRole.getRoleId())));

            return new CommonResponseBody(ResponseMessage.OK);
        }

        // The category will be gained from the first resource.
        String category = sysResourceList.get(0).getResourceCategory();

        // if category value is neither 'admin' nor 'user', this is invalid request.
        if (!(SysResource.Category.ADMIN.equals(category) || SysResource.Category.USER.equals(category))) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
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
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
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

        return new CommonResponseBody(ResponseMessage.OK);

    }


    /**
     * Role delete request.
     */
    @PreAuthorize(Role.Authority.HAS_ROLE_DELETE)
    @RequestMapping(value = "/role/delete", method = RequestMethod.POST)
    public Object roleDelete(
            @RequestBody @Valid RoleDeleteRequestBody requestBody,
            BindingResult bindingResult) {


        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // Check if role is existing in the database.
        Optional<SysRole> optionalSysRole = sysRoleRepository.findOne(QSysRole.sysRole.roleId.eq(requestBody.getRoleId()));
        if (!optionalSysRole.isPresent()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        SysRole sysRole = optionalSysRole.get();
        if (!sysRole.getResources().isEmpty()) {
            // If the role has relation with resource, it can't be deleted.
            return new CommonResponseBody(ResponseMessage.HAS_CHILDREN);
        }


        if (sysRoleUserRepository.exists(QSysRoleUser.sysRoleUser.roleId.eq(sysRole.getRoleId()))) {
            // If there are users assigned with this role, it can't be deleted.
            return new CommonResponseBody(ResponseMessage.HAS_USERS);
        }

        if (sysUserGroupRoleRepository.exists(QSysUserGroupRole.sysUserGroupRole.roleId.eq(sysRole.getRoleId()))) {
            // If there are user groups assigned with this role, it can't be deleted.
            return new CommonResponseBody(ResponseMessage.HAS_USER_GROUPS);
        }


        sysRoleRepository.delete(
                SysRole
                        .builder()
                        .roleId(sysRole.getRoleId())
                        .build()
        );

        return new CommonResponseBody(ResponseMessage.OK);


    }


    /**
     * Data group create request.
     */
    @PreAuthorize(Role.Authority.HAS_DATA_GROUP_CREATE)
    @RequestMapping(value = "/data-group/create", method = RequestMethod.POST)
    public Object dataGroupCreate(
            @RequestBody @Valid DataGroupCreateRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // Create data group with created info.
        SysDataGroup sysDataGroup = sysDataGroupRepository.save(
                (SysDataGroup) requestBody
                        .convert2SysDataGroup()
                        .addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal())
        );

        // Get user Id list from the request.
        List<Long> userIdList = requestBody.getUserIdList();


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

        return new CommonResponseBody(ResponseMessage.OK);
    }


    /**
     * Data group datatable request.
     */
    @RequestMapping(value = "/data-group/get-by-filter-and-page", method = RequestMethod.POST)
    public Object dataGroupGetByFilterAndPage(
            @RequestBody @Valid DataGroupGetByFilterAndPageRequestBody requestBody,
            BindingResult bindingResult) {


        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        QSysDataGroup builder = QSysDataGroup.sysDataGroup;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        DataGroupGetByFilterAndPageRequestBody.Filter filter = requestBody.getFilter();

        if (filter != null) {
            if (!StringUtils.isEmpty(filter.getDataGroupName())) {
                predicate.and(builder.dataGroupName.contains(filter.getDataGroupName()));
            }
        }

        int currentPage = requestBody.getCurrentPage() - 1; // On server side, page is calculated from 0.
        int perPage = requestBody.getPerPage();

        PageRequest pageRequest = PageRequest.of(currentPage, perPage);

        long total = sysDataGroupRepository.count(predicate);
        List<SysDataGroup> data = sysDataGroupRepository.findAll(predicate, pageRequest).getContent();

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
                        ModelJsonFilters.FILTER_SYS_USER,
                        SimpleBeanPropertyFilter.serializeAllExcept("org", "roles", "dataGroups"));

        value.setFilters(filters);

        return value;
    }


    /**
     * Data Group generate file request.
     */
    @RequestMapping(value = "/data-group/export", method = RequestMethod.POST)
    public Object dataGroupGenerateFile(@RequestBody @Valid DataGroupGenerateRequestBody requestBody,
                                   BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        QSysDataGroup builder = QSysDataGroup.sysDataGroup;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        DataGroupGetByFilterAndPageRequestBody.Filter filter = requestBody.getFilter();

        if (filter != null) {
            if (!StringUtils.isEmpty(filter.getDataGroupName())) {
                predicate.and(builder.dataGroupName.contains(filter.getDataGroupName()));
            }
        }


        //get all data group list
        List<SysDataGroup> dataGroupList = StreamSupport
                .stream(sysDataGroupRepository.findAll(predicate).spliterator(), false)
                .collect(Collectors.toList());
        List<SysDataGroup> exportList = new ArrayList<>();
        if(requestBody.getIsAll() == false) {
            String[] splits = requestBody.getIdList().split(",");
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

        if(requestBody.exportType.equals("excel")) {
            InputStream inputStream = DataGroupExcelView.buildExcelDocument(exportList);



            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=data-group.xlsx");

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentType(MediaType.valueOf("application/x-msexcel"))
                    .body(new InputStreamResource(inputStream));
        }
        InputStream inputStream = DataGroupPdfView.buildPDFDocument(exportList);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=data-group.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));

    }

    /**
     * Data group modify request.
     */
    @PreAuthorize(Role.Authority.HAS_DATA_GROUP_MODIFY)
    @RequestMapping(value = "/data-group/modify", method = RequestMethod.POST)
    public Object dataGroupModify(
            @RequestBody @Valid DataGroupModifyRequestBody requestBody,
            BindingResult bindingResult) {


        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        Optional<SysDataGroup> optionalSysDataGroup = sysDataGroupRepository.findOne(QSysDataGroup.sysDataGroup.dataGroupId.eq(requestBody.getDataGroupId()));

        if (!optionalSysDataGroup.isPresent()) {
            // If data group is not found, this request is invalid.
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        SysDataGroup sysDataGroup = optionalSysDataGroup.get();
        List<Long> userIdList = requestBody.getUserIdList();

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

        return new CommonResponseBody(ResponseMessage.OK);

    }


    /**
     * Data group delete request.
     */
    @PreAuthorize(Role.Authority.HAS_DATA_GROUP_DELETE)
    @RequestMapping(value = "/data-group/delete", method = RequestMethod.POST)
    public Object dataGroupDelete(
            @RequestBody @Valid DataGroupDeleteRequestBody requestBody,
            BindingResult bindingResult) {


        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        Optional<SysDataGroup> optionalSysDataGroup = sysDataGroupRepository.findOne(QSysDataGroup.sysDataGroup.dataGroupId.eq(requestBody.getDataGroupId()));
        if (!optionalSysDataGroup.isPresent()) {
            // If data group is not found, this request is invalid.
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        SysDataGroup sysDataGroup = optionalSysDataGroup.get();
        if (!sysDataGroup.getUsers().isEmpty()) {
            // If data group has users, it can't be deleted.
            return new CommonResponseBody(ResponseMessage.HAS_CHILDREN);
        }

        if (sysUserLookupRepository.exists(QSysUserLookup.sysUserLookup.dataGroupId.eq(sysDataGroup.getDataGroupId()))) {
            // If there are users assigned with this data group, it can't be deleted.
            return new CommonResponseBody(ResponseMessage.HAS_USERS);
        }

        if (sysUserGroupLookupRepository.exists(QSysUserGroupLookup.sysUserGroupLookup.dataGroupId.eq(sysDataGroup.getDataGroupId()))) {
            // If there are user groups assigned with this data group, it can't be deleted.
            return new CommonResponseBody(ResponseMessage.HAS_USER_GROUPS);
        }

        sysDataGroupRepository.delete(
                SysDataGroup
                        .builder()
                        .dataGroupId(sysDataGroup.getDataGroupId())
                        .build()
        );

        return new CommonResponseBody(ResponseMessage.OK);


    }




    /**
     * Data group get all request.
     */
    @RequestMapping(value = "/data-group/get-all", method = RequestMethod.POST)
    public Object dataGroupGetAll(@RequestBody @Valid DataGroupGetAllRequestBody requestBody,
                                  BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // Get all first.
        List<SysDataGroup> sysDataGroupList = sysDataGroupRepository.findAll();

        // Filter. Type can be BARE or WITH_USERS.
        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, sysDataGroupList));

        String type = requestBody.getType();

        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();

        switch (type) {
            case DataGroupGetAllRequestBody.GetAllType.BARE:
                filters.addFilter(ModelJsonFilters.FILTER_SYS_DATA_GROUP, SimpleBeanPropertyFilter.serializeAllExcept("users"));
                break;
            case DataGroupGetAllRequestBody.GetAllType.WITH_USERS:
                filters.addFilter(ModelJsonFilters.FILTER_SYS_USER, SimpleBeanPropertyFilter.serializeAllExcept("org", "roles", "dataGroups"));
                break;
            default:
                break;
        }

        value.setFilters(filters);

        return value;
    }


    /**
     * Resource get all request.
     */
    @RequestMapping(value = "/resource/get-all", method = RequestMethod.POST)
    public Object resourceGetAll() {

        List<SysResource> sysResourceList = sysResourceRepository.findAll();


        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, sysResourceList));

        FilterProvider filters = ModelJsonFilters
                .getDefaultFilters();

        value.setFilters(filters);

        return value;


    }

}
