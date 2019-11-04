package com.haomibo.haomibo.validation.annotations;

import com.haomibo.haomibo.validation.validators.RoleIdValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Role Id validator annotation class.
 */
@Documented
@Constraint(validatedBy = RoleIdValidator.class)
@Target({ElementType.FIELD, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RoleId {
    String message() default "Invalid roleId.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
