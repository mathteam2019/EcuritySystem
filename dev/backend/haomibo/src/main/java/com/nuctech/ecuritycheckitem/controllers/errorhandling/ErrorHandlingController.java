/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（ErrorHandlingController）
 * 文件名：	ErrorHandlingController.java
 * 描述：	This is used for handling errors.
 * 作者名：	Sandy
 * 日期：	2019/10/30
 */

package com.nuctech.ecuritycheckitem.controllers.errorhandling;

import com.nuctech.ecuritycheckitem.controllers.BaseController;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
public class ErrorHandlingController extends BaseController implements ErrorController {

    /**
     * Handle error.
     *
     * @param response The HttpServletResponse object.
     * @return The response.
     */
    @RequestMapping("/error")
    public Object handleError(HttpServletResponse response) {

        // Get error status.
        int status = response.getStatus();

        if (status == HttpStatus.FORBIDDEN.value()) {
            // Forbidden error.
            utils.writeForbbidenResponse(response, ResponseMessage.FORBIDDEN);
            return null;
        }
        if (status == HttpStatus.BAD_REQUEST.value()) {
            // Bad request. Usually this is from validation error.
            utils.writeResponse(response, ResponseMessage.BAD_REQUEST);
            return null;
        }

        return null;
    }

    /**
     * Overridden method for getting error path.
     *
     * @return The error path.
     */
    @Override
    public String getErrorPath() {
        return "/error";
    }
}
