package com.haomibo.haomibo.validation.validators;

import com.haomibo.haomibo.repositories.SysDataGroupRepository;
import com.haomibo.haomibo.repositories.SysRoleRepository;
import com.haomibo.haomibo.repositories.SysUserRepository;
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
}
