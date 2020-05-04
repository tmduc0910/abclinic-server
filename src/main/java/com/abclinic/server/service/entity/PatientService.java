package com.abclinic.server.service.entity;

import com.abclinic.server.common.criteria.PatientPredicateBuilder;
import com.abclinic.server.common.criteria.EntityPredicateBuilder;
import com.abclinic.server.exception.NotFoundException;
import com.abclinic.server.model.dao.DoctorDAO;
import com.abclinic.server.model.entity.user.*;
import com.abclinic.server.repository.PatientRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * @author tmduc
 * @package com.abclinic.server.service
 * @created 4/7/2020 8:19 PM
 */
@Service
public class PatientService implements DataMapperService<Patient> {
    private PatientRepository patientRepository;
    private DoctorDAO doctorDAO;

    @Autowired
    public PatientService(PatientRepository patientRepository, DoctorDAO doctorDAO) {
        this.patientRepository = patientRepository;
        this.doctorDAO = doctorDAO;
    }

//    @Transactional
//    public Page<Patient> getPatientsByDoctor(User user, Pageable pageable) throws NotFoundException {
//        Optional<Page<Patient>> op = Optional.empty();
//        switch (user.getRole()) {
//            case COORDINATOR:
//                op = patientRepository.findByPractitioner(null, pageable);
//                break;
//            case PRACTITIONER:
//                op = patientRepository.findByPractitioner((Practitioner) user, pageable);
//                break;
//            case SPECIALIST:
//                op = patientRepository.findBySpecialists((Specialist) user, pageable);
//                break;
//            case DIETITIAN:
//                op = patientRepository.findByDietitians((Dietitian) user, pageable);
//                break;
//        }
//        return op.orElseThrow(() -> new NotFoundException(user.getId()));
//    }

    @Override
    @Transactional
    public Page<Patient> getList(User user, String search, EntityPredicateBuilder builder, Pageable pageable) {
        PatientPredicateBuilder predBuilder = (PatientPredicateBuilder) builder.init(search);
        switch (user.getRole()) {
            case PRACTITIONER:
                predBuilder.with("practitioner.id", "=", doctorDAO.getPractitioner((Doctor) user).getId());
                break;
            case SPECIALIST:
                predBuilder.with("specialist.id", "=", doctorDAO.getSpecialist((Doctor) user).getId());
                break;
            case DIETITIAN:
                predBuilder.with("dietitian.id", "=", doctorDAO.getDietitian((Doctor) user).getId());
                break;
        }
        BooleanExpression expression = predBuilder.build();
        if (expression != null)
            return patientRepository.findAll(expression, pageable);
        return patientRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public Patient getById(long id) throws NotFoundException {
        return patientRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    @Override
    public Patient save(Patient obj) {
        return patientRepository.save(obj);
    }
}