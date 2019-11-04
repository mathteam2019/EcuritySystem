package com.haomibo.haomibo.validation.validators;

import com.haomibo.haomibo.models.db.QSysRole;
import com.haomibo.haomibo.models.db.QSysUser;
import com.haomibo.haomibo.validation.annotations.RoleId;
import com.haomibo.haomibo.validation.annotations.UserId;

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
