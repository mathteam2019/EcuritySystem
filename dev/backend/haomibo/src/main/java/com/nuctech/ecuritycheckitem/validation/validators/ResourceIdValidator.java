/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（ResourceIdValidator)
 * 文件名：	ResourceIdValidator.java
 * 描述：	Resource Id validator.
 * 作者名：	Sandy
 * 日期：	2019/11/10
 *
 */

package com.nuctech.ecuritycheckitem.validation.validators;

import com.nuctech.ecuritycheckitem.models.db.QSysResource;
import com.nuctech.ecuritycheckitem.validation.annotations.ResourceId;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ResourceIdValidator extends BaseValidator implements ConstraintValidator<ResourceId, Long> {

    @Override
    public boolean isValid(Long resourceId, ConstraintValidatorContext constraintValidatorContext) {
        // Check if resourceId is valid from database.
        return sysResourceRepository.exists(QSysResource.sysResource.resourceId.eq(resourceId));
    }
}
