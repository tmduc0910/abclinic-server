package com.abclinic.server.service.entity.component.health_index;

import com.abclinic.server.common.constant.FilterConstant;
import com.abclinic.server.common.criteria.EntityPredicateBuilder;
import com.abclinic.server.common.criteria.PatientHealthIndexFieldPredicateBuilder;
import com.abclinic.server.exception.NotFoundException;
import com.abclinic.server.model.entity.payload.health_index.HealthIndexField;
import com.abclinic.server.model.entity.payload.health_index.HealthIndexSchedule;
import com.abclinic.server.model.entity.payload.health_index.PatientHealthIndexField;
import com.abclinic.server.model.entity.user.User;
import com.abclinic.server.repository.PatientHealthIndexFieldRepository;
import com.abclinic.server.service.entity.DoctorService;
import com.abclinic.server.service.entity.IDataMapperService;
import com.abclinic.server.service.entity.PatientService;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @author tmduc
 * @package com.abclinic.server.service.entity.health_index.component
 * @created 5/6/2020 2:41 PM
 */
@Component
public class PatientHealthIndexFieldComponentService implements IDataMapperService<PatientHealthIndexField> {
    private PatientHealthIndexFieldRepository patientHealthIndexFieldRepository;
    private PatientService patientService;
    private DoctorService doctorService;

    @Autowired
    public PatientHealthIndexFieldComponentService(PatientHealthIndexFieldRepository patientHealthIndexFieldRepository, PatientService patientService, DoctorService doctorService) {
        this.patientHealthIndexFieldRepository = patientHealthIndexFieldRepository;
        this.patientService = patientService;
        this.doctorService = doctorService;
    }

    @Override
    @Transactional
    public PatientHealthIndexField getById(long id) throws NotFoundException {
        return patientHealthIndexFieldRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    @Override
    @Transactional
    public Page<PatientHealthIndexField> getList(User user, Pageable pageable) {
        switch (user.getRole()) {
            case PATIENT:
                return patientHealthIndexFieldRepository.findBySchedulePatientId(user.getId(), pageable)
                        .orElseThrow(NotFoundException::new);
            case PRACTITIONER:
            case SPECIALIST:
            case DIETITIAN:
                return patientHealthIndexFieldRepository.findByScheduleDoctorId(user.getId(), pageable)
                        .orElseThrow(NotFoundException::new);
            default:
                return null;
        }
    }

    @Transactional
    public List<PatientHealthIndexField> getList(User user, List<Long> tagIds) {
        switch (user.getRole()) {
            case PATIENT:
                return patientHealthIndexFieldRepository.findBySchedulePatientIdAndTagIdIn(user.getId(), tagIds)
                        .orElseThrow(NotFoundException::new);
            case PRACTITIONER:
            case SPECIALIST:
            case DIETITIAN:
                return patientHealthIndexFieldRepository.findByScheduleDoctorIdAndTagIdIn(user.getId(), tagIds)
                        .orElseThrow(NotFoundException::new);
            default:
                return null;
        }
    }

    @Transactional
    public List<PatientHealthIndexField> getList(User user, String search, EntityPredicateBuilder builder, Sort sort) {
        String key = search;
        if (search.contains("patient_id"))
            key = search.replace("patient_id", FilterConstant.VAL_PAT_ID.getValue());
        if (search.contains("patient_name"))
            key = search.replace("patient_name", FilterConstant.VAL_PAT_NAME.getValue());
        if (search.contains("index_id"))
            key = search.replace("index_id", FilterConstant.VAL_INDEX_ID.getValue());
        if (search.contains("index_name"))
            key = search.replace("index_name", FilterConstant.VAL_INDEX_NAME.getValue());
        if (search.contains("schedule_id"))
            key = search.replace("schedule_id", FilterConstant.VAL_SCHEDULE_ID.getValue());
        PatientHealthIndexFieldPredicateBuilder predBuilder = (PatientHealthIndexFieldPredicateBuilder) builder.init(key);
        BooleanExpression expression = predBuilder.build();
        if (expression != null)
            return patientHealthIndexFieldRepository.findAll(expression, sort);
        return patientHealthIndexFieldRepository.findAll(sort);
    }

    @Transactional
    public Page<PatientHealthIndexField> getList(HealthIndexField field, Pageable pageable) {
        return patientHealthIndexFieldRepository.findByField(field, pageable)
                .orElseThrow(NotFoundException::new);
    }

    @Transactional
    public Page<PatientHealthIndexField> getList(HealthIndexSchedule schedule, Pageable pageable) {
        return patientHealthIndexFieldRepository.findBySchedule(schedule, pageable)
                .orElseThrow(NotFoundException::new);
    }

    @Transactional
    public List<PatientHealthIndexField> getList(User user, long id) {
        return patientHealthIndexFieldRepository.findByScheduleDoctorAndTagId(user, id)
                .orElseThrow(NotFoundException::new);
    }

    @Transactional
    public Page<Long> getTagIds(Pageable pageable) {
        return patientHealthIndexFieldRepository.findDistinctTagId(pageable);
    }


    @Transactional
    public int countByTag(List<Long> tags) {
        return patientHealthIndexFieldRepository.countIdByTag(tags);
    }

    @Transactional
    public Page<PatientHealthIndexField> getAll(Pageable pageable) {
        return patientHealthIndexFieldRepository.findAll(pageable);
    }

    @Override
    public PatientHealthIndexField save(PatientHealthIndexField obj) {
        return patientHealthIndexFieldRepository.save(obj);
    }
}
