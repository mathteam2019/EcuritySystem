package com.haomibo.haomibo.models.reusables;

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
        this.expirationTimestamp = expirationDate.getTime();
    }

    String token;
    long expirationTimestamp;
}
