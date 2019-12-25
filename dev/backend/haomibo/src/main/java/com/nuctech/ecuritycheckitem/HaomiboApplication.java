/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（HaomiboApplication)
 * 文件名：	HaomiboApplication.java
 * 描述：	Main entrypoint of the project
 * 作者名：	Sandy
 * 日期：	2019/10/11
 *
 */

package com.nuctech.ecuritycheckitem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HaomiboApplication {

    public static void main(String[] args) {
        SpringApplication.run(HaomiboApplication.class, args);
    }

}
