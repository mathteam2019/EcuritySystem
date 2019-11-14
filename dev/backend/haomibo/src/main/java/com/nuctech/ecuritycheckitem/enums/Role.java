/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/10/30
 * @CreatedBy Sandy.
 * @FileName Role.java
 * @ModifyHistory
 *
 */
package com.nuctech.ecuritycheckitem.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum for Roles which are used to protect Controller methods from users who are not granted with specified roles.
 */
@AllArgsConstructor
@Getter
public enum Role {

    ORG_CREATE("org_create"),
    ORG_MODIFY("org_modify"),
    ORG_DELETE("org_delete"),
    ROLE_CREATE("role_create"),
    ROLE_MODIFY("role_modify"),
    ROLE_DELETE("role_delete");

    // This is for @Preauthorize annotation.
    public static class Authority {

        // The Spring Security has ROLE_PREFIX. We can override Spring Security settings and change or remove the prefix but for now, we just respect it.
        public static final String ROLE_PREFIX = "ROLE_";

        public static final String HAS_ORG_CREATE = "hasRole('ROLE_org_create')";
        public static final String HAS_ORG_MODIFY = "hasRole('ROLE_org_modify')";
        public static final String HAS_ORG_DELETE = "hasRole('ROLE_org_delete')";
        public static final String HAS_ROLE_CREATE = "hasRole('ROLE_role_create')";
        public static final String HAS_ROLE_MODIFY = "hasRole('ROLE_role_modify')";
        public static final String HAS_ROLE_DELETE = "hasRole('ROLE_role_delete')";
    }

    private final String value;

}
