/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（UserDataRangeCategoryValidator)
 * 文件名：	UserDataRangeCategoryValidator.java
 * 描述：	User's data range category validator.
 * 作者名：	Sandy
 * 日期：	2019/11/01
 *
 */

package com.nuctech.ecuritycheckitem.validation.validators;

import com.nuctech.ecuritycheckitem.models.db.SysUser;
import com.nuctech.ecuritycheckitem.validation.annotations.UserDataRangeCategory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UserDataRangeCategoryValidator extends BaseValidator implements ConstraintValidator<UserDataRangeCategory, String> {

    @Override
    public boolean isValid(String dataRangeCategory, ConstraintValidatorContext constraintValidatorContext) {
        // Check if user's data range category is valid.

        return SysUser.DataRangeCategory.PERSON.getValue().equals(dataRangeCategory) ||
                SysUser.DataRangeCategory.ORG.getValue().equals(dataRangeCategory) ||
                SysUser.DataRangeCategory.ORG_DESC.getValue().equals(dataRangeCategory) ||
                SysUser.DataRangeCategory.ALL.getValue().equals(dataRangeCategory) ||
                SysUser.DataRangeCategory.SPECIFIED.getValue().equals(dataRangeCategory) ||
                SysUser.DataRangeCategory.EVERYONE.getValue().equals(dataRangeCategory);

    }
}
