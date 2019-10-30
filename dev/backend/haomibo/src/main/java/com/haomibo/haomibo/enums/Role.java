package com.haomibo.haomibo.enums;

import lombok.Getter;

public enum Role {

    ORG_CREATE("org_create"),
    ORG_MODIFY("org_modify"),
    ORG_DELETE("org_delete"),
    ROLE_CREATE("role_create"),
    ROLE_MODIFY("role_modify"),
    ROLE_DELETE("role_delete");

    public static class Authority {

        public static final String ROLE_PREFIX = "ROLE_";
        public static final String HAS_ORG_CREATE = "hasRole('ROLE_org_create')";
        public static final String HAS_ORG_MODIFY = "hasRole('ROLE_org_modify')";
        public static final String HAS_ORG_DELETE = "hasRole('ROLE_org_delete')";
        public static final String HAS_ROLE_CREATE = "hasRole('ROLE_role_create')";
        public static final String HAS_ROLE_MODIFY = "hasRole('ROLE_role_modify')";
        public static final String HAS_ROLE_DELETE = "hasRole('ROLE_role_delete')";
    }

    @Getter
    private final String value;

    Role(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
