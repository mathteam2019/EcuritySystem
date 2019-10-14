package com.haomibo.haomibo.utils;

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
}
