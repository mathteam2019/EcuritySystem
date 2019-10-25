package com.haomibo.haomibo.controllers.permissionmanagement;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.haomibo.haomibo.config.Constants;
import com.haomibo.haomibo.controllers.BaseController;
import com.haomibo.haomibo.jsonfilter.ModelJsonFilters;
import com.haomibo.haomibo.models.db.QSysOrg;
import com.haomibo.haomibo.models.db.SysOrg;
import com.haomibo.haomibo.models.response.CommonResponseBody;
import com.haomibo.haomibo.models.reusables.FilteringAndPaginationResult;
import com.querydsl.core.BooleanBuilder;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.converter.json.MappingJacksonValue;
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

@RestController
@RequestMapping("/permission-management/organization-management")
public class OrganizationManagementController extends BaseController {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class CreateRequestBody {

        @NotNull
        String orgName;

        @NotNull
        String orgNumber;

        @NotNull
        Long parentOrgId;

        String leader;

        String mobile;

        String note;

        SysOrg convert2SysOrg() {

            return SysOrg
                    .builder()
                    .orgName(this.getOrgName())
                    .orgNumber(this.getOrgNumber())
                    .parentOrgId(this.getParentOrgId())
                    .leader(Optional.of(this.getLeader()).orElse(""))
                    .mobile(Optional.of(this.getMobile()).orElse(""))
                    .status(SysOrg.Status.INACTIVE)
                    .note(Optional.of(this.getNote()).orElse(""))
                    .build();

        }

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class DeleteRequestBody {

        @NotNull
        Long orgId;

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
            String orgName;
            String status;
            String parentOrgName;
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
    private static class ModifyRequestBody {

        @NotNull
        Long orgId;

        @NotNull
        String orgName;

        @NotNull
        String orgNumber;

        @NotNull
        Long parentOrgId;

        String leader;

        String mobile;

        String note;

        SysOrg convert2SysOrg() {
            return SysOrg
                    .builder()
                    .orgId(this.getOrgId())
                    .orgName(this.getOrgName())
                    .orgNumber(this.getOrgNumber())
                    .parentOrgId(this.getParentOrgId())
                    .leader(Optional.of(this.getLeader()).orElse(""))
                    .mobile(Optional.of(this.getMobile()).orElse(""))
                    .status(SysOrg.Status.INACTIVE)
                    .note(Optional.of(this.getNote()).orElse(""))
                    .build();
        }

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class UpdateStatusRequestBody {

        @NotNull
        Long orgId;

        @NotNull
        @Pattern(regexp = SysOrg.Status.ACTIVE + "|" + SysOrg.Status.INACTIVE)
        String status;

    }


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class GetAllRequestBody {

        static class GetAllType {
            static final String BARE = "bare";
            static final String WITH_PARENT = "with_parent";
            static final String WITH_CHILDREN = "with_children";
            static final String WITH_USERS = "with_users";
            static final String WITH_PARENT_AND_USERS = "with_parent_and_users";
            static final String WITH_CHILDREN_AND_USERS = "with_children_and_users";
        }

        @Pattern(regexp = GetAllType.BARE + "|" +
                GetAllType.WITH_PARENT + "|" +
                GetAllType.WITH_CHILDREN + "|" +
                GetAllType.WITH_USERS + "|" +
                GetAllType.WITH_PARENT_AND_USERS + "|" +
                GetAllType.WITH_CHILDREN_AND_USERS)
        String type = GetAllType.BARE;


    }

    @Secured({Constants.Roles.SYS_USER})
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public Object create(
            @RequestBody @Valid CreateRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(Constants.ResponseMessages.INVALID_PARAMETER);
        }

        // check if parent org is existing
        if (!sysOrgRepository.exists(QSysOrg.sysOrg.orgId.eq(requestBody.getParentOrgId()))) {
            return new CommonResponseBody(Constants.ResponseMessages.INVALID_PARAMETER);
        }

        SysOrg sysOrg = requestBody.convert2SysOrg();

        sysOrg = sysOrgRepository.save(sysOrg);

        return new CommonResponseBody(Constants.ResponseMessages.OK, sysOrg);
    }


    @Secured({Constants.Roles.SYS_USER})
    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    public Object modify(
            @RequestBody @Valid ModifyRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(Constants.ResponseMessages.INVALID_PARAMETER);
        }

        // check if org is existing
        if (!sysOrgRepository.exists(QSysOrg.sysOrg.orgId.eq(requestBody.getOrgId()))) {
            return new CommonResponseBody(Constants.ResponseMessages.INVALID_PARAMETER);
        }

        // check if parent org is existing
        if (!sysOrgRepository.exists(QSysOrg.sysOrg.orgId.eq(requestBody.getParentOrgId()))) {
            return new CommonResponseBody(Constants.ResponseMessages.INVALID_PARAMETER);
        }

        SysOrg sysOrg = requestBody.convert2SysOrg();

        sysOrg = sysOrgRepository.save(sysOrg);

        return new CommonResponseBody(Constants.ResponseMessages.OK, sysOrg);
    }

    @Secured({Constants.Roles.SYS_USER})
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public Object delete(
            @RequestBody @Valid DeleteRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(Constants.ResponseMessages.INVALID_PARAMETER);
        }

