/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/10/24
 * @CreatedBy Sandy.
 * @FileName UerManagementController.java
 * @ModifyHistory
 */
package com.nuctech.ecuritycheckitem.controllers.permissionmanagement;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.enums.Role;
import com.nuctech.ecuritycheckitem.export.permissionmanagement.assignpermissionmanagement.AssignUserExcelView;
import com.nuctech.ecuritycheckitem.export.permissionmanagement.usermanagement.*;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.*;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.reusables.FilteringAndPaginationResult;
import com.nuctech.ecuritycheckitem.service.permissionmanagement.UserService;
import com.nuctech.ecuritycheckitem.utils.PageResult;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * User management controller.
 */
@RestController
@RequestMapping("/permission-management/user-management")
public class UserManagementController extends BaseController {

    @Autowired
    UserService userService;


    /**
     * User create request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class UserCreateRequestBody {

        static class PasswordType {
            static final String DEFAULT = "default";
            static final String OTHER = "other";
        }

        @NotNull
        long orgId;

        @NotNull
        String userName;

        @NotNull
        String userAccount;

        @NotNull
        @Pattern(regexp = PasswordType.DEFAULT + "|" + PasswordType.OTHER)
        String passwordType;

        String passwordValue;

        @NotNull
        String userNumber;

        @NotNull
        @Pattern(regexp = SysUser.Gender.MALE + "|" + SysUser.Gender.FEMALE + "|" + SysUser.Gender.OTHER)
        String gender;

        @NotNull
        String identityCard;

        String post;
        String education;
        String degree;
        @Email
        String email;
        String mobile;
        String address;


        String note;

        private MultipartFile portrait;

        SysUser convert2SysUser() {

            return SysUser.builder()
                    .orgId(this.getOrgId())
                    .userName(this.getUserName())
                    .userAccount(this.getUserAccount())
                    .password(PasswordType.DEFAULT.equals(this.getPasswordType()) ? Constants.DEFAULT_PASSWORD_FOR_NEW_SYS_USER : this.getPasswordValue())
                    .userNumber(this.getUserNumber())
                    .gender(this.getGender())
                    .identityCard(this.getIdentityCard())
                    .post(this.getPost())
                    .education(this.getEducation())
                    .degree(this.getDegree())
                    .email(this.getEmail())
                    .mobile(this.getMobile())
                    .address(this.getAddress())
                    .category(SysUser.Category.NORMAL)
                    .status(SysUser.Status.INACTIVE)
                    .note(this.getNote())
                    .build();
        }
    }


    /**
     * User modify request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class UserModifyRequestBody {

        @NotNull
        long userId;

        @NotNull
        long orgId;

        @NotNull
        String userName;

        String userAccount;

        @NotNull
        String userNumber;

        @NotNull
        @Pattern(regexp = SysUser.Gender.MALE + "|" + SysUser.Gender.FEMALE + "|" + SysUser.Gender.OTHER)
        String gender;

        @NotNull
        String identityCard;

        String post;
        String education;
        String degree;
        @Email
        String email;
        String mobile;
        String address;
        String note;

        private MultipartFile portrait;

        SysUser convert2SysUser() {

            return SysUser.builder()
                    .userId(this.getUserId())
                    .orgId(this.getOrgId())
                    .userName(this.getUserName())
                    .userAccount(this.getUserAccount())
                    .userNumber(this.getUserNumber())
                    .gender(this.getGender())
                    .identityCard(this.getIdentityCard())
                    .post(this.getPost())
                    .education(this.getEducation())
                    .degree(this.getDegree())
                    .email(this.getEmail())
                    .mobile(this.getMobile())
                    .address(this.getAddress())
                    .category(SysUser.Category.NORMAL)
                    .status(SysUser.Status.INACTIVE)
                    .note(this.getNote())
                    .build();
        }
    }


    /**
     * User get all request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class UserGetAllRequestBody {

        static class GetAllType {
            static final String BARE = "bare";
            static final String WITH_ORG_TREE = "with_org_tree";
        }

        @Pattern(regexp = GetAllType.BARE + "|" +
                GetAllType.WITH_ORG_TREE)
        String type = GetAllType.BARE;


    }

    /**
     * User datatable request body.
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

            @Pattern(regexp = SysUser.Status.ACTIVE + "|" +
                    SysUser.Status.INACTIVE + "|" +
                    SysUser.Status.PENDING + "|" +
                    SysUser.Status.BLOCKED)
            String status;

            @Pattern(regexp = SysUser.Gender.MALE + "|" +
                    SysUser.Gender.FEMALE + "|" +
                    SysUser.Gender.OTHER)
            String gender;


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
     * User update status request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class UserUpdateStatusRequestBody {

        @NotNull
        Long userId;

        @NotNull
        @Pattern(regexp = SysUser.Status.ACTIVE + "|" + SysUser.Status.INACTIVE + "|" + SysUser.Status.BLOCKED + "|" + SysUser.Status.PENDING)
        String status;

    }


    /**
     * User group create request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class UserGroupCreateRequestBody {

        @NotNull
        String groupNumber;

        @NotNull
        String groupName;

        @NotNull
        List<Long> userIdList;

        String note;

    }


    /**
     * User group modify request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class UserGroupModifyRequestBody {
        @NotNull
        long userGroupId;

        @NotNull
        List<Long> userIdList;
    }


    /**
     * User group datatable request body.
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


    /**
     * User group delete request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class UserGroupDeleteRequestBody {
        @NotNull
        long userGroupId;
    }


    /**
     * User create request.
     */
    @PreAuthorize(Role.Authority.HAS_USER_CREATE)
    @RequestMapping(value = "/user/create", method = RequestMethod.POST)
    public Object userCreate(
            @ModelAttribute @Valid UserCreateRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // Check if org is valid.
        if (!userService.checkOrgExist(requestBody.getOrgId())) {
            // If organization is not found, this is invalid request.
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // Check if user account is duplicated.
        if (userService.checkAccountExist(requestBody.getUserAccount(), null)) {
            return new CommonResponseBody(ResponseMessage.USED_USER_ACCOUNT);
        }

        // Check password.
        if (UserCreateRequestBody.PasswordType.OTHER.equals(requestBody.getPasswordType()) && (requestBody.getPasswordValue() == null || requestBody.getPasswordValue().length() < 6)) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // Check if user email is duplicated.
        if (!requestBody.getEmail().equals("") && userService.checkEmailExist(requestBody.getEmail(), null)) {
            return new CommonResponseBody(ResponseMessage.USED_EMAIL);
        }

        // Check if user phone number is duplicated.
        if (!requestBody.getMobile().equals("") && userService.checkMobileExist(requestBody.getMobile(), null)) {
            return new CommonResponseBody(ResponseMessage.USED_MOBILE);
        }

        SysUser sysUser = requestBody.convert2SysUser();
        userService.createUser(sysUser, requestBody.getPortrait());


        return new CommonResponseBody(ResponseMessage.OK);
    }


    /**
     * User modify request.
     */
    @PreAuthorize(Role.Authority.HAS_USER_MODIFY)
    @RequestMapping(value = "/user/modify", method = RequestMethod.POST)
    public Object userModify(
            @ModelAttribute @Valid UserModifyRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // Check if user is existing.

        if (!userService.checkUserExist(requestBody.getUserId())) {
            // If user is not found, this is invalid request.
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }



        // Check if org is valid.
        if (!userService.checkOrgExist(requestBody.getOrgId())) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // Check if user account is duplicated.
        if (userService.checkAccountExist(requestBody.getUserAccount(), requestBody.getUserId())) {
            return new CommonResponseBody(ResponseMessage.USED_USER_ACCOUNT);
        }

        if (!requestBody.getMobile().equals("") && userService.checkMobileExist(requestBody.getMobile(), requestBody.getUserId())) {
            return new CommonResponseBody(ResponseMessage.USED_MOBILE);
        }


        if (!requestBody.getEmail().equals("") && userService.checkEmailExist(requestBody.getEmail(), requestBody.getUserId())) {
            return new CommonResponseBody(ResponseMessage.USED_EMAIL);
        }


        SysUser sysUser = requestBody.convert2SysUser();

        userService.modifyUser(sysUser, requestBody.getPortrait());



        return new CommonResponseBody(ResponseMessage.OK);
    }




    /**
     * User datatable request.
     */
    @RequestMapping(value = "/user/get-by-filter-and-page", method = RequestMethod.POST)
    public Object userGetByFilterAndPage(
            @RequestBody @Valid UserGetByFilterAndPageRequestBody requestBody,
            BindingResult bindingResult) {


        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        String userName = "";
        String status = "";
        String gender = "";
        Long orgId = null;
        if(requestBody.getFilter() != null) {
            userName = requestBody.getFilter().getUserName();
            status = requestBody.getFilter().getStatus();
            gender = requestBody.getFilter().getGender();
            orgId = requestBody.getFilter().getOrgId();
        }

        // Pagination.

        int currentPage = requestBody.getCurrentPage() - 1; // On server side, page is calculated from 0.
        int perPage = requestBody.getPerPage();

        PageResult<SysUser> result = userService.getUserListByPage(userName, status, gender, orgId, currentPage, perPage);
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
                        SimpleBeanPropertyFilter.serializeAllExcept("children", "users"))
                .addFilter(
                        ModelJsonFilters.FILTER_SYS_DATA_GROUP,
                        SimpleBeanPropertyFilter.serializeAllExcept("users"));

        value.setFilters(filters);

        return value;
    }



