package com.nuctech.securitycheck.backgroundservice.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * ForbiddenToken
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
@Table(name = "sys_forbidden_token")
public class ForbiddenToken implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TOKEN_ID", nullable = false)
    private Integer id;

    @Column(name = "TOKEN", nullable = false)
    private String token;

    @Column(name = "CREATEDTIME", nullable = false)
    private Date createdTime;

}

