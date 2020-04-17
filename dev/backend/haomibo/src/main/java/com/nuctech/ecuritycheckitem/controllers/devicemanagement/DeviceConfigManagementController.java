/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（DeviceConfigManagementController）
 * 文件名：	DeviceConfigManagementController.java
 * 描述：	DeviceConfig Management Controller
 * 作者名：	Choe
 * 日期：	2019/11/20
 *
 */

package com.nuctech.ecuritycheckitem.controllers.devicemanagement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.controllers.AsyncController;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.enums.Role;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.*;
import com.nuctech.ecuritycheckitem.models.redis.SysDeviceConfigInfoVO;
import com.nuctech.ecuritycheckitem.models.redis.SysJudgeInfoVO;
import com.nuctech.ecuritycheckitem.models.redis.SysManualInfoVO;
import com.nuctech.ecuritycheckitem.models.redis.SysSecurityInfoVO;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.reusables.FilteringAndPaginationResult;
import com.nuctech.ecuritycheckitem.service.devicemanagement.DeviceConfigService;
import com.nuctech.ecuritycheckitem.service.devicemanagement.SerLoginInfoService;
import com.nuctech.ecuritycheckitem.service.logmanagement.AuditLogService;
import com.nuctech.ecuritycheckitem.utils.CryptUtil;
import com.nuctech.ecuritycheckitem.utils.PageResult;
import com.nuctech.ecuritycheckitem.utils.RedisUtil;
import com.nuctech.ecuritycheckitem.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.alibaba.fastjson.JSONArray;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.*;

@RestController
@RequestMapping("/device-management/device-config")
public class DeviceConfigManagementController extends BaseController {

    @Autowired
    DeviceConfigService deviceConfigService;

    @Autowired
    AuditLogService auditLogService;

    @Autowired
    SerLoginInfoService serLoginInfoService;

    @Autowired
    public MessageSource messageSource;

    @Autowired
    AsyncController asyncController;

