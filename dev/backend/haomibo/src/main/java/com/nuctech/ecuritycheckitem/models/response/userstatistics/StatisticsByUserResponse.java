package com.nuctech.ecuritycheckitem.models.response.userstatistics;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatisticsByUserResponse {

    long totalSeconds;
    long scanSeconds;
    long judgeSeconds;
    long handSeconds;

}