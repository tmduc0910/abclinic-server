package com.abclinic.server.service.entity;

import com.abclinic.server.common.constant.MessageType;
import com.abclinic.server.common.criteria.HealthIndexSchedulePredicateBuilder;
import com.abclinic.server.common.criteria.PatientHealthIndexFieldPredicateBuilder;
import com.abclinic.server.common.utils.DateTimeUtils;
import com.abclinic.server.exception.NotFoundException;
import com.abclinic.server.factory.NotificationFactory;
import com.abclinic.server.model.entity.notification.Notification;
import com.abclinic.server.model.entity.payload.IPayload;
import com.abclinic.server.model.entity.payload.health_index.HealthIndex;
import com.abclinic.server.model.entity.payload.health_index.HealthIndexField;
import com.abclinic.server.model.entity.payload.health_index.HealthIndexSchedule;
import com.abclinic.server.model.entity.payload.health_index.PatientHealthIndexField;
import com.abclinic.server.model.entity.user.Doctor;
import com.abclinic.server.model.entity.user.Patient;
import com.abclinic.server.model.entity.user.User;
import com.abclinic.server.service.NotificationService;
import com.abclinic.server.service.entity.health_index.component.HealthIndexComponentService;
import com.abclinic.server.service.entity.health_index.component.HealthIndexFieldComponentService;
import com.abclinic.server.service.entity.health_index.component.HealthIndexScheduleComponentService;
import com.abclinic.server.service.entity.health_index.component.PatientHealthIndexFieldComponentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author tmduc
 * @package com.abclinic.server.service.entity
 * @created 5/4/2020 2:08 PM
 */
@Service
public class HealthIndexService {
    private HealthIndexComponentService healthIndexComponentService;
    private HealthIndexFieldComponentService healthIndexFieldComponentService;
    private HealthIndexScheduleComponentService healthIndexScheduleComponentService;
    private PatientHealthIndexFieldComponentService patientHealthIndexFieldComponentService;
    private NotificationService notificationService;

    @Autowired
    public HealthIndexService(HealthIndexComponentService healthIndexComponentService, HealthIndexFieldComponentService healthIndexFieldComponentService, HealthIndexScheduleComponentService healthIndexScheduleComponentService, PatientHealthIndexFieldComponentService patientHealthIndexFieldComponentService, NotificationService notificationService) {
        this.healthIndexComponentService = healthIndexComponentService;
        this.healthIndexFieldComponentService = healthIndexFieldComponentService;
        this.healthIndexScheduleComponentService = healthIndexScheduleComponentService;
        this.patientHealthIndexFieldComponentService = patientHealthIndexFieldComponentService;
        this.notificationService = notificationService;
    }

    public HealthIndex getIndex(long id) {
        return healthIndexComponentService.getById(id);
    }

    public Page<HealthIndex> getIndexList(Pageable pageable) {
        return healthIndexComponentService.getList(null, pageable);
    }

    public boolean isIndexExist(String name) {
        return healthIndexComponentService.isExist(name);
    }

    public HealthIndexField getField(long id) throws NotFoundException {
        return healthIndexFieldComponentService.getById(id);
    }

    public List<HealthIndexField> getFieldList(HealthIndex index) {
        return healthIndexFieldComponentService.getList(index);
    }

    public boolean isFieldExist(HealthIndex index, String name) {
        return healthIndexFieldComponentService.isExist(index, name);
    }

    public HealthIndexSchedule getSchedule(long id) {
        return healthIndexScheduleComponentService.getById(id);
    }

    public Page<HealthIndexSchedule> getScheduleList(User user, Pageable pageable) {
        return healthIndexScheduleComponentService.getList(user, pageable);
    }

    public Page<HealthIndexSchedule> getScheduleList(User user, String search, Pageable pageable) {
        return healthIndexScheduleComponentService.getList(user, search, new HealthIndexSchedulePredicateBuilder(), pageable);
    }

    public PatientHealthIndexField getValue(long id) {
        return patientHealthIndexFieldComponentService.getById(id);
    }

