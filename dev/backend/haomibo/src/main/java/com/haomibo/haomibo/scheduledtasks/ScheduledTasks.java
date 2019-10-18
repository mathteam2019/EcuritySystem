package com.haomibo.haomibo.scheduledtasks;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.haomibo.haomibo.config.Constants;
import com.haomibo.haomibo.models.db.QForbiddenToken;
import com.haomibo.haomibo.repositories.ForbiddenTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM-dd-HH:mm:ss");

    @Autowired
    ForbiddenTokenRepository forbiddenTokenRepository;

    void logCurrentTime() {
        Date now = new Date();
        log.info("The time is now {} - {}", (int) (now.getTime() / 1000), dateFormat.format(now));
    }

    @Scheduled(fixedRate = Constants.TASK_PERIOD_SECONDS_CLEAN_FORBIDDEN_TOKEN_TABLE * 1000)
    public void cleanForbiddenTokenTable() {

        log.info("========BEGIN Cleaning Forbidden token table from DB========");

        logCurrentTime();

        Date now = new Date();

        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.SECOND, (int) -Constants.JWT_VALIDITY_SECONDS);

        QForbiddenToken qForbiddenToken = QForbiddenToken.forbiddenToken;

        forbiddenTokenRepository.deleteAll(forbiddenTokenRepository.findAll(qForbiddenToken.createdTime.before(cal.getTime())));

        log.info("========END Cleaning Forbidden token table from DB========");
    }
}
