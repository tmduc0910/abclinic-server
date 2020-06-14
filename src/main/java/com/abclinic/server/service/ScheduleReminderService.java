package com.abclinic.server.service;

import com.abclinic.server.common.constant.Constant;
import com.abclinic.server.common.constant.MessageType;
import com.abclinic.server.common.utils.DateTimeUtils;
import com.abclinic.server.factory.NotificationFactory;
import com.abclinic.server.model.entity.notification.Notification;
import com.abclinic.server.model.entity.payload.health_index.HealthIndexSchedule;
import com.abclinic.server.service.entity.HealthIndexService;
import com.abclinic.server.service.entity.PatientService;
import org.apache.tomcat.jni.Local;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author tmduc
 * @package com.abclinic.server.service
 * @created 5/11/2020 9:17 AM
 */
@Service
public class ScheduleReminderService {
    @Autowired
    private HealthIndexService healthIndexService;

    @Autowired
    private NotificationService notificationService;

    private Logger logger = LoggerFactory.getLogger(ScheduleReminderService.class);

    @Transactional
    @Scheduled(cron = "0 0 */4 ? * *")
    public void remind() {
        List<HealthIndexSchedule> schedules = healthIndexService.getAvailableSchedules();
        LocalDateTime now = DateTimeUtils.getCurrent();
        schedules.forEach(s -> {
            long distance = DateTimeUtils.getDistanceByHour(now, s.getEndedAt());
            if (distance <= Constant.REMINDER_LIMIT && distance >= 0) {
                notificationService.makeNotification(s.getDoctor(), NotificationFactory.getMessage(MessageType.SCHEDULE_REMINDER, s.getPatient(), s));
            }
        });
    }

    @Scheduled(cron = "0 0 0 * * ?")
    @PostConstruct
    public void updateSchedules() {
        List<HealthIndexSchedule> schedules = healthIndexService.getTodaySchedules();
        schedules.forEach(this::schedule);
        logger.info(String.format("Updated %d schedules", schedules.size()));
    }

    private void schedule(HealthIndexSchedule s) {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        TaskScheduler taskScheduler = new ConcurrentTaskScheduler(executorService);
        taskScheduler.schedule(() -> {
            if (s.getEndedAt().toLocalDate().equals(LocalDate.now()) || s.getEndedAt().toLocalDate().isBefore(LocalDate.now())) {
                healthIndexService.updateSchedule(s);
                if (s.getEndedAt().toLocalDate().equals(LocalDate.now()) || s.getEndedAt().toLocalDate().isBefore(LocalDate.now()))
                    schedule(s);
            }
        }, DateTimeUtils.toDate(s.getEndedAt()));
    }
}
