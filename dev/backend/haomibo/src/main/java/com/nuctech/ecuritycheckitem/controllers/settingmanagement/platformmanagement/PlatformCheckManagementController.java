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
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.*;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.sun.istack.NotNull;
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

        @NotNull
        @Pattern(regexp = SerPlatformCheckParams.HistoryData.BUSINESS + "|" + SerPlatformCheckParams.HistoryData.CARTOON
                + "|" + SerPlatformCheckParams.HistoryData.CONVERSION+ "|" + SerPlatformCheckParams.HistoryData.ORIGINAL)
        String historyDataStorage;

        @NotNull
        @Pattern(regexp = SerPlatformCheckParams.HistoryData.BUSINESS + "|" + SerPlatformCheckParams.HistoryData.CARTOON
                + "|" + SerPlatformCheckParams.HistoryData.CONVERSION+ "|" + SerPlatformCheckParams.HistoryData.ORIGINAL)
        String historyDataExport;

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
                    .historyDataStorage(this.getHistoryDataStorage())
                    .historyDataExport(this.getHistoryDataExport())
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


        List<SerPlatformCheckParams> serPlatformCheckParamsList = serPlatformCheckParamRepository.findAll();


        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, serPlatformCheckParamsList));

        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();

        value.setFilters(filters);
        return value;
    }

    /**
     * Platform check modify request.
     */
    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    public Object platformCheckModify(
            @RequestBody @Valid PlatformCheckrModifyRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }




        SerPlatformCheckParams serPlatformCheckParams = requestBody.convert2SerPlatformCheckParams();
        List<SerPlatformCheckParams> serPlatformCheckParamsList = serPlatformCheckParamRepository.findAll();

        if(serPlatformCheckParamsList != null && serPlatformCheckParamsList.size() > 0) {
            serPlatformCheckParams.setScanId(serPlatformCheckParamsList.get(0).getScanId());
        }


        serPlatformCheckParamRepository.save(serPlatformCheckParams);

        return new CommonResponseBody(ResponseMessage.OK);
    }
}
