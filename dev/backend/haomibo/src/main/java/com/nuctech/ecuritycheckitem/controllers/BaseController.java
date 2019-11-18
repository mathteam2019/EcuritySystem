/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/10/14
 * @CreatedBy Sandy.
 * @FileName BaseController.java
 * @ModifyHistory
 *
 */
package com.nuctech.ecuritycheckitem.controllers;

import com.nuctech.ecuritycheckitem.repositories.ForbiddenTokenRepository;
import com.nuctech.ecuritycheckitem.repositories.SysDeviceCategoryRepository;
import com.nuctech.ecuritycheckitem.repositories.SysOrgRepository;
import com.nuctech.ecuritycheckitem.repositories.SysUserRepository;
import com.nuctech.ecuritycheckitem.repositories.SysRoleRepository;
import com.nuctech.ecuritycheckitem.repositories.SysDataGroupRepository;
import com.nuctech.ecuritycheckitem.repositories.SysDataGroupUserRepository;
import com.nuctech.ecuritycheckitem.repositories.SysUserGroupRepository;
import com.nuctech.ecuritycheckitem.repositories.SysResourceRepository;
import com.nuctech.ecuritycheckitem.repositories.SysUserGroupUserRepository;
import com.nuctech.ecuritycheckitem.repositories.SysRoleResourceRepository;
import com.nuctech.ecuritycheckitem.repositories.SysRoleUserRepository;
import com.nuctech.ecuritycheckitem.repositories.SysUserGroupRoleRepository;
import com.nuctech.ecuritycheckitem.repositories.SysUserLookupRepository;
import com.nuctech.ecuritycheckitem.repositories.SysUserGroupLookupRepository;
import com.nuctech.ecuritycheckitem.repositories.SysFieldRepository;
import com.nuctech.ecuritycheckitem.security.AuthenticationFacade;
import com.nuctech.ecuritycheckitem.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The base controller for all controllers. This class defines common fields and methods.
 */
public class BaseController {

    @Autowired
    public SysOrgRepository sysOrgRepository;

    @Autowired
    public ForbiddenTokenRepository forbiddenTokenRepository;

    @Autowired
    public Utils utils;

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

    @Autowired
    public SysRoleUserRepository sysRoleUserRepository;

    @Autowired
    public SysUserGroupRoleRepository sysUserGroupRoleRepository;

    @Autowired
    public SysUserLookupRepository sysUserLookupRepository;

    @Autowired
    public SysUserGroupLookupRepository sysUserGroupLookupRepository;

    @Autowired
    public SysFieldRepository sysFieldRepository;

    @Autowired
    public SysDeviceCategoryRepository sysDeviceCategoryRepository;



}
