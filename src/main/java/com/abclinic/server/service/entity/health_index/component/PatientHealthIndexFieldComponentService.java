package com.abclinic.server.service.entity.health_index.component;

import com.abclinic.server.exception.NotFoundException;
import com.abclinic.server.model.entity.payload.health_index.HealthIndexField;
import com.abclinic.server.model.entity.payload.health_index.HealthIndexSchedule;
import com.abclinic.server.model.entity.payload.health_index.PatientHealthIndexField;
import com.abclinic.server.model.entity.user.Doctor;
import com.abclinic.server.model.entity.user.Patient;
import com.abclinic.server.model.entity.user.User;
import com.abclinic.server.repository.PatientHealthIndexFieldRepository;
import com.abclinic.server.service.entity.IDataMapperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

/**
 * @author tmduc
 * @package com.abclinic.server.service.entity.health_index.component
 * @created 5/6/2020 2:41 PM
 */
@Component
public class PatientHealthIndexFieldComponentService implements IDataMapperService<PatientHealthIndexField> {
    private PatientHealthIndexFieldRepository patientHealthIndexFieldRepository;

    @Autowired
    public PatientHealthIndexFieldComponentService(PatientHealthIndexFieldRepository patientHealthIndexFieldRepository) {
        this.patientHealthIndexFieldRepository = patientHealthIndexFieldRepository;
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
                return patientHealthIndexFieldRepository.findBySchedulePatient((Patient) user, pageable)
                        .orElseThrow(NotFoundException::new);
            case PRACTITIONER:
            case SPECIALIST:
            case DIETITIAN:
                return patientHealthIndexFieldRepository.findByScheduleDoctor((Doctor) user, pageable)
                        .orElseThrow(NotFoundException::new);
            default:
                return null;
        }
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

    @Override
    public PatientHealthIndexField save(PatientHealthIndexField obj) {
        return null;
    }
}
