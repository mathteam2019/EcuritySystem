package com.nuctech.ecuritycheckitem.controllers.fieldmanagement;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.controllers.permissionmanagement.OrganizationManagementController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.enums.Role;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.QSysField;
import com.nuctech.ecuritycheckitem.models.db.SysField;
import com.nuctech.ecuritycheckitem.models.db.SysOrg;
import com.nuctech.ecuritycheckitem.models.db.SysUser;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.reusables.FilteringAndPaginationResult;
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

/**
 * Field management controller.
 */
@RestController
@RequestMapping("/site-management")
public class FieldManagementController extends BaseController {
    /**
     * Field create request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class FieldCreateRequestBody {

        @NotNull
        String fieldSerial;

        @NotNull
        String fieldDesignation;

//        @NotNull
//        Long orgId;

        @NotNull
        Long parentFieldId;

        String leader;

        String mobile;

        String note;

        SysField convert2SysField() {

            return SysField
                    .builder()
                    //.orgId(this.getOrgId())
                    .fieldSerial(this.getFieldSerial())
                    .fieldDesignation(this.getFieldDesignation())
                    .parentFieldId(this.getParentFieldId())
                    .leader(Optional.of(this.getLeader()).orElse(""))
                    .mobile(Optional.of(this.getMobile()).orElse(""))
                    .status(SysField.Status.INACTIVE)
                    .note(Optional.of(this.getNote()).orElse(""))
                    .build();

        }

    }

    /**
     * Field delete request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class FieldDeleteRequestBody {

        @NotNull
        Long fieldId;

    }

    /**
     * Field datatable request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class FieldGetByFilterAndPageRequestBody {

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        static class Filter {
            String fieldDesignation;
            String status;
            String parentFieldDesignation;
        }

        @NotNull
        @Min(1)
        int currentPage;

        @NotNull
        int perPage;

        Filter filter;


    }

    /**
     * Field modify request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class FieldModifyRequestBody {

        @NotNull
        Long fieldId;

        @NotNull
        String fieldSerial;

        @NotNull
        String fieldDesignation;

//        @NotNull
//        Long orgId;

        @NotNull
        Long parentFieldId;

        String leader;

        String mobile;

        String note;

        SysField convert2SysField() {

            return SysField
                    .builder()
                    .fieldId(this.getFieldId())
                    .fieldSerial(this.getFieldSerial())
                    .fieldDesignation(this.getFieldDesignation())
                    //.orgId(this.getOrgId())
                    .parentFieldId(this.getParentFieldId())
                    .leader(Optional.of(this.getLeader()).orElse(""))
                    .mobile(Optional.of(this.getMobile()).orElse(""))
                    .status(SysField.Status.INACTIVE)
                    .note(Optional.of(this.getNote()).orElse(""))
                    .build();

        }

    }

    /**
     * Field update status request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class FieldUpdateStatusRequestBody {

        @NotNull
        Long fieldId;

        @NotNull
        @Pattern(regexp = SysField.Status.ACTIVE + "|" + SysField.Status.INACTIVE)
        String status;

    }



    /**
     * Field create request.
     */
    @RequestMapping(value = "/field/create", method = RequestMethod.POST)
    public Object fieldCreate(
            @RequestBody @Valid FieldCreateRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // Check if parent field is existing.
        if (!sysFieldRepository.exists(QSysField.sysField.fieldId.eq(requestBody.getParentFieldId()))) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        SysField sysField = requestBody.convert2SysField();

        // Add createdInfo.
        sysField.addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());

        sysFieldRepository.save(sysField);

