package com.haomibo.haomibo.models.db;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_forbidden_token")
public class ForbiddenToken implements Serializable {

    public ForbiddenToken(String token, int expirationTimestamp) {
        this.token = token;
        this.expirationTimestamp = expirationTimestamp;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "token", nullable = false)
    private String token;

    @Column(name = "expiration_timestamp", nullable = false)
    private int expirationTimestamp;

}

