package com.nuctech.ecuritycheckitem.models.db;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
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

