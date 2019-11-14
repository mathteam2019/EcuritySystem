/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/01
 * @CreatedBy Sandy.
 * @FileName DataGroupId.java
 * @ModifyHistory
 *
 */
package com.nuctech.ecuritycheckitem.validation.annotations;

import com.nuctech.ecuritycheckitem.validation.validators.DataGroupIdValidator;

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
