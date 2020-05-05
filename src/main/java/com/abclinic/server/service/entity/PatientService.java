package com.abclinic.server.service.entity;

import com.abclinic.server.common.constant.Constant;
import com.abclinic.server.common.constant.FilterConstant;
import com.abclinic.server.common.constant.UserStatus;
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
                predBuilder.with(FilterConstant.PRACTITIONER.getValue(), Constant.EQUAL_SBL, doctorDAO.getPractitioner((Doctor) user).getId());
                if (!predBuilder.hasCriteria(FilterConstant.STATUS.getValue()))
                    predBuilder.with(FilterConstant.STATUS.getValue(), Constant.EQUAL_SBL, UserStatus.ASSIGN_L1.getValue());
                break;
            case SPECIALIST:
                predBuilder.with(FilterConstant.SPECIALIST.getValue(), Constant.CONTAIN_SBL, doctorDAO.getSpecialist((Doctor) user));
                if (!predBuilder.hasCriteria(FilterConstant.STATUS.getValue()))
                    predBuilder.with(FilterConstant.STATUS.getValue(), Constant.EQUAL_SBL, UserStatus.ASSIGN_L2.getValue());
                break;
            case DIETITIAN:
                predBuilder.with(FilterConstant.DIETITIAN.getValue(), Constant.CONTAIN_SBL, doctorDAO.getDietitian((Doctor) user));
                if (!predBuilder.hasCriteria(FilterConstant.STATUS.getValue()))
                    predBuilder.with(FilterConstant.STATUS.getValue(), Constant.EQUAL_SBL, UserStatus.ASSIGN_L3.getValue());
                break;
        }
        BooleanExpression expression = predBuilder.build();
        if (expression != null)
            return patientRepository.findAll(expression, pageable);
        return patientRepository.findAll(pageable);
    }

    public boolean isPatientOf(Patient patient, User doctor) {
        return patient.getPractitioner().equals(doctor) ||
                patient.getSpecialists().contains(doctor) ||
                patient.getDietitians().contains(doctor);
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
