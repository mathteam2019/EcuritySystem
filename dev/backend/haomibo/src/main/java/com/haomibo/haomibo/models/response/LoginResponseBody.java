package com.haomibo.haomibo.models.response;

import com.haomibo.haomibo.models.reusables.Token;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LoginResponseBody {
    int userId;
    Token token;
}
