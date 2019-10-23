package com.haomibo.haomibo.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.haomibo.haomibo.config.Constants;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    public static boolean saveFile(String directoryPath, String fileName, byte[] bytes) {

        File directory = new File(directoryPath);
        if (!directory.exists()) {
            boolean isCreated = directory.mkdirs();
            if (!isCreated) {
                return false;
            }
        }

        Path path = Paths.get(directoryPath + File.separator + fileName);

        try {
            Files.write(path, bytes);
        } catch (IOException e) {
            e.printStackTrace();

            return false;
        }

        return true;
    }
}