        return new CommonResponseBody(ResponseMessage.OK);
    }


    /**
     * Field modify request.
     */
    @RequestMapping(value = "/field/modify", method = RequestMethod.POST)
    public Object fieldModify(
            @RequestBody @Valid FieldModifyRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        SysField oldSysField = sysFieldRepository.findOne(QSysField.sysField.fieldId.eq(requestBody.getFieldId())).orElse(null);

        // Check if field is existing.
        if (oldSysField == null) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // Check if parent field is existing.
        if (!sysFieldRepository.exists(QSysField.sysField.fieldId.eq(requestBody.getParentFieldId()))) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        SysField sysField = requestBody.convert2SysField();

        //Don't modify created by and created time
        sysField.setCreatedBy(oldSysField.getCreatedBy());
        sysField.setCreatedTime(oldSysField.getCreatedTime());

        // Add edited info.
        sysField.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());

        sysFieldRepository.save(sysField);

        return new CommonResponseBody(ResponseMessage.OK);
    }


    /**
     * Field delete request.
     */
    @RequestMapping(value = "/field/delete", method = RequestMethod.POST)
    public Object fieldDelete(
            @RequestBody @Valid FieldDeleteRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // Check if field has children.
        boolean isHavingChildren = sysFieldRepository.exists(QSysField.sysField.parentFieldId.eq(requestBody.getFieldId()));
        if (isHavingChildren) {
            // Can't delete if org has children.
            return new CommonResponseBody(ResponseMessage.HAS_CHILDREN);
        }

        sysFieldRepository.delete(SysField.builder().fieldId(requestBody.getFieldId()).build());

        return new CommonResponseBody(ResponseMessage.OK);
    }


    /**
     * Field update status request.
     */
    @RequestMapping(value = "/field/update-status", method = RequestMethod.POST)
    public Object fieldUpdateStatus(
            @RequestBody @Valid FieldUpdateStatusRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // Check if field is existing.
        Optional<SysField> optionalSysField = sysFieldRepository.findOne(QSysField.sysField.fieldId.eq(requestBody.getFieldId()));
        if (!optionalSysField.isPresent()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        SysField sysField = optionalSysField.get();

        // Update status.
        sysField.setStatus(requestBody.getStatus());

        // Add edited info.
        sysField.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());

        sysFieldRepository.save(sysField);

        return new CommonResponseBody(ResponseMessage.OK);
    }


    /**
     * Field get all request.
     * BARE, WITH_PARENT, WITH_CHILDREN
     */
    @RequestMapping(value = "/field/get-all", method = RequestMethod.POST)
    public Object fieldGetAll() {



        List<SysField> sysFieldList = sysFieldRepository.findAll();

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, sysFieldList));


        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();

        value.setFilters(filters);

        return value;
    }


    /**
     * Field datatable data.
     */
    @RequestMapping(value = "/field/get-by-filter-and-page", method = RequestMethod.POST)
    public Object fieldGetByFilterAndPage(
            @RequestBody @Valid FieldGetByFilterAndPageRequestBody requestBody,
            BindingResult bindingResult) {


        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }


        QSysField builder = QSysField.sysField;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        FieldGetByFilterAndPageRequestBody.Filter filter = requestBody.getFilter();
        if (filter != null) {
            if (!StringUtils.isEmpty(filter.getFieldDesignation())) {
                predicate.and(builder.fieldDesignation.contains(filter.getFieldDesignation()));
            }
            if (!StringUtils.isEmpty(filter.getStatus())) {
                predicate.and(builder.status.eq(filter.getStatus()));
            }
            if (!StringUtils.isEmpty(filter.getParentFieldDesignation())) {
                predicate.and(builder.parent.fieldDesignation.contains(filter.getParentFieldDesignation()));
            }
        }

        int currentPage = requestBody.getCurrentPage() - 1; // On server side, page is calculated from 0.
        int perPage = requestBody.getPerPage();

        PageRequest pageRequest = PageRequest.of(currentPage, perPage);

        long total = sysFieldRepository.count(predicate);
        List<SysField> data = sysFieldRepository.findAll(predicate, pageRequest).getContent();


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
                .getDefaultFilters();

        value.setFilters(filters);

        return value;
    }
}
