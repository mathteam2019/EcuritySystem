package com.nuctech.securitycheck.backgroundservice.common.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TaskInfo
 *
 * @author JuRyongPom
 * @version v1.0
 * @since 2019-12-01
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskInfoVO {

    private Long taskId;

    private String customerGender;

    private String taskNumber;

}
