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
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.*;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.reusables.FilteringAndPaginationResult;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.querydsl.core.BooleanBuilder;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/device-management/device-table")
public class DeviceControlController extends BaseController {

    /**
     * Device datatable request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class DeviceGetByFilterAndPageRequestBody {

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        static class Filter {
            String archivesName;
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

        String registrationNumber;



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
                    .registrationNumber(Optional.ofNullable(this.getRegistrationNumber()).orElse(""))
                    //.categoryId(this.getCategoryId())
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

        String registrationNumber;



        String note;

        private MultipartFile imageUrl;


        SysDevice convert2SysDevice(Long createdBy, Date createdTime) {

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
                    .registrationNumber(Optional.ofNullable(this.getRegistrationNumber()).orElse(""))
//                    .categoryId(this.getCategoryId())
//                    .manufacturer(Optional.of(this.getManufacturer()).orElse(""))
//                    .originalModel(Optional.of(this.getOriginalModel()).orElse(""))
                    .status(SysDevice.Status.INACTIVE)
                    .createdBy(createdBy)
                    .createdTime(createdTime)
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
                GetAllType.WITH_SCAN + "|")
        String type = GetAllType.BARE;
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


        QSysDevice builder = QSysDevice.sysDevice;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        DeviceGetByFilterAndPageRequestBody.Filter filter = requestBody.getFilter();
        if (filter != null) {
            if (!StringUtils.isEmpty(filter.getArchivesName())) {
                predicate.and(builder.archive.archivesName.contains(filter.getArchivesName()));
            }
            if (!StringUtils.isEmpty(filter.getStatus())) {
                predicate.and(builder.status.contains(filter.getStatus()));
            }
            if (!StringUtils.isEmpty(filter.getCategoryName())) {
                predicate.and(builder.archive.archiveTemplate.deviceCategory.categoryName.contains(filter.getCategoryName()));
            }
        }

        int currentPage = requestBody.getCurrentPage() - 1; // On server side, page is calculated from 0.
        int perPage = requestBody.getPerPage();

        PageRequest pageRequest = PageRequest.of(currentPage, perPage);

        long total = sysDeviceRepository.count(predicate);
        List<SysDevice> data = sysDeviceRepository.findAll(predicate, pageRequest).getContent();


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
                .addFilter(ModelJsonFilters.FILTER_SYS_FIELD, SimpleBeanPropertyFilter.serializeAllExcept("parent"))
                .addFilter(ModelJsonFilters.FILTER_SYS_DEVICE_CATEGORY, SimpleBeanPropertyFilter.serializeAllExcept("parent"));


        value.setFilters(filters);

        return value;
    }

    /**
     * Device update status request.
     */
    @RequestMapping(value = "/device/update-status", method = RequestMethod.POST)
    public Object deviceUpdateStatus(
            @RequestBody @Valid DeviceUpdateStatusRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // Check if device is existing.
        Optional<SysDevice> optionalSysDevice = sysDeviceRepository.findOne(QSysDevice.
                sysDevice.deviceId.eq(requestBody.getDeviceId()));
        if (!optionalSysDevice.isPresent()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        SysDevice sysDevice = optionalSysDevice.get();

        // Update status.
        sysDevice.setStatus(requestBody.getStatus());

        // Add edited info.
        sysDevice.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());

        sysDeviceRepository.save(sysDevice);

        return new CommonResponseBody(ResponseMessage.OK);
    }

    /**
     * Device create request.
     */
    @RequestMapping(value = "/device/create", method = RequestMethod.POST)
    public Object deviceCreate(
            @ModelAttribute @Valid DeviceCreateRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // Check if archive is valid.
        if (!serArchiveRepository.exists(QSerArchive.serArchive
                .archiveId.eq(requestBody.getArchiveId()))) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }



        SysDevice sysDevice = requestBody.convert2SysDevice();

        // Process portrait file.
        MultipartFile portraitFile = requestBody.getImageUrl();

