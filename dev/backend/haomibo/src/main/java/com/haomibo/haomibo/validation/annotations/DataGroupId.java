package com.haomibo.haomibo.validation.annotations;

import com.haomibo.haomibo.validation.validators.DataGroupIdValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Data group Id validator annotation class.
 */
@Documented
@Constraint(validatedBy = DataGroupIdValidator.class)
@Target({ElementType.FIELD, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DataGroupId {
    String message() default "Invalid dataGroupId.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