    /**
     * User generate excel request.
     */
    @PreAuthorize(Role.Authority.HAS_USER_EXPORT)
    @RequestMapping(value = "/user/xlsx", method = RequestMethod.POST)
    public Object userGenerateExcelFile(@RequestBody @Valid UserGenerateRequestBody requestBody,
                                   BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        String userName = "";
        String status = "";
        String gender = "";
        Long orgId = null;
        if(requestBody.getFilter() != null) {
            userName = requestBody.getFilter().getUserName();
            status = requestBody.getFilter().getStatus();
            gender = requestBody.getFilter().getGender();
            orgId = requestBody.getFilter().getOrgId();
        }
        List<SysUser> exportList = userService.getExportUserListByPage(userName, status, gender, orgId, requestBody.getIsAll(), requestBody.getIdList());
        setDictionary();
        InputStream inputStream = UserExcelView.buildExcelDocument(exportList);



        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=user.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msexcel"))
                .body(new InputStreamResource(inputStream));

    }

    /**
     * User generate word request.
     */

    @RequestMapping(value = "/user/docx", method = RequestMethod.POST)
    public Object userGenerateWordFile(@RequestBody @Valid UserGenerateRequestBody requestBody,
                                        BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }


        String userName = "";
        String status = "";
        String gender = "";
        Long orgId = null;
        if(requestBody.getFilter() != null) {
            userName = requestBody.getFilter().getUserName();
            status = requestBody.getFilter().getStatus();
            gender = requestBody.getFilter().getGender();
            orgId = requestBody.getFilter().getOrgId();
        }
        List<SysUser> exportList = userService.getExportUserListByPage(userName, status, gender, orgId, requestBody.getIsAll(), requestBody.getIdList());
        setDictionary();
        InputStream inputStream = UserWordView.buildWordDocument(exportList);



        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=user.docx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msword"))
                .body(new InputStreamResource(inputStream));

    }

    /**
     * User generate pdf request.
     */
    @PreAuthorize(Role.Authority.HAS_USER_PRINT)
    @RequestMapping(value = "/user/pdf", method = RequestMethod.POST)
    public Object userGeneratePdfFile(@RequestBody @Valid UserGenerateRequestBody requestBody,
                                   BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }


        String userName = "";
        String status = "";
        String gender = "";
        Long orgId = null;
        if(requestBody.getFilter() != null) {
            userName = requestBody.getFilter().getUserName();
            status = requestBody.getFilter().getStatus();
            gender = requestBody.getFilter().getGender();
            orgId = requestBody.getFilter().getOrgId();
        }
        List<SysUser> exportList = userService.getExportUserListByPage(userName, status, gender, orgId, requestBody.getIsAll(), requestBody.getIdList());
        UserPdfView.setResource(res);
        setDictionary();
        InputStream inputStream = UserPdfView.buildPDFDocument(exportList);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=user.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));

    }


    /**
     * User update status request.
     */
    @PreAuthorize(Role.Authority.HAS_USER_UPDATE_STATUS)
    @RequestMapping(value = "/user/update-status", method = RequestMethod.POST)
    public Object userUpdateStatus(
            @RequestBody @Valid UserUpdateStatusRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // Check if org is existing.

        if (!userService.checkUserExist(requestBody.getUserId())) {
            // If org is not found ,this is invalid request.
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        userService.updateStatus(requestBody.getUserId(), requestBody.getStatus());

        return new CommonResponseBody(ResponseMessage.OK);
    }


    /**
     * User get all request.
     * BARE, WITH_ORG_TREE.
     */
    @RequestMapping(value = "/user/get-all", method = RequestMethod.POST)
    public Object userGetAll(@RequestBody @Valid UserGetAllRequestBody requestBody,
                             BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }


        List<SysUser> sysUserList = userService.findAllUser();


        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, sysUserList));

        String type = requestBody.getType();

        // Set filters.
        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();

        switch (type) {
            case UserGetAllRequestBody.GetAllType.BARE:
                filters.addFilter(ModelJsonFilters.FILTER_SYS_USER, SimpleBeanPropertyFilter.serializeAllExcept("org", "roles", "dataGroups"));
                break;
            case UserGetAllRequestBody.GetAllType.WITH_ORG_TREE:
                filters.addFilter(ModelJsonFilters.FILTER_SYS_USER, SimpleBeanPropertyFilter.serializeAllExcept("roles", "dataGroups"))
                        .addFilter(ModelJsonFilters.FILTER_SYS_ORG, SimpleBeanPropertyFilter.serializeAllExcept("users", "children"));
                break;
            default:
                break;
        }

        value.setFilters(filters);

        return value;


    }

    /**
     * User group create request.
     */
    @PreAuthorize(Role.Authority.HAS_USER_GROUP_CREATE)
    @RequestMapping(value = "/user-group/create", method = RequestMethod.POST)
    public Object userGroupCreate(
            @RequestBody @Valid UserGroupCreateRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        if (userService.checkGroupNameExist(requestBody.getGroupName(), null)) {
            return new CommonResponseBody(ResponseMessage.USED_USER_GROUP_NAME);
        }

        if (userService.checkGroupNumberExist(requestBody.getGroupNumber(), null)) {
            return new CommonResponseBody(ResponseMessage.USED_USER_GROUP_NUMBER);
        }



        // Create user group with created info.

        SysUserGroup userGroup = (SysUserGroup) SysUserGroup
                .builder()
                .groupName(requestBody.getGroupName())
                .groupNumber(requestBody.getGroupNumber())
                .note(requestBody.getNote())
                .build()
                .addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
        List<Long> userIdList = requestBody.getUserIdList();

        userService.createUserGroup(userGroup, userIdList);

        // Build relation array which are valid only.


        return new CommonResponseBody(ResponseMessage.OK);
    }




    /**
     * User group datatable request.
     */
    @RequestMapping(value = "/user-group/get-by-filter-and-page", method = RequestMethod.POST)
    public Object userGroupGetByFilterAndPage(
            @RequestBody @Valid UserGroupGetByFilterAndPageRequestBody requestBody,
            BindingResult bindingResult) {


        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }



        // Pagination.
        int currentPage = requestBody.getCurrentPage() - 1; // On server side, page is calculated from 0.
        int perPage = requestBody.getPerPage();

        String groupName = "";
        if(requestBody.getFilter() != null) {
            groupName = requestBody.getFilter().getGroupName();
        }
        PageResult<SysUserGroup> result = userService.getUserGroupListByPage(groupName, currentPage, perPage);
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
                        ModelJsonFilters.FILTER_SYS_DATA_GROUP,
                        SimpleBeanPropertyFilter.serializeAllExcept("users"))
                .addFilter(
                        ModelJsonFilters.FILTER_SYS_ROLE,
                        SimpleBeanPropertyFilter.serializeAllExcept("resources"));

        value.setFilters(filters);

        return value;
    }



    /**
     * User Group generate excel request.
     */
    @PreAuthorize(Role.Authority.HAS_USER_GROUP_EXPORT)
    @RequestMapping(value = "/user-group/xlsx", method = RequestMethod.POST)
    public Object userGroupGenerateExcelFile(@RequestBody @Valid UserGroupGenerateRequestBody requestBody,
                                        BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        String groupName = "";
        if(requestBody.getFilter() != null) {
            groupName = requestBody.getFilter().getGroupName();
        }

        List<SysUserGroup> exportList = userService.getExportUserGroupListByPage(groupName, requestBody.getIsAll(), requestBody.getIdList());
        setDictionary();
        InputStream inputStream = UserGroupExcelView.buildExcelDocument(exportList);



        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=user-group.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msexcel"))
                .body(new InputStreamResource(inputStream));


    }

    /**
     * User Group generate excel request.
     */

    @RequestMapping(value = "/user-group/docx", method = RequestMethod.POST)
    public Object userGroupGenerateWordFile(@RequestBody @Valid UserGroupGenerateRequestBody requestBody,
                                             BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        String groupName = "";
        if(requestBody.getFilter() != null) {
            groupName = requestBody.getFilter().getGroupName();
        }

        List<SysUserGroup> exportList = userService.getExportUserGroupListByPage(groupName, requestBody.getIsAll(), requestBody.getIdList());
        setDictionary();
        InputStream inputStream = UserGroupWordView.buildWordDocument(exportList);



        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=user-group.docx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msword"))
                .body(new InputStreamResource(inputStream));


    }


    /**
     * User Group generate pdf request.
     */
    @PreAuthorize(Role.Authority.HAS_USER_GROUP_PRINT)
    @RequestMapping(value = "/user-group/pdf", method = RequestMethod.POST)
    public Object userGroupGeneratePDFFile(@RequestBody @Valid UserGroupGenerateRequestBody requestBody,
                                        BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        String groupName = "";
        if(requestBody.getFilter() != null) {
            groupName = requestBody.getFilter().getGroupName();
        }

        List<SysUserGroup> exportList = userService.getExportUserGroupListByPage(groupName, requestBody.getIsAll(), requestBody.getIdList());
        UserGroupPdfView.setResource(res);
        setDictionary();
        InputStream inputStream = UserGroupPdfView.buildPDFDocument(exportList);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=user-group.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));

    }


    /**
     * User group modify request.
     */
    @PreAuthorize(Role.Authority.HAS_USER_GROUP_MODIFY)
    @RequestMapping(value = "/user-group/modify", method = RequestMethod.POST)
    public Object userGroupModify(
            @RequestBody @Valid UserGroupModifyRequestBody requestBody,
            BindingResult bindingResult) {


        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }



        if (!userService.checkUserGroupExist(requestBody.getUserGroupId())) {
            // If user group is not found, this is invalid request.
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        userService.modifyUserGroup(requestBody.getUserGroupId(), requestBody.getUserIdList());

        return new CommonResponseBody(ResponseMessage.OK);

    }


    /**
     * User group delete request.
     */
    @PreAuthorize(Role.Authority.HAS_USER_GROUP_DELETE)
    @RequestMapping(value = "/user-group/delete", method = RequestMethod.POST)
    public Object userGroupDelete(
            @RequestBody @Valid UserGroupDeleteRequestBody requestBody,
            BindingResult bindingResult) {


        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }


        if (!userService.checkUserGroupExist(requestBody.getUserGroupId())) {
            // If user group is not found, this is invalid request.
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }


        if (userService.checkUserGroupUserExist(requestBody.getUserGroupId())) {
            // If user group has users, it can't be delete.
            return new CommonResponseBody(ResponseMessage.HAS_CHILDREN);
        }

        // Delete.
        userService.removeUserGroup(requestBody.getUserGroupId());

        return new CommonResponseBody(ResponseMessage.OK);
    }


}
