package com.haomibo.haomibo.models.request;

import lombok.*;

import javax.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LoginRequestBody {
    @NotNull
    @Email
    String email;

    @NotNull
    String password;
}
