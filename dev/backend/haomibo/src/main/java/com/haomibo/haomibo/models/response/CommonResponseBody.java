package com.haomibo.haomibo.models.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CommonResponseBody {
    String message;
    Object data;
}
