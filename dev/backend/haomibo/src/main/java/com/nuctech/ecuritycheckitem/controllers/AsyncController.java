/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（AuthController 1.0）
 * 文件名：	AuthController.java
 * 描述：	Controller to process API requests of user authentications
 * 作者名：	Sandy
 * 日期：	2019/10/15
 *
 */

package com.nuctech.ecuritycheckitem.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.*;
import com.nuctech.ecuritycheckitem.models.redis.SerSecurityDeviceDetailModel;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.reusables.Token;
import com.nuctech.ecuritycheckitem.models.reusables.User;
import com.nuctech.ecuritycheckitem.security.AuthenticationFacade;
import com.nuctech.ecuritycheckitem.service.auth.AuthService;
import com.nuctech.ecuritycheckitem.service.devicemanagement.DeviceConfigService;
import com.nuctech.ecuritycheckitem.service.logmanagement.AccessLogService;
import com.nuctech.ecuritycheckitem.service.settingmanagement.PlatformOtherService;
import com.nuctech.ecuritycheckitem.utils.CryptUtil;
import com.nuctech.ecuritycheckitem.utils.RedisUtil;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.scheduling.annotation.Async;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Controller for user authentication.
 */
@RestController
@RequestMapping("/async")
public class AsyncController extends BaseController {



    @Autowired
    AuthService authService;

    @Autowired
    DeviceConfigService deviceConfigService;

    @Autowired
    RedisUtil redisUtil;

    @Async
    public void uploadCategoryToRedis() {
        authService.uploadCategoryUserListRedis();
    }


    @Async
    public void updateSecurityDeviceDetail(Long deviceId) {
        SerSecurityDeviceDetailModel detailModel = deviceConfigService.getSecurityInfoFromDatabase(deviceId);
        String guid = detailModel.getDeviceConfig().getSysDevice().getGuid();
        String redisKey = "sys.device.security.info";
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String dataStr = objectMapper.writeValueAsString(detailModel);
            String key = redisKey + guid;
            redisUtil.set(key, CryptUtil.encrypt(dataStr), Constants.EXPIRE_TIME);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
