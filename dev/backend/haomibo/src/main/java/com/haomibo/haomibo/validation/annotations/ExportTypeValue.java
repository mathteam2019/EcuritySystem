package com.haomibo.haomibo.validation.annotations;

import com.haomibo.haomibo.validation.validators.ExportTypeValueValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Data group Id validator annotation class.
 */
@Documented
@Constraint(validatedBy = ExportTypeValueValidator.class)
@Target({ElementType.FIELD, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExportTypeValue {
    String message() default "Invalid exportType.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
