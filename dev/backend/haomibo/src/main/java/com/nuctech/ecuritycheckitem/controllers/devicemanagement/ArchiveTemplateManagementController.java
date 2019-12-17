/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/19
 * @CreatedBy Choe.
 * @FileName ArchiveTemplateManagementController.java
 * @ModifyHistory
 */

package com.nuctech.ecuritycheckitem.controllers.devicemanagement;

import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.enums.Role;
import com.nuctech.ecuritycheckitem.export.devicemanagement.DeviceArchiveTemplateExcelView;
import com.nuctech.ecuritycheckitem.export.devicemanagement.DeviceArchiveTemplatePdfView;
import com.nuctech.ecuritycheckitem.export.devicemanagement.DeviceArchiveTemplateWordView;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.*;

import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.reusables.FilteringAndPaginationResult;
import com.nuctech.ecuritycheckitem.service.devicemanagement.ArchiveTemplateService;
import com.nuctech.ecuritycheckitem.utils.PageResult;
import com.querydsl.core.BooleanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/device-management/document-template")
public class ArchiveTemplateManagementController extends BaseController {

    @Autowired
    ArchiveTemplateService archiveTemplateService;

    /**
     * Archive Template datatable request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class ArchiveTemplateGetByFilterAndPageRequestBody {

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        static class Filter {
            String templateName;
            String status;
            Long categoryId;
        }

        @NotNull
        @Min(1)
        int currentPage;

        @NotNull
        int perPage;

        Filter filter;
    }

    /**
     * Archive Template generate request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class ArchiveTemplateGenerateRequestBody {

        String idList;
        @NotNull
        Boolean isAll;

        ArchiveTemplateGetByFilterAndPageRequestBody.Filter filter;
    }

    /**
     * Archive Template update status request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class ArchiveTemplateUpdateStatusRequestBody {

        @NotNull
        Long archivesTemplateId;

        @NotNull
        @Pattern(regexp = SerArchiveTemplate.Status.ACTIVE + "|" + SerArchiveTemplate.Status.INACTIVE)
        String status;

    }

    /**
     * Archive Template update status request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class ArchiveIndicatorUpdateIsNullRequestBody {

        @NotNull
        Long indicatorsId;

        @NotNull
        @Pattern(regexp = SerArchiveTemplate.Status.YES + "|" + SerArchiveTemplate.Status.NO)
        String isNull;

    }

    /**
     * Archive Template create request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class ArchiveTemplateCreateRequestBody {

        @NotNull
        String templateName;

        @NotNull
        String archivesTemplateNumber;

        @NotNull
        Long categoryId;

        String manufacturer;

        String originalModel;

        String note;

        List<SerArchiveIndicators> archiveIndicatorsList;

        SerArchiveTemplate convert2SerArchiveTemplate() {

            return SerArchiveTemplate
                    .builder()
                    .templateName(this.getTemplateName())
                    .archivesTemplateNumber(this.getArchivesTemplateNumber())
                    .categoryId(this.getCategoryId())
                    .manufacturer(Optional.ofNullable(this.getManufacturer()).orElse(""))
                    .originalModel(Optional.ofNullable(this.getOriginalModel()).orElse(""))
                    .status(SerArchiveTemplate.Status.INACTIVE)
                    .note(Optional.ofNullable(this.getNote()).orElse(""))
                    .archiveIndicatorsList(this.getArchiveIndicatorsList())
                    .build();

        }

    }


    /**
     * Archive Indicator create request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class ArchiveIndicatorCreateRequestBody {

        @NotNull
        String indicatorsName;

        String indicatorsUnit;

        @NotNull
        @Pattern(regexp = SerArchiveTemplate.Status.YES + "|" + SerArchiveTemplate.Status.NO)
        String isNull;

        SerArchiveIndicators convert2SerArchiveIndicator() {

            return SerArchiveIndicators
                    .builder()
                    .indicatorsName(this.getIndicatorsName())
                    .indicatorsUnit(this.getIndicatorsUnit())
                    .isNull(this.getIsNull())
                    .build();

        }

    }

    /**
     * Archive Template modify request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class ArchiveTemplateModifyRequestBody {

        @NotNull
        Long archivesTemplateId;

        @NotNull
        String templateName;

        @NotNull
        String archivesTemplateNumber;

        @NotNull
        Long categoryId;

        String manufacturer;

        String originalModel;

        String note;

        List<SerArchiveIndicators> archiveIndicatorsList;

        SerArchiveTemplate convert2SerArchiveTemplate() {

            return SerArchiveTemplate
                    .builder()
                    .archivesTemplateId(this.getArchivesTemplateId())
                    .templateName(this.getTemplateName())
                    .archivesTemplateNumber(this.getArchivesTemplateNumber())
                    .categoryId(this.getCategoryId())
                    .manufacturer(Optional.ofNullable(this.getManufacturer()).orElse(""))
                    .originalModel(Optional.ofNullable(this.getOriginalModel()).orElse(""))
                    .status(SerArchiveTemplate.Status.INACTIVE)
                    .note(Optional.ofNullable(this.getNote()).orElse(""))
                    .archiveIndicatorsList(this.getArchiveIndicatorsList())
                    .build();

        }

    }

    /**
     * Archive Template delete request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class ArchiveTemplateDeleteRequestBody {

        @NotNull
        Long archivesTemplateId;
    }

    /**
     * Archive Indicator delete request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class ArchiveIndicatorDeleteRequestBody {

        @NotNull
        Long indicatorsId;
    }



    /**
     * Archive Template datatable data.
     */
    @RequestMapping(value = "/archive-template/get-by-filter-and-page", method = RequestMethod.POST)
    public Object archiveTemplateGetByFilterAndPage(
            @RequestBody @Valid ArchiveTemplateGetByFilterAndPageRequestBody requestBody,
            BindingResult bindingResult) {


        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        String templateName = "";
        String status = "";
        Long categoryId = null;
        if(requestBody.getFilter() != null) {
            templateName = requestBody.getFilter().getTemplateName();
            status = requestBody.getFilter().getStatus();
            categoryId = requestBody.getFilter().getCategoryId();
        }

        int currentPage = requestBody.getCurrentPage() - 1; // On server side, page is calculated from 0.
        int perPage = requestBody.getPerPage();

        PageResult<SerArchiveTemplate> result = archiveTemplateService.getArchiveTemplateListByPage(templateName, status, categoryId,
                currentPage, perPage);
        long total = result.getTotal();
        List<SerArchiveTemplate> data = result.getDataList();

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
                .addFilter(ModelJsonFilters.FILTER_SYS_DEVICE_CATEGORY, SimpleBeanPropertyFilter.serializeAllExcept("parent"));

        value.setFilters(filters);

        return value;
    }



