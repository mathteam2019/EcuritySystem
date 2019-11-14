/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/11/01
 * @CreatedBy Sandy.
 * @FileName RoleId.java
 * @ModifyHistory
 *
 */
package com.nuctech.ecuritycheckitem.validation.annotations;

import com.nuctech.ecuritycheckitem.validation.validators.RoleIdValidator;

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
