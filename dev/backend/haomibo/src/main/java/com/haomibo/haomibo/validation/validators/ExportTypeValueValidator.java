package com.haomibo.haomibo.validation.validators;

import com.haomibo.haomibo.enums.ExportType;
import com.haomibo.haomibo.validation.annotations.ExportTypeValue;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * User Id validator.
 */
public class ExportTypeValueValidator extends BaseValidator implements ConstraintValidator<ExportTypeValue, String> {

    @Override
    public boolean isValid(String exportType, ConstraintValidatorContext constraintValidatorContext) {
        // Check if export type is valid.
        return ExportType.PDF.getValue().equals(exportType) ||
                ExportType.DOCX.getValue().equals(exportType) ||
                ExportType.XLSX.getValue().equals(exportType);
    }
}
