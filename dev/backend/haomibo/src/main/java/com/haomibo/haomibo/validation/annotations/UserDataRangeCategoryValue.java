package com.haomibo.haomibo.validation.annotations;

import com.haomibo.haomibo.validation.validators.UserDataRangeCategoryValueValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * User's data range category validator annotation class.
 */
@Documented
@Constraint(validatedBy = UserDataRangeCategoryValueValidator.class)
@Target({ElementType.FIELD, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface UserDataRangeCategoryValue {
    String message() default "Invalid category.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
