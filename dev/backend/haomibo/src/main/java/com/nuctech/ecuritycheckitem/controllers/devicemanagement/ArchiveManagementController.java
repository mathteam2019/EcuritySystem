/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/19
 * @CreatedBy Choe.
 * @FileName ArchiveManagementController.java
 * @ModifyHistory
 */
package com.nuctech.ecuritycheckitem.controllers.devicemanagement;


import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.enums.Role;
import com.nuctech.ecuritycheckitem.export.devicemanagement.DeviceArchiveExcelView;
import com.nuctech.ecuritycheckitem.export.devicemanagement.DeviceArchivePdfView;
import com.nuctech.ecuritycheckitem.export.devicemanagement.DeviceArchiveWordView;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.*;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.reusables.FilteringAndPaginationResult;
import com.nuctech.ecuritycheckitem.service.devicemanagement.ArchiveService;
import com.nuctech.ecuritycheckitem.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ModelAttribute;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/device-management/document-management")
public class ArchiveManagementController extends BaseController {

    @Autowired
    ArchiveService archiveService;

    /**
     * Archive datatable request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class ArchiveGetByFilterAndPageRequestBody {

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        static class Filter {
            String archivesName;
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
     * Archive update status request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class ArchiveUpdateStatusRequestBody {

        @NotNull
        Long archiveId;

        @NotNull
        @Pattern(regexp = SerArchive.Status.ACTIVE + "|" + SerArchive.Status.INACTIVE)
        String status;

    }


    /**
     * Archive create request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class ArchiveCreateRequestBody {

        @NotNull
        Long archivesTemplateId;

        @NotNull
        String archivesName;

        @NotNull
        String archivesNumber;

//        @NotNull
//        Long categoryId;
//
//        String manufacturer;
//
//        String originalModel;

        String note;

        private MultipartFile imageUrl;

        String json;



        SerArchive convert2SerArchive() {

            return SerArchive
                    .builder()
                    .archivesTemplateId(this.getArchivesTemplateId())
                    .archivesName(this.getArchivesName())
                    .archivesNumber(this.getArchivesNumber())
//                    .categoryId(this.getCategoryId())
//                    .manufacturer(Optional.of(this.getManufacturer()).orElse(""))
//                    .originalModel(Optional.of(this.getOriginalModel()).orElse(""))
                    .status(SerArchive.Status.INACTIVE)
                    .note(Optional.ofNullable(this.getNote()).orElse(""))
                    .build();

        }

    }

    /**
     * Archive create request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class ArchiveModifyRequestBody {

        @NotNull
        Long archiveId;

        @NotNull
        Long archivesTemplateId;

        @NotNull
        String archivesName;

        @NotNull
        String archivesNumber;

//        @NotNull
//        Long categoryId;
//
//        String manufacturer;
//
//        String originalModel;

        String note;

        private MultipartFile imageUrl;

        String json;

        SerArchive convert2SerArchive() {

            return SerArchive
                    .builder()
                    .archiveId(this.getArchiveId())
                    .archivesTemplateId(this.getArchivesTemplateId())
                    .archivesName(this.getArchivesName())
                    .archivesNumber(this.getArchivesNumber())
//                    .categoryId(this.getCategoryId())
//                    .manufacturer(Optional.of(this.getManufacturer()).orElse(""))
//                    .originalModel(Optional.of(this.getOriginalModel()).orElse(""))
                    .status(SerArchive.Status.INACTIVE)
                    .note(Optional.ofNullable(this.getNote()).orElse(""))
                    .build();
        }

    }

    /**
     * Archive delete request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class ArchiveDeleteRequestBody {

        @NotNull
        Long archiveId;
    }


    /**
     * Archive  generate request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class ArchiveGenerateRequestBody {


        String idList;
        @NotNull
        Boolean isAll;

        ArchiveGetByFilterAndPageRequestBody.Filter filter;
    }



    /**
     * Archive datatable data.
     */
    @RequestMapping(value = "/archive/get-by-filter-and-page", method = RequestMethod.POST)
    public Object archiveGetByFilterAndPage(
            @RequestBody @Valid ArchiveGetByFilterAndPageRequestBody requestBody,
            BindingResult bindingResult) {


        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        String archiveName = "";
        String status = "";
        Long categoryId = null;
        if(requestBody.getFilter() != null) {
            archiveName = requestBody.getFilter().getArchivesName();
            status = requestBody.getFilter().getStatus();
            categoryId = requestBody.getFilter().getCategoryId();
        }

        int currentPage = requestBody.getCurrentPage();
        int perPage = requestBody.getPerPage();
        currentPage --;
        PageResult<SerArchive> result = archiveService.getArchiveListByPage(archiveName, status, categoryId,
                currentPage, perPage);
        long total = result.getTotal();
        List<SerArchive> data = result.getDataList();

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
                .addFilter(ModelJsonFilters.FILTER_SYS_DEVICE_CATEGORY, SimpleBeanPropertyFilter.serializeAllExcept("parent"));;

        value.setFilters(filters);

        return value;
    }

    /**
     * Archive update status request.
     */
    @PreAuthorize(Role.Authority.HAS_DEVICE_ARCHIVE_UPDATE_STATUS)
    @RequestMapping(value = "/archive/update-status", method = RequestMethod.POST)
    public Object archiveUpdateStatus(
            @RequestBody @Valid ArchiveUpdateStatusRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // Check if archive is existing.
        if (!archiveService.checkArchiveExist(requestBody.getArchiveId())) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        archiveService.updateStatus(requestBody.getArchiveId(), requestBody.getStatus());


        return new CommonResponseBody(ResponseMessage.OK);
    }

