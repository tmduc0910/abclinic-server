package com.abclinic.server.model.dao;

import com.abclinic.server.model.entity.user.Dietitian;
import com.abclinic.server.model.entity.user.Doctor;
import com.abclinic.server.model.entity.user.Practitioner;
import com.abclinic.server.model.entity.user.Specialist;
import com.abclinic.server.repository.DietitianRepository;
import com.abclinic.server.repository.PractitionerRepository;
import com.abclinic.server.repository.SpecialistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author tmduc
 * @package com.abclinic.server.model.dao
 * @created 4/7/2020 8:17 PM
 */
@Component
public class DoctorDAO {
    private final PractitionerRepository practitionerRepository;
    private final SpecialistRepository specialistRepository;
    private final DietitianRepository dietitianRepository;

    @Autowired
    public DoctorDAO(PractitionerRepository practitionerRepository, SpecialistRepository specialistRepository, DietitianRepository dietitianRepository) {
        this.practitionerRepository = practitionerRepository;
        this.specialistRepository = specialistRepository;
        this.dietitianRepository = dietitianRepository;
    }

    public Practitioner getPractitioner(Doctor doctor) {
        return practitionerRepository.findById(doctor.getId());
    }

    public Specialist getSpecialist(Doctor doctor) {
        return specialistRepository.findById(doctor.getId());
    }

    public Dietitian getDietitian(Doctor doctor) {
        return dietitianRepository.findById(doctor.getId());
    }
}
