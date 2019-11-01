package com.haomibo.haomibo.controllers;

import com.haomibo.haomibo.config.Constants;
import com.haomibo.haomibo.enums.ResponseMessage;
import com.haomibo.haomibo.models.db.ForbiddenToken;
import com.haomibo.haomibo.models.db.QSysUser;
import com.haomibo.haomibo.models.db.SysUser;
import com.haomibo.haomibo.models.response.CommonResponseBody;
import com.haomibo.haomibo.models.reusables.Token;
import com.haomibo.haomibo.models.reusables.User;
import com.querydsl.core.types.Predicate;
import lombok.*;
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
        QSysUser qSysUser = QSysUser.sysUser;
        Predicate predicate = qSysUser.email.eq(requestBody.getEmail());

        Optional<SysUser> optionalSysUser = sysUserRepository.findOne(predicate);

        if (!optionalSysUser.isPresent()) {
            // This is when no user is found.
            return new CommonResponseBody(ResponseMessage.USER_NOT_FOUND);
        }

        SysUser sysUser = optionalSysUser.get();

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
