package com.haomibo.haomibo.controllers;

import com.haomibo.haomibo.config.Constants;
import com.haomibo.haomibo.models.response.CommonResponseBody;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {


    @RequestMapping("/error")
    public Object handleError(HttpServletRequest request) {

        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());

            if (statusCode == HttpStatus.UNAUTHORIZED.value()) {
                Object messageObject = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
                if (messageObject != null) {
                    String message = (String) messageObject;
                    if (Constants.ResponseMessages.INVALID_TOKEN.equals(message)) {
                        return new ResponseEntity<>(new CommonResponseBody(Constants.ResponseMessages.INVALID_TOKEN), HttpStatus.OK);
                    }
                    if (Constants.ResponseMessages.TOKEN_EXPIRED.equals(message)) {
                        return new ResponseEntity<>(new CommonResponseBody(Constants.ResponseMessages.TOKEN_EXPIRED), HttpStatus.OK);
                    }
                }
                return new ResponseEntity<>(new CommonResponseBody(Constants.ResponseMessages.INVALID_TOKEN), HttpStatus.OK);

            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                return "error-500";
            }
        }

        return "error";
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }

}
