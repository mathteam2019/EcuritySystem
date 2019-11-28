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
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.*;

import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.reusables.FilteringAndPaginationResult;
import com.querydsl.core.BooleanBuilder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/device-management/document-template")
public class ArchiveTemplateManagementController extends BaseController {

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

        @NotNull
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

        SerArchiveTemplate convert2SerArchiveTemplate(Long createdBy, Date createdTime) {

            return SerArchiveTemplate
                    .builder()
                    .archivesTemplateId(this.getArchivesTemplateId())
                    .templateName(this.getTemplateName())
                    .archivesTemplateNumber(this.getArchivesTemplateNumber())
                    .categoryId(this.getCategoryId())
                    .manufacturer(Optional.ofNullable(this.getManufacturer()).orElse(""))
                    .originalModel(Optional.ofNullable(this.getOriginalModel()).orElse(""))
                    .status(SerArchiveTemplate.Status.INACTIVE)
                    .createdBy(createdBy)
                    .createdTime(createdTime)
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


        QSerArchiveTemplate builder = QSerArchiveTemplate.serArchiveTemplate;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        ArchiveTemplateGetByFilterAndPageRequestBody.Filter filter = requestBody.getFilter();
        if (filter != null) {
            if (!StringUtils.isEmpty(filter.getTemplateName())) {
                predicate.and(builder.templateName.contains(filter.getTemplateName()));
            }
            if (!StringUtils.isEmpty(filter.getStatus())) {
                predicate.and(builder.status.eq(filter.getStatus()));
            }
            if (filter.getCategoryId() != null) {
                predicate.and(builder.deviceCategory.categoryId.eq(filter.getCategoryId()));
            }
        }

        int currentPage = requestBody.getCurrentPage() - 1; // On server side, page is calculated from 0.
        int perPage = requestBody.getPerPage();

        PageRequest pageRequest = PageRequest.of(currentPage, perPage);

        long total = serArchiveTemplateRepository.count(predicate);
        List<SerArchiveTemplate> data = serArchiveTemplateRepository.findAll(predicate, pageRequest).getContent();


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
     * Archive Template update status request.
     */
    @RequestMapping(value = "/archive-template/update-status", method = RequestMethod.POST)
    public Object archiveTemplateUpdateStatus(
            @RequestBody @Valid ArchiveTemplateUpdateStatusRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // Check if template is existing.
        Optional<SerArchiveTemplate> optionalSerArchiveTemplate = serArchiveTemplateRepository.findOne(QSerArchiveTemplate.
                serArchiveTemplate.archivesTemplateId.eq(requestBody.getArchivesTemplateId()));
        if (!optionalSerArchiveTemplate.isPresent()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        SerArchiveTemplate serArchiveTemplate = optionalSerArchiveTemplate.get();


        // Update status.
        serArchiveTemplate.setStatus(requestBody.getStatus());

        // Add edited info.
        serArchiveTemplate.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());

        serArchiveTemplateRepository.save(serArchiveTemplate);

        return new CommonResponseBody(ResponseMessage.OK);
    }

    private boolean isUsedTemplate(long archiveTemplateId) {
        Optional<SerArchive> optionalSerArchive = serArchiveRepository.findOne(QSerArchive.
                serArchive.archivesTemplateId.eq(archiveTemplateId));
        if (!optionalSerArchive.isPresent()) {
            return false;
        }
        return true;
    }

    /**
     * Archive Indicator update status request.
     */
    @RequestMapping(value = "/archive-indicator/update-isnull", method = RequestMethod.POST)
    public Object archiveIndicatorUpdateIsNull(
            @RequestBody @Valid ArchiveIndicatorUpdateIsNullRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // Check if indicator is existing.
        Optional<SerArchiveIndicators> optionalSerArchiveIndicators = serArchiveIndicatorsRepository.findOne(QSerArchiveIndicators.
                serArchiveIndicators.indicatorsId.eq(requestBody.getIndicatorsId()));
        if (!optionalSerArchiveIndicators.isPresent()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        SerArchiveIndicators serArchiveIndicators = optionalSerArchiveIndicators.get();

        Long archiveTemplateId = serArchiveIndicators.getArchivesTemplateId();
        if(archiveTemplateId != null && isUsedTemplate(archiveTemplateId)) {
            return new CommonResponseBody(ResponseMessage.HAS_ARCHIVES);
        }

        // Update status.
        serArchiveIndicators.setIsNull(requestBody.getIsNull());

        // Add edited info.
        serArchiveIndicators.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());

        serArchiveIndicatorsRepository.save(serArchiveIndicators);

        return new CommonResponseBody(ResponseMessage.OK);
    }

    /**
     * Archive Template create request.
     */
    @RequestMapping(value = "/archive-template/create", method = RequestMethod.POST)
    public Object archiveTemplateCreate(
            @RequestBody @Valid ArchiveTemplateCreateRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // Check if category is existing.
        if (!sysDeviceCategoryRepository.exists(QSysDeviceCategory.sysDeviceCategory.categoryId.eq(requestBody.getCategoryId()))) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        SerArchiveTemplate serArchiveTemplate = requestBody.convert2SerArchiveTemplate();

        // Add createdInfo.
        serArchiveTemplate.addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());

        serArchiveTemplateRepository.save(serArchiveTemplate);

        if(serArchiveTemplate.getArchiveIndicatorsList() != null) {
            for(int i = 0; i < serArchiveTemplate.getArchiveIndicatorsList().size(); i ++) {
                SerArchiveIndicators archiveIndicators = serArchiveTemplate.getArchiveIndicatorsList().get(i);
                archiveIndicators.setArchivesTemplateId(serArchiveTemplate.getArchivesTemplateId());
                archiveIndicators.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
                serArchiveIndicatorsRepository.save(serArchiveTemplate.getArchiveIndicatorsList().get(i));
            }
        }
        return new CommonResponseBody(ResponseMessage.OK);
    }

