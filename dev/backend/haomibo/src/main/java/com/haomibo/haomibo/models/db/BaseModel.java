package com.haomibo.haomibo.models.db;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Date;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@SuperBuilder
public class BaseModel {
    @Column(name = "CREATEDBY", length = 20)
    Long createdBy;

    @Column(name = "CREATEDTIME", nullable = false)
    Date createdTime;

    @Column(name = "EDITEDBY", length = 20)
    Long editedBy;

    @Column(name = "EDITEDTIME", nullable = false)
    Date editedTime;

    @Column(name = "NOTE", length = 500, nullable = false)
    String note;
}
