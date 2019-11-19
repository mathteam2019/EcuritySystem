/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/18
 * @CreatedBy Choe.
 * @FileName DeviceCategoryManagementController.java
 * @ModifyHistory
 */


package com.nuctech.ecuritycheckitem.controllers.devicemanagement;

import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.QSysDeviceCategory;
import com.nuctech.ecuritycheckitem.models.db.SysDeviceCategory;
import com.nuctech.ecuritycheckitem.models.db.SysUser;
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
@RequestMapping("/device-management/device-classify")
public class DeviceCategoryManagementController extends BaseController {


    /**
     * Device category create request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class DeviceCategoryCreateRequestBody {

        @NotNull
        String categoryNumber;

        @NotNull
        String categoryName;

        @NotNull
        Long parentCategoryId;

        String note;

        SysDeviceCategory convert2SysDeviceCategory() {

            return SysDeviceCategory
                    .builder()
                    .categoryName(this.getCategoryName())
                    .categoryNumber(this.getCategoryNumber())
                    .parentCategoryId(this.getParentCategoryId())
                    .status(SysDeviceCategory.Status.INACTIVE)
                    .note(Optional.of(this.getNote()).orElse(""))
                    .build();

        }

    }

    /**
     * Device Cateogry delete request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class DeviceCateogryDeleteRequestBody {

        @NotNull
        Long categoryId;

    }

    /**
     * Device Category datatable request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class DeviceCategoryGetByFilterAndPageRequestBody {

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        static class Filter {
            String categoryName;
            String status;
            String parentCategoryName;
        }

        @NotNull
        @Min(1)
        int currentPage;

        @NotNull
        int perPage;

        Filter filter;


    }

    /**
     * Device Category modify request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class DeviceCategoryModifyRequestBody {

        @NotNull
        Long categoryId;

        @NotNull
        String categoryNumber;

        @NotNull
        String categoryName;

        @NotNull
        Long parentCategoryId;

        String note;

        SysDeviceCategory convert2SysDeviceCategory(Long createdBy, Date createdTime) {

            return SysDeviceCategory
                    .builder()
                    .categoryId(this.getCategoryId())
                    .categoryName(this.getCategoryName())
                    .categoryNumber(this.getCategoryNumber())
                    .parentCategoryId(this.getParentCategoryId())
                    .status(SysDeviceCategory.Status.INACTIVE)
                    .createdBy(createdBy)
                    .createdTime(createdTime)
                    .note(Optional.of(this.getNote()).orElse(""))
                    .build();

        }

    }

    /**
     * Device Category update status request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class DeviceCategoryUpdateStatusRequestBody {

        @NotNull
        Long categoryId;

        @NotNull
        @Pattern(regexp = SysDeviceCategory.Status.ACTIVE + "|" + SysDeviceCategory.Status.INACTIVE)
        String status;

    }


    /**
     * Device Category create request.
     */
    @RequestMapping(value = "/category/create", method = RequestMethod.POST)
    public Object deviceCategoryCreate(
            @RequestBody @Valid DeviceCategoryCreateRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // Check if parent category is existing.
        if (!sysDeviceCategoryRepository.exists(QSysDeviceCategory.sysDeviceCategory.categoryId
                .eq(requestBody.getParentCategoryId()))) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        SysDeviceCategory sysDeviceCategory = requestBody.convert2SysDeviceCategory();

        // Add createdInfo.
        sysDeviceCategory.addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());

        sysDeviceCategoryRepository.save(sysDeviceCategory);

