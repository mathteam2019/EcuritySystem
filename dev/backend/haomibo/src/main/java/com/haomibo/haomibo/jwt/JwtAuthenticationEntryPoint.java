package com.haomibo.haomibo.jwt;

import com.haomibo.haomibo.config.Constants;
import com.haomibo.haomibo.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.Optional;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {

    @Autowired
    private Utils utils;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        int k = 0;

        // TODO: This is still ambitious.... this is for handling authentication exceptions but what exception can be thrown ?

//        Optional<String> tokenStringOptional = utils.getTokenString(request.getHeader(Constants.REQUEST_HEADER_AUTH_TOKEN_KEY));
//
//        if (!tokenStringOptional.isPresent()) {
//            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, ResponseMessage.INVALID_TOKEN);
//        } else {
//            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, jwtUtil.getTokenStatus(tokenStringOptional.get()));
//        }


    }
}