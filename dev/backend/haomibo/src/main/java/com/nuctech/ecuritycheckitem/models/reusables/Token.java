/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/10/15
 * @CreatedBy Sandy.
 * @FileName Token.java
 * @ModifyHistory
 *
 */
package com.nuctech.ecuritycheckitem.models.reusables;

import lombok.*;

import java.util.Date;

/**
 * This class represents user's token and its expiration timestamp.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Token {

    /**
     * Constructor. We need to convert Date object to timestamp.
     *
     * @param token          The token.
     * @param expirationDate The expiration date. The type is Date.
     */
    public Token(String token, Date expirationDate) {
        this.token = token;
        this.expirationTimestamp = expirationDate.getTime() / 1000;
    }

    String token;
    long expirationTimestamp;
}
