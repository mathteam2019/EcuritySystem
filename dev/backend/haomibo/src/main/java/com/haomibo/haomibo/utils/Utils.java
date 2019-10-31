package com.haomibo.haomibo.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.haomibo.haomibo.config.Constants;
import com.haomibo.haomibo.enums.ResponseMessage;
import com.haomibo.haomibo.models.db.SysUser;
import com.haomibo.haomibo.models.response.CommonResponseBody;
import com.haomibo.haomibo.models.reusables.Token;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

/**
 * This class contains some useful utility methods.
 */
@Component
public class Utils {


    @Value("${jwt.secret}")
    public String jwtSecret; // This is loaded from application.properties file.


    /**
     * Convert object to json string.
     *
     * @param object Target object used for conversion.
     * @return json string.
     * @throws JsonProcessingException
     */
    private String convertObjectToJson(Object object) throws JsonProcessingException {


        if (object == null) { // Return null if the object is null.
            return null;
        }

        // Make an object mapper from jackson.
        ObjectMapper objectMapper = new ObjectMapper();

        // Return json string.
        return objectMapper.writeValueAsString(object);
    }

    /**
     * Save file to given directory path.
     *
     * @param directoryPath Absolute or relative directory path.
     * @param fileName      File name.
     * @param bytes         Bytes data to write.
     * @return true if file is successfully written, otherwise false.
     */
    public boolean saveFile(String directoryPath, String fileName, byte[] bytes) {

        File directory = new File(directoryPath);
        if (!directory.exists()) { // Check if directory exists.
            boolean isCreated = directory.mkdirs(); // File class has mkdir() and mkdirs() methods.
            if (!isCreated) {
                // This is when the directory creation is failed.
                return false;
            }
        }

        // Create file path for writing.
        Path path = Paths.get(directoryPath + File.separator + fileName);

        try {
            // Write file.
            Files.write(path, bytes);
        } catch (IOException e) {
            e.printStackTrace();

            return false;
        }

        return true;
    }


    /**
     * Generate token for user.
     *
     * @param sysUser Target user.
     * @return Token string and its expiration time wrapped in Token class.
     */
    public Token generateTokenForSysUser(SysUser sysUser) {

        // Create claim.
        Claims claims = Jwts.claims().setSubject(String.valueOf(sysUser.getUserId()));

        // Save userId to claim.
        claims.put("userId", String.valueOf(sysUser.getUserId()));

        // Calculate expiration Date.
        Date expirationDate = new Date(System.currentTimeMillis() + Constants.JWT_VALIDITY_SECONDS * 1000);

        // Generate token.
        return new Token(Jwts.builder()
                .setClaims(claims)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact(), expirationDate);
    }


    /**
     * Write response using HttpServletResponse object. This is often used in filters and error handling parts.
     *
     * @param response The HttpServletResponse object.
     * @param message  Response message to write.
     */
    public void writeResponse(HttpServletResponse response, ResponseMessage message) {
        try {
            // We need to serialize response to JSON string.
            response.getWriter().write(this.convertObjectToJson(new CommonResponseBody(message)));
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            // Hope there won't any exception for this.
            e.printStackTrace();
        }
    }

}
