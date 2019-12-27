/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（UserDataRangeCategory)
 * 文件名：	UserDataRangeCategory.java
 * 描述：	User's data range category validator annotation class.
 * 作者名：	Sandy
 * 日期：	2019/11/1
 *
 */

package com.nuctech.ecuritycheckitem.validation.annotations;

import com.nuctech.ecuritycheckitem.validation.validators.UserDataRangeCategoryValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Constraint(validatedBy = UserDataRangeCategoryValidator.class)
@Target({ElementType.FIELD, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface UserDataRangeCategory {
    String message() default "Invalid category.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
