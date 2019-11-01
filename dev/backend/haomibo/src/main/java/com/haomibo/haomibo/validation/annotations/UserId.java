package com.haomibo.haomibo.validation.annotations;

import com.haomibo.haomibo.validation.validators.UserIdValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * User Id validator annotation class.
 */
@Documented
@Constraint(validatedBy = UserIdValidator.class)
@Target({ElementType.FIELD, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface UserId {
    String message() default "Invalid userId.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
