/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/21
 * @CreatedBy Choe.
 * @FileName PlatformCheckManagementController.java
 * @ModifyHistory
 */
package com.nuctech.ecuritycheckitem.controllers.settingmanagement.platformmanagement;

import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
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

        String historyDataStorage;

        String displayDataExport;

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
                    .displayDataExport(this.getDisplayDataExport())
                    .displayDeleteSuspicion(this.getDisplayDeleteSuspicion())
                    .displayDeleteSuspicionColour(this.getDisplayDeleteSuspicionColour())
                    .build();

        }

    }


    /**
     * Platform Chceck  get all request
     */
    @RequestMapping(value = "/get", method = RequestMethod.POST)
    public Object platformOtherGetAll() {


        List<SerPlatformCheckParams> serPlatformCheckParamsList = serPlatformCheckParamRepository.findAll();
        if(serPlatformCheckParamsList == null || serPlatformCheckParamsList.size() == 0) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(ResponseMessage.OK, serPlatformCheckParamsList));

        return value;
    }

    /**
     * Platform check modify request.
     */
    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    public Object platformCheckModify(
            @ModelAttribute @Valid PlatformCheckrModifyRequestBody requestBody,
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
