/*
 * Copyright 2019 KR-STAR-DEV team.
 *
 * @CreatedDate 2019/10/15
 * @CreatedBy Sandy.
 * @FileName ScheduledTasks.java
 * @ModifyHistory
 *
 */
package com.nuctech.ecuritycheckitem.scheduledtasks;

import com.nuctech.ecuritycheckitem.config.Constants;
import com.nuctech.ecuritycheckitem.models.db.QForbiddenToken;
import com.nuctech.ecuritycheckitem.repositories.ForbiddenTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * This class will hold all scheduled tasks on the server.
 */
@Component
public class ScheduledTasks {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM-dd-HH:mm:ss");

    @Autowired
    ForbiddenTokenRepository forbiddenTokenRepository;

    /**
     * Write current time to log.
     */
    private void logCurrentTime() {
        Date now = new Date();
        log.info("The time is now {} - {}", (int) (now.getTime() / 1000), dateFormat.format(now));
    }

    /**
     * Cleans forbidden token table.
     */
    @Scheduled(fixedRate = Constants.TASK_PERIOD_SECONDS_CLEAN_FORBIDDEN_TOKEN_TABLE * 1000)
    public void cleanForbiddenTokenTable() {

        log.info("========BEGIN Cleaning Forbidden token table from DB========");

        logCurrentTime();

        // Calculate time.
        Date now = new Date();

        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.SECOND, (int) -Constants.JWT_VALIDITY_SECONDS);

        // Delete all records which is created more than JWT validity time ago.

        QForbiddenToken qForbiddenToken = QForbiddenToken.forbiddenToken;

        forbiddenTokenRepository.deleteAll(forbiddenTokenRepository.findAll(qForbiddenToken.createdTime.before(cal.getTime())));

        log.info("========END Cleaning Forbidden token table from DB========");
    }
}
