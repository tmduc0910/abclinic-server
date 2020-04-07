package com.abclinic.server.service;

import com.abclinic.server.exception.NotFoundException;
import com.abclinic.server.model.dao.DoctorDao;
import com.abclinic.server.model.entity.user.*;
import com.abclinic.server.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.print.Doc;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * @author tmduc
 * @package com.abclinic.server.service
 * @created 4/7/2020 7:40 PM
 */

@Service
public class PatientService {
    private PatientRepository patientRepository;
    private DoctorDao doctorDao;

    @Autowired
    public PatientService(PatientRepository patientRepository, DoctorDao doctorDao) {
        this.patientRepository = patientRepository;
        this.doctorDao = doctorDao;
    }

    @Transactional
    public List<Patient> getPatientsByDoctor(User user, Pageable pageable) throws NotFoundException {
        Optional<List<Patient>> op = Optional.empty();
        if (user instanceof Doctor) {
            Doctor doctor = (Doctor) user;
            if (doctor instanceof Practitioner) {
                op = patientRepository.findByPractitioner(doctorDao.getPractitioner(doctor), pageable);
            } else if (doctor instanceof Specialist) {
                op = patientRepository.findBySpecialists(doctorDao.getSpecialist(doctor), pageable);
            } else
                op = patientRepository.findByDietitians(doctorDao.getDietitian(doctor), pageable);
        } else op = patientRepository.findByPractitioner(null, pageable);
        return op.orElseThrow(() -> new NotFoundException(user.getId()));
    }
}
