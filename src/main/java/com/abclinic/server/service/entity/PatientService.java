package com.abclinic.server.service.entity;

import com.abclinic.server.common.PatientPredicatesBuilder;
import com.abclinic.server.common.constant.Constant;
import com.abclinic.server.common.utils.StringUtils;
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
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author tmduc
 * @package com.abclinic.server.service
 * @created 4/7/2020 8:19 PM
 */
@Service
public class PatientService {
    private PatientRepository patientRepository;
    private DoctorDAO doctorDAO;

    @Autowired
    public PatientService(PatientRepository patientRepository, DoctorDAO doctorDAO) {
        this.patientRepository = patientRepository;
        this.doctorDAO = doctorDAO;
    }

    @Transactional
    public Page<Patient> getPatientsByDoctor(User user, Pageable pageable) throws NotFoundException {
        Optional<Page<Patient>> op = Optional.empty();
        switch (user.getRole()) {
            case COORDINATOR:
                op = patientRepository.findByPractitioner(null, pageable);
                break;
            case PRACTITIONER:
                op = patientRepository.findByPractitioner((Practitioner) user, pageable);
                break;
            case SPECIALIST:
                op = patientRepository.findBySpecialists((Specialist) user, pageable);
                break;
            case DIETITIAN:
                op = patientRepository.findByDietitians((Dietitian) user, pageable);
                break;
        }
        return op.orElseThrow(() -> new NotFoundException(user.getId()));
    }

    @Transactional
    public Page<Patient> getPatients(User user, String search, PatientPredicatesBuilder builder, Pageable pageable) {
        if (!StringUtils.endsWith(search, ","))
            search += ",";

        if (!StringUtils.isNull(search)) {
            Pattern pattern = Pattern.compile(Constant.FILTER_REGEX, Pattern.UNICODE_CHARACTER_CLASS);
            Matcher matcher = pattern.matcher(search + ",");
            while (matcher.find()) {
                builder.with(matcher.group(1), matcher.group(2), matcher.group(3));
            }
        }
        switch (user.getRole()) {
            case PRACTITIONER:
                builder.with("practitioner.id", "=", doctorDAO.getPractitioner((Doctor) user).getId());
                break;
            case SPECIALIST:
                builder.with("specialist", "=", doctorDAO.getSpecialist((Doctor) user));
                break;
            case DIETITIAN:
                builder.with("dietitian", "=", doctorDAO.getDietitian((Doctor) user));
                break;
        }
        BooleanExpression expression = builder.build();
        if (expression != null)
            return patientRepository.findAll(expression, pageable);
        return patientRepository.findAll(pageable);
    }
}
