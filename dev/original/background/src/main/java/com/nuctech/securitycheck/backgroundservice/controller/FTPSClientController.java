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
import java.time.LocalDate;

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
            log.error(e.getMessage());
        }

        return false;
    }

}
