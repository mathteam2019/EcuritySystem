package com.nuctech.securitycheck.backgroundservice.service.impl;

import com.nuctech.securitycheck.backgroundservice.common.entity.SerPlatformOtherParams;
import com.nuctech.securitycheck.backgroundservice.common.enums.CommonConstant;
import com.nuctech.securitycheck.backgroundservice.controller.SchedulerController;
import com.nuctech.securitycheck.backgroundservice.service.ISerPlatformOtherParamsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

@Service
public class ZabbixSchedulerService implements SchedulingConfigurer {

    @Autowired
    SchedulerController schedulerController;

    @Autowired
    private ISerPlatformOtherParamsService iSerPlatformOtherParamsService;

    @Bean
    public TaskScheduler poolScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setThreadNamePrefix("Zabbix-");
        scheduler.setPoolSize(1);
        scheduler.initialize();
        return scheduler;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(poolScheduler());
        taskRegistrar.addTriggerTask(new Runnable() {
            @Override
            public void run() {
                // Do not put @Scheduled annotation above this method, we don't need it anymore.
                schedulerController.monitorServerFreeSpace();
            }
        }, new Trigger() {
            @Override
            public Date nextExecutionTime(TriggerContext triggerContext) {
                Calendar nextExecutionTime = new GregorianCalendar();
                Date lastActualExecutionTime = triggerContext.lastActualExecutionTime();
                nextExecutionTime.setTime(lastActualExecutionTime != null ? lastActualExecutionTime : new Date());
                int detectionCycle;
                try {
                    SerPlatformOtherParams serPlatformOtherParams = iSerPlatformOtherParamsService.getLastOtherParams();
                    detectionCycle = serPlatformOtherParams.getStorageDetectionCycle().intValue();
                } catch(Exception ex) {
                    detectionCycle = CommonConstant.DEFAULT_SCHEDULER_ZABBIX_TIME.getValue();
                }
                nextExecutionTime.add(Calendar.MINUTE, detectionCycle);
                return nextExecutionTime.getTime();
            }
        });
    }

}
