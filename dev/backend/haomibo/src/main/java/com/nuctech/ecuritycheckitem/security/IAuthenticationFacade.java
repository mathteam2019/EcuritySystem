/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/10/15
 * @CreatedBy Sandy.
 * @FileName IAuthenticationFacade.java
 * @ModifyHistory
 *
 */
package com.nuctech.ecuritycheckitem.security;

import org.springframework.security.core.Authentication;

/**
 * This interface is used to retrieve authentication information.
 */
public interface IAuthenticationFacade {

    /**
     * @return Authentication information.
     */
    Authentication getAuthentication();

}