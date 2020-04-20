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

import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.reusables.Token;
import com.nuctech.ecuritycheckitem.models.reusables.User;
import com.nuctech.ecuritycheckitem.security.AuthenticationFacade;
import com.nuctech.ecuritycheckitem.service.auth.AuthService;
import com.nuctech.ecuritycheckitem.service.logmanagement.AccessLogService;
import com.nuctech.ecuritycheckitem.service.logmanagement.AuditLogService;
import com.nuctech.ecuritycheckitem.service.settingmanagement.PlatformOtherService;
import com.nuctech.ecuritycheckitem.utils.CryptUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Controller for user authentication.
 */
@RestController
@RequestMapping("/auth")
public class AuthController extends BaseController {



    @Autowired
    AuthService authService;

    @Autowired
    AccessLogService accessLogService;

    @Autowired
    PlatformOtherService platformOtherService;

    @Autowired
    public MessageSource messageSource;

    @Autowired
    AuthenticationFacade authenticationFacade;

    @Autowired
    AsyncController asyncController;

    @Autowired
    AuditLogService auditLogService;

    /**
     * Login request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class LoginRequestBody {
        @NotNull
        String userAccount;

        @NotNull
        String password;

        Integer count;
    }

    /**
     * Register request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class RegisterRequestBody {
        @NotNull
        @Email
        String email;

        @NotNull
        String password;
        @NotNull
        String name;

    }

    /**
     * Change Password request body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class ChangePasswordRequestBody {
        @NotNull
        String oldPassword;
        @NotNull
        String password;

    }


    /**
     * Login response body.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class LoginResponseBody {
        User user;
        Token token;
        List<SysDictionaryData> dictionaryDataList;
        List<SysDeviceDictionaryData> deviceDictionaryDataList;
        List<SysResource> permission;
    }



    /**
     * User login.
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Object login(
            @RequestBody @Valid LoginRequestBody requestBody,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }


        // Find user by his email address.
        SysUser sysUser = authService.getSysUserByUserAccount(requestBody.getUserAccount());

        if (sysUser == null) {
            // This is when no user is found.
            return new CommonResponseBody(ResponseMessage.USER_NOT_FOUND);
        }

        if((sysUser.getStatus().equals(SysUser.Status.PENDING))) {
            accessLogService.saveAccessLog(sysUser, messageSource.getMessage("Login", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale)
                    , messageSource.getMessage("Lock", null, currentLocale), null);
            return new CommonResponseBody(ResponseMessage.USER_PENDING_STATUS);
        }

        if((sysUser.getStatus().equals(SysUser.Status.BLOCKED))) {
            accessLogService.saveAccessLog(sysUser, messageSource.getMessage("Login", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale)
                    , messageSource.getMessage("Block", null, currentLocale), null);
            return new CommonResponseBody(ResponseMessage.USER_BLOCK_STATUS);
        }

        if(!(sysUser.getStatus().equals(SysUser.Status.ACTIVE))) {
            accessLogService.saveAccessLog(sysUser, messageSource.getMessage("Login", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale)
                    , messageSource.getMessage("NonActive", null, currentLocale), null);
            return new CommonResponseBody(ResponseMessage.USER_NON_ACTIVE_STATUS);
        }
        String password = sysUser.getPassword();
        if(password.equals(Constants.DEFAULT_PASSWORD_FOR_NEW_SYS_USER)) {
            password = platformOtherService.findAll().get(0).getInitialPassword();
        }

        if (CryptUtil.matches(requestBody.getPassword(), password) == false) {
        //if (!sysUser.getPassword().equals(requestBody.getPassword())) {
            // This is when the password is incorrect.
            int checkValue = authService.checkPendingUser(sysUser, requestBody.getCount());
            if(checkValue == 2) {
                accessLogService.saveAccessLog(sysUser, messageSource.getMessage("Login", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale)
                        , messageSource.getMessage("Lock", null, currentLocale), null);
                return new CommonResponseBody(ResponseMessage.USER_PENDING_STATUS);
            }
            if(checkValue == 1) {
                accessLogService.saveAccessLog(sysUser, messageSource.getMessage("Login", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale)
                        , messageSource.getMessage("PrePending", null, currentLocale), null);
                return new CommonResponseBody(ResponseMessage.PRE_USER_PENDING_STATUS);
            }
            accessLogService.saveAccessLog(sysUser, messageSource.getMessage("Login", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale)
                    , messageSource.getMessage("InvalidPassword", null, currentLocale), null);
            return new CommonResponseBody(ResponseMessage.INVALID_PASSWORD);
        }

        SerPlatformOtherParams serPlatformOtherParams = null;
        try {
            serPlatformOtherParams = platformOtherService.findAll().get(0);
        } catch(Exception ex) {}


        // Generate token for user.
        Token token = utils.generateTokenForSysUser(sysUser, serPlatformOtherParams);
        ForbiddenToken forbiddenToken = new ForbiddenToken();

        forbiddenToken.setToken(token.getToken());

        forbiddenTokenRepository.save(forbiddenToken);
        forbiddenTokenRepository.flush();

        List<SysResource> availableSysResourceList = authService.getAvailableSysResourceList(sysUser);

        accessLogService.saveAccessLog(sysUser, messageSource.getMessage("Login", null, currentLocale), messageSource.getMessage("Success", null, currentLocale)
                , "", null);

        List<SysDeviceDictionaryData> sysDeviceDictionaryDataList = authService.findAllDeviceDictionary();
        List<SysDictionaryData> sysDictionaryDataList =authService.findAllDictionary();

        MappingJacksonValue value = new MappingJacksonValue(new CommonResponseBody(
                ResponseMessage.OK,
                new LoginResponseBody(
                        User
                                .builder()
                                .id(sysUser.getUserId())
                                .name(sysUser.getUserName())
                                .portrait(sysUser.getPortrait())
                                .category(sysUser.getCategory())
                                .build(),
                        token,
                        sysDictionaryDataList,
                        sysDeviceDictionaryDataList,
                        availableSysResourceList
                )
        ));

        //asyncController.uploadCategoryToRedis();

        SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();
        filters.addFilter(ModelJsonFilters.FILTER_SYS_DICTONARY_DATA, SimpleBeanPropertyFilter.filterOutAllExcept("dictionaryId", "dataCode", "dataValue"))
                .addFilter(ModelJsonFilters.FILTER_SYS_DEVICE_DICTIONARY_DATA, SimpleBeanPropertyFilter.filterOutAllExcept("dictionaryName", "dictionaryId", "dataCode", "dataValue"))
                .addFilter(ModelJsonFilters.FILTER_SYS_RESOURCE, SimpleBeanPropertyFilter.filterOutAllExcept("resourceId", "parentResourceId", "resourceName", "resourceCaption"));

        value.setFilters(filters);

        return value;
    }

    /**
     * User logout.
     *
     * @param authToken Token from the header.
     */
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public Object logout(
            @RequestHeader(value = Constants.REQUEST_HEADER_AUTH_TOKEN_KEY, defaultValue = "") String authToken) {
        Long userId = (Long) authenticationFacade.getAuthentication().getPrincipal();
        SysUser user = authService.getUserById(userId);

        forbiddenTokenRepository.deleteAll(forbiddenTokenRepository.findAll(QForbiddenToken.forbiddenToken.token.eq(authToken)));

        accessLogService.saveAccessLog(user, messageSource.getMessage("Logout", null, currentLocale), messageSource.getMessage("Success", null, currentLocale)
                , "", null);
        return new CommonResponseBody(ResponseMessage.OK);
    }

