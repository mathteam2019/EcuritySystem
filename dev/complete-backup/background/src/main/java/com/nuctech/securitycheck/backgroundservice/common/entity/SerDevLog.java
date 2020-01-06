package com.nuctech.securitycheck.backgroundservice.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;

/**
 * SerDevLog
 *
 * @author XiaYongXun
 * @version v1.0
 * @since 2019-11-28
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "ser_dev_log")
public class SerDevLog extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", length = 20, nullable = false)
    private Long id;

    @Column(name = "GUID", length = 255)
    private String guid;

    @Column(name = "DEV_TYPE", length = 10)
    private String devType;

    @Column(name = "LOGIN_NAME", length = 200)
    private String loginName;

    @Column(name = "CATEGORY", length = 2)
    private Integer category;

    @Column(name = "LEVEL", length = 11)
    private Integer level;

    @Column(name = "CONTENT", length = 2000)
    private String content;

    @Column(name = "TIME", length = 255)
    private String time;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "GUID", referencedColumnName = "GUID", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @MapsId("device")
    private SysDevice device;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LOGIN_NAME", referencedColumnName = "USER_ACCOUNT", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @MapsId("user")
    private SysUser user;

}