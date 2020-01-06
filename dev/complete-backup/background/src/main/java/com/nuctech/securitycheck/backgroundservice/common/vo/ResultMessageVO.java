package com.nuctech.securitycheck.backgroundservice.common.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

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
public class ResultMessageVO implements Serializable {

    private String key;

    private Object content;

}
