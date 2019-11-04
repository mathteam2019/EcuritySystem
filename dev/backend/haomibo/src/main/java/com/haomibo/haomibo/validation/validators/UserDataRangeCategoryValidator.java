package com.haomibo.haomibo.validation.validators;

import com.haomibo.haomibo.models.db.SysUser;
import com.haomibo.haomibo.validation.annotations.UserDataRangeCategory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * User's data range category validator.
 */
public class UserDataRangeCategoryValidator extends BaseValidator implements ConstraintValidator<UserDataRangeCategory, String> {

    @Override
    public boolean isValid(String dataRangeCategory, ConstraintValidatorContext constraintValidatorContext) {
        // Check if user's data range category is valid.

        return SysUser.DataRangeCategory.PERSON.getValue().equals(dataRangeCategory) ||
                SysUser.DataRangeCategory.ORG.getValue().equals(dataRangeCategory) ||
                SysUser.DataRangeCategory.ORG_DESC.getValue().equals(dataRangeCategory) ||
                SysUser.DataRangeCategory.ALL.getValue().equals(dataRangeCategory) ||
                SysUser.DataRangeCategory.SPECIFIED.getValue().equals(dataRangeCategory);

    }
}