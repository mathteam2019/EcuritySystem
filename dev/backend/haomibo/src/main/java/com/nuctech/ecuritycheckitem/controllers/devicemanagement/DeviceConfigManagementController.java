/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/20
 * @CreatedBy Choe.
 * @FileName DeviceConfigManagementController.java
 * @ModifyHistory
 */
package com.nuctech.ecuritycheckitem.controllers.devicemanagement;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.enums.Role;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.*;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.reusables.FilteringAndPaginationResult;
import com.nuctech.ecuritycheckitem.service.devicemanagement.DeviceConfigService;
import com.nuctech.ecuritycheckitem.utils.PageResult;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/device-management/device-config")
public class DeviceConfigManagementController extends BaseController {

    @Autowired
    DeviceConfigService deviceConfigService;
    /**
     * Device datatable request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class DeviceConfigGetByFilterAndPageRequestBody {

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        static class Filter {
            String deviceName;
            Long categoryId;
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
     * Device create request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class DeviceConfigModifyRequestBody {
        Long configId;

        Long modeId;

        String atrSwitch;

        String manualSwitch;

        String manRemoteGender;

        String womanRemoteGender;

        List<Long> manualDeviceIdList;

        String manManualGender;

        String womanManualGender;

        List<Long> judgeDeviceIdList;

        String manDeviceGender;

        String womanDeviceGender;

        List<Long> fromDeviceIdList;

    }

    /**
     * Device config delete request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class DeviceConfigDeleteRequestBody {

        @NotNull
        Long configId;
    }

    /**
     * Device config get by id request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class DeviceConfigGetByIdRequestBody {

        @NotNull
        Long configId;
    }

    /**
     * Device config get all request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class DeviceConfigGetAllRequestBody {
        Long deviceId;
    }


    @RequestMapping(value = "/config/get-by-id", method = RequestMethod.POST)
    public Object deviceConfigGetById(
            @RequestBody @Valid DeviceConfigGetByIdRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }
        Long configId = requestBody.getConfigId();

        SysDeviceConfig sysDeviceConfig = deviceConfigService.findConfigById(configId);

        if (sysDeviceConfig == null) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, sysDeviceConfig));
        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();
        filters.addFilter(ModelJsonFilters.FILTER_SYS_DEVICE, SimpleBeanPropertyFilter.serializeAllExcept("deviceConfig", "scanParam"))
                .addFilter(ModelJsonFilters.FILTER_SYS_DEVICE_CATEGORY, SimpleBeanPropertyFilter.serializeAllExcept("parent"));
        value.setFilters(filters);
        return value;
    }

    /**
     * Device config datatable data.
     */
    @RequestMapping(value = "/config/get-by-filter-and-page", method = RequestMethod.POST)
    public Object deviceConfigGetByFilterAndPage(
            @RequestBody @Valid DeviceConfigGetByFilterAndPageRequestBody requestBody,
            BindingResult bindingResult) {


        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        int currentPage = requestBody.getCurrentPage();
        currentPage = currentPage - 1;
        int perPage = requestBody.getPerPage();
        String deviceName  = "";
        Long fieldId = null;
        Long categoryId = null;
        if(requestBody.getFilter() != null) {
            deviceName = requestBody.getFilter().getDeviceName();
            fieldId = requestBody.getFilter().getFieldId();
            categoryId = requestBody.getFilter().getCategoryId();
        }
        PageResult<SysDeviceConfig> result = deviceConfigService.findConfigByFilter(deviceName, fieldId, categoryId, currentPage, perPage);

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(
                ResponseMessage.OK,
                FilteringAndPaginationResult
                        .builder()
                        .total(result.getTotal())
                        .perPage(perPage)
                        .currentPage(currentPage + 1)
                        .lastPage((int) Math.ceil(((double) result.getTotal()) / perPage))
                        .from(perPage * currentPage + 1)
                        .to(perPage * currentPage + result.getDataList().size())
                        .data(result.getDataList())
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
     * Device config modify request.
     */
    @PreAuthorize(Role.Authority.HAS_DEVICE_CONFIG_MODIFY)
    @RequestMapping(value = "/config/modify", method = RequestMethod.POST)
    public Object deviceConfigModify(
            @RequestBody @Valid DeviceConfigModifyRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }


        SysDeviceConfig sysDeviceConfig = deviceConfigService.findConfigById(requestBody.getConfigId());

        //check if device config is valid.
        if(sysDeviceConfig == null) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }
        List<Long> manualDeviceIdList = requestBody.getManualDeviceIdList();
        List<Long> judgeDeviceIdList = requestBody.getJudgeDeviceIdList();
        List<Long> configDeviceIdList = requestBody.getFromDeviceIdList();

        sysDeviceConfig.setModeId(requestBody.getModeId());
        sysDeviceConfig.setAtrSwitch(requestBody.getAtrSwitch());
        sysDeviceConfig.setManualSwitch(requestBody.getManualSwitch());
        sysDeviceConfig.setManRemoteGender(requestBody.getManRemoteGender());
        sysDeviceConfig.setWomanRemoteGender(requestBody.getWomanRemoteGender());
        sysDeviceConfig.setManManualGender(requestBody.getManManualGender());
        sysDeviceConfig.setWomanManualGender(requestBody.getWomanManualGender());
        sysDeviceConfig.setManDeviceGender(requestBody.getManDeviceGender());
        sysDeviceConfig.setWomanDeviceGender(requestBody.getWomanDeviceGender());

        deviceConfigService.modifyDeviceConfig(sysDeviceConfig, manualDeviceIdList, judgeDeviceIdList, configDeviceIdList);



        return new CommonResponseBody(ResponseMessage.OK);
    }

