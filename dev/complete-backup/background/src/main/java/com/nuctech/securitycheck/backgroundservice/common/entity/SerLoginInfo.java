package com.nuctech.securitycheck.backgroundservice.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * SerLoginInfo
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
@Table(name = "ser_login_info")
public class SerLoginInfo extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LOGIN_INFO_ID", length = 20, nullable = false)
    private Long loginInfoId;

    @Column(name = "USER_ID", length = 20)
    private Long userId;

    @Column(name = "DEVICE_ID", length = 20)
    private Long deviceId;

    @Column(name = "LOGIN_CATEGORY", length = 10)
    private String loginCategory;

    @Column(name = "TIME", nullable = false)
    private Date time;

    @Column(name = "LOGOUT_TIME", nullable = false)
    private Date logoutTime;

}
