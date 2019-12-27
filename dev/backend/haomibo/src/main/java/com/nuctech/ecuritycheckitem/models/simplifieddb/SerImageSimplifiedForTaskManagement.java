/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（SerImage）
 * 文件名：	SerImage.java
 * 描述：	SerImage Model
 * 作者名：	Tiny
 * 日期：	2019/11/21
 */

package com.nuctech.ecuritycheckitem.models.simplifieddb;


import com.fasterxml.jackson.annotation.JsonFilter;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import com.nuctech.ecuritycheckitem.models.db.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@ToString()
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@JsonFilter(ModelJsonFilters.FILTER_SER_IMAGE)
@Table(name = "ser_image")
public class SerImageSimplifiedForTaskManagement extends BaseEntitySimple implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IMAGE_ID", length = 20)
    private Long imageId ;

    @Column(name = "IMAGE_URL", length = 200)
    private String imageUrl;

    @Column(name = "IMAGE_LABEL", length = 50)
    private String imageLabel;

}