    /**
     * Archive Template generate excel file request.
     */
    @PreAuthorize(Role.Authority.HAS_DEVICE_TEMPLATE_EXPORT)
    @RequestMapping(value = "/archive-template/xlsx", method = RequestMethod.POST)
    public Object archiveTemplateGenerateExcelFile(@RequestBody @Valid ArchiveTemplateGenerateRequestBody requestBody,
                                      BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        String templateName = "";
        String status = "";
        Long categoryId = null;
        if(requestBody.getFilter() != null) {
            templateName = requestBody.getFilter().getTemplateName();
            status = requestBody.getFilter().getStatus();
            categoryId = requestBody.getFilter().getCategoryId();
        }

        List<SerArchiveTemplate> exportList = archiveTemplateService.getExportListByFilter(templateName, status, categoryId, requestBody.getIsAll(), requestBody.getIdList());
        setDictionary();
        InputStream inputStream = DeviceArchiveTemplateExcelView.buildExcelDocument(exportList);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=archive-template.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msexcel"))
                .body(new InputStreamResource(inputStream));

    }

    /**
     * Archive Template generate word file request.
     */

    @RequestMapping(value = "/archive-template/docx", method = RequestMethod.POST)
    public Object archiveTemplateGenerateWordFile(@RequestBody @Valid ArchiveTemplateGenerateRequestBody requestBody,
                                                   BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        String templateName = "";
        String status = "";
        Long categoryId = null;
        if(requestBody.getFilter() != null) {
            templateName = requestBody.getFilter().getTemplateName();
            status = requestBody.getFilter().getStatus();
            categoryId = requestBody.getFilter().getCategoryId();
        }

        List<SerArchiveTemplate> exportList = archiveTemplateService.getExportListByFilter(templateName, status, categoryId, requestBody.getIsAll(), requestBody.getIdList());
        setDictionary();
        InputStream inputStream = DeviceArchiveTemplateWordView.buildWordDocument(exportList);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=archive-template.docx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msword"))
                .body(new InputStreamResource(inputStream));

    }

