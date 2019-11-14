package com.nuctech.ecuritycheckitem.controllers.permissionmanagement;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.*;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.reusables.FilteringAndPaginationResult;
import com.querydsl.core.BooleanBuilder;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.IOException;
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
        @Pattern(regexp = SysUser.Status.ACTIVE + "|" + SysUser.Status.INACTIVE + "|" + SysUser.Status.BLOCKED)
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
    @RequestMapping(value = "/user/create", method = RequestMethod.POST)
    public Object userCreate(
            @ModelAttribute @Valid UserCreateRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // Check if org is valid.
        if (!sysOrgRepository.exists(QSysOrg.sysOrg.orgId.eq(requestBody.getOrgId()))) {
            // If organization is not found, this is invalid request.
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // Check if user account is duplicated.
        if (sysUserRepository.exists(QSysUser.sysUser.userAccount.eq(requestBody.userAccount))) {
            return new CommonResponseBody(ResponseMessage.USED_USER_ACCOUNT);
        }

        // Check password.
        if (UserCreateRequestBody.PasswordType.OTHER.equals(requestBody.getPasswordType()) && (requestBody.getPasswordValue() == null || requestBody.getPasswordValue().length() < 6)) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        SysUser sysUser = requestBody.convert2SysUser();

        // Process portrait file.
        MultipartFile portraitFile = requestBody.getPortrait();

        if (portraitFile != null && !portraitFile.isEmpty()) {
            try {
                byte[] bytes = portraitFile.getBytes();

                String directoryPath = Constants.PORTRAIT_FILE_UPLOAD_DIRECTORY;
                String fileName = new Date().getTime() + "_" + portraitFile.getOriginalFilename();

                boolean isSucceeded = utils.saveFile(directoryPath, fileName, bytes);

                if (isSucceeded) {
                    // Save file name.
                    sysUser.setPortrait(Constants.PORTRAIT_FILE_SERVING_BASE_URL + fileName);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        // Add created info.
        sysUser.addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());

        sysUserRepository.save(sysUser);

        return new CommonResponseBody(ResponseMessage.OK);
    }


    /**
     * User modify request.
     */
    @RequestMapping(value = "/user/modify", method = RequestMethod.POST)
    public Object userModify(
            @ModelAttribute @Valid UserModifyRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // Check if user is existing.
        Optional<SysUser> optionalOldSysUser = sysUserRepository.findOne(QSysUser.sysUser.userId.eq(requestBody.getUserId()));
        if (!optionalOldSysUser.isPresent()) {
            // If user is not found, this is invalid request.
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        SysUser oldSysUser = optionalOldSysUser.get();

        // Check if org is valid.
        if (!sysOrgRepository.exists(QSysOrg.sysOrg.orgId.eq(requestBody.getOrgId()))) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // Check if user account is duplicated.
        if (sysUserRepository.exists(QSysUser.sysUser.userAccount.eq(requestBody.userAccount).and(QSysUser.sysUser.userId.ne(requestBody.getUserId())))) {
            return new CommonResponseBody(ResponseMessage.USED_USER_ACCOUNT);
        }

        SysUser sysUser = requestBody.convert2SysUser();

        // Don't modify password.
        sysUser.setPassword(oldSysUser.getPassword());

        // Don't modify portrait if uploaded file is not found.
        sysUser.setPortrait(oldSysUser.getPortrait());

        // Process user portrait file.
        MultipartFile portraitFile = requestBody.getPortrait();

        if (portraitFile != null && !portraitFile.isEmpty()) {
            try {
                byte[] bytes = portraitFile.getBytes();

                String directoryPath = Constants.PORTRAIT_FILE_UPLOAD_DIRECTORY;
                String fileName = new Date().getTime() + "_" + portraitFile.getOriginalFilename();

                boolean isSucceeded = utils.saveFile(directoryPath, fileName, bytes);

                if (isSucceeded) {
                    // Update portrait.
                    sysUser.setPortrait(Constants.PORTRAIT_FILE_SERVING_BASE_URL + fileName);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        // Add edited info.
        sysUser.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());

        sysUserRepository.save(sysUser);

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

        // Build query.
        QSysUser builder = QSysUser.sysUser;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        UserGetByFilterAndPageRequestBody.Filter filter = requestBody.getFilter();

        if (filter != null) {
            if (!StringUtils.isEmpty(filter.getUserName())) {
                predicate.and(builder.userName.contains(filter.getUserName()));
            }
            if (!StringUtils.isEmpty(filter.getStatus())) {
                predicate.and(builder.status.eq(filter.getStatus()));
            }
            if (!StringUtils.isEmpty(filter.getGender())) {
                predicate.and(builder.gender.eq(filter.getGender()));
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
                        SimpleBeanPropertyFilter.serializeAllExcept("children", "users"))
                .addFilter(
                        ModelJsonFilters.FILTER_SYS_DATA_GROUP,
                        SimpleBeanPropertyFilter.serializeAllExcept("users"));

        value.setFilters(filters);

        return value;
    }


    /**
     * User update status request.
     */
    @RequestMapping(value = "/user/update-status", method = RequestMethod.POST)
    public Object userUpdateStatus(
            @RequestBody @Valid UserUpdateStatusRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // Check if org is existing.
        Optional<SysUser> optionalSysUser = sysUserRepository.findOne(QSysUser.sysUser.userId.eq(requestBody.getUserId()));
        if (!optionalSysUser.isPresent()) {
            // If org is not found ,this is invalid request.
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        SysUser sysUser = optionalSysUser.get();

        sysUser.setStatus(requestBody.getStatus());

        // Add created info.
        sysUser.addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());

        sysUserRepository.save(sysUser);

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


        List<SysUser> sysUserList = sysUserRepository.findAll();


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
    @RequestMapping(value = "/user-group/create", method = RequestMethod.POST)
    public Object userGroupCreate(
            @RequestBody @Valid UserGroupCreateRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // Create user group with created info.


        SysUserGroup sysUserGroup = sysUserGroupRepository.save(
                (SysUserGroup) SysUserGroup
                        .builder()
                        .groupName(requestBody.getGroupName())
                        .groupNumber(requestBody.getGroupNumber())
                        .note(requestBody.getNote())
                        .build()
                        .addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal())
        );


        List<Long> userIdList = requestBody.getUserIdList();

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

        // Build query.
        QSysUserGroup builder = QSysUserGroup.sysUserGroup;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        UserGroupGetByFilterAndPageRequestBody.Filter filter = requestBody.getFilter();

        if (filter != null) {
            if (!StringUtils.isEmpty(filter.getGroupName())) {
                predicate.and(builder.groupName.contains(filter.getGroupName()));
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
                        ModelJsonFilters.FILTER_SYS_DATA_GROUP,
                        SimpleBeanPropertyFilter.serializeAllExcept("users"))
                .addFilter(
                        ModelJsonFilters.FILTER_SYS_ROLE,
                        SimpleBeanPropertyFilter.serializeAllExcept("resources"));

        value.setFilters(filters);

        return value;
    }


    /**
     * User group modify request.
     */
    @RequestMapping(value = "/user-group/modify", method = RequestMethod.POST)
    public Object userGroupModify(
            @RequestBody @Valid UserGroupModifyRequestBody requestBody,
            BindingResult bindingResult) {


        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        Optional<SysUserGroup> optionalSysUserGroup = sysUserGroupRepository.findOne(QSysUserGroup.sysUserGroup.userGroupId.eq(requestBody.getUserGroupId()));

        if (!optionalSysUserGroup.isPresent()) {
            // If user group is not found, this is invalid request.
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        SysUserGroup sysUserGroup = optionalSysUserGroup.get();
        List<Long> userIdList = requestBody.getUserIdList();

        // Delete all existing relation.
        sysUserGroupUserRepository.deleteAll(sysUserGroupUserRepository.findAll(QSysUserGroupUser.sysUserGroupUser.userGroupId.eq(sysUserGroup.getUserGroupId())));

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

        return new CommonResponseBody(ResponseMessage.OK);

    }


    /**
     * User group delete request.
     */
    @RequestMapping(value = "/user-group/delete", method = RequestMethod.POST)
    public Object userGroupDelete(
            @RequestBody @Valid UserGroupDeleteRequestBody requestBody,
            BindingResult bindingResult) {


        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        Optional<SysUserGroup> optionalSysUserGroup = sysUserGroupRepository.findOne(QSysUserGroup.sysUserGroup.userGroupId.eq(requestBody.getUserGroupId()));
        if (!optionalSysUserGroup.isPresent()) {
            // If user group is not found, this is invalid request.
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        SysUserGroup sysUserGroup = optionalSysUserGroup.get();
        if (!sysUserGroup.getUsers().isEmpty()) {
            // If user group has users, it can't be delete.
            return new CommonResponseBody(ResponseMessage.HAS_CHILDREN);
        }

        // Delete.
        sysUserGroupRepository.delete(
                SysUserGroup
                        .builder()
                        .userGroupId(sysUserGroup.getUserGroupId())
                        .build()
        );

        return new CommonResponseBody(ResponseMessage.OK);
    }


}
