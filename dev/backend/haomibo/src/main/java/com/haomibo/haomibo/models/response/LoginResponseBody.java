package com.haomibo.haomibo.models.response;

import com.haomibo.haomibo.models.reusables.Token;
import com.haomibo.haomibo.models.reusables.User;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LoginResponseBody {
    User user;
    Token token;
}
