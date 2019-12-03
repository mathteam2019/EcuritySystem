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
import com.nuctech.ecuritycheckitem.export.devicemanagement.DeviceExcelView;
import com.nuctech.ecuritycheckitem.export.devicemanagement.DevicePdfView;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.*;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.reusables.FilteringAndPaginationResult;
import com.nuctech.ecuritycheckitem.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
        @NotNull
        String exportType;

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
                    .deviceIp(Optional.ofNullable(this.getDeviceIp()).orElse(""))
                    .guid(this.getGuid())
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

        long total = 0;
        List<SysDevice> allData = deviceService.getFilterDeviceList(requestBody);
        List<SysDevice> data = new ArrayList<>();
        if(filter != null && filter.getCategoryId() != null) {

            for(int i = 0; i < allData.size(); i ++) {
                SysDevice deviceData = allData.get(i);
                try {
                    if(deviceData.getArchive().getArchiveTemplate().getDeviceCategory().getCategoryId() == filter.getCategoryId()) {
                        if(total >= startIndex && total < endIndex) {
                            data.add(deviceData);
                        }
                        total ++;

                    }
                } catch(Exception ex) {
                    ex.printStackTrace();
                }
            }
        } else {
            for(int i = 0; i < allData.size(); i ++) {
                SysDevice deviceData = allData.get(i);
                if(i >= startIndex && i < endIndex) {
                    data.add(deviceData);
                }
            }
            total = allData.size();
        }


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
     * Device generate file request.
     */
    @RequestMapping(value = "/device/export", method = RequestMethod.POST)
    public Object deviceGenerateFile(@RequestBody @Valid DeviceGenerateRequestBody requestBody,
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
                predicate.and(builder.status.eq(filter.getStatus()));
            }

            if (!StringUtils.isEmpty(filter.getDeviceName())) {
                predicate.and(builder.deviceName.contains(filter.getDeviceName()));
            }

            if(filter.getFieldId() != null) {
                predicate.and(builder.fieldId.eq(filter.getFieldId()));
            }
