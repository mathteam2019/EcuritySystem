/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（PageResult<T>)
 * 文件名：	PageResult.java
 * 描述：	This class contains some useful utility methods.
 * 作者名：	Sandy
 * 日期：	2019/10/25
 *
 */

package com.nuctech.ecuritycheckitem.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.models.db.SerPlatformOtherParams;
import com.nuctech.ecuritycheckitem.models.db.SysUser;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.reusables.Token;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTP;
import org.springframework.beans.factory.annotation.Value;
import org.apache.commons.net.ftp.FTPSClient;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.util.ArrayList;
import java.util.Calendar;

@Component
@Slf4j
public class Utils {


    @Value("${jwt.secret}")
    public String jwtSecret; // This is loaded from application.properties file.

    //@Value("${ftp.server.host}")
    private String host;

    //@Value("${ftp.server.user}")
    private String user;

    //@Value("${ftp.server.password}")
    private String password;

    //@Value("${ftp.server.port}")
    private int port;

    public String ipAddress;

    @Value("${server.port}")
    String serverPort;

    @Value("${server.ipaddress}")
    String serverIpAddress;


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

    public static Integer parseInt(Object obj) {
        try {
            return Integer.parseInt(obj.toString());
        } catch(Exception ex) {
            return null;
        }
    }

    public static Long parseLong(Object obj) {
        try {
            return Long.parseLong(obj.toString());
        } catch(Exception ex) {
            return null;
        }
    }

