/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（BaseEntity）
 * 文件名：	BaseEntity.java
 * 描述：	All Entity includes createdBy, createdTime, editedBy, editedTime and note.
 *          This class is base class for other entities.
 * 作者名：	Sandy
 * 日期：	2019/11/13
 */

package com.nuctech.ecuritycheckitem.models.simplifieddb;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.persistence.MappedSuperclass;

@Getter
@Setter
@ToString
@NoArgsConstructor
@MappedSuperclass
@SuperBuilder(toBuilder = true)

public class BaseEntitySimple {

}
