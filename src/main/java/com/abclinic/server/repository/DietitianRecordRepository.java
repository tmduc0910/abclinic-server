package com.abclinic.server.repository;

import com.abclinic.server.model.entity.DietitianRecord;
import com.abclinic.server.model.entity.Disease;
import com.abclinic.server.model.entity.MedicalRecord;
import com.abclinic.server.model.entity.user.Patient;
import com.abclinic.server.model.entity.user.Practitioner;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * @author tmduc
 * @package com.abclinic.server.repository
 * @created 1/5/2020 11:06 AM
 */
public interface DietitianRecordRepository extends JpaRepository<DietitianRecord, Long> {
    Optional<DietitianRecord> findById(long id);

    @Query("select dr from DietitianRecord dr " +
            "where (:patient is null or dr.patient.name like :patient) " +
            "and (:status is null or dr.status = :status)")
    Optional<List<DietitianRecord>> findByPatientAndStatus(@Param("patient") String patientName, @Param("status") int status, Pageable pageable);

    @Query("select dr from DietitianRecord dr " +
            "where (:patient is null or dr.patient.name like :patient) " +
            "and (:status is null or dr.status = :status) " +
            "and dr.practitioner = :practitioner")
    Optional<List<DietitianRecord>> findByPractitionerAndPatientAndStatus(@Param("practitioner") Practitioner practitioner, @Param("patient") String patientName, @Param("status") int status, Pageable pageable);
}