    @Autowired
    private RedisUtil redisUtil;



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
            String deviceName; //device name
            Long categoryId; //device category id
            Long mode;
            Long fieldId; //field id
        }

        @NotNull
        @Min(1)
        int currentPage;
        @NotNull
        int perPage;
        String sort;
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

        Long configId; //config id
        Long modeId; //mode id
        String atrSwitch; //atr switch
        String manualSwitch; //manual switch
        String manRemoteGender; //man remote gender
        String womanRemoteGender; //woman remote gender
        List<Long> manualDeviceIdList; //manual device id list
        String manManualGender; //man manual gender
        String womanManualGender; //woman manual gender
        List<Long> judgeDeviceIdList; //judge device id list
        String manDeviceGender; //man device gender
        String womanDeviceGender; //woman device gender
        List<Long> fromDeviceIdList; //device idlist
    }

    /**
     * Device config delete request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private static class DeviceConfigUpdateStatusRequestBody {

        @NotNull
        Long configId; // device id
        String status; //update status
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
        Long configId; //config id
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
        Long deviceId; //device id
    }

    /**
     * get a config from id
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/config/get-by-id", method = RequestMethod.POST)
    public Object deviceConfigGetById(
            @RequestBody @Valid DeviceConfigGetByIdRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }
        Long configId = requestBody.getConfigId();
        SysDeviceConfig sysDeviceConfig = deviceConfigService.findConfigById(configId);

        if (sysDeviceConfig == null) { //if specified id doesn't exist
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, sysDeviceConfig));
        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();
        filters.addFilter(ModelJsonFilters.FILTER_SYS_DEVICE_CATEGORY, SimpleBeanPropertyFilter.serializeAllExcept("parent")); //return all fields except parent from SysDeviceCategory model
        value.setFilters(filters);
        return value;
    }

    /**
     * Device config datatable data.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/config/get-by-filter-and-page", method = RequestMethod.POST)
    public Object deviceConfigGetByFilterAndPage(
            @RequestBody @Valid DeviceConfigGetByFilterAndPageRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        int currentPage = requestBody.getCurrentPage();
        currentPage = currentPage - 1;
        int perPage = requestBody.getPerPage();
        String deviceName  = "";
        Long fieldId = null;
        Long categoryId = null;
        Long mode = null;
        if(requestBody.getFilter() != null) {
            deviceName = requestBody.getFilter().getDeviceName(); //get device name from input parameter
            fieldId = requestBody.getFilter().getFieldId(); //get field id from input parameter
            categoryId = requestBody.getFilter().getCategoryId(); //get device category id from input parameter
            mode = requestBody.getFilter().getMode();
        }

        String sortBy = "";
        String order = "";
        Map<String, String> sortParams = new HashMap<String, String>();
        if (requestBody.getSort() != null && !requestBody.getSort().isEmpty()) {
            sortParams = Utils.getSortParams(requestBody.getSort());
            if (!sortParams.isEmpty()) {
                sortBy = sortParams.get("sortBy");
                order = sortParams.get("order");
            }
        }
        PageResult<SysDeviceConfig> result = deviceConfigService.findConfigByFilter(sortBy, order, deviceName, fieldId, categoryId, mode, currentPage, perPage); //get result from database through deviceConfigService

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(
                ResponseMessage.OK, //set response message as OK
                FilteringAndPaginationResult
                        .builder()
                        .total(result.getTotal()) //set total count
                        .perPage(perPage) //set record count per page
                        .currentPage(currentPage + 1) //set current page number
                        .lastPage((int) Math.ceil(((double) result.getTotal()) / perPage)) //set last page number
                        .from(perPage * currentPage + 1) //set start index of current page
                        .to(perPage * currentPage + result.getDataList().size()) //set last index of current page
                        .data(result.getDataList()) //set data
                        .build()));

        // Set filters.

        FilterProvider filters = ModelJsonFilters
                .getDefaultFilters()
                .addFilter(ModelJsonFilters.FILTER_SYS_DEVICE_CATEGORY, SimpleBeanPropertyFilter.serializeAllExcept("parent")); // return all fields except parent from SysDeviceCategory model
        value.setFilters(filters);

        return value;
    }

    /**
     * Device config modify request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    //@PreAuthorize(Role.Authority.HAS_DEVICE_CONFIG_MODIFY)
    @RequestMapping(value = "/config/modify", method = RequestMethod.POST)
    public Object deviceConfigModify(
            @RequestBody @Valid DeviceConfigModifyRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            auditLogService.saveAudioLog(messageSource.getMessage("Modify", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("DeviceConfig", null, currentLocale),
                    messageSource.getMessage("ParameterError", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        SysDeviceConfig sysDeviceConfig = deviceConfigService.findConfigById(requestBody.getConfigId()); //get config by id through deviceConfigService

        //check if device config is valid.
        if(sysDeviceConfig == null) {
            auditLogService.saveAudioLog(messageSource.getMessage("Modify", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("DeviceConfig", null, currentLocale),
                    messageSource.getMessage("ParameterError", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        if(!deviceConfigService.checkDeviceHaveField(sysDeviceConfig.getDeviceId())) {
            auditLogService.saveAudioLog(messageSource.getMessage("UpdateStatus", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("DeviceConfig", null, currentLocale),
                    messageSource.getMessage("Device.Error.NotField", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.DEVICE_NOT_FIELD);
        }
        List<Long> manualDeviceIdList = requestBody.getManualDeviceIdList(); //get Manual Device IdList from input paramenter
        List<Long> judgeDeviceIdList = requestBody.getJudgeDeviceIdList(); //get Judge Device IdList from input paramenter
        List<Long> configDeviceIdList = requestBody.getFromDeviceIdList(); //get From Device IdList from input paramenter

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

        asyncController.updateSecurityDeviceDetail(sysDeviceConfig.getDevice().getGuid());
        //updateRedisValue(requestBody.getConfigId());
        return new CommonResponseBody(ResponseMessage.OK);
    }

    /**
     * Device Config update status request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/config/update", method = RequestMethod.POST)
    public Object deviceConfigUpdateStatus(
            @RequestBody @Valid DeviceConfigUpdateStatusRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            auditLogService.saveAudioLog(messageSource.getMessage("UpdateStatus", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("DeviceConfig", null, currentLocale),
                    messageSource.getMessage("ParameterError", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }
        SysDeviceConfig sysDeviceConfig = deviceConfigService.findConfigById(requestBody.getConfigId()); //get config by id through deviceConfigService

        if(sysDeviceConfig == null) {//check device config exist or not
            auditLogService.saveAudioLog(messageSource.getMessage("UpdateStatus", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("DeviceConfig", null, currentLocale),
                    messageSource.getMessage("ParameterError", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }
        Long deviceId = sysDeviceConfig.getDeviceId();
        if(requestBody.getStatus().equals(SysDeviceConfig.Status.INACTIVE)) {
            if(deviceConfigService.checkDeviceOnline(deviceId)) {
                auditLogService.saveAudioLog(messageSource.getMessage("UpdateStatus", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                        "", messageSource.getMessage("DeviceConfig", null, currentLocale),
                        messageSource.getMessage("DeviceOnline", null, currentLocale), "", null, false, "", "");
                return new CommonResponseBody(ResponseMessage.DEVICE_ONLINE);
            }
        } else {
            if(!deviceConfigService.checkDeviceHaveField(deviceId)) {
                auditLogService.saveAudioLog(messageSource.getMessage("UpdateStatus", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                        "", messageSource.getMessage("DeviceConfig", null, currentLocale),
                        messageSource.getMessage("Device.Error.NotField", null, currentLocale), "", null, false, "", "");
                return new CommonResponseBody(ResponseMessage.DEVICE_NOT_FIELD);
            }
        }

        deviceConfigService.updateStatusDeviceConfig(requestBody.getConfigId(), requestBody.getStatus()); //remove correspond manual group
        return new CommonResponseBody(ResponseMessage.OK);
    }

    /**
     * Device  config get all request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/config/get-all", method = RequestMethod.POST)
    public Object deviceConfigGetAll(@RequestBody @Valid DeviceConfigGetAllRequestBody requestBody,
                                     BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        List<SysDeviceConfig> sysDeviceConfigList = deviceConfigService.findAllDeviceConfigExceptId(requestBody.getDeviceId()); //get device config list from database through deviceConfigService

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, sysDeviceConfigList));
        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();
        filters.addFilter(ModelJsonFilters.FILTER_SYS_DEVICE_CATEGORY, SimpleBeanPropertyFilter.serializeAllExcept("parent")); // return all fields except parent from SysDeviceCategory model

        value.setFilters(filters);

        return value;
    }

    /**
     * Work Mode get all request.
     * @return
     */
    @RequestMapping(value = "/work-mode/get-all", method = RequestMethod.POST)
    public Object workModeGetAll() {

        List<SysWorkMode> sysWorkModeList = deviceConfigService.findAllWorkMode(); //get workmode list from database through service
        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, sysWorkModeList));

        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();
        filters.addFilter(ModelJsonFilters.FILTER_SYS_DEVICE, SimpleBeanPropertyFilter.filterOutAllExcept("deviceName")); //only return deviceName from SysDevice model
        value.setFilters(filters);

        return value;
    }

    /**
     * Manual Device get all request.
     * @return
     */
    @RequestMapping(value = "/manual-device/get-all", method = RequestMethod.POST)
    public Object manualDeviceGetAll() {

        List<SysManualDevice> sysManualDeviceList = deviceConfigService.findAllManualDevice(); //get manual device list from database through service
        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, sysManualDeviceList));

        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();
        filters.addFilter(ModelJsonFilters.FILTER_SYS_DEVICE, SimpleBeanPropertyFilter.filterOutAllExcept("deviceName")); //only return deviceName from SysDevice model
        value.setFilters(filters);

        return value;
    }

    /**
     * Judge Device get all request.
     * @return
     */
    @RequestMapping(value = "/judge-device/get-all", method = RequestMethod.POST)
    public Object judgeDeviceGetAll() {

        List<SysJudgeDevice> sysJudgeDeviceList = deviceConfigService.findAllJudgeDevice(); //get all judge device list from database through service

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, sysJudgeDeviceList));
        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();
        filters.addFilter(ModelJsonFilters.FILTER_SYS_DEVICE, SimpleBeanPropertyFilter.filterOutAllExcept("deviceName")); //only return deviceName from SysDevice model
        value.setFilters(filters);

        return value;
    }



}
