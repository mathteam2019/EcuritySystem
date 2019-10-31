package com.haomibo.haomibo.security;

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