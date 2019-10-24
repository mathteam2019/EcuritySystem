package com.haomibo.haomibo.controllers;

import com.haomibo.haomibo.jwt.JwtUtil;
import com.haomibo.haomibo.repositories.*;
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

    @Autowired
    public SysRoleRepository sysRoleRepository;

    @Autowired
    public SysDataGroupRepository sysDataGroupRepository;

    @Autowired
    public SysDataGroupUserRepository sysDataGroupUserRepository;

    @Autowired
    public SysUserGroupRepository sysUserGroupRepository;



}
