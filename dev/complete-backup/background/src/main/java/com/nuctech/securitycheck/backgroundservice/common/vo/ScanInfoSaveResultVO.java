package com.nuctech.securitycheck.backgroundservice.common.vo;

import com.nuctech.securitycheck.backgroundservice.common.entity.SysDeviceConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ResultMessage
 *
 * @author JuRyongPom
 * @version v1.0
 * @since 2019-11-29
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScanInfoSaveResultVO {

    private Boolean isSucceed;

    private String taskNumber;

    private String guid;

    private TaskInfoVO taskInfoVO;

    private String workModeName;

    private int modeId;

    private String manualSwitch;

    private String atrMode;
    
}