        return new CommonResponseBody(ResponseMessage.OK);
    }


    /**
     * Device Category modify request.
     */
    @RequestMapping(value = "/category/modify", method = RequestMethod.POST)
    public Object deviceCategoryModify(
            @RequestBody @Valid DeviceCategoryModifyRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        SysDeviceCategory oldSysDeviceCategory = sysDeviceCategoryRepository.findOne(QSysDeviceCategory.sysDeviceCategory
                .categoryId.eq(requestBody.getCategoryId())).orElse(null);

        // Check if category is existing.
        if (oldSysDeviceCategory == null) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // Check if parent category is existing.
        if (!sysDeviceCategoryRepository.exists(QSysDeviceCategory.sysDeviceCategory
                .categoryId.eq(requestBody.getParentCategoryId()))) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        //Don't modify created by and created time
        SysDeviceCategory sysDeviceCategory = requestBody.convert2SysDeviceCategory(oldSysDeviceCategory.getCreatedBy(),
                oldSysDeviceCategory.getCreatedTime());


        // Add edited info.
        sysDeviceCategory.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());

        sysDeviceCategoryRepository.save(sysDeviceCategory);

        return new CommonResponseBody(ResponseMessage.OK);
    }


    /**
     * Device Category delete request.
     */
    @RequestMapping(value = "/category/delete", method = RequestMethod.POST)
    public Object deviceCategoryDelete(
            @RequestBody @Valid DeviceCateogryDeleteRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // Check if category has children.
        boolean isHavingChildren = sysDeviceCategoryRepository.exists(QSysDeviceCategory.sysDeviceCategory
                .parentCategoryId.eq(requestBody.getCategoryId()));
        if (isHavingChildren) {
            // Can't delete if category has children.
            return new CommonResponseBody(ResponseMessage.HAS_CHILDREN);
        }

        /*
        TODO
            Check if device contain this category
        */


        sysDeviceCategoryRepository.delete(SysDeviceCategory.builder().categoryId(requestBody.getCategoryId()).build());

        return new CommonResponseBody(ResponseMessage.OK);
    }




    /**
     * Device Category get all request.
     */
    @RequestMapping(value = "/category/get-all", method = RequestMethod.POST)
    public Object deviceCategoryGetAll() {



        List<SysDeviceCategory> sysDeviceCategoryList = sysDeviceCategoryRepository.findAll();

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, sysDeviceCategoryList));


        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();

        value.setFilters(filters);

        return value;
    }


    /**
     * Device Category datatable data.
     */
    @RequestMapping(value = "/category/get-by-filter-and-page", method = RequestMethod.POST)
    public Object deviceCategoryGetByFilterAndPage(
            @RequestBody @Valid DeviceCategoryGetByFilterAndPageRequestBody requestBody,
            BindingResult bindingResult) {


        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }


        QSysDeviceCategory builder = QSysDeviceCategory.sysDeviceCategory;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        DeviceCategoryGetByFilterAndPageRequestBody.Filter filter = requestBody.getFilter();
        if (filter != null) {
            if (!StringUtils.isEmpty(filter.getCategoryName())) {
                predicate.and(builder.categoryName.contains(filter.getCategoryName()));
            }
            if (!StringUtils.isEmpty(filter.getStatus())) {
                predicate.and(builder.status.contains(filter.getStatus()));
            }
            if (!StringUtils.isEmpty(filter.getParentCategoryName())) {
                predicate.and(builder.parent.categoryName.contains(filter.getParentCategoryName()));
            }
        }

        int currentPage = requestBody.getCurrentPage() - 1; // On server side, page is calculated from 0.
        int perPage = requestBody.getPerPage();

        PageRequest pageRequest = PageRequest.of(currentPage, perPage);

        long total = sysDeviceCategoryRepository.count(predicate);
        List<SysDeviceCategory> data = sysDeviceCategoryRepository.findAll(predicate, pageRequest).getContent();


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
     * Device Category update status request.
     */
    @RequestMapping(value = "/category/update-status", method = RequestMethod.POST)
    public Object deviceCategoryUpdateStatus(
            @RequestBody @Valid DeviceCategoryUpdateStatusRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // Check if category is existing.
        Optional<SysDeviceCategory> optionalSysDeviceCategory = sysDeviceCategoryRepository.findOne(QSysDeviceCategory.sysDeviceCategory
                .categoryId.eq(requestBody.getCategoryId()));
        if (!optionalSysDeviceCategory.isPresent()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        SysDeviceCategory sysDeviceCategory = optionalSysDeviceCategory.get();

        // Update status.
        sysDeviceCategory.setStatus(requestBody.getStatus());

        // Add edited info.
        sysDeviceCategory.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());

        sysDeviceCategoryRepository.save(sysDeviceCategory);

        return new CommonResponseBody(ResponseMessage.OK);
    }


}
