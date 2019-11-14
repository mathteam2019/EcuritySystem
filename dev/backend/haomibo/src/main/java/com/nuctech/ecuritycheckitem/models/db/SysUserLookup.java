package com.nuctech.ecuritycheckitem.models.db;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@ToString()
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Table(name = "sys_user_lookup")
public class SysUserLookup extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LOOKUP_ID", length = 20, nullable = false)
    Long lookupId;

    @Column(name = "USER_ID", length = 20)
    Long userId;

    @Column(name = "DATA_GROUP_ID", length = 20)
    Long dataGroupId;


}

