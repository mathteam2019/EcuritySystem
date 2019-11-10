package com.haomibo.haomibo.validation.validators;

import com.haomibo.haomibo.models.db.QSysResource;
import com.haomibo.haomibo.validation.annotations.ResourceId;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Resource Id validator.
 */
public class ResourceIdValidator extends BaseValidator implements ConstraintValidator<ResourceId, Long> {

    @Override
    public boolean isValid(Long resourceId, ConstraintValidatorContext constraintValidatorContext) {
        // Check if resourceId is valid from database.
        return sysResourceRepository.exists(QSysResource.sysResource.resourceId.eq(resourceId));
    }
}