    /**
     * Archive Template generate excel file request.
     */
    @PreAuthorize(Role.Authority.HAS_DEVICE_TEMPLATE_PRINT)
    @RequestMapping(value = "/archive-template/pdf", method = RequestMethod.POST)
    public Object archiveTemplateGeneratePDFFile(@RequestBody @Valid ArchiveTemplateGenerateRequestBody requestBody,
                                              BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        String templateName = "";
        String status = "";
        Long categoryId = null;
        if(requestBody.getFilter() != null) {
            templateName = requestBody.getFilter().getTemplateName();
            status = requestBody.getFilter().getStatus();
            categoryId = requestBody.getFilter().getCategoryId();
        }

        List<SerArchiveTemplate> exportList = archiveTemplateService.getExportListByFilter(templateName, status, categoryId, requestBody.getIsAll(), requestBody.getIdList());
        DeviceArchiveTemplatePdfView.setResource(res);
        setDictionary();
        InputStream inputStream = DeviceArchiveTemplatePdfView.buildPDFDocument(exportList);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=archive-template.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));

    }

    /**
     * Archive Template update status request.
     */
    @PreAuthorize(Role.Authority.HAS_DEVICE_TEMPLATE_UPDATE_STATUS)
    @RequestMapping(value = "/archive-template/update-status", method = RequestMethod.POST)
    public Object archiveTemplateUpdateStatus(
            @RequestBody @Valid ArchiveTemplateUpdateStatusRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        if (!archiveTemplateService.checkArchiveTemplateExist(requestBody.getArchivesTemplateId())) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        archiveTemplateService.updateStatus(requestBody.getArchivesTemplateId(), requestBody.getStatus());

        return new CommonResponseBody(ResponseMessage.OK);
    }



    /**
     * Archive Indicator update status request.
     */
    @PreAuthorize(Role.Authority.HAS_DEVICE_INDICATOR_UPDATE_ISNULL)
    @RequestMapping(value = "/archive-indicator/update-isnull", method = RequestMethod.POST)
    public Object archiveIndicatorUpdateIsNull(
            @RequestBody @Valid ArchiveIndicatorUpdateIsNullRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        int status = archiveTemplateService.updateIndicatorStatus(requestBody.getIndicatorsId(), requestBody.getIsNull());
        switch (status) {
            case 0:
                return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
            case 1:
                return new CommonResponseBody(ResponseMessage.HAS_ARCHIVES);
        }


        return new CommonResponseBody(ResponseMessage.OK);
    }

    /**
     * Archive Template create request.
     */
    @PreAuthorize(Role.Authority.HAS_DEVICE_TEMPLATE_CREATE)
    @RequestMapping(value = "/archive-template/create", method = RequestMethod.POST)
    public Object archiveTemplateCreate(
            @RequestBody @Valid ArchiveTemplateCreateRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // Check if category is existing.
        if (!archiveTemplateService.checkCategoryExist(requestBody.getCategoryId())) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        if(archiveTemplateService.checkTemplateNameExist(requestBody.getTemplateName(), null)) {
            return new CommonResponseBody(ResponseMessage.USED_TEMPLATE_NAME);
        }

        if(archiveTemplateService.checkTemplateNumberExist(requestBody.getArchivesTemplateNumber(), null)) {
            return new CommonResponseBody(ResponseMessage.USED_TEMPLATE_NUMBER);
        }

        SerArchiveTemplate serArchiveTemplate = requestBody.convert2SerArchiveTemplate();
        archiveTemplateService.createSerArchiveTemplate(serArchiveTemplate);

        return new CommonResponseBody(ResponseMessage.OK);
    }

    /**
     * Archive Indicator create request.
     */
    @PreAuthorize(Role.Authority.HAS_DEVICE_INDICATOR_CREATE)
    @RequestMapping(value = "/archive-indicator/create", method = RequestMethod.POST)
    public Object archiveIndicatorCreate(
            @RequestBody @Valid ArchiveIndicatorCreateRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }


        SerArchiveIndicators serArchiveIndicators = requestBody.convert2SerArchiveIndicator();

        archiveTemplateService.createArchiveIndicator(serArchiveIndicators);

        return new CommonResponseBody(ResponseMessage.OK, serArchiveIndicators.getIndicatorsId());
    }

    /**
     * Archive Template modify request.
     */
    @PreAuthorize(Role.Authority.HAS_DEVICE_TEMPLATE_MODIFY)
    @RequestMapping(value = "/archive-template/modify", method = RequestMethod.POST)
    public Object archiveTemplateModify(
            @RequestBody @Valid ArchiveTemplateModifyRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // Check if category is existing.
        if (!archiveTemplateService.checkCategoryExist(requestBody.getCategoryId())) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }


        //check if template exist
        if(!archiveTemplateService.checkArchiveTemplateExist(requestBody.getArchivesTemplateId())) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        //check this template used
        if(archiveTemplateService.checkArchiveExist(requestBody.getArchivesTemplateId())) {
            return new CommonResponseBody(ResponseMessage.HAS_ARCHIVES);
        }

        if(archiveTemplateService.checkTemplateNameExist(requestBody.getTemplateName(), requestBody.getArchivesTemplateId())) {
            return new CommonResponseBody(ResponseMessage.USED_TEMPLATE_NAME);
        }

        if(archiveTemplateService.checkTemplateNumberExist(requestBody.getArchivesTemplateNumber(), requestBody.getArchivesTemplateId())) {
            return new CommonResponseBody(ResponseMessage.USED_TEMPLATE_NUMBER);
        }

        SerArchiveTemplate serArchiveTemplate = requestBody.convert2SerArchiveTemplate();

        archiveTemplateService.modifySerArchiveTemplate(serArchiveTemplate);

        return new CommonResponseBody(ResponseMessage.OK);
    }

    /**
     * Archive Template delete request.
     */
    @PreAuthorize(Role.Authority.HAS_DEVICE_TEMPLATE_DELETE)
    @RequestMapping(value = "/archive-template/delete", method = RequestMethod.POST)
    public Object archiveTemplateDelete(
            @RequestBody @Valid ArchiveTemplateDeleteRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }


        //check template exist or not
        if(!archiveTemplateService.checkArchiveTemplateExist(requestBody.getArchivesTemplateId())) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        if(archiveTemplateService.checkArchiveExist(requestBody.getArchivesTemplateId())) {
            return new CommonResponseBody(ResponseMessage.HAS_ARCHIVES);
        }

        archiveTemplateService.removeSerArchiveTemplate(requestBody.getArchivesTemplateId());


        return new CommonResponseBody(ResponseMessage.OK);
    }

    /**
     * Archive Indicators delete request.
     */
    @PreAuthorize(Role.Authority.HAS_DEVICE_INDICATOR_DELETE)
    @RequestMapping(value = "/archive-indicator/delete", method = RequestMethod.POST)
    public Object archiveIndicatorDelete(
            @RequestBody @Valid ArchiveIndicatorDeleteRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        int status = archiveTemplateService.removeSerArchiveIndicator(requestBody.getIndicatorsId());
        switch (status) {
            case 0:
                return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
            case 1:
                return new CommonResponseBody(ResponseMessage.HAS_ARCHIVES);
        }

        return new CommonResponseBody(ResponseMessage.OK);
    }

    /**
     * Archive Template get all request.
     */
    @RequestMapping(value = "/archive-template/get-all", method = RequestMethod.POST)
    public Object archiveTemplateGetAll() {


        List<SerArchiveTemplate> serArchiveTemplateList = archiveTemplateService.findAll();

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, serArchiveTemplateList));


        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();
        filters.addFilter(ModelJsonFilters.FILTER_SYS_DEVICE_CATEGORY, SimpleBeanPropertyFilter.serializeAllExcept("parent"));

        value.setFilters(filters);

        return value;
    }

}
