package com.abclinic.server.service.entity.component.health_index;

import com.abclinic.server.common.constant.FilterConstant;
import com.abclinic.server.common.constant.PayloadStatus;
import com.abclinic.server.common.constant.Role;
import com.abclinic.server.common.criteria.EntityPredicateBuilder;
import com.abclinic.server.common.criteria.HealthIndexSchedulePredicateBuilder;
import com.abclinic.server.common.utils.DateTimeUtils;
import com.abclinic.server.exception.NotFoundException;
import com.abclinic.server.model.entity.payload.health_index.HealthIndexSchedule;
import com.abclinic.server.model.entity.user.Doctor;
import com.abclinic.server.model.entity.user.Patient;
import com.abclinic.server.model.entity.user.User;
import com.abclinic.server.repository.HealthIndexScheduleRepository;
import com.abclinic.server.service.entity.DoctorService;
import com.abclinic.server.service.entity.IDataMapperService;
import com.abclinic.server.service.entity.PatientService;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author tmduc
 * @package com.abclinic.server.service.entity.health_index.component
 * @created 5/6/2020 2:28 PM
 */
@Component
public class HealthIndexScheduleComponentService implements IDataMapperService<HealthIndexSchedule> {
    private HealthIndexScheduleRepository healthIndexScheduleRepository;
    private PatientService patientService;
    private DoctorService doctorService;

    @Autowired
    public HealthIndexScheduleComponentService(HealthIndexScheduleRepository healthIndexScheduleRepository, PatientService patientService, DoctorService doctorService) {
        this.healthIndexScheduleRepository = healthIndexScheduleRepository;
        this.patientService = patientService;
        this.doctorService = doctorService;
    }

    @Override
    @Transactional
    public HealthIndexSchedule getById(long id) throws NotFoundException {
        return healthIndexScheduleRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    @Override
    @Transactional
    public Page<HealthIndexSchedule> getList(User user, Pageable pageable) {
        switch (user.getRole()) {
            case COORDINATOR:
                return healthIndexScheduleRepository.findAll(pageable);
            case PATIENT:
                return healthIndexScheduleRepository.findByPatient(patientService.getById(user.getId()), pageable)
                        .orElseThrow(NotFoundException::new);
            case PRACTITIONER:
            case SPECIALIST:
            case DIETITIAN:
                return healthIndexScheduleRepository.findByDoctor((Doctor) doctorService.getById(user.getId()), pageable)
                        .orElseThrow(NotFoundException::new);
            default:
                return null;
        }
    }

    public HealthIndexSchedule updateSchedule(HealthIndexSchedule schedule) {
        schedule.setStartedAt(DateTimeUtils.plusSeconds(schedule.getStartedAt(), schedule.getScheduledTime()));
        schedule.setEndedAt(DateTimeUtils.plusSeconds(schedule.getEndedAt(), schedule.getScheduledTime()));
        return save(schedule);
    }

    @Transactional
    public List<HealthIndexSchedule> getAllAvailableSchedules() {
        return healthIndexScheduleRepository.findByStatus(PayloadStatus.UNREAD);
    }

    @Transactional
    public List<HealthIndexSchedule> getTodaySchedules() {
        return healthIndexScheduleRepository.findByEndedAt(LocalDate.now().plusDays(1).atStartOfDay());
    }

    @Override
    @Transactional
    public Page<HealthIndexSchedule> getList(User user, String search, EntityPredicateBuilder builder, Pageable pageable) {
        String key = search;
        if (search.contains("id"))
            key = search.replace("id", FilterConstant.SCHEDULE_PAT_ID.getValue());
        if (search.contains("name"))
            key = search.replace("name", FilterConstant.SCHEDULE_PAT_NAME.getValue());
        HealthIndexSchedulePredicateBuilder predBuilder = (HealthIndexSchedulePredicateBuilder) builder.init(key);
        BooleanExpression expression = predBuilder.build();
        if (expression != null)
            return healthIndexScheduleRepository.findAll(expression, pageable);
        return healthIndexScheduleRepository.findAll(pageable);
    }

    @Override
    public HealthIndexSchedule save(HealthIndexSchedule obj) {
        return healthIndexScheduleRepository.save(obj);
    }
}
