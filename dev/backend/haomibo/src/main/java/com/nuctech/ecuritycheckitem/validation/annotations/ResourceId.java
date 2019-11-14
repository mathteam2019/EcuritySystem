package com.nuctech.ecuritycheckitem.validation.annotations;

import com.nuctech.ecuritycheckitem.validation.validators.ResourceIdValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Resource Id validator annotation class.
 */
@Documented
@Constraint(validatedBy = ResourceIdValidator.class)
@Target({ElementType.FIELD, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ResourceId {
    String message() default "Invalid resourceId.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
