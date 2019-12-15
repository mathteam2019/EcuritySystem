/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/18
 * @CreatedBy Choe.
 * @FileName FieldManagementController.java
 * @ModifyHistory
 */

package com.nuctech.ecuritycheckitem.controllers.fieldmanagement;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.enums.Role;
import com.nuctech.ecuritycheckitem.export.devicemanagement.DeviceExcelView;
import com.nuctech.ecuritycheckitem.export.fieldmanagement.FieldManagementExcelView;
import com.nuctech.ecuritycheckitem.export.fieldmanagement.FieldManagementPdfView;
import com.nuctech.ecuritycheckitem.export.fieldmanagement.FieldManagementWordView;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.QSysDevice;
import com.nuctech.ecuritycheckitem.models.db.QSysField;
import com.nuctech.ecuritycheckitem.models.db.SysField;
import com.nuctech.ecuritycheckitem.models.db.SysUser;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.reusables.FilteringAndPaginationResult;
import com.nuctech.ecuritycheckitem.service.fieldmanagement.FieldService;
import com.nuctech.ecuritycheckitem.utils.PageResult;
import com.querydsl.core.BooleanBuilder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
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
 * Field management controller.
 */
@RestController
@RequestMapping("/site-management")
public class FieldManagementController extends BaseController {

    @Autowired
    FieldService fieldService;
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
                    .leader(Optional.ofNullable(this.getLeader()).orElse(""))
                    .mobile(Optional.ofNullable(this.getMobile()).orElse(""))
                    .status(SysField.Status.INACTIVE)
                    .note(Optional.ofNullable(this.getNote()).orElse(""))
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
     * Field  generate request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class FieldGenerateRequestBody {


        String idList;
        @NotNull
        Boolean isAll;

        FieldGetByFilterAndPageRequestBody.Filter filter;
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
                    .leader(Optional.ofNullable(this.getLeader()).orElse(""))
                    .mobile(Optional.ofNullable(this.getMobile()).orElse(""))
                    .status(SysField.Status.INACTIVE)
                    .note(Optional.ofNullable(this.getNote()).orElse(""))
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
    @PreAuthorize(Role.Authority.HAS_FIELD_CREATE)
    @RequestMapping(value = "/field/create", method = RequestMethod.POST)
    public Object fieldCreate(
            @RequestBody @Valid FieldCreateRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // Check if parent field is existing.
        if (requestBody.getParentFieldId() != 0 && fieldService.checkFieldExist(requestBody.getParentFieldId())) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        SysField sysField = requestBody.convert2SysField();
        fieldService.createField(sysField);

        return new CommonResponseBody(ResponseMessage.OK);
    }


    /**
     * Field modify request.
     */
    @PreAuthorize(Role.Authority.HAS_FIELD_MODIFY)
    @RequestMapping(value = "/field/modify", method = RequestMethod.POST)
    public Object fieldModify(
            @RequestBody @Valid FieldModifyRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }



        // Check if field is existing.
        if (!fieldService.checkFieldExist(requestBody.getFieldId())) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // Check if parent field is existing.
        if (requestBody.getParentFieldId() != 0 && !fieldService.checkFieldExist(requestBody.getParentFieldId())) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // Check if field has children.
        if (fieldService.checkHasChild(requestBody.getFieldId())) {
            // Can't delete if field has children.
            return new CommonResponseBody(ResponseMessage.HAS_CHILDREN);
        }


        //check if device use this field
        if(fieldService.checkDeviceExist(requestBody.getFieldId())) {
            return new CommonResponseBody(ResponseMessage.HAS_DEVICES);
        }

        SysField sysField = requestBody.convert2SysField();
        fieldService.modifyField(sysField);

        return new CommonResponseBody(ResponseMessage.OK);
    }


    /**
     * Field delete request.
     */
    @PreAuthorize(Role.Authority.HAS_FIELD_DELETE)
    @RequestMapping(value = "/field/delete", method = RequestMethod.POST)
    public Object fieldDelete(
            @RequestBody @Valid FieldDeleteRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // Check if field has children.
        if (fieldService.checkHasChild(requestBody.getFieldId())) {
            // Can't delete if field has children.
            return new CommonResponseBody(ResponseMessage.HAS_CHILDREN);
        }

        if(fieldService.checkDeviceExist(requestBody.getFieldId())) {
            return new CommonResponseBody(ResponseMessage.HAS_DEVICES);
        }

        fieldService.removeField(requestBody.getFieldId());

        return new CommonResponseBody(ResponseMessage.OK);
    }

