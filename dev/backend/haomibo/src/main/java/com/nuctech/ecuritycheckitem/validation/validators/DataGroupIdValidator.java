/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（DataGroupIdValidator)
 * 文件名：	DataGroupIdValidator.java
 * 描述：	Data group Id validator.
 * 作者名：	Sandy
 * 日期：	2019/11/1
 *
 */

package com.nuctech.ecuritycheckitem.validation.validators;

import com.nuctech.ecuritycheckitem.models.db.QSysDataGroup;
import com.nuctech.ecuritycheckitem.validation.annotations.DataGroupId;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Data group Id validator.
 */
public class DataGroupIdValidator extends BaseValidator implements ConstraintValidator<DataGroupId, Long> {


    @Override
    public boolean isValid(Long dataGroupId, ConstraintValidatorContext constraintValidatorContext) {
        // Check if dataGroupId is valid from database.
        return sysDataGroupRepository.exists(QSysDataGroup.sysDataGroup.dataGroupId.eq(dataGroupId));
    }
}