    /**
     * Device Config delete request.
     */
    @RequestMapping(value = "/config/delete", method = RequestMethod.POST)
    public Object deviceConfigDelete(
            @RequestBody @Valid DeviceConfigDeleteRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        SysDeviceConfig sysDeviceConfig = deviceConfigService.findConfigById(requestBody.getConfigId());

        //check device config exist or not
        if(sysDeviceConfig == null) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        //remove correspond manual group
        deviceConfigService.removeDeviceConfig(sysDeviceConfig);


        return new CommonResponseBody(ResponseMessage.OK);
    }



    /**
     * Device  config get all request.
     */
    @RequestMapping(value = "/config/get-all", method = RequestMethod.POST)
    public Object deviceConfigGetAll(@RequestBody @Valid DeviceConfigGetAllRequestBody requestBody,
                                     BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        List<SysDeviceConfig> sysDeviceConfigList = deviceConfigService.findAllDeviceConfigExceptId(requestBody.getDeviceId());

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, sysDeviceConfigList));

        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();

        filters.addFilter(ModelJsonFilters.FILTER_SYS_DEVICE, SimpleBeanPropertyFilter.serializeAllExcept("deviceConfig", "scanParam"))
                .addFilter(ModelJsonFilters.FILTER_SYS_DEVICE_CATEGORY, SimpleBeanPropertyFilter.serializeAllExcept("parent"));

        value.setFilters(filters);

        return value;
    }

    /**
     * Work Mode get all request.
     */
    @RequestMapping(value = "/work-mode/get-all", method = RequestMethod.POST)
    public Object workModeGetAll() {


        List<SysWorkMode> sysWorkModeList = deviceConfigService.findAllWorkMode();

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, sysWorkModeList));


        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();
        filters.addFilter(ModelJsonFilters.FILTER_SYS_DEVICE, SimpleBeanPropertyFilter.filterOutAllExcept("deviceName"));

        value.setFilters(filters);

        return value;
    }

    /**
     * Manual Device get all request.
     */
    @RequestMapping(value = "/manual-device/get-all", method = RequestMethod.POST)
    public Object manualDeviceGetAll() {


        List<SysManualDevice> sysManualDeviceList = deviceConfigService.findAllManualDevice();

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, sysManualDeviceList));


        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();
        filters.addFilter(ModelJsonFilters.FILTER_SYS_DEVICE, SimpleBeanPropertyFilter.filterOutAllExcept("deviceName"));

        value.setFilters(filters);

        return value;
    }

    /**
     * Judge Device get all request.
     */
    @RequestMapping(value = "/judge-device/get-all", method = RequestMethod.POST)
    public Object judgeDeviceGetAll() {


        List<SysJudgeDevice> sysJudgeDeviceList = deviceConfigService.findAllJudgeDevice();

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, sysJudgeDeviceList));


        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();
        filters.addFilter(ModelJsonFilters.FILTER_SYS_DEVICE, SimpleBeanPropertyFilter.filterOutAllExcept("deviceName"));
        value.setFilters(filters);

        return value;
    }

}
