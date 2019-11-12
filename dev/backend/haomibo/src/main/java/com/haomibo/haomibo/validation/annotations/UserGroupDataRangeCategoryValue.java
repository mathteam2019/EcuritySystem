package com.haomibo.haomibo.validation.annotations;

import com.haomibo.haomibo.validation.validators.UserGroupDataRangeCategoryValueValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * User group's data range category validator annotation class.
 */
@Documented
@Constraint(validatedBy = UserGroupDataRangeCategoryValueValidator.class)
@Target({ElementType.FIELD, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface UserGroupDataRangeCategoryValue {
    String message() default "Invalid category.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
