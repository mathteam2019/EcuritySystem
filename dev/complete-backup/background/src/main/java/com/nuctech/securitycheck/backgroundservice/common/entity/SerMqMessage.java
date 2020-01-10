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
 * SerMqMessage
 *
 * @author Choe
 * @version v1.0
 * @since 2020-01-09
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "ser_mq_message")
public class SerMqMessage extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", length = 20, nullable = false)
    private Long id;

    @Column(name = "GUID", length = 255)
    private String guid;

    @Column(name = "TYPE", length = 2)
    private Integer type;

    @Column(name = "IMAGEGUID", length = 50)
    private String imageGuid;

    @Column(name = "MQ_KEY", length = 100)
    private String mqKey;

    @Column(name = "RESULT_CODE", length = 50)
    private String resultCode;

    @Column(name = "MQ_CONTENT", length = 1000)
    private String mqContent;


}