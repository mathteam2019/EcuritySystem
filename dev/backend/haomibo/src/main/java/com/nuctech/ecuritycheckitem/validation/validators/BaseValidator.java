package com.nuctech.ecuritycheckitem.validation.validators;

import com.nuctech.ecuritycheckitem.repositories.SysDataGroupRepository;
import com.nuctech.ecuritycheckitem.repositories.SysResourceRepository;
import com.nuctech.ecuritycheckitem.repositories.SysRoleRepository;
import com.nuctech.ecuritycheckitem.repositories.SysUserRepository;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Base validator class used for validators.
 */
public class BaseValidator {

    @Autowired
    SysDataGroupRepository sysDataGroupRepository;

    @Autowired
    SysUserRepository sysUserRepository;

    @Autowired
    SysRoleRepository sysRoleRepository;

    @Autowired
    SysResourceRepository sysResourceRepository;
}