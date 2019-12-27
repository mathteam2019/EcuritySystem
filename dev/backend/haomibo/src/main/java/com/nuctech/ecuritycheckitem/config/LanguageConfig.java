/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（Language Configuration）
 * 文件名：	LanguageConfig.java
 * 描述：	Configure language internationalize
 * 作者名：	Tiny
 * 日期：	2019/12/20
 *
 */

package com.nuctech.ecuritycheckitem.config;

import lombok.var;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;


@Configuration
public class LanguageConfig {

    @Bean
    public ResourceBundleMessageSource messageSource() {

        var source = new ResourceBundleMessageSource();

        source.setBasenames("language/messages");
        source.setDefaultEncoding("UTF-8");
        source.setUseCodeAsDefaultMessage(true);

        return source;
    }
}