    public Page<PatientHealthIndexField> getValuesList(User user, Pageable pageable) {
        return patientHealthIndexFieldComponentService.getList(user, pageable);
    }

    public Page<PatientHealthIndexField> getValuesList(HealthIndexSchedule schedule, Pageable pageable) {
        return patientHealthIndexFieldComponentService.getList(schedule, pageable);
    }

    public Page<PatientHealthIndexField> getValuesList(HealthIndexField field, Pageable pageable) {
        return patientHealthIndexFieldComponentService.getList(field, pageable);
    }

    public Page<PatientHealthIndexField> getValuesList(User user, String search, Pageable pageable) {
        return patientHealthIndexFieldComponentService.getList(user, search, new PatientHealthIndexFieldPredicateBuilder(), pageable);
    }

    public HealthIndexSchedule createSchedule(Patient patient, Doctor doctor, long scheduleTime, LocalDateTime startTime, HealthIndex index) {
        HealthIndexSchedule schedule = new HealthIndexSchedule(patient, doctor, index, scheduleTime, startTime);
        schedule = (HealthIndexSchedule) save(schedule);
        List<Doctor> doctors = patient.getDoctors();
        HealthIndexSchedule finalSchedule = schedule;
        doctors.stream()
                .filter(d -> !d.equals(doctor))
                .forEach(d -> notificationService.makeNotification(doctor,
                        NotificationFactory.getMessage(MessageType.SCHEDULE, d, finalSchedule)));
        notificationService.makeNotification(doctor, NotificationFactory.getMessage(MessageType.SCHEDULE, patient, schedule));
        return schedule;
    }

    public HealthIndexSchedule updateSchedule(HealthIndexSchedule schedule) {
        return healthIndexScheduleComponentService.updateSchedule(schedule);
    }

    public List<HealthIndexSchedule> getAvailableSchedules() {
        return healthIndexScheduleComponentService.getAllAvailableSchedules();
    }

    public HealthIndex createIndex(String indexName, String description, String[] fieldNames) {
        HealthIndex index = new HealthIndex(indexName, description);
        index = (HealthIndex) save(index);
        for (String fieldName: fieldNames) {
            HealthIndexField field = new HealthIndexField(index, fieldName);
            save(field);
        }
        return getIndex(index.getId());
    }

    public List<PatientHealthIndexField> createResults(HealthIndexSchedule schedule, List<HealthIndexField> fields, List<String> values) {
        List<PatientHealthIndexField> results = new ArrayList<>();
        schedule = updateSchedule(schedule);
        for (int i = 0; i < fields.size(); i++) {
            PatientHealthIndexField f = createResult(schedule, fields.get(i), values.get(i));
            if (i >= 1) {
                f.setId(fields.get(0).getId());
                save(f);
            }
            f.setSchedule(schedule);
            results.add(f);
        }
        return results;
    }

    private PatientHealthIndexField createResult(HealthIndexSchedule schedule, HealthIndexField field, String value) {
        if (schedule.getIndex().getFields().contains(field)) {
            PatientHealthIndexField result = new PatientHealthIndexField(schedule, field, value);
            result = (PatientHealthIndexField) save(result);
            Patient patient = schedule.getPatient();
            PatientHealthIndexField finalResult = result;
            patient.getDoctors().forEach(d -> {
                notificationService.makeNotification(patient,
                        NotificationFactory.getMessage(MessageType.SEND_INDEX, d, finalResult));
            });
            return finalResult;
        } else throw new IllegalArgumentException();
    }

    public Object save(Object obj) {
        if (obj instanceof HealthIndex)
            return healthIndexComponentService.save((HealthIndex) obj);
        else if (obj instanceof HealthIndexSchedule)
            return healthIndexScheduleComponentService.save((HealthIndexSchedule) obj);
        else if (obj instanceof HealthIndexField)
            return healthIndexFieldComponentService.save((HealthIndexField) obj);
        else if (obj instanceof PatientHealthIndexField)
            return patientHealthIndexFieldComponentService.save((PatientHealthIndexField) obj);
        else throw new IllegalArgumentException();
    }
}
