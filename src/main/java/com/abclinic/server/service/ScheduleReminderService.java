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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

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

    @Transactional
    @Scheduled(cron = "0 0 */4 ? * *")
    public void remind() {
        List<HealthIndexSchedule> schedules = healthIndexService.getAvailableSchedules();
        LocalDateTime now = DateTimeUtils.getCurrent();
        schedules.forEach(s -> {
            long distance = DateTimeUtils.getDistanceByHour(now, s.getEndedAt());
            if (distance < 0) {
                healthIndexService.updateSchedule(s);
            } else if (distance <= Constant.REMINDER_LIMIT) {
                notificationService.makeNotification(s.getDoctor(), NotificationFactory.getMessage(MessageType.SCHEDULE_REMINDER, s.getPatient(), s));
            }
        });
    }
}
