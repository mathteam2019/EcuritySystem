/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/01
 * @CreatedBy Sandy.
 * @FileName RoleIdValidator.java
 * @ModifyHistory
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
