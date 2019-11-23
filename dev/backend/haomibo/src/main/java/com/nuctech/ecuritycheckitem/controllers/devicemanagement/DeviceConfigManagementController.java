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
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.*;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.reusables.FilteringAndPaginationResult;
import com.querydsl.core.BooleanBuilder;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/device-management/device-config")
public class DeviceConfigManagementController extends BaseController {

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
            String categoryName;
            String fieldDesignation;
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

        Long manualDeviceId;

        String manManualGender;

        String womanManualGender;

        Long judgeDeviceId;

        String manDeviceGender;

        String womanDeviceGender;

        Long deviceId;

    }

    /**
     * Device delete request body.
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
     * Device config datatable data.
     */
    @RequestMapping(value = "/config/get-by-filter-and-page", method = RequestMethod.POST)
    public Object deviceConfigGetByFilterAndPage(
            @RequestBody @Valid DeviceConfigGetByFilterAndPageRequestBody requestBody,
            BindingResult bindingResult) {


        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }


        QSysDeviceConfig builder = QSysDeviceConfig.sysDeviceConfig;

        BooleanBuilder predicate = new BooleanBuilder(builder.isNotNull());

        DeviceConfigGetByFilterAndPageRequestBody.Filter filter = requestBody.getFilter();
        if (filter != null) {
            if (!StringUtils.isEmpty(filter.getDeviceName())) {
                predicate.and(builder.device.deviceName.contains(filter.getDeviceName()));
            }
            if (!StringUtils.isEmpty(filter.getFieldDesignation())) {
                predicate.and(builder.device.field.fieldDesignation.contains(filter.getFieldDesignation()));
            }
            if (!StringUtils.isEmpty(filter.getCategoryName())) {
                predicate.and(builder.device.archive.archiveTemplate.deviceCategory.categoryName.contains(filter.getCategoryName()));
            }
        }

        int currentPage = requestBody.getCurrentPage() - 1; // On server side, page is calculated from 0.
        int perPage = requestBody.getPerPage();

        PageRequest pageRequest = PageRequest.of(currentPage, perPage);

        long total = sysDeviceConfigRepository.count(predicate);
        List<SysDeviceConfig> data = sysDeviceConfigRepository.findAll(predicate, pageRequest).getContent();


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
                .addFilter(ModelJsonFilters.FILTER_SYS_DEVICE_CATEGORY, SimpleBeanPropertyFilter.serializeAllExcept("parent"));;

        value.setFilters(filters);

        return value;
    }




    /**
     * Device config modify request.
     */
    @RequestMapping(value = "/config/modify", method = RequestMethod.POST)
    public Object deviceConfigModify(
            @ModelAttribute @Valid DeviceConfigModifyRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }


        SysDeviceConfig sysDeviceConfig = sysDeviceConfigRepository.findOne(QSysDeviceConfig.sysDeviceConfig
                .configId.eq(requestBody.getConfigId())).orElse(null);

        //check if device config is valid.
        if(sysDeviceConfig == null) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        Long manualDeviceId = requestBody.getManualDeviceId();
        Long judgeDeviceId = requestBody.getJudgeDeviceId();
        Long configDeviceId = requestBody.getDeviceId();


        SysManualGroup manualGroup = (sysDeviceConfig.getManualGroupList() != null &&  sysDeviceConfig.getManualGroupList().size() > 0)?
                sysDeviceConfig.getManualGroupList().get(0): null;
        //check manual Group exist or not
        if(manualGroup != null) {
            if(manualDeviceId != null) {
                manualGroup.setManualDeviceId(manualDeviceId);
                manualGroup.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
                sysManualGroupRepository.save(manualGroup);
            } else {
                sysManualGroupRepository.delete(manualGroup);
            }

        } else if(manualDeviceId != null) {//create manual group
            manualGroup = SysManualGroup.
                    builder()
                    .manualDeviceId(manualDeviceId)
                    .configId(requestBody.getConfigId())
                    .build();
            manualGroup.addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
            sysManualGroupRepository.save(manualGroup);
        }

        SysJudgeGroup judgeGroup = (sysDeviceConfig.getJudgeGroupList() != null &&  sysDeviceConfig.getJudgeGroupList().size() > 0)?
                sysDeviceConfig.getJudgeGroupList().get(0): null;
        //check judge Group exist or not
        if(judgeGroup != null) {
            if(judgeDeviceId != null) {
                judgeGroup.setJudgeDeviceId(judgeDeviceId);
                judgeGroup.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
                sysJudgeGroupRepository.save(judgeGroup);
            } else {
                sysJudgeGroupRepository.delete(judgeGroup);
            }

        } else if(judgeDeviceId != null) {//create judge group
            judgeGroup = SysJudgeGroup.
                    builder()
                    .judgeDeviceId(judgeDeviceId)
                    .configId(requestBody.getConfigId())
                    .build();
            judgeGroup.addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
            sysJudgeGroupRepository.save(judgeGroup);
        }

        FromConfigId fromConfigId = (sysDeviceConfig.getFromConfigIdList() != null &&  sysDeviceConfig.getFromConfigIdList().size() > 0)?
                sysDeviceConfig.getFromConfigIdList().get(0): null;
        //check from config exist or not
        if(fromConfigId != null) {
            if(configDeviceId != null) {
                fromConfigId.setFromDeviceId(configDeviceId);
                fromConfigId.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
                fromConfigIdRepository.save(fromConfigId);
            } else {
                fromConfigIdRepository.delete(fromConfigId);
            }

        } else if(configDeviceId != null) {//create judge group
            fromConfigId = FromConfigId.
                    builder()
                    .fromDeviceId(configDeviceId)
                    .deviceId(sysDeviceConfig.getDeviceId())
                    .configId(requestBody.getConfigId())
                    .build();
            fromConfigId.addCreatedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());
            fromConfigIdRepository.save(fromConfigId);
        }





        sysDeviceConfig.setModeId(requestBody.getModeId());
        sysDeviceConfig.setAtrSwitch(requestBody.getAtrSwitch());
        sysDeviceConfig.setManualSwitch(requestBody.getManualSwitch());
        sysDeviceConfig.setManRemoteGender(requestBody.getManRemoteGender());
        sysDeviceConfig.setWomanRemoteGender(requestBody.getWomanRemoteGender());
        sysDeviceConfig.setManManualGender(requestBody.getManManualGender());
        sysDeviceConfig.setWomanManualGender(requestBody.getWomanManualGender());
        sysDeviceConfig.setManDeviceGender(requestBody.getManDeviceGender());
        sysDeviceConfig.setWomanDeviceGender(requestBody.getWomanDeviceGender());

        // Add edited info.
        sysDeviceConfig.addEditedInfo((SysUser) authenticationFacade.getAuthentication().getPrincipal());

        sysDeviceConfigRepository.save(sysDeviceConfig);

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

        SysDeviceConfig sysDeviceConfig = sysDeviceConfigRepository.findOne(QSysDeviceConfig.sysDeviceConfig
                .configId.eq(requestBody.getConfigId())).orElse(null);

        //check device config exist or not
        if(sysDeviceConfig == null) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

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


        return new CommonResponseBody(ResponseMessage.OK);
    }



    /**
     * Device  config get all request.
     */
    @RequestMapping(value = "/config/get-all", method = RequestMethod.POST)
    public Object deviceConfigGetAll() {


        List<SysDeviceConfig> sysDeviceConfigList = sysDeviceConfigRepository.findAll();

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, sysDeviceConfigList));


        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();

        filters.addFilter(ModelJsonFilters.FILTER_SYS_DEVICE, SimpleBeanPropertyFilter.serializeAllExcept("deviceConfig", "scanParam"))
                .addFilter(ModelJsonFilters.FILTER_SYS_FIELD, SimpleBeanPropertyFilter.serializeAllExcept("parent"))
                .addFilter(ModelJsonFilters.FILTER_SYS_DEVICE_CATEGORY, SimpleBeanPropertyFilter.serializeAllExcept("parent"));;

        value.setFilters(filters);

        return value;
    }

    /**
     * Work Mode get all request.
     */
    @RequestMapping(value = "/work-mode/get-all", method = RequestMethod.POST)
    public Object workModeGetAll() {


        List<SysWorkMode> sysWorkModeList = sysWorkModeRepository.findAll();

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, sysWorkModeList));


        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();

        value.setFilters(filters);

        return value;
    }

    /**
     * Manual Device get all request.
     */
    @RequestMapping(value = "/manual-device/get-all", method = RequestMethod.POST)
    public Object manualDeviceGetAll() {


        List<SysManualDevice> sysManualDeviceList = sysManualDeviceRepository.findAll();

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, sysManualDeviceList));


        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();

        value.setFilters(filters);

        return value;
    }

    /**
     * Judge Device get all request.
     */
    @RequestMapping(value = "/judge-device/get-all", method = RequestMethod.POST)
    public Object judgeDeviceGetAll() {


        List<SysJudgeDevice> sysJudgeDeviceList = sysJudgeDeviceRepository.findAll();

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, sysJudgeDeviceList));


        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();

        value.setFilters(filters);

        return value;
    }

}
