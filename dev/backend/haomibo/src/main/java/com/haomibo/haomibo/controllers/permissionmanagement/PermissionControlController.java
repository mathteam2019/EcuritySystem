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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/permission-management/permission-control")
public class PermissionControlController extends BaseController {


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class CreateRoleRequestBody {

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
    private static class CreateDataGroupRequestBody {

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
    @ToString
    private static class GetDataGroupByFilterAndPageRequestBody {

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
    @ToString
    private static class GetRoleByFilterAndPageRequestBody {

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


    @Secured({Constants.Roles.SYS_USER})
    @RequestMapping(value = "/create-role", method = RequestMethod.POST)
    public Object createRole(
            @RequestBody @Valid CreateRoleRequestBody requestBody,
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
    public Object createDataGroup(
            @RequestBody @Valid CreateDataGroupRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(Constants.ResponseMessages.INVALID_PARAMETER);
        }

        SysDataGroup sysDataGroup = requestBody.convert2SysDataGroup();

        sysDataGroup = sysDataGroupRepository.save(sysDataGroup);

        return new CommonResponseBody(Constants.ResponseMessages.OK, sysDataGroup);
    }


    @Secured({Constants.Roles.SYS_USER})
    @RequestMapping(value = "/get-data-group-by-filter-and-page", method = RequestMethod.POST)
    public Object getDataGroupByFilterAndPage(
            @RequestBody @Valid GetDataGroupByFilterAndPageRequestBody requestBody,
            BindingResult bindingResult) {


        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(Constants.ResponseMessages.INVALID_PARAMETER);
        }

        QSysDataGroup builder = QSysDataGroup.sysDataGroup;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        GetDataGroupByFilterAndPageRequestBody.Filter filter = requestBody.getFilter();

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

}
