package com.haomibo.haomibo.validation.validators;

import com.haomibo.haomibo.models.db.QSysUser;
import com.haomibo.haomibo.validation.annotations.UserId;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * User Id validator.
 */
public class UserIdValidator extends BaseValidator implements ConstraintValidator<UserId, Long> {

    @Override
    public boolean isValid(Long userId, ConstraintValidatorContext constraintValidatorContext) {
        // Check if userId is valid from database.
        return sysUserRepository.exists(QSysUser.sysUser.userId.eq(userId));
    }
}
