/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/10/19
 * @CreatedBy Sandy.
 * @FileName OrganizationManagementController.java
 * @ModifyHistory
 */
package com.nuctech.ecuritycheckitem.controllers.permissionmanagement;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.enums.Role;
import com.nuctech.ecuritycheckitem.export.permissionmanagement.OrganizationExcelView;
import com.nuctech.ecuritycheckitem.export.permissionmanagement.OrganizationPdfView;
import com.nuctech.ecuritycheckitem.export.permissionmanagement.OrganizationWordView;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.QSysOrg;
import com.nuctech.ecuritycheckitem.models.db.SysOrg;
import com.nuctech.ecuritycheckitem.models.db.SysUser;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.reusables.FilteringAndPaginationResult;
import com.nuctech.ecuritycheckitem.repositories.SysOrgRepository;
import com.nuctech.ecuritycheckitem.service.permissionmanagement.OrganizationService;
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
 * Organization management controller.
 */
@RestController
@RequestMapping("/permission-management/organization-management")
public class OrganizationManagementController extends BaseController {

    @Autowired
    OrganizationService organizationService;

    /**
     * Organization create request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class OrganizationCreateRequestBody {

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
                    .leader(Optional.ofNullable(this.getLeader()).orElse(""))
                    .mobile(Optional.ofNullable(this.getMobile()).orElse(""))
                    .status(SysOrg.Status.INACTIVE)
                    .note(Optional.ofNullable(this.getNote()).orElse(""))
                    .build();

        }

    }

    /**
     * Organization delete request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class OrganizationDeleteRequestBody {

        @NotNull
        Long orgId;

    }

    /**
     * Organization datatable request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class OrganizationGetByFilterAndPageRequestBody {

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

    /**
     * Organization  generate request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class OrganizationGenerateRequestBody {

        String idList;
        @NotNull
        Boolean isAll;

        OrganizationGetByFilterAndPageRequestBody.Filter filter;
    }

    /**
     * Organization modify request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class OrganizationModifyRequestBody {

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
                    .leader(Optional.ofNullable(this.getLeader()).orElse(""))
                    .mobile(Optional.ofNullable(this.getMobile()).orElse(""))
                    .status(SysOrg.Status.INACTIVE)
                    .note(Optional.ofNullable(this.getNote()).orElse(""))
                    .build();
        }

    }

    /**
     * Organization update status request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class OrganizationUpdateStatusRequestBody {

        @NotNull
        Long orgId;

        @NotNull
        @Pattern(regexp = SysOrg.Status.ACTIVE + "|" + SysOrg.Status.INACTIVE)
        String status;

    }


    /**
     * Organization get all request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class OrganizationGetAllRequestBody {

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

    /**
     * Organization create request.
     */
    @PreAuthorize(Role.Authority.HAS_ORG_CREATE)
    @RequestMapping(value = "/organization/create", method = RequestMethod.POST)
    public Object organizationCreate(
            @RequestBody @Valid OrganizationCreateRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        SysOrg sysOrg = requestBody.convert2SysOrg();
        if (organizationService.createOrganization(requestBody.getParentOrgId(), sysOrg)) {
            return new CommonResponseBody(ResponseMessage.OK);
        } else {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }
    }


    /**
     * Organization modify request.
     */
    @PreAuthorize(Role.Authority.HAS_ORG_MODIFY)
    @RequestMapping(value = "/organization/modify", method = RequestMethod.POST)
    public Object organizationModify(
            @RequestBody @Valid OrganizationModifyRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        SysOrg sysOrg = requestBody.convert2SysOrg();

        if (organizationService.modifyOrganization(requestBody.getOrgId(), requestBody.getParentOrgId(), sysOrg)) {
            return new CommonResponseBody(ResponseMessage.OK);
        } else {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }
    }

