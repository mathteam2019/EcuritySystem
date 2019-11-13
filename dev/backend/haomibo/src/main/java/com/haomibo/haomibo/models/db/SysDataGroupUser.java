package com.haomibo.haomibo.models.db;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Getter
@Setter
@ToString()
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Table(name = "sys_data_group_user")
public class SysDataGroupUser extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DATA_GROUP_USER_ID", length = 20, nullable = false)
    Long dataGroupUserId;

    @Column(name = "USER_ID", length = 20)
    Long userId;

    @Column(name = "DATA_GROUP_ID", length = 20)
    Long dataGroupId;

}

