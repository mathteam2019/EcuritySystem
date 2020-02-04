/*
 * 版权所有 ( c ) 同方威视技术股份有限公司2019。保留所有权利。
 *
 * 本系统是商用软件，未经授权不得擅自复制或传播本程序的部分或全部
 *
 * 项目：	Haomibo V1.0（SysDeviceCategory）
 * 文件名：	SysDeviceCategory.java
 * 描述：	SysDeviceCategory Model
 * 作者名：	Choe
 * 日期：	2019/11/18
 */

package com.nuctech.ecuritycheckitem.models.db;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.nuctech.ecuritycheckitem.jsonfilter.ModelJsonFilters;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.ManyToOne;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import java.io.Serializable;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@JsonFilter(ModelJsonFilters.FILTER_SYS_DEVICE_CATEGORY)
@Table(name = "sys_device_category")
public class SysDeviceCategory extends BaseEntity implements Serializable {

    public static class Status {
        public static final String ACTIVE = "1000000701";
        public static final String INACTIVE = "1000000702";
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CATEGORY_ID", length = 20, nullable = false)
    private Long categoryId;


    @Column(name = "PARENT_CATEGORY_ID", length = 20)
    private Long parentCategoryId;

    @Column(name = "CATEGORY_NUMBER", length = 50)
    private String categoryNumber;

    @Column(name = "CATEGORY_NAME", length = 50)
    private String categoryName;

    @Column(name = "STATUS", length = 10)
    private String status;


    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_CATEGORY_ID", referencedColumnName = "CATEGORY_ID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @MapsId("parentCategory")
    private SysDeviceCategory parent;

}
