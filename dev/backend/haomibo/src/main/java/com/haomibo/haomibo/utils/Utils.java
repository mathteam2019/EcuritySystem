package com.haomibo.haomibo.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.haomibo.haomibo.config.Constants;

import java.util.Optional;

public class Utils {

    public static String removePrefixFromToken(String token) {
        return token.replace(Constants.TOKEN_PREFIX, "");
    }

    public static Optional<String> getTokenString(String header) {
        if (header == null) {
            return Optional.empty();
        } else {
            if (header.startsWith(Constants.TOKEN_PREFIX)) {
                return Optional.of(removePrefixFromToken(header));
            }
            return Optional.empty();
        }
    }
    public static String convertObjectToJson(Object object) throws JsonProcessingException {
        Optional<Object> opt = Optional.ofNullable(object);
        ObjectMapper objectMapper = new ObjectMapper();
        if (!opt.isPresent()) return null;
        return objectMapper.writeValueAsString(object);
    }
}
