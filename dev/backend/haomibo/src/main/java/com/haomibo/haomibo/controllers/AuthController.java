package com.haomibo.haomibo.controllers;

import com.haomibo.haomibo.config.Constants;
import com.haomibo.haomibo.models.db.ForbiddenToken;
import com.haomibo.haomibo.models.db.QSysUser;
import com.haomibo.haomibo.models.db.SysUser;
import com.haomibo.haomibo.models.request.LoginRequestBody;
import com.haomibo.haomibo.models.request.RegisterRequestBody;
import com.haomibo.haomibo.models.response.CommonResponseBody;
import com.haomibo.haomibo.models.response.LoginResponseBody;
import com.haomibo.haomibo.models.reusables.Token;
import com.haomibo.haomibo.models.reusables.User;
import com.haomibo.haomibo.repositories.ForbiddenTokenRepository;
import com.haomibo.haomibo.repositories.SysUserRepository;
import com.haomibo.haomibo.jwt.JwtUtil;
import com.haomibo.haomibo.security.AuthenticationFacade;
import com.haomibo.haomibo.utils.Utils;
import com.querydsl.core.types.Predicate;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController extends BaseController {
    @Autowired
    SysUserRepository sysUserRepository;

    @Autowired
    ForbiddenTokenRepository forbiddenTokenRepository;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    AuthenticationFacade authenticationFacade;


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
        boolean isEmailUsed = sysUserRepository.count(qSysUser.email.eq(requestBody.getEmail())) > 0;

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
        Date expirationToken = jwtUtil.getExpirationDateFromToken(authToken);
        int expirationTimestamp = (int) (expirationToken.getTime() / 1000);

        ForbiddenToken forbiddenToken = new ForbiddenToken(authToken, expirationTimestamp);

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
