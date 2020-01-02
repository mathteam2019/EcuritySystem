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

import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;

import com.nuctech.ecuritycheckitem.models.db.SysDictionaryData;
import com.nuctech.ecuritycheckitem.models.db.SysDeviceDictionaryData;
import com.nuctech.ecuritycheckitem.models.db.SysResource;
import com.nuctech.ecuritycheckitem.models.db.SysUser;
import com.nuctech.ecuritycheckitem.models.db.ForbiddenToken;

import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.reusables.Token;
import com.nuctech.ecuritycheckitem.models.reusables.User;
import com.nuctech.ecuritycheckitem.security.AuthenticationFacade;
import com.nuctech.ecuritycheckitem.service.auth.AuthService;
import com.nuctech.ecuritycheckitem.service.logmanagement.AccessLogService;
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

    public static Locale currentLocale = Locale.CHINESE;

    @Autowired
    AuthService authService;

    @Autowired
    AccessLogService accessLogService;

    @Autowired
    public MessageSource messageSource;

    @Autowired
    AuthenticationFacade authenticationFacade;

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

        if(sysUser.getStatus().equals(SysUser.Status.PENDING)) {
            accessLogService.saveAccessLog(sysUser, messageSource.getMessage("Login", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale)
                    , messageSource.getMessage("Block", null, currentLocale), null);
            return new CommonResponseBody(ResponseMessage.USER_PENDING_STATUS);
        }

        if (!sysUser.getPassword().equals(requestBody.getPassword())) {
            // This is when the password is incorrect.
            authService.checkPendingUser(sysUser, requestBody.getCount());
            accessLogService.saveAccessLog(sysUser, messageSource.getMessage("Login", null, currentLocale), messageSource.getMessage("Fail", null, currentLocale)
                    , messageSource.getMessage("InvalidPassword", null, currentLocale), null);
            return new CommonResponseBody(ResponseMessage.INVALID_PASSWORD);
        }

        // Generate token for user.
        Token token = utils.generateTokenForSysUser(sysUser);

        List<SysResource> availableSysResourceList = authService.getAvailableSysResourceList(sysUser);

        accessLogService.saveAccessLog(sysUser, messageSource.getMessage("Login", null, currentLocale), messageSource.getMessage("Success", null, currentLocale)
                , "", null);

        List<SysDeviceDictionaryData> sysDeviceDictionaryDataList = authService.findAllDeviceDictionary();
        List<SysDictionaryData> sysDictionaryDataList = authService.findAllDictionary();

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
        SysUser user = (SysUser) authenticationFacade.getAuthentication().getPrincipal();
        ForbiddenToken forbiddenToken = new ForbiddenToken();

        forbiddenToken.setToken(authToken);

        forbiddenTokenRepository.save(forbiddenToken);
        forbiddenTokenRepository.flush();

        accessLogService.saveAccessLog(user, messageSource.getMessage("Logout", null, currentLocale), messageSource.getMessage("Success", null, currentLocale)
                , "", null);
        return new CommonResponseBody(ResponseMessage.OK);
    }

    @RequestMapping(value = "/change-password", method = RequestMethod.POST)
    public Object changePassword(
            @RequestBody @Valid ChangePasswordRequestBody requestBody,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        SysUser sysUser = (SysUser) authenticationFacade.getAuthentication().getPrincipal();

        boolean result = authService.modifyPassword(sysUser.getUserId(), requestBody.getPassword());
        if(result == false) {
            return new CommonResponseBody(ResponseMessage.INVALID_PARAMETER);
        }

        return new CommonResponseBody(ResponseMessage.OK);

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

        SysUser sysUser = (SysUser) authenticationFacade.getAuthentication().getPrincipal();

        Token token = utils.generateTokenForSysUser(sysUser);

        return new CommonResponseBody(
                ResponseMessage.OK,
                token
        );
    }
}
