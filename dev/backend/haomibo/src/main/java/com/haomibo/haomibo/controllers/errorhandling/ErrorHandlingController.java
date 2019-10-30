package com.haomibo.haomibo.controllers.errorhandling;

import com.haomibo.haomibo.controllers.BaseController;
import com.haomibo.haomibo.enums.ResponseMessage;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
public class ErrorHandlingController extends BaseController implements ErrorController {

    @RequestMapping("/error")
    public Object handleError(HttpServletResponse response) {

        int status = response.getStatus();

        if (status == HttpStatus.FORBIDDEN.value()) {
            utils.writeResponse(response, ResponseMessage.FORBIDDEN);
        }

        return null;
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}