    /**
     * Archive create request.
     */
    @PreAuthorize(Role.Authority.HAS_DEVICE_ARCHIVE_CREATE)
    @RequestMapping(value = "/archive/create", method = RequestMethod.POST)
    public Object archiveCreate(
            @ModelAttribute @Valid ArchiveCreateRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }


        // Check if template is valid
        if (!archiveService.checkArchiveTemplateExist(requestBody.getArchivesTemplateId())) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }


        SerArchive serArchive = requestBody.convert2SerArchive();

        archiveService.createSerArchive(requestBody.getImageUrl(), serArchive, requestBody.getJson());

        return new CommonResponseBody(ResponseMessage.OK);
    }


    /**
     * Archive modify request.
     */
    @PreAuthorize(Role.Authority.HAS_DEVICE_ARCHIVE_MODIFY)
    @RequestMapping(value = "/archive/modify", method = RequestMethod.POST)
    public Object archiveModify(
            @ModelAttribute @Valid ArchiveModifyRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        //check archive is valid
        if(!archiveService.checkArchiveExist(requestBody.getArchiveId())) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // Check if template is valid
        if (!archiveService.checkArchiveTemplateExist(requestBody.getArchivesTemplateId())) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        SerArchive serArchive = requestBody.convert2SerArchive();

        if(archiveService.checkDeviceExist(requestBody.getArchiveId())) {
            return new CommonResponseBody(ResponseMessage.HAS_DEVICES);
        }
        archiveService.modifySerArchive(requestBody.getImageUrl(), serArchive, requestBody.getJson());

        return new CommonResponseBody(ResponseMessage.OK);
    }

    /**
     * Archive delete request.
     */
    @PreAuthorize(Role.Authority.HAS_DEVICE_ARCHIVE_DELETE)
    @RequestMapping(value = "/archive/delete", method = RequestMethod.POST)
    public Object archiveDelete(
            @RequestBody @Valid ArchiveDeleteRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        //check archive exist or not
        if(!archiveService.checkArchiveExist(requestBody.getArchiveId())) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        //check used device
        if(archiveService.checkDeviceExist(requestBody.getArchiveId())) {
            return new CommonResponseBody(ResponseMessage.HAS_DEVICES);
        }

        archiveService.removeSerArchive(requestBody.getArchiveId());


        return new CommonResponseBody(ResponseMessage.OK);
    }



    /**
     * Archive  get all request.
     */
    @RequestMapping(value = "/archive/get-all", method = RequestMethod.POST)
    public Object archiveGetAll() {


        List<SerArchive> serArchiveList = archiveService.findAll();

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, serArchiveList));

        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();
        filters.addFilter(ModelJsonFilters.FILTER_SYS_DEVICE_CATEGORY, SimpleBeanPropertyFilter.serializeAllExcept("parent"));

        value.setFilters(filters);

        return value;
    }


    /**
     * Archive generate file request.
     */
    @PreAuthorize(Role.Authority.HAS_DEVICE_ARCHIVE_EXPORT)
    @RequestMapping(value = "/archive/export", method = RequestMethod.POST)
    public Object archiveGenerateExcelFile(@RequestBody @Valid ArchiveGenerateRequestBody requestBody,
                                                    BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        String archiveName = "";
        String status = "";
        Long categoryId = null;
        if(requestBody.getFilter() != null) {
            archiveName = requestBody.getFilter().getArchivesName();
            status = requestBody.getFilter().getStatus();
            categoryId = requestBody.getFilter().getCategoryId();
        }

        List<SerArchive> exportList = archiveService.getExportListByFilter(archiveName, status, categoryId, requestBody.getIsAll(), requestBody.getIdList());
        setDictionary();

        InputStream inputStream = DeviceArchiveExcelView.buildExcelDocument(exportList);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=archive.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msexcel"))
                .body(new InputStreamResource(inputStream));

    }

    /**
     * Archive generate file request.
     */
    @PreAuthorize(Role.Authority.HAS_DEVICE_ARCHIVE_TOWORD)
    @RequestMapping(value = "/archive/word", method = RequestMethod.POST)
    public Object archiveGenerateWordFile(@RequestBody @Valid ArchiveGenerateRequestBody requestBody,
                                           BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        String archiveName = "";
        String status = "";
        Long categoryId = null;
        if(requestBody.getFilter() != null) {
            archiveName = requestBody.getFilter().getArchivesName();
            status = requestBody.getFilter().getStatus();
            categoryId = requestBody.getFilter().getCategoryId();
        }

        List<SerArchive> exportList = archiveService.getExportListByFilter(archiveName, status, categoryId, requestBody.getIsAll(), requestBody.getIdList());
        setDictionary();
        InputStream inputStream = DeviceArchiveWordView.buildWordDocument(exportList);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=archive.docx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msword"))
                .body(new InputStreamResource(inputStream));

    }


    /**
     * Archive generate file request.
     */
    @PreAuthorize(Role.Authority.HAS_DEVICE_ARCHIVE_PRINT)
    @RequestMapping(value = "/archive/print", method = RequestMethod.POST)
    public Object archiveGeneratePDFFile(@RequestBody @Valid ArchiveGenerateRequestBody requestBody,
                                           BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        String archiveName = "";
        String status = "";
        Long categoryId = null;
        if(requestBody.getFilter() != null) {
            archiveName = requestBody.getFilter().getArchivesName();
            status = requestBody.getFilter().getStatus();
            categoryId = requestBody.getFilter().getCategoryId();
        }

        List<SerArchive> exportList = archiveService.getExportListByFilter(archiveName, status, categoryId, requestBody.getIsAll(), requestBody.getIdList());
        DeviceArchivePdfView.setResource(res);
        setDictionary();
        InputStream inputStream = DeviceArchivePdfView.buildPDFDocument(exportList);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=archive.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));

    }
}
