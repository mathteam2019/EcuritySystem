package com.haomibo.haomibo.validation.validators;

import com.haomibo.haomibo.models.db.SysUser;
import com.haomibo.haomibo.models.db.SysUserGroup;
import com.haomibo.haomibo.validation.annotations.UserGroupDataRangeCategoryValue;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * User groups's data range category validator.
 */
public class UserGroupDataRangeCategoryValueValidator extends BaseValidator implements ConstraintValidator<UserGroupDataRangeCategoryValue, String> {

    @Override
    public boolean isValid(String dataRangeCategory, ConstraintValidatorContext constraintValidatorContext) {
        // Check if user group's data range category is valid.

        return SysUserGroup.DataRangeCategory.PERSON.getValue().equals(dataRangeCategory) ||
                SysUserGroup.DataRangeCategory.GROUP.getValue().equals(dataRangeCategory) ||
                SysUserGroup.DataRangeCategory.ALL.getValue().equals(dataRangeCategory) ||
                SysUserGroup.DataRangeCategory.SPECIFIED.getValue().equals(dataRangeCategory);

    }
}
