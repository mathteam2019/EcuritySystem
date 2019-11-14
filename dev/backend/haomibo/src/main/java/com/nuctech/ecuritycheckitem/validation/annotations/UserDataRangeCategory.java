package com.nuctech.ecuritycheckitem.validation.annotations;

import com.nuctech.ecuritycheckitem.validation.validators.UserDataRangeCategoryValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * User's data range category validator annotation class.
 */
@Documented
@Constraint(validatedBy = UserDataRangeCategoryValidator.class)
@Target({ElementType.FIELD, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface UserDataRangeCategory {
    String message() default "Invalid category.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