//            if (filter.getCategoryId() != null) {
//                predicate.and(builder.archive.archiveTemplate.category.categoryId.eq(filter.getCategoryId()));
//            }
        }

        //get all device list
        List<SysDevice> preDeviceList = StreamSupport
                .stream(sysDeviceRepository.findAll(predicate).spliterator(), false)
                .collect(Collectors.toList());
        List<SysDevice> deviceList = new ArrayList<>();
        if(filter != null && filter.getCategoryId() != null) {
            for(int i = 0; i < preDeviceList.size(); i ++) {
                SysDevice deviceData = preDeviceList.get(i);
                try {
                    if(deviceData.getArchive().getArchiveTemplate().getDeviceCategory().getCategoryId() == filter.getCategoryId()) {
                        deviceList.add(deviceData);
                    }
                } catch(Exception ex) {
                    ex.printStackTrace();
                }
            }
        } else {
            deviceList = preDeviceList;
        }
        List<SysDevice> exportList = new ArrayList<>();
        if(requestBody.getIsAll() == false) {
            String[] splits = requestBody.getIdList().split(",");
            for(int i = 0; i < deviceList.size(); i ++) {
                SysDevice device = deviceList.get(i);
                boolean isExist = false;
                for(int j = 0; j < splits.length; j ++) {
                    if(splits[j].equals(device.getDeviceId().toString())) {
                        isExist = true;
                        break;
                    }
                }
                if(isExist == true) {
                    exportList.add(device);
                }
            }
        } else {
            exportList = deviceList;
        }

        if(requestBody.exportType.equals("excel")) {
            InputStream inputStream = DeviceExcelView.buildExcelDocument(exportList);



            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=device.xlsx");

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentType(MediaType.valueOf("application/x-msexcel"))
                    .body(new InputStreamResource(inputStream));
        }
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
        SysDeviceConfig deviceConfig = SysDeviceConfig.builder().deviceId(sysDevice.getDeviceId()).build();
        deviceConfig.addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
        sysDeviceConfigRepository.save(deviceConfig);



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
        sysDevice.setFieldId(oldSysDevice.getFieldId());

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


        SysDeviceConfig sysDeviceConfig = sysDeviceConfigRepository.findOne(QSysDeviceConfig.sysDeviceConfig
                .deviceId.eq(sysDevice.getDeviceId())).orElse(null);

        //check device config exist or not
        if(sysDeviceConfig != null) {
            //remove correspond manual group
            SysManualGroup manualGroup = (sysDeviceConfig.getManualGroupList() != null &&  sysDeviceConfig.getManualGroupList().size() > 0)?
                    sysDeviceConfig.getManualGroupList().get(0): null;
            if(manualGroup != null) {
                sysManualGroupRepository.delete(manualGroup);
            }

            //remove correspond judge group
            SysJudgeGroup judgeGroup = (sysDeviceConfig.getJudgeGroupList() != null &&  sysDeviceConfig.getJudgeGroupList().size() > 0)?
                    sysDeviceConfig.getJudgeGroupList().get(0): null;
            if(judgeGroup != null) {
                sysJudgeGroupRepository.delete(judgeGroup);
            }

            //remove correspond from config.
            FromConfigId fromConfigId = (sysDeviceConfig.getFromConfigIdList() != null &&  sysDeviceConfig.getFromConfigIdList().size() > 0)?
                    sysDeviceConfig.getFromConfigIdList().get(0): null;
            if(fromConfigId != null) {
                fromConfigIdRepository.delete(fromConfigId);
            }

            sysDeviceConfigRepository.delete(sysDeviceConfig);
        }




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
            SysDevice realDevice = sysDeviceRepository.findOne(QSysDevice.sysDevice
                    .deviceId.eq(device.getDeviceId())).orElse(null);
            if(realDevice != null) {
                realDevice.setField(device.getField());
                realDevice.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
                sysDeviceRepository.save(realDevice);
            }
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



        List<SysDevice> preSysDeviceList = sysDeviceRepository.findAll();
        List<SysDevice> sysDeviceList = new ArrayList<>();
        if(requestBody.getCategoryId() != null) {
            for(int i = 0; i < preSysDeviceList.size(); i ++) {
                SysDevice device = preSysDeviceList.get(i);
                if(device.getFieldId() != null) {
                    continue;
                }
                try {
                    if(device.getArchive().getArchiveTemplate().getDeviceCategory().getCategoryId() == requestBody.getCategoryId()) {
                        sysDeviceList.add(device);
                    }
                } catch(Exception ex) {

                }
            }
        } else {
            for(int i = 0; i < preSysDeviceList.size(); i ++) {
                SysDevice device = preSysDeviceList.get(i);
                if (device.getFieldId() != null) {
                    continue;
                }
                sysDeviceList.add(device);
            }
        }

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

        QSysDevice builder = QSysDevice.sysDevice;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());



        if(requestBody.getFieldId() != null) {
            predicate.and(builder.fieldId.eq(requestBody.getFieldId()));
        }


        List<SysDevice> preSysDeviceList = StreamSupport
                .stream(sysDeviceRepository.findAll(predicate).spliterator(), false)
                .collect(Collectors.toList());

        List<SysDevice> sysDeviceList = new ArrayList<>();
        if(requestBody.getCategoryId() != null) {
            for(int i = 0; i < preSysDeviceList.size(); i ++) {
                SysDevice device = preSysDeviceList.get(i);
                try {
                    if(device.getArchive().getArchiveTemplate().getDeviceCategory().getCategoryId() == requestBody.getCategoryId()) {
                        sysDeviceList.add(device);
                    }
                } catch(Exception ex) {

                }
            }
        } else {
            sysDeviceList = preSysDeviceList;
        }

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, sysDeviceList));

        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();

        filters.addFilter(ModelJsonFilters.FILTER_SYS_DEVICE_CATEGORY, SimpleBeanPropertyFilter.serializeAllExcept("parent"));
        filters.addFilter(ModelJsonFilters.FILTER_SYS_DEVICE, SimpleBeanPropertyFilter.serializeAllExcept("deviceConfig", "scanParam"))
                .addFilter(ModelJsonFilters.FILTER_SYS_DEVICE_CATEGORY, SimpleBeanPropertyFilter.serializeAllExcept("parent"));



        value.setFilters(filters);

        return value;
    }

}
