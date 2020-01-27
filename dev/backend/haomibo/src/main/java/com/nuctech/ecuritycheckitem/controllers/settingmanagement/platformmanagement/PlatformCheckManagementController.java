/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（PlatformCheckManagementController）
 * 文件名：	PlatformCheckManagementController.java
 * 描述：	Platform Check Management Controller.
 * 作者名：	Choe
 * 日期：	2019/11/21
 */

package com.nuctech.ecuritycheckitem.controllers.settingmanagement.platformmanagement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.enums.Role;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.*;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.service.logmanagement.AuditLogService;
import com.nuctech.ecuritycheckitem.service.settingmanagement.PlatformCheckService;
import com.nuctech.ecuritycheckitem.utils.CryptUtil;
import com.nuctech.ecuritycheckitem.utils.RedisUtil;
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
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/system-setting/platform-check")
public class PlatformCheckManagementController extends BaseController {

    @Autowired
    PlatformCheckService platformCheckService;

    @Autowired
    AuditLogService auditLogService;

    @Autowired
    public MessageSource messageSource;

    @Autowired
    private RedisUtil redisUtil;



    /**
     * Platform check modify request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class PlatformCheckrModifyRequestBody {

        String scanRecogniseColour; //scan Recognize Colour
        Long scanOverTime; //scan OverTime
        Long judgeAssignTime; //judge AssignTime
        Long judgeProcessingTime; //judge Processing Time
        Long judgeScanOvertime; //judge ScanOvertime
        String judgeRecogniseColour; //judge Recognise Colour
        Long handOverTime; //hand OverTime
        String handRecogniseColour; //hand Recognise Colour
        List<String> historyDataStorageList; //history Data Storage List
        List<String> historyDataExportList; //history Data Export List
        String displayDeleteSuspicion; //display Delete Suspicion
        String displayDeleteSuspicionColour; //display Delete Suspicion Colour

        SerPlatformCheckParams convert2SerPlatformCheckParams() { //create new object from input parameters

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
     * @return
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
     * @param requestBody
     * @param bindingResult
     * @return
     */
    @PreAuthorize(Role.Authority.HAS_PLATFORM_CHECK_MODIFY)
    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    public Object platformCheckModify(
            @RequestBody @Valid PlatformCheckrModifyRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //return invalid parameter if input parameter validation failed
            auditLogService.saveAudioLog(messageSource.getMessage("Modify", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale)
                    , "", messageSource.getMessage("ParameterError", null, currentLocale), "",null);
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

        auditLogService.saveAudioLog(messageSource.getMessage("Modify", null, currentLocale), messageSource.getMessage("Success", null, currentLocale)
                , "", "", "",null);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String serPlatformCheckParamsStr = objectMapper.writeValueAsString(serPlatformCheckParams);
            serPlatformCheckParamsStr = CryptUtil.encrypt(serPlatformCheckParamsStr);
            redisUtil.set(("sys.setting.platform.check"),
                    serPlatformCheckParamsStr, 8 * 60 * 60);
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return new CommonResponseBody(ResponseMessage.OK);
    }
}
