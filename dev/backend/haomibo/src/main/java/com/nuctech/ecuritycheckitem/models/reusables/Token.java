/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（Token）
 * 文件名：	Token.java
 * 描述：	This class represents user's token and its expiration timestamp.
 * 作者名：	Sandy
 * 日期：	2019/10/24
 */


package com.nuctech.ecuritycheckitem.models.reusables;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Token {

    /**
     * Constructor. We need to convert Date object to timestamp.
     *
     * @param token          The token.
     * @param expirationDate The expiration date. The type is Date.
     */
    public Token(String token, Date expirationDate) {
        this.token = token;
        this.expirationTimestamp = expirationDate.getTime() / 1000;
    }

    String token; //token
    long expirationTimestamp; //expiration time stamp
}
