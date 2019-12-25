/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（BaseValidator)
 * 文件名：	BaseValidator.java
 * 描述：	Base validator class used for validators.
 * 作者名：	Sandy
 * 日期：	2019/11/1
 *
 */

package com.nuctech.ecuritycheckitem.validation.validators;

import com.nuctech.ecuritycheckitem.repositories.SysDataGroupRepository;
import com.nuctech.ecuritycheckitem.repositories.SysResourceRepository;
import com.nuctech.ecuritycheckitem.repositories.SysRoleRepository;
import com.nuctech.ecuritycheckitem.repositories.SysUserRepository;
import org.springframework.beans.factory.annotation.Autowired;

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
