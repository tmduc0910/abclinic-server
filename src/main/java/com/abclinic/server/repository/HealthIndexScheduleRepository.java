package com.abclinic.server.repository;

import com.abclinic.server.model.entity.payload.health_index.HealthIndexSchedule;
import com.abclinic.server.model.entity.user.Doctor;
import com.abclinic.server.model.entity.user.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author tmduc
 * @package com.abclinic.server.repository
 * @created 1/9/2020 2:32 PM
 */
public interface HealthIndexScheduleRepository extends JpaRepository<HealthIndexSchedule, Long> {
    Optional<HealthIndexSchedule> findById(long id);
    Optional<Page<HealthIndexSchedule>> findByPatient(Patient patient, Pageable pageable);
    Optional<Page<HealthIndexSchedule>> findByDoctor(Doctor doctor, Pageable pageable);
}