/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/10/14
 * @CreatedBy Sandy.
 * @FileName CommonResponseBody.java
 * @ModifyHistory
 *
 */
package com.nuctech.ecuritycheckitem.models.response;

import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
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
