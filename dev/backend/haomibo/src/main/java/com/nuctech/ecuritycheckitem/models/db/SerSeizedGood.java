/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（SerLoginInfo）
 * 文件名：	SerLoginInfo.java
 * 描述：	SerLoginInfo Model
 * 作者名：	Choe
 * 日期：	2019/11/26
 */


package com.nuctech.ecuritycheckitem.models.db;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@JsonFilter(ModelJsonFilters.FILTER_SER_SEIZED_GOOD)
@Table(name = "ser_seized_goods")
public class SerSeizedGood extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GOODS_ID", length = 20, nullable = false)
    private Long goodsId;

    @Column(name = "SEIZED_GOODS", length = 20)
    private String seizedGoods;

    @Column(name = "SEIZED_GOODS_TYPE", length = 10)
    private String seizedGoodType;

    @Column(name = "SEIZED_GOODS_LEVEL", length = 10)
    private String seizedGoodsLevel;
    
}
