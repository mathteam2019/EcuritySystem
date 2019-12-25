/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（IAuthenticationFacade）
 * 文件名：	IAuthenticationFacade.java
 * 描述：	This interface is used to retrieve authentication information.
 * 作者名：	Sandy
 * 日期：	2019/10/15
 */

package com.nuctech.ecuritycheckitem.security;

import org.springframework.security.core.Authentication;

/**
 * This interface is used to retrieve authentication information.
 */
public interface IAuthenticationFacade {

    /**
     * @return Authentication information.
     */
    Authentication getAuthentication();

}