        // check if org has children
        boolean isHavingChildren = sysOrgRepository.exists(QSysOrg.sysOrg.parentOrgId.eq(requestBody.getOrgId()));
        if (isHavingChildren) {
            return new CommonResponseBody(Constants.ResponseMessages.HAS_CHILDREN);
        }

        sysOrgRepository.delete(SysOrg.builder().orgId(requestBody.getOrgId()).build());

        return new CommonResponseBody(Constants.ResponseMessages.OK);
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
        Optional<SysOrg> optionalSysOrg = sysOrgRepository.findOne(QSysOrg.sysOrg.orgId.eq(requestBody.getOrgId()));
        if (!optionalSysOrg.isPresent()) {
            return new CommonResponseBody(Constants.ResponseMessages.INVALID_PARAMETER);
        }

        SysOrg sysOrg = optionalSysOrg.get();
        sysOrg.setStatus(requestBody.getStatus());

        sysOrgRepository.save(sysOrg);

        return new CommonResponseBody(Constants.ResponseMessages.OK);
    }


    @Secured({Constants.Roles.SYS_USER})
    @RequestMapping(value = "/get-all", method = RequestMethod.POST)
    public Object getAll(@RequestBody @Valid GetAllRequestBody requestBody,
                         BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(Constants.ResponseMessages.INVALID_PARAMETER);
        }


        List<SysOrg> sysOrgList = sysOrgRepository.findAll();


        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(Constants.ResponseMessages.OK, sysOrgList));

        String type = requestBody.getType();

        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();

        switch (type) {

            case GetAllRequestBody.GetAllType.BARE:
                filters.addFilter(ModelJsonFilters.FILTER_SYS_ORG, SimpleBeanPropertyFilter.serializeAllExcept("parent", "users", "children"));
                break;
            case GetAllRequestBody.GetAllType.WITH_PARENT:
                filters.addFilter(ModelJsonFilters.FILTER_SYS_ORG, SimpleBeanPropertyFilter.serializeAllExcept("users", "children"));
                break;
            case GetAllRequestBody.GetAllType.WITH_CHILDREN:
                filters.addFilter(ModelJsonFilters.FILTER_SYS_ORG, SimpleBeanPropertyFilter.serializeAllExcept("parent", "users"));
                break;
            case GetAllRequestBody.GetAllType.WITH_USERS:
                filters.addFilter(ModelJsonFilters.FILTER_SYS_ORG, SimpleBeanPropertyFilter.serializeAllExcept("parent", "children"))
                        .addFilter(ModelJsonFilters.FILTER_SYS_USER, SimpleBeanPropertyFilter.serializeAllExcept("org"));
                break;
            case GetAllRequestBody.GetAllType.WITH_PARENT_AND_USERS:
                filters.addFilter(ModelJsonFilters.FILTER_SYS_ORG, SimpleBeanPropertyFilter.serializeAllExcept("children"))
                        .addFilter(ModelJsonFilters.FILTER_SYS_USER, SimpleBeanPropertyFilter.serializeAllExcept("org"));
                break;
            case GetAllRequestBody.GetAllType.WITH_CHILDREN_AND_USERS:
                filters.addFilter(ModelJsonFilters.FILTER_SYS_ORG, SimpleBeanPropertyFilter.serializeAllExcept("parent"))
                        .addFilter(ModelJsonFilters.FILTER_SYS_USER, SimpleBeanPropertyFilter.serializeAllExcept("org"));
                break;
            default:

                break;
        }

        value.setFilters(filters);

        return value;
    }

    @Secured({Constants.Roles.SYS_USER})
    @RequestMapping(value = "/get-by-filter-and-page", method = RequestMethod.POST)
    public Object getByFilterAndPage(
            @RequestBody @Valid GetByFilterAndPageRequestBody requestBody,
            BindingResult bindingResult) {


        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(Constants.ResponseMessages.INVALID_PARAMETER);
        }


        QSysOrg builder = QSysOrg.sysOrg;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        GetByFilterAndPageRequestBody.Filter filter = requestBody.getFilter();
        if (filter != null) {
            if (!StringUtils.isEmpty(filter.getOrgName())) {
                predicate.and(builder.orgName.contains(filter.getOrgName()));
            }
            if (!StringUtils.isEmpty(filter.getStatus())) {
                predicate.and(builder.status.eq(filter.getStatus()));
            }
            if (!StringUtils.isEmpty(filter.getParentOrgName())) {
                predicate.and(builder.parent.orgName.contains(filter.getParentOrgName()));
            }
        }

        int currentPage = requestBody.getCurrentPage() - 1; // on server side, page is calculated from 0
        int perPage = requestBody.getPerPage();

        PageRequest pageRequest = PageRequest.of(currentPage, perPage);

        long total = sysOrgRepository.count(predicate);
        List<SysOrg> data = sysOrgRepository.findAll(predicate, pageRequest).getContent();


        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(
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
                        .build()));

        FilterProvider filters = ModelJsonFilters
                .getDefaultFilters()
                .addFilter(
                        ModelJsonFilters.FILTER_SYS_ORG,
                        SimpleBeanPropertyFilter.serializeAllExcept("children", "users"));

        value.setFilters(filters);

        return value;
    }

}
