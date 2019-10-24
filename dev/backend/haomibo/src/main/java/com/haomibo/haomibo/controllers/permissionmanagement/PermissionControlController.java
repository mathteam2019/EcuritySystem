package com.haomibo.haomibo.controllers.permissionmanagement;

import com.haomibo.haomibo.config.Constants;
import com.haomibo.haomibo.controllers.BaseController;
import com.haomibo.haomibo.models.db.*;
import com.haomibo.haomibo.models.response.CommonResponseBody;
import com.haomibo.haomibo.models.reusables.FilteringAndPaginationResult;
import com.querydsl.core.BooleanBuilder;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/permission-management/permission-control")
public class PermissionControlController extends BaseController {


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class RoleCreateRequestBody {

        @NotNull
        String roleName;

        String note;

        SysRole convert2SysRole() {

            return SysRole
                    .builder()
                    .roleName(this.getRoleName())
                    .note(this.note)
                    .build();

        }
    }


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

            @Pattern(regexp = SysDataGroup.Flag.SET + "|" + SysDataGroup.Flag.UNSET)
            String category;
            String roleName;
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
    private static class DataGroupGetByFilterAndPageRequestBody {

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        static class Filter {

            @Pattern(regexp = SysDataGroup.Flag.SET + "|" + SysDataGroup.Flag.UNSET)
            String flag;
            String dataGroupName;
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
    private static class DataGroupCreateRequestBody {

        @NotNull
        String dataGroupName;

        String note;

        SysDataGroup convert2SysDataGroup() {

            return SysDataGroup
                    .builder()
                    .dataGroupName(this.getDataGroupName())
                    .note(this.note)
                    .build();

        }
    }


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

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class DataGroupDeleteRequestBody {

        @NotNull
        long dataGroupId;

    }

    @Secured({Constants.Roles.SYS_USER})
    @RequestMapping(value = "/role/create", method = RequestMethod.POST)
    public Object roleCreate(
            @RequestBody @Valid RoleCreateRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(Constants.ResponseMessages.INVALID_PARAMETER);
        }

        SysRole sysRole = requestBody.convert2SysRole();

        sysRole = sysRoleRepository.save(sysRole);

        return new CommonResponseBody(Constants.ResponseMessages.OK, sysRole);
    }


    @Secured({Constants.Roles.SYS_USER})
    @RequestMapping(value = "/create-data-group", method = RequestMethod.POST)
    public Object dataGroupCreate(
            @RequestBody @Valid DataGroupCreateRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(Constants.ResponseMessages.INVALID_PARAMETER);
        }

        SysDataGroup sysDataGroup = requestBody.convert2SysDataGroup();

        sysDataGroup = sysDataGroupRepository.save(sysDataGroup);

        return new CommonResponseBody(Constants.ResponseMessages.OK, sysDataGroup);
    }


    @Secured({Constants.Roles.SYS_USER})
    @RequestMapping(value = "/data-group/get-by-filter-and-page", method = RequestMethod.POST)
    public Object dataGroupGetByFilterAndPage(
            @RequestBody @Valid DataGroupGetByFilterAndPageRequestBody requestBody,
            BindingResult bindingResult) {


        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(Constants.ResponseMessages.INVALID_PARAMETER);
        }

        QSysDataGroup builder = QSysDataGroup.sysDataGroup;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        DataGroupGetByFilterAndPageRequestBody.Filter filter = requestBody.getFilter();

        if (filter != null) {
            if (!StringUtils.isEmpty(filter.getDataGroupName())) {
                predicate.and(builder.dataGroupName.contains(filter.getDataGroupName()));
            }
            if (SysDataGroup.Flag.SET.equals(filter.getFlag())) {
                predicate.and(builder.users.isNotEmpty());
            }
            if (SysDataGroup.Flag.UNSET.equals(filter.getFlag())) {
                predicate.and(builder.users.isEmpty());
            }
        }

        int currentPage = requestBody.getCurrentPage() - 1; // on server side, page is calculated from 0
        int perPage = requestBody.getPerPage();

        PageRequest pageRequest = PageRequest.of(currentPage, perPage);

        long total = sysDataGroupRepository.count(predicate);
        List<SysDataGroup> data = sysDataGroupRepository.findAll(predicate, pageRequest).getContent();

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
    @RequestMapping(value = "/data-group/modify", method = RequestMethod.POST)
    public Object dataGroupModify(
            @RequestBody @Valid DataGroupModifyRequestBody requestBody,
            BindingResult bindingResult) {


        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(Constants.ResponseMessages.INVALID_PARAMETER);
        }

        Optional<SysDataGroup> optionalSysDataGroup = sysDataGroupRepository.findOne(QSysDataGroup.sysDataGroup.dataGroupId.eq(requestBody.getDataGroupId()));

        if (!optionalSysDataGroup.isPresent()) {
            return new CommonResponseBody(Constants.ResponseMessages.INVALID_PARAMETER);
        }

        SysDataGroup sysDataGroup = optionalSysDataGroup.get();
        List<Long> userIdList = requestBody.getUserIdList();

        List<SysDataGroupUser> sysDataGroupUserList = StreamSupport.stream(
                sysUserRepository.findAll(QSysUser.sysUser.userId.in(userIdList)).spliterator(),
                false)
                .map(sysUser -> SysDataGroupUser
                        .builder()
                        .dataGroupId(sysDataGroup.getDataGroupId())
                        .userId(sysUser.getUserId())
                        .build()).collect(Collectors.toList());

        sysDataGroupUserRepository.saveAll(sysDataGroupUserList);

        return new CommonResponseBody(Constants.ResponseMessages.OK);

    }

    @Secured({Constants.Roles.SYS_USER})
    @RequestMapping(value = "/data-group/delete", method = RequestMethod.POST)
    public Object dataGroupDelete(
            @RequestBody @Valid DataGroupDeleteRequestBody requestBody,
            BindingResult bindingResult) {


        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(Constants.ResponseMessages.INVALID_PARAMETER);
        }

        Optional<SysDataGroup> optionalSysDataGroup = sysDataGroupRepository.findOne(QSysDataGroup.sysDataGroup.dataGroupId.eq(requestBody.getDataGroupId()));
        if (!optionalSysDataGroup.isPresent()) {
            return new CommonResponseBody(Constants.ResponseMessages.INVALID_PARAMETER);
        }

        SysDataGroup sysDataGroup = optionalSysDataGroup.get();
        if (!sysDataGroup.getUsers().isEmpty()) {
            return new CommonResponseBody(Constants.ResponseMessages.HAS_CHILDREN);
        }

        sysDataGroupRepository.delete(
                SysDataGroup
                        .builder()
                        .dataGroupId(sysDataGroup.getDataGroupId())
                        .build()
        );

        return new CommonResponseBody(Constants.ResponseMessages.OK);


    }

}
