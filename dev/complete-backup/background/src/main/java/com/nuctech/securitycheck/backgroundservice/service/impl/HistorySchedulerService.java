package com.nuctech.securitycheck.backgroundservice.service.impl;

import com.nuctech.securitycheck.backgroundservice.common.entity.QHistory;
import com.nuctech.securitycheck.backgroundservice.common.entity.SerPlatformOtherParams;
import com.nuctech.securitycheck.backgroundservice.common.enums.CommonConstant;
import com.nuctech.securitycheck.backgroundservice.controller.SchedulerController;
import com.nuctech.securitycheck.backgroundservice.repositories.HistoryRepository;
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
public class HistorySchedulerService implements SchedulingConfigurer {

    @Autowired
    SchedulerController schedulerController;

    @Autowired
    private ISerPlatformOtherParamsService iSerPlatformOtherParamsService;

    @Autowired
    private HistoryRepository historyRepository;

    @Bean
    public TaskScheduler poolScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setThreadNamePrefix("ThreadPoolTaskScheduler");
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
                int historyCycle;
                try {
                    SerPlatformOtherParams serPlatformOtherParams = iSerPlatformOtherParamsService.getLastOtherParams();
                    historyCycle = serPlatformOtherParams.getHistoryDataCycle().intValue();
                } catch(Exception ex) {
                    historyCycle = CommonConstant.DEFAULT_SCHEDULER_ZABBIX_TIME.getValue();
                }

                if(historyCycle > 0) {
                    Date now = new Date();

                    Calendar cal = Calendar.getInstance();
                    cal.setTime(now);
                    cal.add(Calendar.DATE, (int) -historyCycle);
                    historyRepository.removeAllHistory(cal.getTime());
                }
                // Do not put @Scheduled annotation above this method, we don't need it anymore.

            }
        }, new Trigger() {
            @Override
            public Date nextExecutionTime(TriggerContext triggerContext) {
                Calendar nextExecutionTime = new GregorianCalendar();
                Date lastActualExecutionTime = triggerContext.lastActualExecutionTime();
                nextExecutionTime.setTime(lastActualExecutionTime != null ? lastActualExecutionTime : new Date());
                int historyCycle;
                try {
                    SerPlatformOtherParams serPlatformOtherParams = iSerPlatformOtherParamsService.getLastOtherParams();
                    historyCycle = serPlatformOtherParams.getHistoryDataCycle().intValue();
                } catch(Exception ex) {
                    historyCycle = CommonConstant.DEFAULT_SCHEDULER_ZABBIX_TIME.getValue();
                }
                if(historyCycle == 0) {
                    historyCycle = 1;
                }
                nextExecutionTime.add(Calendar.DATE, historyCycle);
                return nextExecutionTime.getTime();
            }
        });
    }

}
