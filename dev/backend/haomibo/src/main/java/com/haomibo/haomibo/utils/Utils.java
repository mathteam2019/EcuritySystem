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
import java.util.Optional;

@Component
public class Utils {


    @Value("${jwt.secret}")
    public String jwtSecret;


    public String convertObjectToJson(Object object) throws JsonProcessingException {
        Optional<Object> opt = Optional.ofNullable(object);
        ObjectMapper objectMapper = new ObjectMapper();
        if (!opt.isPresent()) return null;

        return objectMapper.writeValueAsString(object);
    }

    public boolean saveFile(String directoryPath, String fileName, byte[] bytes) {

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


    public Token generateTokenForSysUser(SysUser sysUser) {

        Claims claims = Jwts.claims().setSubject(String.valueOf(sysUser.getUserId()));

        claims.put("userId", String.valueOf(sysUser.getUserId()));

        Date expirationDate = new Date(System.currentTimeMillis() + Constants.JWT_VALIDITY_SECONDS * 1000);

        return new Token(Jwts.builder()
                .setClaims(claims)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact(), expirationDate);
    }


    public void writeResponse(HttpServletResponse response, ResponseMessage message) {
        // write response for request
        try {
            // we need to serialize response to JSON string
            response.getWriter().write(this.convertObjectToJson(new CommonResponseBody(message)));
        } catch (Exception e) {
            // hope there won't any exception for this
            e.printStackTrace();
        }
    }

}
