package com.haomibo.haomibo.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum for Roles which are used to protect Controller methods from users who are not granted with specified roles.
 */
@AllArgsConstructor
@Getter
public enum ExportType {

    PDF("pdf"),
    DOCX("docx"),
    XLSX("xlsx");

    private final String value;

}
