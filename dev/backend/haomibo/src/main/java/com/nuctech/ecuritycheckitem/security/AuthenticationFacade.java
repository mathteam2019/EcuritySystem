/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/10/15
 * @CreatedBy Sandy.
 * @FileName AuthenticationFacade.java
 * @ModifyHistory
 *
 */
package com.nuctech.ecuritycheckitem.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Implementation of IAuthenticationFacade.
 */
@Component
public class AuthenticationFacade implements IAuthenticationFacade {

    /**
     * Empty constructor.
     */
    public AuthenticationFacade() {
        super();
    }

    @Override
    public final Authentication getAuthentication() {
        // Return saved authentication information in SecurityContextHolder.
        return SecurityContextHolder.getContext().getAuthentication();
    }

}