    /**
     * Organization delete request.
     */
    @PreAuthorize(Role.Authority.HAS_ORG_DELETE)
    @RequestMapping(value = "/organization/delete", method = RequestMethod.POST)
    public Object organizationDelete(
            @RequestBody @Valid OrganizationDeleteRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        if (organizationService.deleteOrganization(requestBody.getOrgId())) {
            return new CommonResponseBody(ResponseMessage.OK);
        } else {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

    }


    /**
     * Organization update status request.
     */
    @PreAuthorize(Role.Authority.HAS_ORG_UPDATE_STATUS)
    @RequestMapping(value = "/organization/update-status", method = RequestMethod.POST)
    public Object organizationUpdateStatus(
            @RequestBody @Valid OrganizationUpdateStatusRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        if (organizationService.updateOrganizationStatus(requestBody.getOrgId(), requestBody.getStatus())) {
            return new CommonResponseBody(ResponseMessage.OK);
        } else {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }
    }

    /**
     * Organization get all request.
     * BARE, WITH_PARENT, WITH_CHILDREN, WITH_USERS, WITH_PARENT_AND_USERS, WITH_CHILDREN_AND_USERS.
     */
    @RequestMapping(value = "/organization/get-all", method = RequestMethod.POST)
    public Object organizationGetAll(@RequestBody @Valid OrganizationGetAllRequestBody requestBody,
                                     BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }


        List<SysOrg> sysOrgList = organizationService.getAllOrganization();

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, sysOrgList));

        String type = requestBody.getType();

        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();

        // Set filters for different type.
        switch (type) {

            case OrganizationGetAllRequestBody.GetAllType.BARE:
                filters.addFilter(ModelJsonFilters.FILTER_SYS_ORG, SimpleBeanPropertyFilter.serializeAllExcept("parent", "users", "children"));
                break;
            case OrganizationGetAllRequestBody.GetAllType.WITH_PARENT:
                filters.addFilter(ModelJsonFilters.FILTER_SYS_ORG, SimpleBeanPropertyFilter.serializeAllExcept("users", "children"));
                break;
            case OrganizationGetAllRequestBody.GetAllType.WITH_CHILDREN:
                filters.addFilter(ModelJsonFilters.FILTER_SYS_ORG, SimpleBeanPropertyFilter.serializeAllExcept("parent", "users"));
                break;
            case OrganizationGetAllRequestBody.GetAllType.WITH_USERS:
                filters.addFilter(ModelJsonFilters.FILTER_SYS_ORG, SimpleBeanPropertyFilter.serializeAllExcept("parent", "children"))
                        .addFilter(ModelJsonFilters.FILTER_SYS_USER, SimpleBeanPropertyFilter.serializeAllExcept("org", "roles", "dataGroups"));
                break;
            case OrganizationGetAllRequestBody.GetAllType.WITH_PARENT_AND_USERS:
                filters.addFilter(ModelJsonFilters.FILTER_SYS_ORG, SimpleBeanPropertyFilter.serializeAllExcept("children"))
                        .addFilter(ModelJsonFilters.FILTER_SYS_USER, SimpleBeanPropertyFilter.serializeAllExcept("org", "roles", "dataGroups"));
                break;
            case OrganizationGetAllRequestBody.GetAllType.WITH_CHILDREN_AND_USERS:
                filters.addFilter(ModelJsonFilters.FILTER_SYS_ORG, SimpleBeanPropertyFilter.serializeAllExcept("parent"))
                        .addFilter(ModelJsonFilters.FILTER_SYS_USER, SimpleBeanPropertyFilter.serializeAllExcept("org", "roles", "dataGroups"));
                break;
            default:

                break;
        }

        value.setFilters(filters);

        return value;
    }

    /**
     * Organization datatable data.
     */
    @RequestMapping(value = "/organization/get-by-filter-and-page", method = RequestMethod.POST)
    public Object organizationGetByFilterAndPage(
            @RequestBody @Valid OrganizationGetByFilterAndPageRequestBody requestBody,
            BindingResult bindingResult) {


        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        Integer currentPage = requestBody.getCurrentPage();
        Integer perPage = requestBody.getPerPage();
        currentPage--;
        PageResult<SysOrg> result = organizationService.getOrganizationByFilterAndPage(
                requestBody.getFilter().getOrgName(),
                requestBody.getFilter().getStatus(),
                requestBody.getFilter().getParentOrgName(),
                currentPage,
                perPage);

        long total = result.getTotal();
        List<SysOrg> data = result.getDataList();


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
                        SimpleBeanPropertyFilter.serializeAllExcept("children", "users"));

        value.setFilters(filters);

        return value;
    }

    private List<SysOrg> getExportList(List<SysOrg> orgList, boolean isAll, String idList) {
        List<SysOrg> exportList = new ArrayList<>();
        if (isAll == false) {
            String[] splits = idList.split(",");
            for (int i = 0; i < orgList.size(); i++) {
                SysOrg org = orgList.get(i);
                boolean isExist = false;
                for (int j = 0; j < splits.length; j++) {
                    if (splits[j].equals(org.getOrgId().toString())) {
                        isExist = true;
                        break;
                    }
                }
                if (isExist == true) {
                    exportList.add(org);
                }
            }
        } else {
            exportList = orgList;
        }
        return exportList;
    }

    /**
     * Organization generate excel request.
     */
    @PreAuthorize(Role.Authority.HAS_ORG_EXPORT)
    @RequestMapping(value = "/organization/xlsx", method = RequestMethod.POST)
    public Object organizationGenerateExcelFile(@RequestBody @Valid OrganizationGenerateRequestBody requestBody,
                                                BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        //get all org list
        List<SysOrg> orgList = organizationService.getOrganizationByFilter(
                requestBody.getFilter().getOrgName(),
                requestBody.getFilter().getStatus(),
                requestBody.getFilter().getParentOrgName()
        );

        List<SysOrg> exportList = getExportList(orgList, requestBody.getIsAll(), requestBody.getIdList());
        setDictionary();
        InputStream inputStream = OrganizationExcelView.buildExcelDocument(exportList);


        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=organization.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msexcel"))
                .body(new InputStreamResource(inputStream));

    }

    /**
     * Organization generate word request.
     */
    @PreAuthorize(Role.Authority.HAS_ORG_TOWORD)
    @RequestMapping(value = "/organization/docx", method = RequestMethod.POST)
    public Object organizationGenerateWordFile(@RequestBody @Valid OrganizationGenerateRequestBody requestBody,
                                               BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        //get all org list
        List<SysOrg> orgList = organizationService.getOrganizationByFilter(
                requestBody.getFilter().getOrgName(),
                requestBody.getFilter().getStatus(),
                requestBody.getFilter().getParentOrgName()
        );

        List<SysOrg> exportList = getExportList(orgList, requestBody.getIsAll(), requestBody.getIdList());
        setDictionary();
        InputStream inputStream = OrganizationWordView.buildWordDocument(exportList);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=organization.docx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msword"))
                .body(new InputStreamResource(inputStream));

    }

    /**
     * Organization generate pdf request.
     */
    @PreAuthorize(Role.Authority.HAS_ORG_PRINT)
    @RequestMapping(value = "/organization/pdf", method = RequestMethod.POST)
    public Object organizationGeneratePdfFile(@RequestBody @Valid OrganizationGenerateRequestBody requestBody,
                                              BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        //get all org list
        List<SysOrg> orgList = organizationService.getOrganizationByFilter(
                requestBody.getFilter().getOrgName(),
                requestBody.getFilter().getStatus(),
                requestBody.getFilter().getParentOrgName()
        );

        List<SysOrg> exportList = getExportList(orgList, requestBody.getIsAll(), requestBody.getIdList());

        OrganizationPdfView.setResource(res);
        setDictionary();
        InputStream inputStream = OrganizationPdfView.buildPDFDocument(exportList);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=organization.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));

    }

}
