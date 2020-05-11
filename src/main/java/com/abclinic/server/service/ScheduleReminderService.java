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

import java.time.LocalDateTime;
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
    private PatientService patientService;

    @Autowired
    private NotificationService notificationService;

    @Scheduled(cron = "0 0 0 ? ? ?", fixedDelay = Constant.REMINDER_CYCLE *
            Constant.MIN_OF_HOUR *
            Constant.SEC_OF_MIN *
            Constant.MIL_OF_SEC)
    public void remind() {
        List<HealthIndexSchedule> schedules = healthIndexService.getAvailableSchedules();
        LocalDateTime now = DateTimeUtils.getCurrent();
        schedules.forEach(s -> {
            if (DateTimeUtils.getDistanceByHour(now, s.getEndedAt()) <= Constant.REMINDER_LIMIT) {
                notificationService.makeNotification(s.getDoctor(), NotificationFactory.getMessage(MessageType.SCHEDULE_REMINDER, s.getPatient(), s));
            }
        });
    }
}
