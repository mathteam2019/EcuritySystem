/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/10/14
 * @CreatedBy Sandy.
 * @FileName AuthController.java
 * @ModifyHistory
 */
package com.nuctech.ecuritycheckitem.controllers;

import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.models.db.ForbiddenToken;
import com.nuctech.ecuritycheckitem.models.db.QSysUser;
import com.nuctech.ecuritycheckitem.models.db.SysUser;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.reusables.Token;
import com.nuctech.ecuritycheckitem.models.reusables.User;
import com.nuctech.ecuritycheckitem.service.AuthService;
import com.querydsl.core.types.Predicate;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.Optional;

/**
 * Controller for user authentication.
 */
@RestController
@RequestMapping("/auth")
public class AuthController extends BaseController {


    @Autowired
    AuthService authService;

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
        @Email
        String email;

        @NotNull
        String password;
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
        SysUser sysUser = authService.getSysUserByEmail(requestBody.getEmail());

        if (sysUser == null) {
            // This is when no user is found.
            return new CommonResponseBody(ResponseMessage.USER_NOT_FOUND);
        }

        if (!sysUser.getPassword().equals(requestBody.getPassword())) {
            // This is when the password is incorrect.
            return new CommonResponseBody(ResponseMessage.INVALID_PASSWORD);
        }

        // Generate token for user.
        Token token = utils.generateTokenForSysUser(sysUser);

        return new CommonResponseBody(
                ResponseMessage.OK,
                new LoginResponseBody(
                        User
                                .builder()
                                .id(sysUser.getUserId())
                                .name(sysUser.getUserName())
                                .portrait(sysUser.getPortrait())
                                .category(sysUser.getCategory())
                                .build(),
                        token
                )
        );
    }


    /**
     * User logout.
     *
     * @param authToken Token from the header.
     */
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public Object logout(
            @RequestHeader(value = Constants.REQUEST_HEADER_AUTH_TOKEN_KEY, defaultValue = "") String authToken) {

        ForbiddenToken forbiddenToken = new ForbiddenToken();

        forbiddenToken.setToken(authToken);

        forbiddenTokenRepository.save(forbiddenToken);
        forbiddenTokenRepository.flush();

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
