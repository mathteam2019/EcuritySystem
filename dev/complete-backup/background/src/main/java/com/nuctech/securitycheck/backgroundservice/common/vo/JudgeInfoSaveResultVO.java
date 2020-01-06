package com.nuctech.securitycheck.backgroundservice.common.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JudgeInfoSaveResult
 *
 * @author JuRyongPom
 * @version v1.0
 * @since 2019-12-01
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JudgeInfoSaveResultVO {

    private String guid;

    private String taskNumber;

    private Boolean isSucceed;

    private String workModeName;
    
}
