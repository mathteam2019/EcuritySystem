/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（RoleIdValidator)
 * 文件名：	RoleIdValidator.java
 * 描述：	Role Id Validator
 * 作者名：	Sandy
 * 日期：	2019/11/01
 *
 */


package com.nuctech.ecuritycheckitem.validation.validators;

import com.nuctech.ecuritycheckitem.models.db.QSysRole;
import com.nuctech.ecuritycheckitem.validation.annotations.RoleId;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Role Id validator.
 */
public class RoleIdValidator extends BaseValidator implements ConstraintValidator<RoleId, Long> {

    @Override
    public boolean isValid(Long roleId, ConstraintValidatorContext constraintValidatorContext) {
        // Check if roleId is valid from database.
        return sysRoleRepository.exists(QSysRole.sysRole.roleId.eq(roleId));
    }
}
