/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（PlatformOtherManagementController）
 * 文件名：	PlatformOtherManagementController.java
 * 描述：	Platform Other Management Controller.
 * 作者名：	Choe
 * 日期：	2019/11/21
 */

package com.nuctech.ecuritycheckitem.controllers.settingmanagement.platformmanagement;

import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.enums.Role;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.*;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.service.logmanagement.AuditLogService;
import com.nuctech.ecuritycheckitem.service.settingmanagement.PlatformOtherService;
import com.sun.istack.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.validation.BindingResult;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/system-setting/platform-other")
public class PlatformOtherManagementController extends BaseController {

    @Autowired
    PlatformOtherService platformOtherService;

    @Autowired
    AuditLogService auditLogService;

    @Autowired
    public MessageSource messageSource;



    /**
     * Platform other modify request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class PlatformOtherModifyRequestBody {
        @Size(max = 255)
        String initialPassword;
        @NotNull
        Long loginNumber;
        @NotNull
        Long logMaxNumber;
        @NotNull
        Integer deviceTrafficSettings;
        @NotNull
        Integer deviceTrafficHigh;
        @NotNull
        Integer deviceTrafficMiddle;
        @NotNull
        Integer storageDetectionCycle;
        @NotNull
        Integer storageAlarm;
        @NotNull
        Integer historyDataCycle;
        @NotNull
        Integer operatingTimeLimit;

        SerPlatformOtherParams convert2SerPlatformOtherParam() {//create new object from input parameters
            return SerPlatformOtherParams
                    .builder()
                    .initialPassword(this.getInitialPassword())
                    .loginNumber(this.getLoginNumber())
                    .logMaxNumber(this.getLogMaxNumber())
                    .deviceTrafficSettings(this.getDeviceTrafficSettings())
                    .deviceTrafficHigh(this.getDeviceTrafficHigh())
                    .deviceTrafficMiddle(this.getDeviceTrafficMiddle())
                    .storageDetectionCycle(this.getStorageDetectionCycle())
                    .storageAlarm(this.getStorageAlarm())
                    .historyDataCycle(this.getHistoryDataCycle())
                    .operatingTimeLimit(this.getOperatingTimeLimit())
                    .build();
        }
    }

    /**
     * PlatformOther  get request
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.POST)
    public Object platformOtherGet() {

        List<SerPlatformOtherParams> serPlatformOtherParamsList = platformOtherService.findAll();
        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, serPlatformOtherParamsList));
        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();
        value.setFilters(filters);

        return value;
    }

    /**
     * Platform other modify request.
     * @param requestBody
     * @param bindingResult
     * @return
     */
    //@PreAuthorize(Role.Authority.HAS_PLATFORM_OTHER_MODIFY)
    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    public Object platformOtherModify(
            @RequestBody @Valid PlatformOtherModifyRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            auditLogService.saveAudioLog(messageSource.getMessage("Modify", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("PlatformOther", null, currentLocale),
                    messageSource.getMessage("ParameterError", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        SerPlatformOtherParams serPlatformOtherParams = requestBody.convert2SerPlatformOtherParam();
        List<SerPlatformOtherParams> serPlatformOtherParamsList = platformOtherService.findAll();
        if(serPlatformOtherParamsList != null && serPlatformOtherParamsList.size() > 0) {
            serPlatformOtherParams.setId(serPlatformOtherParamsList.get(0).getId());
        }
        platformOtherService.modifyPlatform(serPlatformOtherParams);
        return new CommonResponseBody(ResponseMessage.OK);
    }
}
