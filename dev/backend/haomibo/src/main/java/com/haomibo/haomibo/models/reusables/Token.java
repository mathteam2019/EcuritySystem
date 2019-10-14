package com.haomibo.haomibo.models.reusables;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Token {

    public Token(String token, Date expirationDate) {
        this.token = token;
        this.expirationTimestamp = (int) (expirationDate.getTime() / 1000);
    }

    String token;
    int expirationTimestamp;
}
