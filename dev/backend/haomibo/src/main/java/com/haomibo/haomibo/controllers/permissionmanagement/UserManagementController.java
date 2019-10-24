package com.haomibo.haomibo.controllers.permissionmanagement;

import com.haomibo.haomibo.config.Constants;
import com.haomibo.haomibo.controllers.BaseController;
import com.haomibo.haomibo.models.db.*;
import com.haomibo.haomibo.models.response.CommonResponseBody;
import com.haomibo.haomibo.models.reusables.FilteringAndPaginationResult;
import com.haomibo.haomibo.utils.Utils;
import com.querydsl.core.BooleanBuilder;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.haomibo.haomibo.models.db.QSysUser.sysUser;

@RestController
@RequestMapping("/permission-management/user-management")
public class UserManagementController extends BaseController {


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class CreateUserRequestBody {

        static class PasswordType {
            static final String DEFAULT = "default";
            static final String OTHER = "other";
        }

        @NotNull
        long orgId;

        @NotNull
        String userName;

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
        String email;
        String mobile;
        String address;

        @NotNull
        @Pattern(regexp = SysUser.Category.ADMIN + "|" + SysUser.Category.NORMAL)
        String category;
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
                    .category(this.getCategory())
                    .status(SysUser.Status.INACTIVE)
                    .note(this.getNote())
                    .build();
        }
    }


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class ModifyUserRequestBody {

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
        String email;
        String mobile;
        String address;
        String category;
        String note;

        private MultipartFile portrait;

        SysUser convert2SysUser() {

            return SysUser.builder()
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
                    .category(this.getCategory())
                    .status(SysUser.Status.INACTIVE)
                    .note(this.getNote())
                    .build();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class GetUserByFilterAndPageRequestBody {

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
            String category;
        }

        @NotNull
        @Min(1)
        int currentPage;

        @NotNull
        int perPage;

        Filter filter;

    }


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class GetUserGroupByFilterAndPageRequestBody {

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        static class Filter {
            String groupName;

            @Pattern(regexp = SysDataGroup.Flag.SET + "|" +
                    SysDataGroup.Flag.UNSET)
            String flag;
        }

        @NotNull
        @Min(1)
        int currentPage;

        @NotNull
        int perPage;

        Filter filter;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class UpdateUserStatusRequestBody {

        @NotNull
        Long userId;

        @NotNull
        @Pattern(regexp = SysUser.Status.ACTIVE + "|" + SysUser.Status.INACTIVE + "|" + SysUser.Status.BLOCKED)
        String status;

    }


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class CreateUserGroupRequestBody {

        @NotNull
        String groupName;

        String note;

        SysUserGroup convert2SysUserGroup() {

            return SysUserGroup
                    .builder()
                    .groupName(this.getGroupName())
                    .note(this.note)
                    .build();

        }

    }


    @Secured({Constants.Roles.SYS_USER})
    @RequestMapping(value = "/user/create", method = RequestMethod.POST)
    public Object createUser(
            @ModelAttribute @Valid CreateUserRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(Constants.ResponseMessages.INVALID_PARAMETER);
        }

        // check if org is valid
        if (!sysOrgRepository.exists(QSysOrg.sysOrg.orgId.eq(requestBody.getOrgId()))) {
            return new CommonResponseBody(Constants.ResponseMessages.INVALID_PARAMETER);
        }

        // check if user account is duplicated
        if (sysUserRepository.exists(sysUser.userAccount.eq(requestBody.userAccount))) {
            return new CommonResponseBody(Constants.ResponseMessages.USED_USER_ACCOUNT);
        }

        // check password
        if (CreateUserRequestBody.PasswordType.OTHER.equals(requestBody.getPasswordType()) && (requestBody.getPasswordValue() == null || requestBody.getPasswordValue().length() < 6)) {
            return new CommonResponseBody(Constants.ResponseMessages.INVALID_PARAMETER);
        }

        SysUser sysUser = requestBody.convert2SysUser();

        MultipartFile portraitFile = requestBody.getPortrait();

        if (portraitFile != null && !portraitFile.isEmpty()) {
            try {
                byte[] bytes = portraitFile.getBytes();

                String directoryPath = Constants.PORTRAIT_FILE_UPLOAD_DIRECTORY;
                String fileName = new Date().getTime() + "_" + portraitFile.getOriginalFilename();

                boolean isSucceeded = Utils.saveFile(directoryPath, fileName, bytes);

                if (isSucceeded) {
                    sysUser.setPortrait(Constants.PORTRAIT_FILE_SERVING_BASE_URL + fileName);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        sysUser = sysUserRepository.save(sysUser);

        return new CommonResponseBody(Constants.ResponseMessages.OK, sysUser);
    }


    @Secured({Constants.Roles.SYS_USER})
    @RequestMapping(value = "/user/modify", method = RequestMethod.POST)
    public Object modifyUser(
            @ModelAttribute @Valid ModifyUserRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(Constants.ResponseMessages.INVALID_PARAMETER);
        }

        // check if user if existing
        Optional<SysUser> optionalOldSysUser = sysUserRepository.findOne(sysUser.userId.eq(requestBody.getUserId()));
        if (!optionalOldSysUser.isPresent()) {
            return new CommonResponseBody(Constants.ResponseMessages.INVALID_PARAMETER);
        }

        SysUser oldSysUser = optionalOldSysUser.get();

        // check if org is valid
        if (!sysOrgRepository.exists(QSysOrg.sysOrg.orgId.eq(requestBody.getOrgId()))) {
            return new CommonResponseBody(Constants.ResponseMessages.INVALID_PARAMETER);
        }

        // check if user account is duplicated
        if (sysUserRepository.exists(sysUser.userAccount.eq(requestBody.userAccount).and(sysUser.userId.ne(requestBody.getUserId())))) {
            return new CommonResponseBody(Constants.ResponseMessages.USED_USER_ACCOUNT);
        }

        SysUser sysUser = requestBody.convert2SysUser();
        sysUser.setPassword(oldSysUser.getPassword());
        sysUser.setPortrait(oldSysUser.getPortrait());

        MultipartFile portraitFile = requestBody.getPortrait();

        if (portraitFile != null && !portraitFile.isEmpty()) {
            try {
                byte[] bytes = portraitFile.getBytes();

                String directoryPath = Constants.PORTRAIT_FILE_UPLOAD_DIRECTORY;
                String fileName = new Date().getTime() + "_" + portraitFile.getOriginalFilename();

                boolean isSucceeded = Utils.saveFile(directoryPath, fileName, bytes);

                if (isSucceeded) {
                    sysUser.setPortrait(Constants.PORTRAIT_FILE_SERVING_BASE_URL + fileName);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        sysUser = sysUserRepository.save(sysUser);

        return new CommonResponseBody(Constants.ResponseMessages.OK, sysUser);
    }

    @Secured({Constants.Roles.SYS_USER})
    @RequestMapping(value = "/user/get-by-filter-and-page", method = RequestMethod.POST)
    public Object getUserByFilterAndPage(
            @RequestBody @Valid GetUserByFilterAndPageRequestBody requestBody,
            BindingResult bindingResult) {


        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(Constants.ResponseMessages.INVALID_PARAMETER);
        }

        QSysUser builder = sysUser;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        GetUserByFilterAndPageRequestBody.Filter filter = requestBody.getFilter();

        if (filter != null) {
            if (!StringUtils.isEmpty(filter.getUserName())) {
                predicate.and(builder.userName.contains(filter.getUserName()));
            }
            if (!StringUtils.isEmpty(filter.getStatus())) {
                predicate.and(builder.status.eq(filter.getStatus()));
            }
            if (!StringUtils.isEmpty(filter.getCategory())) {
                predicate.and(builder.category.contains(filter.getCategory()));
            }
            if (filter.getOrgId() != null) {

                sysOrgRepository.findOne(QSysOrg.sysOrg.orgId.eq(filter.getOrgId())).ifPresent(parentSysOrg -> {

                    List<SysOrg> parentOrgList = parentSysOrg.generateChildrenList();
                    List<Long> parentOrgIdList = parentOrgList.stream().map(SysOrg::getOrgId).collect(Collectors.toList());

                    predicate.and(builder.orgId.in(parentOrgIdList));

                });
            }
        }

        int currentPage = requestBody.getCurrentPage() - 1; // on server side, page is calculated from 0
        int perPage = requestBody.getPerPage();

        PageRequest pageRequest = PageRequest.of(currentPage, perPage);

        long total = sysUserRepository.count(predicate);
        List<SysUser> data = sysUserRepository.findAll(predicate, pageRequest).getContent();

        return new CommonResponseBody(
                Constants.ResponseMessages.OK,
                FilteringAndPaginationResult
                        .builder()
                        .total(total)
                        .perPage(perPage)
                        .currentPage(currentPage + 1)
                        .lastPage((int) Math.ceil(((double) total) / perPage))
                        .from(perPage * currentPage + 1)
                        .to(perPage * currentPage + data.size())
                        .data(data)
                        .build());
    }


    @Secured({Constants.Roles.SYS_USER})
    @RequestMapping(value = "/user/update-status", method = RequestMethod.POST)
    public Object updateUserStatus(
            @RequestBody @Valid UpdateUserStatusRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(Constants.ResponseMessages.INVALID_PARAMETER);
        }

        // check if org is existing
        Optional<SysUser> optionalSysUser = sysUserRepository.findOne(sysUser.userId.eq(requestBody.getUserId()));
        if (!optionalSysUser.isPresent()) {
            return new CommonResponseBody(Constants.ResponseMessages.INVALID_PARAMETER);
        }

        SysUser sysUser = optionalSysUser.get();
        sysUser.setStatus(requestBody.getStatus());

        sysUserRepository.save(sysUser);

        return new CommonResponseBody(Constants.ResponseMessages.OK);
    }

    @Secured({Constants.Roles.SYS_USER})
    @RequestMapping(value = "/user/get-all", method = RequestMethod.POST)
    public Object getAllUser() {

        List<SysUser> sysUserList = sysUserRepository.findAll();

        return new CommonResponseBody(Constants.ResponseMessages.OK, sysUserList);
    }

    @Secured({Constants.Roles.SYS_USER})
    @RequestMapping(value = "/user-group/create", method = RequestMethod.POST)
    public Object createUserGroup(
            @RequestBody @Valid CreateUserGroupRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(Constants.ResponseMessages.INVALID_PARAMETER);
        }

        SysUserGroup sysUserGroup = requestBody.convert2SysUserGroup();

        sysUserGroup = sysUserGroupRepository.save(sysUserGroup);

        return new CommonResponseBody(Constants.ResponseMessages.OK, sysUserGroup);
    }


    @Secured({Constants.Roles.SYS_USER})
    @RequestMapping(value = "/user-group/get-by-filter-and-page", method = RequestMethod.POST)
    public Object getUserGroupByFilterAndPage(
            @RequestBody @Valid GetUserGroupByFilterAndPageRequestBody requestBody,
            BindingResult bindingResult) {


        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(Constants.ResponseMessages.INVALID_PARAMETER);
        }

        QSysUserGroup builder = QSysUserGroup.sysUserGroup;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        GetUserGroupByFilterAndPageRequestBody.Filter filter = requestBody.getFilter();

        if (filter != null) {
            if (!StringUtils.isEmpty(filter.getGroupName())) {
                predicate.and(builder.groupName.contains(filter.getGroupName()));
            }
            if (SysDataGroup.Flag.SET.equals(filter.getFlag()) ) {
                predicate.and(builder.users.isNotEmpty());
            }
            if (SysDataGroup.Flag.UNSET.equals(filter.getFlag()) ) {
                predicate.and(builder.users.isEmpty());
            }

        }

        int currentPage = requestBody.getCurrentPage() - 1; // on server side, page is calculated from 0
        int perPage = requestBody.getPerPage();

        PageRequest pageRequest = PageRequest.of(currentPage, perPage);

        long total = sysUserGroupRepository.count(predicate);
        List<SysUserGroup> data = sysUserGroupRepository.findAll(predicate, pageRequest).getContent();

        return new CommonResponseBody(
                Constants.ResponseMessages.OK,
                FilteringAndPaginationResult
                        .builder()
                        .total(total)
                        .perPage(perPage)
                        .currentPage(currentPage + 1)
                        .lastPage((int) Math.ceil(((double) total) / perPage))
                        .from(perPage * currentPage + 1)
                        .to(perPage * currentPage + data.size())
                        .data(data)
                        .build());
    }


}
