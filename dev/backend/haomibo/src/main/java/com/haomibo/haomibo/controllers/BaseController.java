package com.haomibo.haomibo.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.haomibo.haomibo.jwt.JwtUtil;
import com.haomibo.haomibo.repositories.*;
import com.haomibo.haomibo.security.AuthenticationFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;

public class BaseController {

    @Autowired
    public SysOrgRepository sysOrgRepository;

    @Autowired
    public ForbiddenTokenRepository forbiddenTokenRepository;

    @Autowired
    public JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper jacksonObjectMapper;

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

    @Autowired
    public SysResourceRepository sysResourceRepository;

    @Autowired
    public SysUserGroupUserRepository sysUserGroupUserRepository;

    @Autowired
    public SysRoleResourceRepository sysRoleResourceRepository;





}
