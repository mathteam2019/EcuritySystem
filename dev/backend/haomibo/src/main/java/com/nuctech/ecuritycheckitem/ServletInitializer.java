/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（ServletInitializer)
 * 文件名：	ServletInitializer.java
 * 描述：	Initialize spring servlet.
 * 作者名：	Sandy
 * 日期：	2019/10/11
 *
 */

package com.nuctech.ecuritycheckitem;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

public class ServletInitializer extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(HaomiboApplication.class);
    }

}
