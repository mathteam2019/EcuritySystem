/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/20
 * @CreatedBy Choe.
 * @FileName DeviceControlController.java
 * @ModifyHistory
 */

package com.nuctech.ecuritycheckitem.controllers.devicemanagement;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.enums.Role;
import com.nuctech.ecuritycheckitem.export.devicemanagement.DeviceExcelView;
import com.nuctech.ecuritycheckitem.export.devicemanagement.DeviceFieldExcelView;
import com.nuctech.ecuritycheckitem.export.devicemanagement.DeviceFieldPdfView;
import com.nuctech.ecuritycheckitem.export.devicemanagement.DevicePdfView;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.*;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.reusables.FilteringAndPaginationResult;
import com.nuctech.ecuritycheckitem.service.devicemanagement.DeviceService;
import com.nuctech.ecuritycheckitem.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/device-management/device-table")
public class DeviceControlController extends BaseController {

    @Autowired
    DeviceService deviceService;

    /**
     * Device datatable request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class DeviceGetByFilterAndPageRequestBody {

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Filter {
            String archivesName;
            String status;
            Long categoryId;
            String deviceName;
            Long fieldId;
        }

        @NotNull
        @Min(1)
        int currentPage;

        @NotNull
        int perPage;

        Filter filter;
    }

    /**
     * Device  generate request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class DeviceGenerateRequestBody {

        String idList;
        @NotNull
        Boolean isAll;

        DeviceGetByFilterAndPageRequestBody.Filter filter;
    }

    /**
     * Device update status request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class DeviceUpdateStatusRequestBody {

        @NotNull
        Long deviceId;

        @NotNull
        @Pattern(regexp = SysDevice.Status.ACTIVE + "|" + SysDevice.Status.INACTIVE)
        String status;

    }

    /**
     * Device create request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class DeviceCreateRequestBody {



        @NotNull
        String deviceName;

        @NotNull
        String deviceType;

        @NotNull
        Long archiveId;

        @NotNull
        String deviceSerial;

//        Long categoryId;
//
//        String manufacturer;
//
//        String originalModel;

        String originalFactoryNumber;

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        Date manufacturerDate;

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        Date purchaseDate;

        String supplier;

        String contacts;

        String mobile;

        String deviceIp;

        String guid;



        String note;

        private MultipartFile imageUrl;


        SysDevice convert2SysDevice() {

            return SysDevice
                    .builder()
                    .archiveId(this.getArchiveId())
                    .deviceName(this.getDeviceName())
                    .deviceType(this.getDeviceType())
                    .deviceSerial(this.getDeviceSerial())
                    .originalFactoryNumber(Optional.ofNullable(this.getOriginalFactoryNumber()).orElse(""))
                    .manufacturerDate(this.getManufacturerDate())
                    .purchaseDate(this.getPurchaseDate())
                    .supplier(Optional.ofNullable(this.getSupplier()).orElse(""))
                    .contacts(Optional.ofNullable(this.getContacts()).orElse(""))
                    .mobile(Optional.ofNullable(this.getMobile()).orElse(""))
                    .deviceIp(Optional.ofNullable(this.getDeviceIp()).orElse(""))
                    .guid(this.getGuid())
                    //.manufacturer(Optional.of(this.getManufacturer()).orElse(""))
                    //.originalModel(Optional.of(this.getOriginalModel()).orElse(""))
                    .status(SysDevice.Status.INACTIVE)
                    .note(Optional.ofNullable(this.getNote()).orElse(""))
                    .build();

        }

    }

    /**
     * Device config modify request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class DeviceModifyRequestBody {

        @NotNull
        Long deviceId;

        @NotNull
        String deviceName;

        @NotNull
        String deviceType;

        @NotNull
        Long archiveId;

        @NotNull
        String deviceSerial;

//        Long categoryId;
//
//        String manufacturer;
//
//        String originalModel;

        String originalFactoryNumber;

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        Date manufacturerDate;

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        Date purchaseDate;

        String supplier;

        String contacts;

        String mobile;

        String deviceIp;

        String guid;



        String note;

        private MultipartFile imageUrl;


        SysDevice convert2SysDevice() {

            return SysDevice
                    .builder()
                    .deviceId(this.getDeviceId())
                    .archiveId(this.getArchiveId())
                    .deviceName(this.getDeviceName())
                    .deviceType(this.getDeviceType())
                    .deviceSerial(this.getDeviceSerial())
                    .originalFactoryNumber(Optional.ofNullable(this.getOriginalFactoryNumber()).orElse(""))
                    .manufacturerDate(this.getManufacturerDate())
                    .purchaseDate(this.getPurchaseDate())
                    .supplier(Optional.ofNullable(this.getSupplier()).orElse(""))
                    .contacts(Optional.ofNullable(this.getContacts()).orElse(""))
                    .mobile(Optional.ofNullable(this.getMobile()).orElse(""))
                    .deviceIp(Optional.ofNullable(this.getDeviceIp()).orElse(""))
                    .guid(this.getGuid())
//                    .manufacturer(Optional.of(this.getManufacturer()).orElse(""))
//                    .originalModel(Optional.of(this.getOriginalModel()).orElse(""))
                    .status(SysDevice.Status.INACTIVE)
                    .note(Optional.ofNullable(this.getNote()).orElse(""))
                    .build();

        }

    }

    /**
     * Device delete request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class DeviceDeleteRequestBody {

        @NotNull
        Long deviceId;
    }

    /**
     * Device field modify request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class DeviceFieldModifyRequestBody {

        List<SysDevice> deviceList;
    }

    /**
     * Organization get all request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class DeviceGetAllRequestBody {

        static class GetAllType {
            static final String BARE = "bare";
            static final String WITH_CONFIG = "with_config";
            static final String WITH_SCAN = "with_scan";
        }

        @Pattern(regexp = GetAllType.BARE + "|" +
                GetAllType.WITH_CONFIG + "|" +
                GetAllType.WITH_SCAN)
        String type = GetAllType.BARE;
    }

    /**
     * Empty field device list request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class DeviceGetEmptyFieldRequestBody {
        Long categoryId;
    }

    /**
     * Empty field device list request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class DeviceFilterByFieldRequestBody {
        Long categoryId;
        Long fieldId;
    }

    /**
     * Device datatable data.
     */
    @RequestMapping(value = "/device/get-by-filter-and-page", method = RequestMethod.POST)
    public Object deviceGetByFilterAndPage(
            @RequestBody @Valid DeviceGetByFilterAndPageRequestBody requestBody,
            BindingResult bindingResult) {


        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        DeviceGetByFilterAndPageRequestBody.Filter filter = requestBody.getFilter();
        int currentPage = requestBody.getCurrentPage() - 1; // On server side, page is calculated from 0.
        int perPage = requestBody.getPerPage();

        int startIndex = perPage * currentPage;
        int endIndex = perPage * (currentPage + 1);

        String archiveName = "";
        String deviceName = "";
        String status = "";
        Long fieldId = null;
        Long categoryId = null;
        if(filter != null) {
            archiveName = filter.getArchivesName();
            deviceName = filter.getDeviceName();
            status = filter.getStatus();
            fieldId = filter.getFieldId();
            categoryId = filter.getCategoryId();
        }
        PageResult<SysDevice> result = deviceService.getFilterDeviceList(archiveName, deviceName, status, fieldId, categoryId, startIndex, endIndex);
        List<SysDevice> data = result.getDataList();
        long total = result.getTotal();

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
                .addFilter(ModelJsonFilters.FILTER_SYS_DEVICE, SimpleBeanPropertyFilter.serializeAllExcept("deviceConfig", "scanParam"))
                .addFilter(ModelJsonFilters.FILTER_SYS_DEVICE_CATEGORY, SimpleBeanPropertyFilter.serializeAllExcept("parent"));


        value.setFilters(filters);

        return value;
    }



