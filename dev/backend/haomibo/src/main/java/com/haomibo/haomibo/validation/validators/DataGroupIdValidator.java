package com.haomibo.haomibo.validation.validators;

import com.haomibo.haomibo.models.db.QSysDataGroup;
import com.haomibo.haomibo.validation.annotations.DataGroupId;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Data group Id validator.
 */
public class DataGroupIdValidator extends BaseValidator implements ConstraintValidator<DataGroupId, Long> {


    @Override
    public boolean isValid(Long dataGroupId, ConstraintValidatorContext constraintValidatorContext) {
        // Check if dataGroupId is valid from database.
        return sysDataGroupRepository.exists(QSysDataGroup.sysDataGroup.dataGroupId.eq(dataGroupId));
    }
}