    /**
     * Archive Indicator create request.
     */
    @RequestMapping(value = "/archive-indicator/create", method = RequestMethod.POST)
    public Object archiveIndicatorCreate(
            @RequestBody @Valid ArchiveIndicatorCreateRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }


        SerArchiveIndicators serArchiveIndicators = requestBody.convert2SerArchiveIndicator();

        // Add createdInfo.
        serArchiveIndicators.addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());

        serArchiveIndicatorsRepository.save(serArchiveIndicators);

        return new CommonResponseBody(ResponseMessage.OK, serArchiveIndicators.getIndicatorsId());
    }

    /**
     * Archive Template modify request.
     */
    @RequestMapping(value = "/archive-template/modify", method = RequestMethod.POST)
    public Object archiveTemplateModify(
            @RequestBody @Valid ArchiveTemplateModifyRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // Check if category is existing.
        if (!sysDeviceCategoryRepository.exists(QSysDeviceCategory.sysDeviceCategory.categoryId.eq(requestBody.getCategoryId()))) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        SerArchiveTemplate oldSerArchiveTemplate = serArchiveTemplateRepository.findOne(QSerArchiveTemplate.serArchiveTemplate
                .archivesTemplateId.eq(requestBody.getArchivesTemplateId())).orElse(null);

        //check if template exist
        if(oldSerArchiveTemplate == null) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        if(isUsedTemplate(oldSerArchiveTemplate.getArchivesTemplateId())) {
            return new CommonResponseBody(ResponseMessage.HAS_ARCHIVES);
        }

        //don't modify created by and create time.
        SerArchiveTemplate serArchiveTemplate = requestBody.convert2SerArchiveTemplate(oldSerArchiveTemplate.getCreatedBy(),
                oldSerArchiveTemplate.getCreatedTime());

        // Add editInfo.
        serArchiveTemplate.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());

        serArchiveTemplateRepository.save(serArchiveTemplate);

        if(serArchiveTemplate.getArchiveIndicatorsList() != null) {
            for(int i = 0; i < serArchiveTemplate.getArchiveIndicatorsList().size(); i ++) {
                SerArchiveIndicators archiveIndicators = serArchiveTemplate.getArchiveIndicatorsList().get(i);
                archiveIndicators.setArchivesTemplateId(serArchiveTemplate.getArchivesTemplateId());
                archiveIndicators.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
                serArchiveIndicatorsRepository.save(serArchiveTemplate.getArchiveIndicatorsList().get(i));
            }
        }

        return new CommonResponseBody(ResponseMessage.OK);
    }

    /**
     * Archive Template delete request.
     */
    @RequestMapping(value = "/archive-template/delete", method = RequestMethod.POST)
    public Object archiveTemplateDelete(
            @RequestBody @Valid ArchiveTemplateDeleteRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        SerArchiveTemplate serArchiveTemplate = serArchiveTemplateRepository.findOne(QSerArchiveTemplate.serArchiveTemplate
                .archivesTemplateId.eq(requestBody.getArchivesTemplateId())).orElse(null);

        //check template exist or not
        if(serArchiveTemplate == null) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }


        if(isUsedTemplate(serArchiveTemplate.getArchivesTemplateId())) {
            return new CommonResponseBody(ResponseMessage.HAS_ARCHIVES);
        }

        //remove it's indicators
        if(serArchiveTemplate.getArchiveIndicatorsList() != null) {
            for(int i = 0; i < serArchiveTemplate.getArchiveIndicatorsList().size(); i ++) {
                serArchiveIndicatorsRepository.delete(serArchiveTemplate.getArchiveIndicatorsList().get(i));
            }
        }

        serArchiveTemplateRepository.delete(serArchiveTemplate);


        return new CommonResponseBody(ResponseMessage.OK);
    }

    /**
     * Archive Indicators delete request.
     */
    @RequestMapping(value = "/archive-indicator/delete", method = RequestMethod.POST)
    public Object archiveIndicatorDelete(
            @RequestBody @Valid ArchiveIndicatorDeleteRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        SerArchiveIndicators serArchiveIndicators = serArchiveIndicatorsRepository.findOne(QSerArchiveIndicators.serArchiveIndicators
                .indicatorsId.eq(requestBody.getIndicatorsId())).orElse(null);

        //check indicators exist or not
        if(serArchiveIndicators == null) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        Long archiveTemplateId = serArchiveIndicators.getArchivesTemplateId();
        if(archiveTemplateId != null && isUsedTemplate(archiveTemplateId)) {
            return new CommonResponseBody(ResponseMessage.HAS_ARCHIVES);
        }

        serArchiveIndicatorsRepository.delete(serArchiveIndicators);


        return new CommonResponseBody(ResponseMessage.OK);
    }

    /**
     * Archive Template get all request.
     */
    @RequestMapping(value = "/archive-template/get-all", method = RequestMethod.POST)
    public Object archiveTemplateGetAll() {


        List<SerArchiveTemplate> serArchiveTemplateList = serArchiveTemplateRepository.findAll();

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, serArchiveTemplateList));


        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();
        filters.addFilter(ModelJsonFilters.FILTER_SYS_DEVICE_CATEGORY, SimpleBeanPropertyFilter.serializeAllExcept("parent"));

        value.setFilters(filters);

        return value;
    }

}
