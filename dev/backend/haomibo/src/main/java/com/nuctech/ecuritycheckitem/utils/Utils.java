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
import com.nuctech.ecuritycheckitem.config.ConstantDictionary;
import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.enums.ResponseMessage;
import com.nuctech.ecuritycheckitem.models.db.*;
import com.nuctech.ecuritycheckitem.models.response.CommonResponseBody;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.DetailTimeStatistics;
import com.nuctech.ecuritycheckitem.models.response.userstatistics.TotalTimeStatistics;
import com.nuctech.ecuritycheckitem.models.reusables.Token;
import com.nuctech.ecuritycheckitem.models.simplifieddb.HistorySimplifiedForHistoryTableManagement;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
            return 0l;
        }
    }

    public static Double parseDouble(Object obj) {
        try {
            return Double.parseDouble(obj.toString());
        } catch(Exception ex) {
            return 0.0;
        }
    }

    public static String convertSecond(long realTime) {
        if(realTime == 0) {
            return "0";
        }
        long time = realTime;
        long sec = time % 60;
        time = time / 60;
        long minutes = time % 60;
        time = time / 60;
        long hour = time % 24;
        long day = time / 24;
        String answer = "";
        if(day > 0) {
            answer = answer + "" + day + "d ";
        }
        if(hour > 0) {
            answer = answer + hour + "h ";
        }

        if(minutes > 0) {
            answer = answer + minutes + "m ";
        }

        if(sec > 0) {
            answer = answer + sec + "s";
        }

        return answer;
    }

    public static List<TotalTimeStatistics> convertTimeStatistcis(List<DetailTimeStatistics> detailTimeStatisticsList) {
        List<String> nameList = new ArrayList<>();
        List<String> timeList = new ArrayList<>();
        nameList.add(SysDevice.DeviceType.ALL);
        nameList.add(SysDevice.DeviceType.SECURITY);
        nameList.add(SysDevice.DeviceType.JUDGE);
        nameList.add(SysDevice.DeviceType.MANUAL);
        for(int i = 0; i < detailTimeStatisticsList.size(); i ++) {
            String name = detailTimeStatisticsList.get(i).getUserName();
            String time = detailTimeStatisticsList.get(i).getTime();
            if(!nameList.contains(name)) {
                nameList.add(name);
            }
            if(!timeList.contains(time)) {
                timeList.add(time);
            }
        }
        List<TotalTimeStatistics> answer = new ArrayList<>();
        for(int i = 0; i < timeList.size(); i ++) {
            List<DetailTimeStatistics> detailList = new ArrayList<>();
            for(int j = 0; j < nameList.size(); j ++) {
                DetailTimeStatistics statistics = new DetailTimeStatistics();
                statistics.setUserName(nameList.get(j));
                statistics.setWorkingTime(0);
                statistics.setTime(timeList.get(i));
                detailList.add(statistics);
            }
            TotalTimeStatistics totalTimeStatistics = new TotalTimeStatistics();
            totalTimeStatistics.setTime(timeList.get(i));
            totalTimeStatistics.setDetailedStatistics(detailList);
            answer.add(totalTimeStatistics);
        }

        for(int i = 0; i < detailTimeStatisticsList.size(); i ++) {
            DetailTimeStatistics detailTimeStatistics = detailTimeStatisticsList.get(i);
            String time = detailTimeStatistics.getTime();
            String name = detailTimeStatistics.getUserName();
            String deviceType = detailTimeStatistics.getDeviceType();
            Long workTime = detailTimeStatistics.getWorkingTime();
            int time_index = timeList.indexOf(time);
            int name_index = nameList.indexOf(name);
            TotalTimeStatistics totalTimeStatistics = answer.get(time_index);
            DetailTimeStatistics detailTimeStatisticsOriginanl = totalTimeStatistics.getDetailedStatistics().get(name_index);
            detailTimeStatisticsOriginanl.setWorkingTime(detailTimeStatisticsOriginanl.getWorkingTime() + workTime);

            //add to sum
            DetailTimeStatistics sumDetailTimeStatistics = totalTimeStatistics.getDetailedStatistics().get(0);
            sumDetailTimeStatistics.setWorkingTime(sumDetailTimeStatistics.getWorkingTime() + workTime);

            for(int j = 1; j <= 3; j ++) {
                if(nameList.get(j).equals(deviceType)) {
                    DetailTimeStatistics categoryDetailTimeStatistics = totalTimeStatistics.getDetailedStatistics().get(j);
                    categoryDetailTimeStatistics.setWorkingTime(categoryDetailTimeStatistics.getWorkingTime() + workTime);
                }
            }
        }
        return answer;
    }

    public static String getTaskResultFromKnowledge(SerKnowledgeCase task) {
        String taskResult = "";
        if(task.getHandTaskResult() != null) {
            if(StringUtils.isEmpty(task.getHandGoods())) {
                taskResult = ConstantDictionary.getDataValue(SerHandExamination.Result.FALSE);
            } else {
                taskResult = ConstantDictionary.getDataValue(SerHandExamination.Result.TRUE);
            }
        } else {
            boolean result = false;
            if(task.getJudgeUserId().equals(Constants.DEFAULT_SYSTEM_USER)) {
                if(SerScan.ATRResult.TRUE.equals(task.getScanAtrResult())) {
                    result = true;
                }
            } else {
                if(SerJudgeGraph.Result.TRUE.equals(task.getScanAtrResult())) {
                    result = true;
                }
            }
            if(result == false) {
                taskResult = ConstantDictionary.getDataValue("nodoubt");
            } else {
                taskResult = ConstantDictionary.getDataValue("doubt");
            }
        }
        return taskResult;
    }
    public static String getTaskResult(HistorySimplifiedForHistoryTableManagement task) {
        String taskResult = "";
        if(task.getHandTaskResult() != null) {
            if(StringUtils.isEmpty(task.getHandGoods())) {
                taskResult = ConstantDictionary.getDataValue(SerHandExamination.Result.FALSE);
            } else {
                taskResult = ConstantDictionary.getDataValue(SerHandExamination.Result.TRUE);
            }
        } else {
            boolean result = false;
            if(task.getJudgeUserId().equals(Constants.DEFAULT_SYSTEM_USER)) {
                if(SerScan.ATRResult.TRUE.equals(task.getScanAtrResult())) {
                    result = true;
                }
            } else {
                if(SerJudgeGraph.Result.TRUE.equals(task.getScanAtrResult())) {
                    result = true;
                }
            }
            if(result == false) {
                taskResult = ConstantDictionary.getDataValue("nodoubt");
            } else {
                taskResult = ConstantDictionary.getDataValue("doubt");
            }
        }
        return taskResult;
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

    public static String getGroupByTime(String statWidth) {
        List<String> timeKeyList = getTimeKeyByStatistics(statWidth);

        String groupBy = timeKeyList.get(0) + "(groupby)";
        if(timeKeyList.size() > 1) {
            for(int i = 1; i < timeKeyList.size(); i ++) {
                groupBy = groupBy + ", SPACE(1)," +  "LPAD(" + timeKeyList.get(i) + "(groupby), 2, 0)";
            }
            groupBy = "CONCAT(" + groupBy + ")";
        }
        return groupBy;
    }

    public static List<String> getTimeKeyByStatistics(String statisticsWidth) {
        String width;
        if(statisticsWidth == null || statisticsWidth.isEmpty()) {
            width = Constants.StatisticWidth.HOUR;
        } else {
            width = statisticsWidth;
        }
        List<String> answer = new ArrayList<>();
        switch (width) {
            case Constants.StatisticWidth.DAY:
                answer = new ArrayList<String>() {{
                    add(Constants.StatisticWidth.YEAR);
                    add(Constants.StatisticWidth.MONTH);
                    add(Constants.StatisticWidth.DAY);
                }};
                break;
            case Constants.StatisticWidth.WEEK:
                answer = new ArrayList<String>() {{
                    add(Constants.StatisticWidth.YEAR);
                    add(Constants.StatisticWidth.WEEK);
                }};
                break;
            case Constants.StatisticWidth.MONTH:
                answer = new ArrayList<String>() {{
                    add(Constants.StatisticWidth.YEAR);
                    add(Constants.StatisticWidth.MONTH);
                }};
                break;
            case Constants.StatisticWidth.QUARTER:
                answer = new ArrayList<String>() {{
                    add(Constants.StatisticWidth.YEAR);
                    add(Constants.StatisticWidth.QUARTER);
                }};
                break;
            case Constants.StatisticWidth.YEAR:
                answer = new ArrayList<String>() {{
                    add(Constants.StatisticWidth.YEAR);
                }};
                break;
            default:
                answer = new ArrayList<String>() {{
                    add(Constants.StatisticWidth.YEAR);
                    add(Constants.StatisticWidth.MONTH);
                    add(Constants.StatisticWidth.DAY);
                    add(Constants.StatisticWidth.HOUR);
                }};
                break;
        }
        return answer;
    }

    public static String formatDateByStatisticWidth(String statisticsWidth, String curDate) {
        String width;
        if(statisticsWidth == null || statisticsWidth.isEmpty()) {
            width = Constants.StatisticWidth.HOUR;
        } else {
            width = statisticsWidth;
        }
        String answer = "";
        String[] dateSplit = curDate.split(" ");
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        int year = 0;
        int month = 0;
        int day = 0;
        String startDate = "";
        String endDate = "";
        switch (width) {
            case Constants.StatisticWidth.DAY:
                answer = dateSplit[0] + "-" + dateSplit[1] + "-" + dateSplit[2];
                break;
            case Constants.StatisticWidth.WEEK:
                calendar.set(Calendar.YEAR, Integer.valueOf(dateSplit[0]));
                calendar.set(Calendar.WEEK_OF_YEAR, Integer.valueOf(dateSplit[1]));
                calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH) + 1;
                day = calendar.get(Calendar.DAY_OF_MONTH);
                startDate = year + "-" + String.format("%02d", month) + "-" + String.format("%02d", day);

                calendar.add(Calendar.DATE, 6);

                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH) + 1;
                day = calendar.get(Calendar.DAY_OF_MONTH);
                endDate = year + "-" + String.format("%02d", month) + "-" + String.format("%02d", day);
                answer = startDate + " ~ " + endDate;
                break;
            case Constants.StatisticWidth.MONTH:
                answer = dateSplit[0] + "-" + dateSplit[1];
                break;
            case Constants.StatisticWidth.QUARTER:
                answer = dateSplit[0] + "-" + dateSplit[1];
                break;
            case Constants.StatisticWidth.YEAR:
                answer = dateSplit[0];
                break;
            default:
                startDate = dateSplit[0] + "-" + dateSplit[1] + "-" + dateSplit[2] + " " + dateSplit[3] + ":00";

                calendar.set(Calendar.YEAR, Integer.valueOf(dateSplit[0]));
                calendar.set(Calendar.MONTH, Integer.valueOf(dateSplit[1]) - 1);
                calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(dateSplit[2]));
                calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(dateSplit[3]));
                calendar.add(Calendar.HOUR, 1);

                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH) + 1;
                day = calendar.get(Calendar.DAY_OF_MONTH);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);

                endDate = year + "-" + String.format("%02d", month) + "-" + String.format("%02d", day) + " " +
                        String.format("%02d", hour) + ":00";
                answer = startDate + " ~ " + endDate;
                break;
        }
        return answer;
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
