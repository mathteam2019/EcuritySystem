package com.nuctech.securitycheck.backgroundservice.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPSClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Date;

@Api(tags = "FTPSClientController")
@RestController
@Slf4j
@RequestMapping(value = "api/ftps/")
public class FTPSClientController {

    @Value("${ftp.server.host}")
    private String host;

    @Value("${ftp.server.user}")
    private String user;

    @Value("${ftp.server.password}")
    private String password;

    @Value("${ftp.server.port}")
    private int port;

    @ApiOperation("5.0.0.1 文档上传")
    @PostMapping("upload")
    public String fileUpload(@ApiParam("注册信息") MultipartFile file) {
        try {
//            FTPSClient ftpClient = new FTPSClient();
//            ftpClient.connect(host, port);
//            ftpClient.login(user, password);
//            ftpClient.enterLocalPassiveMode();
//            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
//
//            LocalDate currentDate = LocalDate.now();
//            String directoryName = "upload/" + currentDate.getYear() + "/" + currentDate.getMonth() + "/" + currentDate.getDayOfMonth();
//            ftpClient.makeDirectory(directoryName);
//
//            String firstRemoteFile = file.getOriginalFilename();
//            BufferedInputStream inStream = new BufferedInputStream(file.getInputStream());
//            boolean done = ftpClient.storeFile(firstRemoteFile, inStream);
//            inStream.close();
            String fileName = saveImageFile(file);
            //if (done) {
                return fileName;
            //}
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return "";
    }

    public String saveImageFile(MultipartFile portraitFile) {
        if (portraitFile != null && !portraitFile.isEmpty()) {
            try {
                byte[] bytes = portraitFile.getBytes();
                String directoryPath = "portrait";
                String fileName = new Date().getTime() + "_" + portraitFile.getOriginalFilename();

                boolean isSucceeded = saveFile(directoryPath, fileName, bytes);
                String ip = "http://" + InetAddress.getLocalHost().getHostAddress() + ":" + "8081";
                if (isSucceeded) {
                    // Save file name.
                    //return ip + Constants.PORTRAIT_FILE_SERVING_BASE_URL + fileName;
                    return ip + "/portrait/" + fileName;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }


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

}
