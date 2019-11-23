/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/21
 * @CreatedBy Choe.
 * @FileName PlatformOtherManagementController.java
 * @ModifyHistory
 */
package com.nuctech.ecuritycheckitem.controllers.settingmanagement.platformmanagement;


import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.*;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import org.springframework.web.bind.annotation.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.validation.BindingResult;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/system-setting/platform-other")
public class PlatformOtherManagementController extends BaseController {

    /**
     * Platform other modify request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class PlatformOtherModifyRequestBody {

        String initialPassword;

        Long loginNumber;

        Long logMaxNumber;

        Long deviceTrafficSettings;

        Long storageDetectionCycle;

        Long storageAlarm;

        Long historyDataCycle;


        SerPlatformOtherParams convert2SerPlatformOtherParam() {

            return SerPlatformOtherParams
                    .builder()
                    .initialPassword(this.getInitialPassword())
                    .loginNumber(this.getLoginNumber())
                    .logMaxNumber(this.getLogMaxNumber())
                    .deviceTrafficSettings(this.getDeviceTrafficSettings())
                    .storageDetectionCycle(this.getStorageDetectionCycle())
                    .storageAlarm(this.getStorageAlarm())
                    .historyDataCycle(this.getHistoryDataCycle())
                    .build();

        }

    }


    /**
     * PlatformOther  get request
     */
    @RequestMapping(value = "/get", method = RequestMethod.POST)
    public Object platformOtherGet() {


        List<SerPlatformOtherParams> serPlatformOtherParamsList = serPlatformOtherParamRepository.findAll();

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, serPlatformOtherParamsList));

        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();

        value.setFilters(filters);
        return value;
    }

    /**
     * Platform other modify request.
     */
    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    public Object platformOtherModify(
            @RequestBody @Valid PlatformOtherModifyRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }




        SerPlatformOtherParams serPlatformOtherParams = requestBody.convert2SerPlatformOtherParam();
        List<SerPlatformOtherParams> serPlatformOtherParamsList = serPlatformOtherParamRepository.findAll();

        if(serPlatformOtherParamsList != null && serPlatformOtherParamsList.size() > 0) {
            serPlatformOtherParams.setId(serPlatformOtherParamsList.get(0).getId());
        }


        serPlatformOtherParamRepository.save(serPlatformOtherParams);

        return new CommonResponseBody(ResponseMessage.OK);
    }
}
