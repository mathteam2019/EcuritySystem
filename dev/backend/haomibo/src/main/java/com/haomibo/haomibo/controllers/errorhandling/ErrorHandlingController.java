package com.haomibo.haomibo.controllers.errorhandling;

import com.haomibo.haomibo.controllers.BaseController;
import com.haomibo.haomibo.enums.ResponseMessage;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * This is used for handling errors.
 */
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
            utils.writeResponse(response, ResponseMessage.FORBIDDEN);
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
