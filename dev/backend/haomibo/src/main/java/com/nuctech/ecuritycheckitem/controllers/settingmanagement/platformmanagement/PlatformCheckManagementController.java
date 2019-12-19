/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/21
 * @CreatedBy Choe.
 * @FileName PlatformCheckManagementController.java
 * @ModifyHistory
 */
package com.nuctech.ecuritycheckitem.controllers.settingmanagement.platformmanagement;

import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.enums.Role;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.*;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.service.settingmanagement.PlatformCheckService;
import com.sun.istack.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.validation.BindingResult;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.util.List;


@RestController
@RequestMapping("/system-setting/platform-check")
public class PlatformCheckManagementController extends BaseController {

    @Autowired
    PlatformCheckService platformCheckService;
    /**
     * Platform check modify request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class PlatformCheckrModifyRequestBody {

        String scanRecogniseColour;

        Long scanOverTime;

        Long judgeAssignTime;

        Long judgeProcessingTime;

        Long judgeScanOvertime;

        String judgeRecogniseColour;

        Long handOverTime;

        String handRecogniseColour;


        List<String> historyDataStorageList;


        List<String> historyDataExportList;

        Long displayDeleteSuspicion;

        String displayDeleteSuspicionColour;


        SerPlatformCheckParams convert2SerPlatformCheckParams() {

            return SerPlatformCheckParams
                    .builder()
                    .scanRecogniseColour(this.getScanRecogniseColour())
                    .scanOverTime(this.getScanOverTime())
                    .judgeAssignTime(this.getJudgeAssignTime())
                    .judgeProcessingTime(this.getJudgeProcessingTime())
                    .judgeScanOvertime(this.getJudgeScanOvertime())
                    .judgeRecogniseColour(this.getJudgeRecogniseColour())
                    .handOverTime(this.getHandOverTime())
                    .handRecogniseColour(this.getHandRecogniseColour())
                    .displayDeleteSuspicion(this.getDisplayDeleteSuspicion())
                    .displayDeleteSuspicionColour(this.getDisplayDeleteSuspicionColour())
                    .build();

        }

    }


    /**
     * Platform Check  get request
     */
    @RequestMapping(value = "/get", method = RequestMethod.POST)
    public Object platformCheckGet() {


        List<SerPlatformCheckParams> serPlatformCheckParamsList = platformCheckService.findAll();


        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, serPlatformCheckParamsList));

        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();

        value.setFilters(filters);
        return value;
    }

    /**
     * Platform check modify request.
     */
    @PreAuthorize(Role.Authority.HAS_PLATFORM_CHECK_MODIFY)
    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    public Object platformCheckModify(
            @RequestBody @Valid PlatformCheckrModifyRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        SerPlatformCheckParams serPlatformCheckParams = requestBody.convert2SerPlatformCheckParams();
        serPlatformCheckParams.setHistoryDataStorage(String.join(",", requestBody.getHistoryDataStorageList()));
        serPlatformCheckParams.setHistoryDataExport(String.join(",", requestBody.getHistoryDataExportList()));
        List<SerPlatformCheckParams> serPlatformCheckParamsList = platformCheckService.findAll();

        boolean isCreate = true;

        if(serPlatformCheckParamsList != null && serPlatformCheckParamsList.size() > 0) {
            serPlatformCheckParams.setScanId(serPlatformCheckParamsList.get(0).getScanId());
            serPlatformCheckParams.setCreatedBy(serPlatformCheckParamsList.get(0).getCreatedBy());
            serPlatformCheckParams.setCreatedTime(serPlatformCheckParamsList.get(0).getCreatedTime());
            isCreate = false;
        }

        platformCheckService.modifyPlatform(serPlatformCheckParams, isCreate);

        return new CommonResponseBody(ResponseMessage.OK);
    }
}