    public static Double parseDouble(Object obj) {
        try {
            return Double.parseDouble(obj.toString());
        } catch(Exception ex) {
            return null;
        }
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
                log.error("Failed to create file directory");
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
            log.error("Failed to upload file");
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
    public Token generateTokenForSysUser(SysUser sysUser, SerPlatformOtherParams serPlatformOtherParams) {

        // Create claim.
        Claims claims = Jwts.claims().setSubject(String.valueOf(sysUser.getUserId()));

        // Save userId to claim.
        claims.put("userId", String.valueOf(sysUser.getUserId()));
        int jwt_validity_second;
        if(serPlatformOtherParams != null) {
            jwt_validity_second = serPlatformOtherParams.getOperatingTimeLimit() * 60;
        } else {
            jwt_validity_second = Constants.DEFAULT_JWT_VALIDITY_SECONDS;
        }


        // Calculate expiration Date.
        Date expirationDate = new Date(System.currentTimeMillis() + jwt_validity_second * 1000);

        // Generate token.
        return new Token(Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .setId(String.valueOf(System.currentTimeMillis()))
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


    /**
     * Write Forbbiden response using HttpServletResponse object. This is often used in filters and error handling parts.
     *
     * @param response The HttpServletResponse object.
     * @param message  Response message to write.
     */
    public void writeForbbidenResponse(HttpServletResponse response, ResponseMessage message) {
        try {
            // We need to serialize response to JSON string.
            response.getWriter().write(this.convertObjectToJson(new CommonResponseBody(message)));
            response.setStatus(HttpServletResponse.SC_CREATED);
        } catch (Exception e) {
            // Hope there won't any exception for this.
            e.printStackTrace();
        }
    }

    /**
     * save Image file
     * @param portraitFile
     * @return
     */
    public String saveImageFile(MultipartFile portraitFile) {
        if (portraitFile != null && !portraitFile.isEmpty()) {
            try {
                byte[] bytes = portraitFile.getBytes();
                String directoryPath = Constants.PORTRAIT_FILE_UPLOAD_DIRECTORY;
                String fileName = new Date().getTime() + "_" + portraitFile.getOriginalFilename();

                boolean isSucceeded = saveFile(directoryPath, fileName, bytes);
                String ip = "http://" + serverIpAddress + ":" + serverPort;
                if (isSucceeded) {
                    // Save file name.
                    //return ip + Constants.PORTRAIT_FILE_SERVING_BASE_URL + fileName;
                    return ip + Constants.PORTRAIT_FILE_SERVING_BASE_URL + fileName;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    /**
     * Get key value for statistics (eg: statisticsWidth - hour, return [0, 23])
     * @param statisticsWidth
     * @param startTime
     * @param endTime
     * @return
     */
    public static List<Integer> getKeyValuesforStatistics(String statisticsWidth, Date startTime, Date endTime) {

        Integer keyValueMin = 1, keyValueMax = 0;
        if (statisticsWidth != null && !statisticsWidth.isEmpty()) {
            switch (statisticsWidth) {
                case Constants.StatisticWidth.HOUR:
                    keyValueMin = 0;
                    keyValueMax = 23;
                    break;
                case Constants.StatisticWidth.DAY:
                    keyValueMax = 31;
                    break;
                case Constants.StatisticWidth.WEEK:
                    keyValueMax = 53;
                    break;
                case Constants.StatisticWidth.MONTH:
                    keyValueMax = 12;
                    break;
                case Constants.StatisticWidth.QUARTER:
                    keyValueMax = 4;
                    break;
                case Constants.StatisticWidth.YEAR:
                    Map<String, Integer> availableYearRage = getAvailableYearRange(startTime, endTime);
                    keyValueMax = availableYearRage.get("max");
                    keyValueMin = availableYearRage.get("min");
                    break;
                default:
                    keyValueMin = 0;
                    keyValueMax = -1;
                    break;
            }
        }

        List<Integer> result = new ArrayList<>();
        result.add(keyValueMin);
        result.add(keyValueMax);

        return result;
    }

    /**
     * return available Year range
     * @param startTime
     * @param endTime
     * @return
     */
    public static Map<String, Integer> getAvailableYearRange(Date startTime, Date endTime) {

        Integer keyValueMin = 0, keyValueMax = 0;

        Integer yearMax = Calendar.getInstance().get(Calendar.YEAR);
        Integer yearMin = 1970;
        Calendar calendar = Calendar.getInstance();

        if (startTime != null) {
            calendar.setTime(startTime);
            keyValueMin = calendar.get(Calendar.YEAR);
        } else {
            keyValueMin = Calendar.getInstance().get(Calendar.YEAR) - 10 + 1;
        }
        if (endTime != null) {
            calendar.setTime(endTime);
            keyValueMax = calendar.get(Calendar.YEAR);
        } else {
            keyValueMax = Calendar.getInstance().get(Calendar.YEAR);
        }
        if (keyValueMin < yearMin) {
            keyValueMin = yearMin;
        }
        if (keyValueMax > yearMax) {
            keyValueMax = yearMax;
        }

        Map<String, Integer> result = new HashMap<String, Integer>();

        result.put("min", keyValueMin);
        result.put("max", keyValueMax);

        return result;
    }

    /**
     * validate sort by param
     * @param sortBy
     * @return "sortBy": "userId", "order": "asc"
     */
    public static Map<String, String> getSortParams(String sortBy) {

        String[] sortParams = sortBy.split("\\|");
        if (sortParams.length != 2) {
            return null;
        }
        else {
            if (!sortParams[1].equals(Constants.SortOrder.ASC) && !sortParams[1].equals(Constants.SortOrder.DESC)) {
                return null;
            }
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("sortBy",sortParams[0]);
        params.put("order", sortParams[1]);

        return params;
    }

    public boolean fileUpload(@ApiParam("注册信息") MultipartFile file) {
        try {
            FTPSClient ftpClient = new FTPSClient();
            ftpClient.connect(host, port);
            ftpClient.login(user, password);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            LocalDate currentDate = LocalDate.now();
            String directoryName = "upload/" + currentDate.getYear() + "/" + currentDate.getMonth() + "/" + currentDate.getDayOfMonth();
            ftpClient.makeDirectory(directoryName);

            String firstRemoteFile = file.getOriginalFilename();
            BufferedInputStream inStream = new BufferedInputStream(file.getInputStream());
            boolean done = ftpClient.storeFile(firstRemoteFile, inStream);
            inStream.close();
            if (done) {
                return true;
            }
        } catch (Exception e) {
        }

        return false;
    }


}
