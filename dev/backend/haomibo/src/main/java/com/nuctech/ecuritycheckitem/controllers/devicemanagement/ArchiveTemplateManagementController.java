/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/19
 * @CreatedBy Choe.
 * @FileName ArchiveTemplateManagementController.java
 * @ModifyHistory
 */

package com.nuctech.ecuritycheckitem.controllers.devicemanagement;

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
     * Device Category datatable request body.
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
            String s_template_name;
            String status;
            String categoryName;
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
        Long archives_template_id;

        @NotNull
        @Pattern(regexp = SerArchiveTemplate.Status.ACTIVE + "|" + SerArchiveTemplate.Status.INACTIVE)
        String status;

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
        String s_template_name;

        @NotNull
        String archives_template_number;

        @NotNull
        Long category_id;

        String manufacturer;

        String original_model;

        String note;

        List<SerArchiveIndicators> indicatorsList;

        SerArchiveTemplate convert2SerArchiveTemplate() {

            return SerArchiveTemplate
                    .builder()
                    .s_template_name(this.getS_template_name())
                    .archives_template_number(this.getArchives_template_number())
                    .category_id(this.getCategory_id())
                    .manufacturer(Optional.of(this.getManufacturer()).orElse(""))
                    .original_model(Optional.of(this.getOriginal_model()).orElse(""))
                    .status(SerArchiveTemplate.Status.INACTIVE)
                    .note(Optional.of(this.getNote()).orElse(""))
                    .archiveIndicatorsList(this.getIndicatorsList())
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
        Long archives_template_id;

        @NotNull
        String s_template_name;

        @NotNull
        String archives_template_number;

        @NotNull
        Long category_id;

        String manufacturer;

        String original_model;

        String note;

        List<SerArchiveIndicators> indicatorsList;

        SerArchiveTemplate convert2SerArchiveTemplate(Long createdBy, Date createdTime) {

            return SerArchiveTemplate
                    .builder()
                    .archives_template_id(this.getArchives_template_id())
                    .s_template_name(this.getS_template_name())
                    .archives_template_number(this.getArchives_template_number())
                    .category_id(this.getCategory_id())
                    .manufacturer(Optional.of(this.getManufacturer()).orElse(""))
                    .original_model(Optional.of(this.getOriginal_model()).orElse(""))
                    .status(SerArchiveTemplate.Status.INACTIVE)
                    .createdBy(createdBy)
                    .createdTime(createdTime)
                    .note(Optional.of(this.getNote()).orElse(""))
                    .archiveIndicatorsList(this.getIndicatorsList())
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
        Long archives_template_id;
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
            if (!StringUtils.isEmpty(filter.getS_template_name())) {
                predicate.and(builder.s_template_name.contains(filter.getS_template_name()));
            }
            if (!StringUtils.isEmpty(filter.getStatus())) {
                predicate.and(builder.status.contains(filter.getStatus()));
            }
            if (!StringUtils.isEmpty(filter.getCategoryName())) {
                predicate.and(builder.deviceCategory.categoryName.contains(filter.getCategoryName()));
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
                .getDefaultFilters();

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
                serArchiveTemplate.archives_template_id.eq(requestBody.getArchives_template_id()));
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

    /**
     * Archive Template create request.
     */
    @RequestMapping(value = "/archive-template/create", method = RequestMethod.POST)
    public Object ArchiveTemplateCreate(
            @RequestBody @Valid ArchiveTemplateCreateRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // Check if category is existing.
        if (!sysDeviceCategoryRepository.exists(QSysDeviceCategory.sysDeviceCategory.categoryId.eq(requestBody.getCategory_id()))) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        SerArchiveTemplate serArchiveTemplate = requestBody.convert2SerArchiveTemplate();

        // Add createdInfo.
        serArchiveTemplate.addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());

        serArchiveTemplateRepository.save(serArchiveTemplate);

        if(serArchiveTemplate.getArchiveIndicatorsList() != null) {
            for(int i = 0; i < serArchiveTemplate.getArchiveIndicatorsList().size(); i ++) {
                serArchiveIndicatorsRepository.save(serArchiveTemplate.getArchiveIndicatorsList().get(i));
            }
        }
        return new CommonResponseBody(ResponseMessage.OK);
    }

    /**
     * Archive Template modify request.
     */
    @RequestMapping(value = "/archive-template/modify", method = RequestMethod.POST)
    public Object ArchiveTemplateCreate(
            @RequestBody @Valid ArchiveTemplateModifyRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // Check if category is existing.
        if (!sysDeviceCategoryRepository.exists(QSysDeviceCategory.sysDeviceCategory.categoryId.eq(requestBody.getCategory_id()))) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        SerArchiveTemplate oldSerArchiveTemplate = serArchiveTemplateRepository.findOne(QSerArchiveTemplate.serArchiveTemplate
                .archives_template_id.eq(requestBody.getArchives_template_id())).orElse(null);

        /*
        Todo
            Check can modify or not(other archive contain this or not)
         */

        //don't modify created by and create time.
        SerArchiveTemplate serArchiveTemplate = requestBody.convert2SerArchiveTemplate(oldSerArchiveTemplate.getCreatedBy(),
                oldSerArchiveTemplate.getCreatedTime());

        // Add createdInfo.
        serArchiveTemplate.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());

        serArchiveTemplateRepository.save(serArchiveTemplate);

        /*
            Todo
               Modify correspond indicators.
         */

        return new CommonResponseBody(ResponseMessage.OK);
    }

    /**
     * Archive Template delete request.
     */
    @RequestMapping(value = "/archive-template/delete", method = RequestMethod.POST)
    public Object fieldDelete(
            @RequestBody @Valid ArchiveTemplateDeleteRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        SerArchiveTemplate serArchiveTemplate = serArchiveTemplateRepository.findOne(QSerArchiveTemplate.serArchiveTemplate
                .archives_template_id.eq(requestBody.getArchives_template_id())).orElse(null);


        /*
        Todo
            Check if archive contain this template or not
         */

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
     * Archive Template get all request.
     */
    @RequestMapping(value = "/archive-template/get-all", method = RequestMethod.POST)
    public Object archiveTemplateGetAll() {


        List<SerArchiveTemplate> serArchiveTemplateList = serArchiveTemplateRepository.findAll();

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, serArchiveTemplateList));


        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();

        value.setFilters(filters);

        return value;
    }

}
