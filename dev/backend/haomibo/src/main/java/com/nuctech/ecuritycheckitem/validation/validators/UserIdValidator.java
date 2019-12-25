/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（UserIdValidator)
 * 文件名：	UserIdValidator.java
 * 描述：	User Id validator.
 * 作者名：	Sandy
 * 日期：	2019/11/01
 *
 */

package com.nuctech.ecuritycheckitem.validation.validators;

import com.nuctech.ecuritycheckitem.models.db.QSysUser;
import com.nuctech.ecuritycheckitem.validation.annotations.UserId;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UserIdValidator extends BaseValidator implements ConstraintValidator<UserId, Long> {

    @Override
    public boolean isValid(Long userId, ConstraintValidatorContext constraintValidatorContext) {
        // Check if userId is valid from database.
        return sysUserRepository.exists(QSysUser.sysUser.userId.eq(userId));
    }
}
