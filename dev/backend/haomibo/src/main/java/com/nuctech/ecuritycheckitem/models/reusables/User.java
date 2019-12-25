/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（User）
 * 文件名：	User.java
 * 描述：	This class is used for user's information in login response.
 * 作者名：	Sandy
 * 日期：	2019/10/15
 */

package com.nuctech.ecuritycheckitem.models.reusables;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.Builder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder()
public class User {

    long id; //id
    String name; //name
    String category; //category
    String portrait; //portrait
}