        if (portraitFile != null && !portraitFile.isEmpty()) {
            try {
                byte[] bytes = portraitFile.getBytes();

                String directoryPath = Constants.PORTRAIT_FILE_UPLOAD_DIRECTORY;
                String fileName = new Date().getTime() + "_" + portraitFile.getOriginalFilename();

                boolean isSucceeded = utils.saveFile(directoryPath, fileName, bytes);

                if (isSucceeded) {
                    // Save file name.
                    sysDevice.setImageUrl(Constants.PORTRAIT_FILE_SERVING_BASE_URL + fileName);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        // Add created info.
        sysDevice.addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());

        sysDeviceRepository.save(sysDevice);

        return new CommonResponseBody(ResponseMessage.OK);
    }


    /**
     * Device modify request.
     */
    @RequestMapping(value = "/device/modify", method = RequestMethod.POST)
    public Object deviceModify(
            @ModelAttribute @Valid DeviceModifyRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        // Check if archive is valid.
        if (!serArchiveRepository.exists(QSerArchive.serArchive
                .archiveId.eq(requestBody.getArchiveId()))) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }


        SysDevice oldSysDevice = sysDeviceRepository.findOne(QSysDevice.sysDevice
                .deviceId.eq(requestBody.getDeviceId())).orElse(null);

        //check if device is valid.
        if(oldSysDevice == null) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }


        SysDevice sysDevice = requestBody.convert2SysDevice(oldSysDevice.getCreatedBy(), oldSysDevice.getCreatedTime());

        // Process portrait file.
        MultipartFile portraitFile = requestBody.getImageUrl();

        if (portraitFile != null && !portraitFile.isEmpty()) {
            try {
                byte[] bytes = portraitFile.getBytes();

                String directoryPath = Constants.PORTRAIT_FILE_UPLOAD_DIRECTORY;
                String fileName = new Date().getTime() + "_" + portraitFile.getOriginalFilename();

                boolean isSucceeded = utils.saveFile(directoryPath, fileName, bytes);

                if (isSucceeded) {
                    // Save file name.
                    sysDevice.setImageUrl(Constants.PORTRAIT_FILE_SERVING_BASE_URL + fileName);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        // Add edited info.
        sysDevice.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());

        sysDeviceRepository.save(sysDevice);

        return new CommonResponseBody(ResponseMessage.OK);
    }

    /**
     * Device delete request.
     */
    @RequestMapping(value = "/device/delete", method = RequestMethod.POST)
    public Object deviceDelete(
            @RequestBody @Valid DeviceDeleteRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        SysDevice sysDevice = sysDeviceRepository.findOne(QSysDevice.sysDevice
                .deviceId.eq(requestBody.getDeviceId())).orElse(null);

        //check device exist or not
        if(sysDevice == null) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        sysDeviceRepository.delete(sysDevice);


        return new CommonResponseBody(ResponseMessage.OK);
    }

    /**
     * Device field modify request.
     */
    @RequestMapping(value = "/device/field-modify", method = RequestMethod.POST)
    public Object deviceFieldModify(
            @RequestBody @Valid DeviceFieldModifyRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        List<SysDevice> deviceList = requestBody.getDeviceList();
        for(int i = 0; i < deviceList.size(); i ++) {
            SysDevice device = deviceList.get(i);
            // Add edited info.
            device.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
            sysDeviceRepository.save(device);
        }


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

        List<SysDevice> sysDeviceList = sysDeviceRepository.findAll();

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, sysDeviceList));

        String type = requestBody.getType();
        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();

        filters.addFilter(ModelJsonFilters.FILTER_SYS_FIELD, SimpleBeanPropertyFilter.serializeAllExcept("parent"));
        filters.addFilter(ModelJsonFilters.FILTER_SYS_DEVICE_CATEGORY, SimpleBeanPropertyFilter.serializeAllExcept("parent"));

        switch (type) {

            case DeviceGetAllRequestBody.GetAllType.BARE:
                filters.addFilter(ModelJsonFilters.FILTER_SYS_DEVICE, SimpleBeanPropertyFilter.serializeAllExcept("deviceConfig", "scanParam"));
                break;
            case DeviceGetAllRequestBody.GetAllType.WITH_CONFIG:
                filters.addFilter(ModelJsonFilters.FILTER_SYS_DEVICE, SimpleBeanPropertyFilter.serializeAllExcept("scanParam"))
                        .addFilter(ModelJsonFilters.FILTER_SYS_DEVICE_CONFIG, SimpleBeanPropertyFilter.serializeAllExcept("device"));
                break;
            case DeviceGetAllRequestBody.GetAllType.WITH_SCAN:
                filters.addFilter(ModelJsonFilters.FILTER_SYS_DEVICE, SimpleBeanPropertyFilter.serializeAllExcept("deviceConfig"))
                        .addFilter(ModelJsonFilters.FILTER_SER_SCAN_PARAM, SimpleBeanPropertyFilter.serializeAllExcept("device"));
                break;

            default:

                break;
        }

        value.setFilters(filters);

        return value;
    }

}
