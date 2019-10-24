package com.haomibo.haomibo.controllers.permissionmanagement;

import com.haomibo.haomibo.config.Constants;
import com.haomibo.haomibo.controllers.BaseController;
import com.haomibo.haomibo.models.db.QSysOrg;
import com.haomibo.haomibo.models.db.QSysUser;
import com.haomibo.haomibo.models.db.SysOrg;
import com.haomibo.haomibo.models.db.SysUser;
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

@RestController
@RequestMapping("/permission-management/user-management")
public class UserManagementController extends BaseController {


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class CreateRequestBody {

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
    private static class ModifyRequestBody {

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
    private static class GetByFilterAndPageRequestBody {

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
    private static class UpdateStatusRequestBody {

        @NotNull
        Long userId;

        @NotNull
        @Pattern(regexp = SysUser.Status.ACTIVE + "|" + SysUser.Status.INACTIVE + "|" + SysUser.Status.BLOCKED)
        String status;

    }


    @Secured({Constants.Roles.SYS_USER})
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public Object create(
            @ModelAttribute @Valid CreateRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(Constants.ResponseMessages.INVALID_PARAMETER);
        }

        // check if org is valid
        if (!sysOrgRepository.exists(QSysOrg.sysOrg.orgId.eq(requestBody.getOrgId()))) {
            return new CommonResponseBody(Constants.ResponseMessages.INVALID_PARAMETER);
        }

        // check if user account is duplicated
        if (sysUserRepository.exists(QSysUser.sysUser.userAccount.eq(requestBody.userAccount))) {
            return new CommonResponseBody(Constants.ResponseMessages.USED_USER_ACCOUNT);
        }

        // check password
        if (CreateRequestBody.PasswordType.OTHER.equals(requestBody.getPasswordType()) && (requestBody.getPasswordValue() == null || requestBody.getPasswordValue().length() < 6)) {
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
    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    public Object modify(
            @ModelAttribute @Valid ModifyRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(Constants.ResponseMessages.INVALID_PARAMETER);
        }

        // check if user if existing
        Optional<SysUser> optionalOldSysUser = sysUserRepository.findOne(QSysUser.sysUser.userId.eq(requestBody.getUserId()));
        if (!optionalOldSysUser.isPresent()) {
            return new CommonResponseBody(Constants.ResponseMessages.INVALID_PARAMETER);
        }

        SysUser oldSysUser = optionalOldSysUser.get();

        // check if org is valid
        if (!sysOrgRepository.exists(QSysOrg.sysOrg.orgId.eq(requestBody.getOrgId()))) {
            return new CommonResponseBody(Constants.ResponseMessages.INVALID_PARAMETER);
        }

        // check if user account is duplicated
        if (sysUserRepository.exists(QSysUser.sysUser.userAccount.eq(requestBody.userAccount).and(QSysUser.sysUser.userId.ne(requestBody.getUserId())))) {
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
    @RequestMapping(value = "/get-by-filter-and-page", method = RequestMethod.POST)
    public Object getByFilterAndPage(
            @RequestBody @Valid GetByFilterAndPageRequestBody requestBody,
            BindingResult bindingResult) {


        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(Constants.ResponseMessages.INVALID_PARAMETER);
        }

        QSysUser builder = QSysUser.sysUser;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        GetByFilterAndPageRequestBody.Filter filter = requestBody.getFilter();

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
    @RequestMapping(value = "/update-status", method = RequestMethod.POST)
    public Object updateStatus(
            @RequestBody @Valid UpdateStatusRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(Constants.ResponseMessages.INVALID_PARAMETER);
        }

        // check if org is existing
        Optional<SysUser> optionalSysUser = sysUserRepository.findOne(QSysUser.sysUser.userId.eq(requestBody.getUserId()));
        if (!optionalSysUser.isPresent()) {
            return new CommonResponseBody(Constants.ResponseMessages.INVALID_PARAMETER);
        }

        SysUser sysUser = optionalSysUser.get();
        sysUser.setStatus(requestBody.getStatus());

        sysUserRepository.save(sysUser);

        return new CommonResponseBody(Constants.ResponseMessages.OK);
    }

    @Secured({Constants.Roles.SYS_USER})
    @RequestMapping(value = "/get-all", method = RequestMethod.POST)
    public Object getAll() {

        List<SysUser> sysUserList = sysUserRepository.findAll();

        return new CommonResponseBody(Constants.ResponseMessages.OK, sysUserList);
    }
}