    /**
     * Device generate excel file request.
     */
    @PreAuthorize(Role.Authority.HAS_DEVICE_EXPORT)
    @RequestMapping(value = "/device/export", method = RequestMethod.POST)
    public Object deviceGenerateExcelFile(@RequestBody @Valid DeviceGenerateRequestBody requestBody,
                                             BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        String archiveName = "";
        String deviceName = "";
        String status = "";
        Long fieldId = null;
        Long categoryId = null;
        DeviceGetByFilterAndPageRequestBody.Filter filter = requestBody.getFilter();
        if(filter != null) {
            archiveName = filter.getArchivesName();
            deviceName = filter.getDeviceName();
            status = filter.getStatus();
            fieldId = filter.getFieldId();
            categoryId = filter.getCategoryId();
        }

        List<SysDevice> exportList = deviceService.getExportDataList(archiveName, deviceName, status, fieldId, categoryId,
                requestBody.getIsAll(), requestBody.getIdList());
        InputStream inputStream = DeviceExcelView.buildExcelDocument(exportList);



        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=device.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msexcel"))
                .body(new InputStreamResource(inputStream));

    }

    /**
     * Device generate pdf file request.
     */
    @PreAuthorize(Role.Authority.HAS_DEVICE_PRINT)
    @RequestMapping(value = "/device/print", method = RequestMethod.POST)
    public Object deviceGeneratePDFFile(@RequestBody @Valid DeviceGenerateRequestBody requestBody,
                                     BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        String archiveName = "";
        String deviceName = "";
        String status = "";
        Long fieldId = null;
        Long categoryId = null;
        DeviceGetByFilterAndPageRequestBody.Filter filter = requestBody.getFilter();
        if(filter != null) {
            archiveName = filter.getArchivesName();
            deviceName = filter.getDeviceName();
            status = filter.getStatus();
            fieldId = filter.getFieldId();
            categoryId = filter.getCategoryId();
        }

        List<SysDevice> exportList = deviceService.getExportDataList(archiveName, deviceName, status, fieldId, categoryId,
                requestBody.getIsAll(), requestBody.getIdList());

        DevicePdfView.setResource(res);
        InputStream inputStream = DevicePdfView.buildPDFDocument(exportList);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=device.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));

    }


    /**
     * Device generate excel file request.
     */
    @PreAuthorize(Role.Authority.HAS_DEVICE_FIELD_EXPORT)
    @RequestMapping(value = "/device/field/export", method = RequestMethod.POST)
    public Object deviceFieldGenerateExcelFile(@RequestBody @Valid DeviceGenerateRequestBody requestBody,
                                          BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        String archiveName = "";
        String deviceName = "";
        String status = "";
        Long fieldId = null;
        Long categoryId = null;
        DeviceGetByFilterAndPageRequestBody.Filter filter = requestBody.getFilter();
        if(filter != null) {
            archiveName = filter.getArchivesName();
            deviceName = filter.getDeviceName();
            status = filter.getStatus();
            fieldId = filter.getFieldId();
            categoryId = filter.getCategoryId();
        }

        List<SysDevice> exportList = deviceService.getExportDataList(archiveName, deviceName, status, fieldId, categoryId,
                requestBody.getIsAll(), requestBody.getIdList());

        InputStream inputStream = DeviceFieldExcelView.buildExcelDocument(exportList);



        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=device-field.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/x-msexcel"))
                .body(new InputStreamResource(inputStream));

    }

    /**
     * Device generate pdf file request.
     */
    @PreAuthorize(Role.Authority.HAS_DEVICE_FIELD_PRINT)
    @RequestMapping(value = "/device/field/print", method = RequestMethod.POST)
    public Object deviceFieldGeneratePDFFile(@RequestBody @Valid DeviceGenerateRequestBody requestBody,
                                        BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        String archiveName = "";
        String deviceName = "";
        String status = "";
        Long fieldId = null;
        Long categoryId = null;
        DeviceGetByFilterAndPageRequestBody.Filter filter = requestBody.getFilter();
        if(filter != null) {
            archiveName = filter.getArchivesName();
            deviceName = filter.getDeviceName();
            status = filter.getStatus();
            fieldId = filter.getFieldId();
            categoryId = filter.getCategoryId();
        }

        List<SysDevice> exportList = deviceService.getExportDataList(archiveName, deviceName, status, fieldId, categoryId,
                requestBody.getIsAll(), requestBody.getIdList());

        DeviceFieldPdfView.setResource(res);
        InputStream inputStream = DeviceFieldPdfView.buildPDFDocument(exportList);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=device-field.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));

    }

    /**
     * Device update status request.
     */
    @PreAuthorize(Role.Authority.HAS_DEVICE_UPDATE_STATUS)
    @RequestMapping(value = "/device/update-status", method = RequestMethod.POST)
    public Object deviceUpdateStatus(
            @RequestBody @Valid DeviceUpdateStatusRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // Check if device is existing.

        if (!deviceService.checkDeviceExist(requestBody.getDeviceId())) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        deviceService.updateStatus(requestBody.getDeviceId(), requestBody.getStatus());

        return new CommonResponseBody(ResponseMessage.OK);
    }

    /**
     * Device create request.
     */
    @PreAuthorize(Role.Authority.HAS_DEVICE_CREATE)
    @RequestMapping(value = "/device/create", method = RequestMethod.POST)
    public Object deviceCreate(
            @ModelAttribute @Valid DeviceCreateRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // Check if archive is valid.
        if (!deviceService.checkArchiveExist(requestBody.getArchiveId())) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        SysDevice sysDevice = requestBody.convert2SysDevice();
        deviceService.createDevice(sysDevice, requestBody.getImageUrl());
        return new CommonResponseBody(ResponseMessage.OK);
    }


    /**
     * Device modify request.
     */
    @PreAuthorize(Role.Authority.HAS_DEVICE_MODIFY)
    @RequestMapping(value = "/device/modify", method = RequestMethod.POST)
    public Object deviceModify(
            @ModelAttribute @Valid DeviceModifyRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // Check if archive is valid.
        if (!deviceService.checkArchiveExist(requestBody.getArchiveId())) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // Check if device is valid.
        if (!deviceService.checkDeviceExist(requestBody.getDeviceId())) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        SysDevice sysDevice = requestBody.convert2SysDevice();

        deviceService.modifyDevice(sysDevice, requestBody.getImageUrl());

        return new CommonResponseBody(ResponseMessage.OK);
    }

    /**
     * Device delete request.
     */
    @PreAuthorize(Role.Authority.HAS_DEVICE_DELETE)
    @RequestMapping(value = "/device/delete", method = RequestMethod.POST)
    public Object deviceDelete(
            @RequestBody @Valid DeviceDeleteRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        //check device exist or not
        if(deviceService.checkDeviceExist(requestBody.getDeviceId())) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        deviceService.removeDevice(requestBody.getDeviceId());
        return new CommonResponseBody(ResponseMessage.OK);
    }

    /**
     * Device field modify request.
     */
    @PreAuthorize(Role.Authority.HAS_DEVICE_FIELD_MODIFY)
    @RequestMapping(value = "/device/field-modify", method = RequestMethod.POST)
    public Object deviceFieldModify(
            @RequestBody @Valid DeviceFieldModifyRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        List<SysDevice> deviceList = requestBody.getDeviceList();
        deviceService.modifyDeviceField(deviceList);

        return new CommonResponseBody(ResponseMessage.OK);
    }

    /**
     * Device  get all request.
     * BARE, WITH_CONFIG, WITH_SCAN
     */
    @RequestMapping(value = "/device/get-all", method = RequestMethod.POST)
    public Object deviceGetAll(@RequestBody @Valid DeviceGetAllRequestBody requestBody,
                               BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        List<SysDevice> sysDeviceList = deviceService.findAll();

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, sysDeviceList));

        String type = requestBody.getType();
        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();


        switch (type) {

            case DeviceGetAllRequestBody.GetAllType.BARE:
                filters.addFilter(ModelJsonFilters.FILTER_SYS_DEVICE, SimpleBeanPropertyFilter.serializeAllExcept("deviceConfig", "scanParam"))
                        .addFilter(ModelJsonFilters.FILTER_SYS_DEVICE_CATEGORY, SimpleBeanPropertyFilter.serializeAllExcept("parent"));
                break;
            case DeviceGetAllRequestBody.GetAllType.WITH_CONFIG:
                filters.addFilter(ModelJsonFilters.FILTER_SYS_DEVICE, SimpleBeanPropertyFilter.serializeAllExcept("scanParam"))
                        .addFilter(ModelJsonFilters.FILTER_SYS_DEVICE_CONFIG, SimpleBeanPropertyFilter.serializeAllExcept("device"))
                        .addFilter(ModelJsonFilters.FILTER_SYS_DEVICE_CATEGORY, SimpleBeanPropertyFilter.serializeAllExcept("parent"));
                break;
            case DeviceGetAllRequestBody.GetAllType.WITH_SCAN:
                filters.addFilter(ModelJsonFilters.FILTER_SYS_DEVICE, SimpleBeanPropertyFilter.serializeAllExcept("deviceConfig"))
                        .addFilter(ModelJsonFilters.FILTER_SER_SCAN_PARAM, SimpleBeanPropertyFilter.serializeAllExcept("device"))
                        .addFilter(ModelJsonFilters.FILTER_SER_SCAN_PARAMS_FROM, SimpleBeanPropertyFilter.serializeAllExcept("device"))
                        .addFilter(ModelJsonFilters.FILTER_SYS_DEVICE_CATEGORY, SimpleBeanPropertyFilter.serializeAllExcept("parent"));
                break;


            default:

                break;
        }

        value.setFilters(filters);

        return value;
    }

    /**
     * Get empty field device request.
     */
    @RequestMapping(value = "/device/get-empty-field", method = RequestMethod.POST)
    public Object deviceGetEmptyField(@RequestBody @Valid DeviceGetEmptyFieldRequestBody requestBody,
                                      BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }


        List<SysDevice> sysDeviceList = deviceService.getEmptyFieldDevice(requestBody.getCategoryId());

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, sysDeviceList));

        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();

        filters.addFilter(ModelJsonFilters.FILTER_SYS_DEVICE_CATEGORY, SimpleBeanPropertyFilter.serializeAllExcept("parent"));
        filters.addFilter(ModelJsonFilters.FILTER_SYS_DEVICE, SimpleBeanPropertyFilter.serializeAllExcept("deviceConfig", "scanParam"))
                .addFilter(ModelJsonFilters.FILTER_SYS_DEVICE_CATEGORY, SimpleBeanPropertyFilter.serializeAllExcept("parent"));


        value.setFilters(filters);

        return value;
    }

    /**
     * Get device list with field request.
     */
    @RequestMapping(value = "/device/get-by-field", method = RequestMethod.POST)
    public Object deviceGetByField(@RequestBody @Valid DeviceFilterByFieldRequestBody requestBody,
                                      BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        List<SysDevice> sysDeviceList = deviceService.getDeviceByField(requestBody.getFieldId(), requestBody.getCategoryId());

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, sysDeviceList));

        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();

        filters.addFilter(ModelJsonFilters.FILTER_SYS_DEVICE_CATEGORY, SimpleBeanPropertyFilter.serializeAllExcept("parent"));
        filters.addFilter(ModelJsonFilters.FILTER_SYS_DEVICE, SimpleBeanPropertyFilter.serializeAllExcept("deviceConfig", "scanParam"))
                .addFilter(ModelJsonFilters.FILTER_SYS_DEVICE_CATEGORY, SimpleBeanPropertyFilter.serializeAllExcept("parent"));

        value.setFilters(filters);

        return value;
    }

}
