package com.haomibo.haomibo.models.request;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RegisterRequestBody {
    @NotNull
    @Email
    String email;

    @NotNull
    String password;
    @NotNull
    String name;


}
