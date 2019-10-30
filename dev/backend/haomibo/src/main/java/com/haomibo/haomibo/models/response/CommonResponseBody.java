package com.haomibo.haomibo.models.response;

import com.haomibo.haomibo.enums.ResponseMessage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonResponseBody {
    String message;
    Object data;

    public CommonResponseBody(ResponseMessage message) {
        this.message = message.getValue();
        this.data = null;
    }

    public CommonResponseBody(ResponseMessage message, Object data) {
        this.message = message.getValue();
        this.data = null;
    }
}
