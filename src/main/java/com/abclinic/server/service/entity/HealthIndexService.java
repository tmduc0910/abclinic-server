package com.abclinic.server.service.entity;

import com.abclinic.server.common.constant.MessageType;
import com.abclinic.server.common.constant.Role;
import com.abclinic.server.common.constant.UserStatus;
import com.abclinic.server.common.criteria.HealthIndexSchedulePredicateBuilder;
import com.abclinic.server.common.criteria.PatientHealthIndexFieldPredicateBuilder;
import com.abclinic.server.common.utils.DateTimeUtils;
import com.abclinic.server.common.utils.StatusUtils;
import com.abclinic.server.exception.ForbiddenException;
import com.abclinic.server.exception.NotFoundException;
import com.abclinic.server.factory.NotificationFactory;
import com.abclinic.server.model.dto.GetIndexResultResponseDto;
import com.abclinic.server.model.dto.PageDto;
import com.abclinic.server.model.dto.ResultDto;
import com.abclinic.server.model.dto.TagDto;
import com.abclinic.server.model.entity.payload.health_index.HealthIndex;
import com.abclinic.server.model.entity.payload.health_index.HealthIndexField;
import com.abclinic.server.model.entity.payload.health_index.HealthIndexSchedule;
import com.abclinic.server.model.entity.payload.health_index.PatientHealthIndexField;
import com.abclinic.server.model.entity.user.Doctor;
import com.abclinic.server.model.entity.user.Patient;
import com.abclinic.server.model.entity.user.User;
import com.abclinic.server.service.NotificationService;
import com.abclinic.server.service.entity.component.health_index.HealthIndexComponentService;
import com.abclinic.server.service.entity.component.health_index.HealthIndexFieldComponentService;
import com.abclinic.server.service.entity.component.health_index.HealthIndexScheduleComponentService;
import com.abclinic.server.service.entity.component.health_index.PatientHealthIndexFieldComponentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

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
    private UserService userService;

    @Autowired
    public HealthIndexService(HealthIndexComponentService healthIndexComponentService, HealthIndexFieldComponentService healthIndexFieldComponentService, HealthIndexScheduleComponentService healthIndexScheduleComponentService, PatientHealthIndexFieldComponentService patientHealthIndexFieldComponentService, NotificationService notificationService, UserService userService) {
        this.healthIndexComponentService = healthIndexComponentService;
        this.healthIndexFieldComponentService = healthIndexFieldComponentService;
        this.healthIndexScheduleComponentService = healthIndexScheduleComponentService;
        this.patientHealthIndexFieldComponentService = patientHealthIndexFieldComponentService;
        this.notificationService = notificationService;
        this.userService = userService;
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

    public HealthIndexField getOldField(String name) {
        List<HealthIndexField> list = healthIndexFieldComponentService.getByName(name);
        if (list.isEmpty())
            return null;
        return list.get(0);
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

    public PageDto<GetIndexResultResponseDto> getValuesList(User user, Pageable pageable) {
        Page<TagDto> tags = getTags(user, pageable);
        List<Long> tagIds = new LinkedHashSet<>(tags.getContent())
                .stream()
                .map(TagDto::getTagId)
                .collect(Collectors.toList());
        List<PatientHealthIndexField> res = patientHealthIndexFieldComponentService
                .getList(user, tagIds);
        List<ResultDto> dtos = res.stream().map(ResultDto::new).collect(Collectors.toList());
        return new PageDto<>(getResultResponseDto(res, tagIds),
                patientHealthIndexFieldComponentService.countDistinctTagId(),
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort());
    }

    private Page<TagDto> getTags(User user, Pageable pageable) {
        return patientHealthIndexFieldComponentService.getTagIds(user, pageable);
    }

    public Page<PatientHealthIndexField> getValuesList(HealthIndexSchedule schedule, Pageable pageable) {
        return patientHealthIndexFieldComponentService.getList(schedule, pageable);
    }

    public Page<PatientHealthIndexField> getValuesList(HealthIndexField field, Pageable pageable) {
        return patientHealthIndexFieldComponentService.getList(field, pageable);
    }

    public PageDto<GetIndexResultResponseDto> getValuesList(User user, String search, Pageable pageable) {
        List<PatientHealthIndexField> res;
        List<Long> temp;

        List<PatientHealthIndexField> p = patientHealthIndexFieldComponentService.getList(user, search, new PatientHealthIndexFieldPredicateBuilder(), pageable.getSort());
        List<Long> tags = p
                .stream()
                .map(PatientHealthIndexField::getTagId)
                .distinct()
                .collect(Collectors.toList());
        PageDto<GetIndexResultResponseDto> r = new PageDto<>(tags.size(), pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        if (!tags.isEmpty()) {
            int end = (pageable.getPageNumber() + 1) * pageable.getPageSize();
            temp = tags.subList(pageable.getPageNumber() * pageable.getPageSize(), Math.min(tags.size(), end));
            res = p.stream().filter(i -> temp.contains(i.getTagId())).collect(Collectors.toList());
            r.setContent(getResultResponseDto(res, temp));
        }
        return r;
    }

    private List<GetIndexResultResponseDto> getResultResponseDto(List<PatientHealthIndexField> root, List<Long> tags) {
        List<ResultDto> dtos = root.stream().map(ResultDto::new).collect(Collectors.toList());
        return tags.stream().map(t -> {
            List<ResultDto> temp = dtos.stream().filter(d -> d.getTagId() == t).collect(Collectors.toList());
            return new GetIndexResultResponseDto(t, temp.get(0).getSchedule(), temp);
        }).collect(Collectors.toList());
    }

    public List<PatientHealthIndexField> getValuesList(User user, long id) {
        List<PatientHealthIndexField> list = patientHealthIndexFieldComponentService.getList(id);
        if (list.get(0).getSchedule().getPatient().getPractitioner().equals(user) ||
                list.get(0).getSchedule().getPatient().getSubDoctors().contains(user))
            return list;
        else throw new ForbiddenException(user.getId(), "Bệnh nhân không thuộc quyền quản lý");
    }

    public HealthIndexSchedule createSchedule(Patient patient, Doctor doctor, String description, long scheduleTime, LocalDateTime startTime, HealthIndex index) {
        HealthIndexSchedule schedule = new HealthIndexSchedule(patient, doctor, index, description, scheduleTime, startTime);
        schedule = (HealthIndexSchedule) save(schedule);
        List<User> doctors = patient.getSubDoctors();
        HealthIndexSchedule finalSchedule = schedule;
        doctors.stream()
                .filter(d -> !d.equals(doctor))
                .forEach(d -> notificationService.makeNotification(doctor,
                        NotificationFactory.getMessage(MessageType.SCHEDULE, d, finalSchedule)));
        patient = StatusUtils.update(patient, UserStatus.SCHEDULED);
        patient = (Patient) userService.save(patient);
        notificationService.makeNotification(doctor, NotificationFactory.getMessage(MessageType.SCHEDULE, patient, schedule));
        return schedule;
    }

    @Transactional
    public HealthIndexSchedule updateSchedule(HealthIndexSchedule schedule) {
        return healthIndexScheduleComponentService.updateSchedule(schedule);
    }

    @Transactional
    public List<HealthIndexSchedule> getAvailableSchedules() {
        return healthIndexScheduleComponentService.getAllAvailableSchedules();
    }

    @Transactional
    public List<HealthIndexSchedule> getTodaySchedules() {
        return healthIndexScheduleComponentService.getTodaySchedules();
    }

    public HealthIndex createIndex(String indexName, String description, String[] fieldNames) {
        HealthIndex index = new HealthIndex(indexName, description);
        index = (HealthIndex) save(index);
        for (String fieldName : fieldNames) {
            HealthIndexField field = new HealthIndexField(index, fieldName);
            index.addField(field);
            save(field);
        }
        return index;
    }

    public List<PatientHealthIndexField> createResults(User user, HealthIndexSchedule schedule, List<HealthIndexField> fields, List<String> values) {
        List<PatientHealthIndexField> results = new ArrayList<>();
        long tagId = -1;
        boolean isInit = schedule == null && user.getRole() == Role.PATIENT && StatusUtils.containsStatus(user, UserStatus.NEW);
        Patient patient = schedule != null ? schedule.getPatient() : (Patient) user;

        if (!isInit && DateTimeUtils.getCurrent().isAfter(schedule.getStartedAt()))
            schedule = updateSchedule(schedule);
        for (int i = 0; i < fields.size(); i++) {
            PatientHealthIndexField f = createResult(schedule, fields.get(i), values.get(i));
            if (tagId < 0)
                tagId = f.getId();
            f.setTagId(tagId);
            f = (PatientHealthIndexField) save(f);
            PatientHealthIndexField finalF = f;
            patient.getSubDoctors().forEach(d -> notificationService.makeNotification(patient,
                    NotificationFactory.getMessage(MessageType.SEND_INDEX, d, finalF)));
            results.add(f);
        }
        if (!isInit)
            notificationService.makeNotification(patient, NotificationFactory.getMessage(MessageType.SEND_INDEX, patient.getPractitioner(), results.get(0)));
        return results;
    }

    private PatientHealthIndexField createResult(HealthIndexSchedule schedule, HealthIndexField field, String value) {
        if (schedule == null) {
            return (PatientHealthIndexField) save(new PatientHealthIndexField(field, value));
        } else if (schedule.getIndex().getFields().contains(field)) {
            PatientHealthIndexField result = new PatientHealthIndexField(schedule, field, value);
            return (PatientHealthIndexField) save(result);
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