    /**
     * Field update status request.
     */
    @PreAuthorize(Role.Authority.HAS_FIELD_UPDATE_STATUS)
    @RequestMapping(value = "/field/update-status", method = RequestMethod.POST)
    public Object fieldUpdateStatus(
            @RequestBody @Valid FieldUpdateStatusRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // Check if field is existing.

        if (!fieldService.checkFieldExist(requestBody.getFieldId())) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        fieldService.updateStatus(requestBody.getFieldId(), requestBody.getStatus());

        return new CommonResponseBody(ResponseMessage.OK);
    }


    /**
     * Field get all request.
     */
    @RequestMapping(value = "/field/get-all", method = RequestMethod.POST)
    public Object fieldGetAll() {

        List<SysField> sysFieldList = fieldService.findAll();

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


        String designation = "";
        String status = "";
        String parentDesignation = "";
        if(requestBody.getFilter() != null) {
            designation = requestBody.getFilter().getFieldDesignation();
            status = requestBody.getFilter().getStatus();
            parentDesignation = requestBody.getFilter().getParentFieldDesignation();
        }
        int currentPage = requestBody.getCurrentPage() - 1; // On server side, page is calculated from 0.
        int perPage = requestBody.getPerPage();

        PageResult<SysField> result = fieldService.getDeviceListByFilter(designation, status, parentDesignation, currentPage, perPage);
        long total = result.getTotal();
        List<SysField> data = result.getDataList();

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
                .addFilter(ModelJsonFilters.FILTER_SYS_FIELD, SimpleBeanPropertyFilter.serializeAllExcept("parent"));

        value.setFilters(filters);

        return value;
    }



    /**
     * Field generate excel file request.
     */
    @PreAuthorize(Role.Authority.HAS_FIELD_EXPORT)
    @RequestMapping(value = "/field/export", method = RequestMethod.POST)
    public Object fieldGenerateExcelFile(@RequestBody @Valid FieldGenerateRequestBody requestBody,
                                     BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        String designation = "";
        String status = "";
        String parentDesignation = "";
        if(requestBody.getFilter() != null) {
            designation = requestBody.getFilter().getFieldDesignation();
            status = requestBody.getFilter().getStatus();
            parentDesignation = requestBody.getFilter().getParentFieldDesignation();
        }
        List<SysField> exportList = fieldService.getExportList(designation, status, parentDesignation, requestBody.getIsAll(), requestBody.getIdList());
        setDictionary();
        InputStream inputStream = FieldManagementExcelView.buildExcelDocument(exportList);



        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=field.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msexcel"))
                .body(new InputStreamResource(inputStream));

    }

    /**
     * Field generate word file request.
     */
    @PreAuthorize(Role.Authority.HAS_FIELD_TOWORD)
    @RequestMapping(value = "/field/word", method = RequestMethod.POST)
    public Object fieldGenerateWordFile(@RequestBody @Valid FieldGenerateRequestBody requestBody,
                                         BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        String designation = "";
        String status = "";
        String parentDesignation = "";
        if(requestBody.getFilter() != null) {
            designation = requestBody.getFilter().getFieldDesignation();
            status = requestBody.getFilter().getStatus();
            parentDesignation = requestBody.getFilter().getParentFieldDesignation();
        }
        List<SysField> exportList = fieldService.getExportList(designation, status, parentDesignation, requestBody.getIsAll(), requestBody.getIdList());
        setDictionary();
        InputStream inputStream = FieldManagementWordView.buildWordDocument(exportList);



        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=field.docx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msword"))
                .body(new InputStreamResource(inputStream));

    }


    /**
     * Field generate pdf file request.
     */
    @PreAuthorize(Role.Authority.HAS_FIELD_PRINT)
    @RequestMapping(value = "/field/print", method = RequestMethod.POST)
    public Object fieldGeneratePDFFile(@RequestBody @Valid FieldGenerateRequestBody requestBody,
                                         BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        String designation = "";
        String status = "";
        String parentDesignation = "";
        if(requestBody.getFilter() != null) {
            designation = requestBody.getFilter().getFieldDesignation();
            status = requestBody.getFilter().getStatus();
            parentDesignation = requestBody.getFilter().getParentFieldDesignation();
        }
        List<SysField> exportList = fieldService.getExportList(designation, status, parentDesignation, requestBody.getIsAll(), requestBody.getIdList());

        FieldManagementPdfView.setResource(res);
        setDictionary();
        InputStream inputStream = FieldManagementPdfView.buildPDFDocument(exportList);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=field.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));

    }
}
