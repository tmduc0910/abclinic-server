package com.abclinic.server.service;

import com.abclinic.server.exception.NotFoundException;
import com.abclinic.server.model.dao.DoctorDAO;
import com.abclinic.server.model.entity.user.*;
import com.abclinic.server.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

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
    public List<Patient> getPatientsByDoctor(User user, Pageable pageable) throws NotFoundException {
        Optional<List<Patient>> op = Optional.empty();
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
}
