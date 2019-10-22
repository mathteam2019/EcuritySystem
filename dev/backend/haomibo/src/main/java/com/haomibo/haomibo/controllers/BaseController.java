package com.haomibo.haomibo.controllers;

import com.haomibo.haomibo.jwt.JwtUtil;
import com.haomibo.haomibo.repositories.ForbiddenTokenRepository;
import com.haomibo.haomibo.repositories.SysOrgRepository;
import com.haomibo.haomibo.repositories.SysUserRepository;
import com.haomibo.haomibo.security.AuthenticationFacade;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseController {

    @Autowired
    public SysOrgRepository sysOrgRepository;

    @Autowired
    public ForbiddenTokenRepository forbiddenTokenRepository;

    @Autowired
    public JwtUtil jwtUtil;

    @Autowired
    public AuthenticationFacade authenticationFacade;

    @Autowired
    public SysUserRepository sysUserRepository;

}
