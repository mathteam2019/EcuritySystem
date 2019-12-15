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
import com.nuctech.ecuritycheckitem.enums.Role;
import com.nuctech.ecuritycheckitem.export.devicemanagement.DeviceCategoryExcelView;
import com.nuctech.ecuritycheckitem.export.devicemanagement.DeviceCategoryPdfView;
import com.nuctech.ecuritycheckitem.export.devicemanagement.DeviceCategoryWordView;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.QSerArchiveTemplate;
import com.nuctech.ecuritycheckitem.models.db.QSysDeviceCategory;
import com.nuctech.ecuritycheckitem.models.db.SysDeviceCategory;
import com.nuctech.ecuritycheckitem.models.db.SysUser;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.reusables.FilteringAndPaginationResult;
import com.nuctech.ecuritycheckitem.service.devicemanagement.DeviceCategoryService;
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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/device-management/device-classify")
public class DeviceCategoryManagementController extends BaseController {


    @Autowired
    DeviceCategoryService deviceCategoryService;
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
                    .note(Optional.ofNullable(this.getNote()).orElse(""))
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
     * Device Category  generate request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class DeviceCategoryGenerateRequestBody {


        String idList;
        @NotNull
        Boolean isAll;

        DeviceCategoryGetByFilterAndPageRequestBody.Filter filter;
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

        SysDeviceCategory convert2SysDeviceCategory() {

            return SysDeviceCategory
                    .builder()
                    .categoryId(this.getCategoryId())
                    .categoryName(this.getCategoryName())
                    .categoryNumber(this.getCategoryNumber())
                    .parentCategoryId(this.getParentCategoryId())
                    .status(SysDeviceCategory.Status.INACTIVE)
                    .note(Optional.ofNullable(this.getNote()).orElse(""))
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
    @PreAuthorize(Role.Authority.HAS_DEVICE_CATEGORY_CREATE)
    @RequestMapping(value = "/category/create", method = RequestMethod.POST)
    public Object deviceCategoryCreate(
            @RequestBody @Valid DeviceCategoryCreateRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // Check if parent category is existing.
        if (requestBody.getParentCategoryId() != 0 && !deviceCategoryService.checkCategoryExist(requestBody.getParentCategoryId())) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        SysDeviceCategory sysDeviceCategory = requestBody.convert2SysDeviceCategory();

        deviceCategoryService.createSysDeviceCategory(sysDeviceCategory);

        return new CommonResponseBody(ResponseMessage.OK);
    }


    /**
     * Device Category modify request.
     */
    @PreAuthorize(Role.Authority.HAS_DEVICE_CATEGORY_MODIFY)
    @RequestMapping(value = "/category/modify", method = RequestMethod.POST)
    public Object deviceCategoryModify(
            @RequestBody @Valid DeviceCategoryModifyRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }



        // Check if category is existing.
        if (!deviceCategoryService.checkCategoryExist(requestBody.getCategoryId())) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // Check if parent category is existing.
        if (!deviceCategoryService.checkCategoryExist(requestBody.getParentCategoryId())) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        //Check if archive template contain this category

        if(deviceCategoryService.checkArchiveTemplateExist(requestBody.getCategoryId())) {
            return new CommonResponseBody(ResponseMessage.HAS_ARCHIVE_TEMPLATE);
        }

        SysDeviceCategory sysDeviceCategory = requestBody.convert2SysDeviceCategory();

        deviceCategoryService.modifySysDeviceCategory(sysDeviceCategory);

        return new CommonResponseBody(ResponseMessage.OK);
    }


    /**
     * Device Category delete request.
     */
    @PreAuthorize(Role.Authority.HAS_DEVICE_CATEGORY_DELETE)
    @RequestMapping(value = "/category/delete", method = RequestMethod.POST)
    public Object deviceCategoryDelete(
            @RequestBody @Valid DeviceCateogryDeleteRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // Check if category has children.

        if (deviceCategoryService.checkChildernCategoryExist(requestBody.getCategoryId())) {
            return new CommonResponseBody(ResponseMessage.HAS_CHILDREN);
        }


        //Check if archive template contain this category
        if(deviceCategoryService.checkArchiveTemplateExist(requestBody.getCategoryId())) {
            return new CommonResponseBody(ResponseMessage.HAS_ARCHIVE_TEMPLATE);
        }

        deviceCategoryService.removeSysDeviceCategory(requestBody.getCategoryId());

        return new CommonResponseBody(ResponseMessage.OK);
    }




    /**
     * Device Category get all request.
     */
    @RequestMapping(value = "/category/get-all", method = RequestMethod.POST)
    public Object deviceCategoryGetAll() {



        List<SysDeviceCategory> sysDeviceCategoryList = sysDeviceCategoryRepository.findAll();

        //set parent's  parent to null so prevent recursion
        for(int i = 0; i < sysDeviceCategoryList.size(); i ++) {
            SysDeviceCategory deviceCategory = sysDeviceCategoryList.get(i);
            if(deviceCategory.getParent() != null) {
                deviceCategory.getParent().setParent(null);
            }
        }

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

        String categoryName = "";
        String status = "";
        String parentCategoryName = "";
        if(requestBody.getFilter() != null) {
            categoryName = requestBody.getFilter().getCategoryName();
            status = requestBody.getFilter().getStatus();
            parentCategoryName = requestBody.getFilter().getParentCategoryName();
        }
        int currentPage = requestBody.getCurrentPage();
        int perPage = requestBody.getPerPage();
        currentPage --;
        PageResult<SysDeviceCategory> result = deviceCategoryService.getDeviceCategoryListByPage(categoryName, status, parentCategoryName,
                currentPage, perPage);
        long total = result.getTotal();
        List<SysDeviceCategory> data = result.getDataList();

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
     * Device Category generate file request.
     */
    @PreAuthorize(Role.Authority.HAS_DEVICE_CATEGORY_EXPORT)
    @RequestMapping(value = "/category/export", method = RequestMethod.POST)
    public Object deviceCategoryGenerateExcelFile(@RequestBody @Valid DeviceCategoryGenerateRequestBody requestBody,
                                              BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        String categoryName = "";
        String status = "";
        String parentCategoryName = "";
        if(requestBody.getFilter() != null) {
            categoryName = requestBody.getFilter().getCategoryName();
            status = requestBody.getFilter().getStatus();
            parentCategoryName = requestBody.getFilter().getParentCategoryName();
        }

        List<SysDeviceCategory> exportList = deviceCategoryService.getExportListByFilter(categoryName, status, parentCategoryName,
                requestBody.getIsAll(), requestBody.getIdList());

        setDictionary();
        InputStream inputStream = DeviceCategoryExcelView.buildExcelDocument(exportList);



        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=device-category.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msexcel"))
                .body(new InputStreamResource(inputStream));

    }

    /**
     * Device Category generate file request.
     */
    @PreAuthorize(Role.Authority.HAS_DEVICE_CATEGORY_TOWORD)
    @RequestMapping(value = "/category/word", method = RequestMethod.POST)
    public Object deviceCategoryGenerateWordFile(@RequestBody @Valid DeviceCategoryGenerateRequestBody requestBody,
                                                  BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        String categoryName = "";
        String status = "";
        String parentCategoryName = "";
        if(requestBody.getFilter() != null) {
            categoryName = requestBody.getFilter().getCategoryName();
            status = requestBody.getFilter().getStatus();
            parentCategoryName = requestBody.getFilter().getParentCategoryName();
        }

        List<SysDeviceCategory> exportList = deviceCategoryService.getExportListByFilter(categoryName, status, parentCategoryName,
                requestBody.getIsAll(), requestBody.getIdList());

        setDictionary();
        InputStream inputStream = DeviceCategoryWordView.buildWordDocument(exportList);



        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=device-category.docx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msword"))
                .body(new InputStreamResource(inputStream));

    }

    /**
     * Device Category generate pdf file request.
     */
    @PreAuthorize(Role.Authority.HAS_DEVICE_CATEGORY_PRINT)
    @RequestMapping(value = "/category/print", method = RequestMethod.POST)
    public Object deviceCategoryGeneratePDFFile(@RequestBody @Valid DeviceCategoryGenerateRequestBody requestBody,
                                             BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        String categoryName = "";
        String status = "";
        String parentCategoryName = "";
        if(requestBody.getFilter() != null) {
            categoryName = requestBody.getFilter().getCategoryName();
            status = requestBody.getFilter().getStatus();
            parentCategoryName = requestBody.getFilter().getParentCategoryName();
        }

        List<SysDeviceCategory> exportList = deviceCategoryService.getExportListByFilter(categoryName, status, parentCategoryName,
                requestBody.getIsAll(), requestBody.getIdList());

        DeviceCategoryPdfView.setResource(res);
        setDictionary();
        InputStream inputStream = DeviceCategoryPdfView.buildPDFDocument(exportList);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=device-category.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));

    }

    /**
     * Device Category update status request.
     */
    @PreAuthorize(Role.Authority.HAS_DEVICE_CATEGORY_UPDATE_STATUS)
    @RequestMapping(value = "/category/update-status", method = RequestMethod.POST)
    public Object deviceCategoryUpdateStatus(
            @RequestBody @Valid DeviceCategoryUpdateStatusRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // Check if category is existing.

        if (!deviceCategoryService.checkCategoryExist(requestBody.getCategoryId())) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        deviceCategoryService.updateStatus(requestBody.getCategoryId(), requestBody.getStatus());

        return new CommonResponseBody(ResponseMessage.OK);
    }


}
