/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（CommonResponseBody）
 * 文件名：	CommonResponseBody.java
 * 描述：	Container for response.
 * 作者名：	Sandy
 * 日期：	2019/10/14
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

    /**
     * Compose response with only message
     * @param message
     */
    public CommonResponseBody(ResponseMessage message) {
        this.message = message.getValue();
        this.data = null;
    }

    /**
     * Compose response with message and data
     * @param message
     * @param data
     */
    public CommonResponseBody(ResponseMessage message, Object data) {
        this.message = message.getValue();
        this.data = data;
    }
}
