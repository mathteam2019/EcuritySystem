package com.nuctech.securitycheck.backgroundservice.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

/**
 * SerSeizedGood
 *
 * @author Choe
 * @version v1.0
 * @since 2020-01-12
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "ser_seized_goods")
public class SerSeizedGood extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GOODS_ID", length = 20, nullable = false)
    private Long goodsId;

    @Column(name = "SEIZED_GOODS", length = 255)
    private String seizedGoods;

    @Column(name = "SEIZED_GOODS_TYPE", length = 10)
    private String seizedGoodType;

    @Column(name = "SEIZED_GOODS_LEVEL", length = 10)
    private String seizedGoodsLevel;

    @Column(name = "GOOD_CODE", length = 10)
    private String goodCode;

}
