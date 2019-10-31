package com.haomibo.haomibo.models.response;

import com.haomibo.haomibo.enums.ResponseMessage;
import lombok.Getter;
import lombok.Setter;

/**
 * Container for response.
 */
@Getter
@Setter
public class CommonResponseBody {

    String message; // This field represents message. This will be one of ResponseMessage.
    Object data; // This field will hold response data.

    public CommonResponseBody(ResponseMessage message) {
        this.message = message.getValue();
        this.data = null;
    }

    public CommonResponseBody(ResponseMessage message, Object data) {
        this.message = message.getValue();
        this.data = data;
    }
}
