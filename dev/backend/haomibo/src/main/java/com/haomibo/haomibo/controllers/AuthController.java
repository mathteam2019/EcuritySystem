package com.haomibo.haomibo.controllers;

import com.haomibo.haomibo.config.Constants;
import com.haomibo.haomibo.models.db.ForbiddenToken;
import com.haomibo.haomibo.models.db.QSysUser;
import com.haomibo.haomibo.models.db.SysUser;
import com.haomibo.haomibo.models.response.CommonResponseBody;
import com.haomibo.haomibo.models.reusables.Token;
import com.haomibo.haomibo.models.reusables.User;
import com.haomibo.haomibo.utils.Utils;
import com.querydsl.core.types.Predicate;
import lombok.*;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController extends BaseController {


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


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class LoginResponseBody {
        User user;
        Token token;
    }


    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Object login(
            @RequestBody @Valid LoginRequestBody requestBody,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(Constants.ResponseMessages.INVALID_PARAMETER);
        }

        QSysUser qSysUser = QSysUser.sysUser;
        Predicate predicate = qSysUser.email.eq(requestBody.getEmail());

        Optional<SysUser> optionalSysUser = sysUserRepository.findOne(predicate);

        if (!optionalSysUser.isPresent()) {
            return new CommonResponseBody(Constants.ResponseMessages.USER_NOT_FOUND);
        }

        SysUser sysUser = optionalSysUser.get();

        if (!sysUser.getPassword().equals(requestBody.getPassword())) {
            return new CommonResponseBody(Constants.ResponseMessages.INVALID_PASSWORD);
        }

        String token = jwtUtil.generateTokenForSysUser(sysUser);

        return new CommonResponseBody(
                Constants.ResponseMessages.OK,
                new LoginResponseBody(
                        new User(sysUser.getId(), sysUser.getName()),
                        new Token(token, jwtUtil.getExpirationDateFromToken(Utils.removePrefixFromToken(token)))
                )
        );
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public Object register(@RequestBody @Valid RegisterRequestBody requestBody, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(Constants.ResponseMessages.INVALID_PARAMETER);
        }

        QSysUser qSysUser = QSysUser.sysUser;
        boolean isEmailUsed = sysUserRepository.exists(qSysUser.email.eq(requestBody.getEmail()));

        if (isEmailUsed) {
            return new CommonResponseBody(Constants.ResponseMessages.USED_EMAIL);
        }

        try {
            SysUser sysUser = new SysUser();
            sysUser.setName(requestBody.getName());
            sysUser.setEmail(requestBody.getEmail());
            sysUser.setPassword(requestBody.getPassword());

            sysUserRepository.save(sysUser);

            return new CommonResponseBody(Constants.ResponseMessages.OK);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new CommonResponseBody(Constants.ResponseMessages.SERVER_ERROR);
    }

    @Secured({Constants.Roles.SYS_USER})
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public Object logout(
            @RequestHeader(value = Constants.REQUEST_HEADER_AUTH_TOKEN_KEY, defaultValue = "") String authToken) {

        authToken = Utils.removePrefixFromToken(authToken);

        ForbiddenToken forbiddenToken = new ForbiddenToken();

        forbiddenToken.setToken(authToken);

        forbiddenTokenRepository.save(forbiddenToken);
        forbiddenTokenRepository.flush();

        return new CommonResponseBody(Constants.ResponseMessages.OK);
    }

    @Secured({Constants.Roles.SYS_USER})
    @RequestMapping(value = "/refresh-token", method = RequestMethod.POST)
    public Object refreshToken(
            @RequestHeader(value = Constants.REQUEST_HEADER_AUTH_TOKEN_KEY, defaultValue = "") String authToken) {

        SysUser sysUser = (SysUser) authenticationFacade.getAuthentication().getPrincipal();

        String token = jwtUtil.generateTokenForSysUser(sysUser);

        return new CommonResponseBody(
                Constants.ResponseMessages.OK,
                new Token(token, jwtUtil.getExpirationDateFromToken(Utils.removePrefixFromToken(token)))
        );
    }
}
