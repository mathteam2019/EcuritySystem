package com.haomibo.haomibo.controllers;

import com.haomibo.haomibo.config.Constants;
import com.haomibo.haomibo.models.db.QSysUser;
import com.haomibo.haomibo.models.db.SysUser;
import com.haomibo.haomibo.models.db.Test;
import com.haomibo.haomibo.models.request.LoginRequestBody;
import com.haomibo.haomibo.models.response.CommonResponseBody;
import com.haomibo.haomibo.models.response.LoginResponseBody;
import com.haomibo.haomibo.repositories.SysUserRepository;
import com.haomibo.haomibo.repositories.TestRepository;
import com.haomibo.haomibo.security.JwtTokenUtil;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController extends BaseController {
    @Autowired
    SysUserRepository sysUserRepository;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Object login(@RequestBody @Valid LoginRequestBody requestBody, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new CommonResponseBody(
                    Constants.ResponseMessages.INVALID_PARAMETER,
                    null
            );
        }
        QSysUser qSysUser = QSysUser.sysUser;
        Predicate predicate = qSysUser.email.eq(requestBody.getEmail());

        Optional<SysUser> optionalSysUser = sysUserRepository.findOne(predicate);

        if (!optionalSysUser.isPresent()) {
            return new CommonResponseBody(
                    Constants.ResponseMessages.USER_NOT_FOUND,
                    null
            );
        }

        SysUser sysUser = optionalSysUser.get();

        if (!sysUser.getPassword().equals(requestBody.getPassword())) {
            return new CommonResponseBody(
                    Constants.ResponseMessages.INVALID_PASSWORD,
                    null
            );
        }

        String token = jwtTokenUtil.generateToken(sysUser);

        return new CommonResponseBody(
                Constants.ResponseMessages.OK,
                new LoginResponseBody(
                        sysUser.getId(),
                        token
                )
        );
    }
}