    @RequestMapping(value = "/change-password", method = RequestMethod.POST)
    public Object changePassword(
            @RequestBody @Valid ChangePasswordRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            auditLogService.saveAudioLog(messageSource.getMessage("ModifyPassword", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("User", null, currentLocale),
                    messageSource.getMessage("ParameterError", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        Long userId = (Long) authenticationFacade.getAuthentication().getPrincipal();
        SysUser sysUser = authService.getUserById(userId);
        String password = sysUser.getPassword();
        if(password.equals(Constants.DEFAULT_PASSWORD_FOR_NEW_SYS_USER)) {
            password = platformOtherService.findAll().get(0).getInitialPassword();
        }
        if (CryptUtil.matches(requestBody.getOldPassword(), password) == false) {
        //if(!sysUser.getPassword().equals(requestBody.getOldPassword())) {
            auditLogService.saveAudioLog(messageSource.getMessage("ModifyPassword", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("User", null, currentLocale),
                    messageSource.getMessage("ParameterError", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.INVALID_PASSWORD);
        }
        String valueBefore = getJsonFromUser(sysUser);
        boolean result = authService.modifyPassword(sysUser.getUserId(), CryptUtil.encode(requestBody.getPassword()));
        if(result == false) {
            auditLogService.saveAudioLog(messageSource.getMessage("ModifyPassword", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale),
                    "", messageSource.getMessage("User", null, currentLocale),
                    messageSource.getMessage("ParameterError", null, currentLocale), "", null, false, "", "");
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        sysUser.setPassword(password);
        String valueAfter = getJsonFromUser(sysUser);
        auditLogService.saveAudioLog(messageSource.getMessage("ModifyPassword", null, currentLocale), messageSource.getMessage("Success", null, currentLocale),
                "", messageSource.getMessage("User", null, currentLocale), "", sysUser.getUserId().toString(), null, true, valueBefore, valueAfter);

        return new CommonResponseBody(ResponseMessage.OK);

    }

    public String getJsonFromUser(SysUser user) {
        SysUser newUser = SysUser.builder()
                .userId(user.getUserId())
                .orgId(user.getOrgId())
                .userName(user.getUserName())
                .userAccount(user.getUserAccount())
                .password(user.getPassword())
                .dataRangeCategory(user.getDataRangeCategory())
                .userNumber(user.getUserNumber())
                .gender(user.getGender())
                .identityCard(user.getIdentityCard())
                .post(user.getPost())
                .education(user.getEducation())
                .degree(user.getDegree())
                .email(user.getEmail())
                .mobile(user.getMobile())
                .address(user.getAddress())
                .category(user.getDataRangeCategory())
                .status(user.getStatus())
                .portrait(user.getPortrait())
                .taskId(user.getTaskId())
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        String answer = "";
        try {
            SimpleFilterProvider filters = ModelJsonFilters.getDefaultFilters();
            answer = objectMapper.writer(filters).writeValueAsString(newUser);
        } catch(Exception ex) {
        }
        return answer;
    }


    /**
     * Generate a new token.
     *
     * @param authToken Token from the header.
     * @return New token.
     */
    @RequestMapping(value = "/refresh-token", method = RequestMethod.POST)
    public Object refreshToken(
            @RequestHeader(value = Constants.REQUEST_HEADER_AUTH_TOKEN_KEY, defaultValue = "") String authToken) {

        Long userId = (Long) authenticationFacade.getAuthentication().getPrincipal();
        SysUser sysUser = authService.getUserById(userId);

        SerPlatformOtherParams serPlatformOtherParams = null;
        try {
            serPlatformOtherParams = platformOtherService.findAll().get(0);
        } catch(Exception ex) {}


        // Generate token for user.
        Token token = utils.generateTokenForSysUser(sysUser, serPlatformOtherParams);

        return new CommonResponseBody(
                ResponseMessage.OK,
                token
        );
    }
}
