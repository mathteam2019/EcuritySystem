

package com.haomibo.haomibo.controllers.permissionmanagement;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.haomibo.haomibo.controllers.BaseController;
import com.haomibo.haomibo.enums.ResponseMessage;
import com.haomibo.haomibo.enums.Role;
import com.haomibo.haomibo.jsonfilter.ModelJsonFilters;
import com.haomibo.haomibo.models.db.*;
import com.haomibo.haomibo.models.response.CommonResponseBody;
import com.haomibo.haomibo.models.reusables.FilteringAndPaginationResult;
import com.querydsl.core.BooleanBuilder;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
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
        String roleName;

        String note;

        SysRole convert2SysRole() {

            return SysRole
                    .builder()
                    .roleName(this.getRoleName())
                    .roleFlag(SysRole.Flag.UNSET)
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
        long roleId;

        @NotNull
        List<Long> resourceIdList;
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

            @Pattern(regexp = SysRole.Flag.SET + "|" +
                    SysRole.Flag.UNSET + "|" +
                    SysRole.Flag.ADMIN + "|" +
                    SysRole.Flag.USER)
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

        String note;

        SysDataGroup convert2SysDataGroup() {

            return SysDataGroup
                    .builder()
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

        SysRole sysRole = requestBody.convert2SysRole();

        sysRoleRepository.save(sysRole);

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
            if (SysRole.Flag.ADMIN.equals(filter.getCategory())) {
                predicate.and(builder.roleFlag.eq(SysRole.Flag.ADMIN));
            }
            if (SysRole.Flag.USER.equals(filter.getCategory())) {
                predicate.and(builder.roleFlag.eq(SysRole.Flag.USER));
            }
            if (SysRole.Flag.SET.equals(filter.getCategory())) {
                predicate.and(builder.roleFlag.eq(SysRole.Flag.ADMIN).or(builder.roleFlag.eq(SysRole.Flag.USER)));
            }
            if (SysRole.Flag.UNSET.equals(filter.getCategory())) {
                predicate.and(builder.roleFlag.isNull().or(builder.roleFlag.ne(SysRole.Flag.ADMIN).and(builder.roleFlag.ne(SysRole.Flag.USER))));
            }
            if (!StringUtils.isEmpty(filter.getRoleName())) {
                predicate.and(builder.roleName.eq(filter.getRoleName()));
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

        // Try to get role.
        Optional<SysRole> optionalSysRole = sysRoleRepository.findOne(QSysRole.sysRole.roleId.eq(requestBody.getRoleId()));

        if (!optionalSysRole.isPresent()) {
            // If role is not found, it's invalid parameter.
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        SysRole sysRole = optionalSysRole.get();
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
            // If no resource is assigned to role, this role's flag value is 'unset'.
            sysRole.setRoleFlag(SysRole.Flag.UNSET);
            sysRoleRepository.save(sysRole);
            return new CommonResponseBody(ResponseMessage.OK);
        }

        // The category will be gained from the first resource.
        String category = sysResourceList.get(0).getResourceCategory();

        // if category value is neither 'admin' nor 'user', this is invalid request.
        if (!(SysRole.Flag.ADMIN.equals(category) || SysRole.Flag.USER.equals(category))) {
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

        // Set role flag value as category.
        sysRole.setRoleFlag(category);

        // Save role.
        sysRoleRepository.save(sysRole);

        // Delete all relations.
        sysRoleResourceRepository.deleteAll(sysRoleResourceRepository.findAll(QSysRoleResource.sysRoleResource.roleId.eq(sysRole.getRoleId())));

        // Save relation.
        List<SysRoleResource> relationList = sysResourceList.stream()
                .map(sysResource -> SysRoleResource
                        .builder()
                        .roleId(sysRole.getRoleId())
                        .resourceId(sysResource.getResourceId())
                        .build()).collect(Collectors.toList());

        sysRoleResourceRepository.saveAll(relationList);

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
    @RequestMapping(value = "/data-group/create", method = RequestMethod.POST)
    public Object dataGroupCreate(
            @RequestBody @Valid DataGroupCreateRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        SysDataGroup sysDataGroup = requestBody.convert2SysDataGroup();

        sysDataGroupRepository.save(sysDataGroup);

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
            if (SysDataGroup.Flag.SET.equals(filter.getFlag())) {
                predicate.and(builder.users.isNotEmpty());
            }
            if (SysDataGroup.Flag.UNSET.equals(filter.getFlag())) {
                predicate.and(builder.users.isEmpty());
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
                        SimpleBeanPropertyFilter.serializeAllExcept("org", "roles"));

        value.setFilters(filters);

        return value;
    }


    /**
     * Data group modify request.
     */
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
                false)
                .map(sysUser -> SysDataGroupUser
                        .builder()
                        .dataGroupId(sysDataGroup.getDataGroupId())
                        .userId(sysUser.getUserId())
                        .build()).collect(Collectors.toList());

        // Save.
        sysDataGroupUserRepository.saveAll(relationList);

        return new CommonResponseBody(ResponseMessage.OK);

    }


    /**
     * Data group delete request.
     */
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

        sysDataGroupRepository.delete(
                SysDataGroup
                        .builder()
                        .dataGroupId(sysDataGroup.getDataGroupId())
                        .build()
        );

        return new CommonResponseBody(ResponseMessage.OK);


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
