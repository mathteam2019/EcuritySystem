/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/01
 * @CreatedBy Sandy.
 * @FileName UserIdValidator.java
 * @ModifyHistory
 *
 */
package com.nuctech.ecuritycheckitem.validation.validators;

import com.nuctech.ecuritycheckitem.models.db.QSysUser;
import com.nuctech.ecuritycheckitem.validation.annotations.UserId;

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
