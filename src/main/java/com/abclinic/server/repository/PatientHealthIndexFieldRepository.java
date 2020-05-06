package com.abclinic.server.repository;

import com.abclinic.server.model.entity.payload.health_index.HealthIndexField;
import com.abclinic.server.model.entity.payload.health_index.HealthIndexSchedule;
import com.abclinic.server.model.entity.payload.health_index.PatientHealthIndexField;
import com.abclinic.server.model.entity.user.Doctor;
import com.abclinic.server.model.entity.user.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author tmduc
 * @package com.abclinic.server.repository
 * @created 5/6/2020 2:26 PM
 */
public interface PatientHealthIndexFieldRepository extends JpaRepository<PatientHealthIndexField, Long> {
    Optional<PatientHealthIndexField> findById(long id);

    Optional<Page<PatientHealthIndexField>> findByField(HealthIndexField field, Pageable pageable);

    Optional<Page<PatientHealthIndexField>> findBySchedule(HealthIndexSchedule schedule, Pageable pageable);

    Optional<Page<PatientHealthIndexField>> findBySchedulePatient(Patient patient, Pageable pageable);

    Optional<Page<PatientHealthIndexField>> findByScheduleDoctor(Doctor doctor, Pageable pageable);
}