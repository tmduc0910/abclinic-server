package com.abclinic.server.repository;

import com.abclinic.server.model.entity.Disease;
import com.abclinic.server.model.entity.payload.record.MedicalRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    Optional<MedicalRecord> findById(long id);
//    Optional<List<MedicalRecord>> findByPatient(Patient patient);
    Optional<Page<MedicalRecord>> findByInquiryPatientPractitionerId(long id, Pageable pageable);
    Optional<Page<MedicalRecord>> findByInquiryPatientIdAndStatus(long id, int status, Pageable pageable);
    Optional<Page<MedicalRecord>> findByDisease(Disease disease, Pageable pageable);
    Optional<Page<MedicalRecord>> findBySpecialistId(long id, Pageable pageable);

//    @Query("select mr from MedicalRecord mr " +
//            "where (:patient is null or mr.patient.name like :patient) " +
//            "and (:disease is null or mr.disease = :disease) " +
//            "and (:status is null or mr.status = :status)")
//    Optional<List<MedicalRecord>> findByPatientAndDisease(@Param("patient") String patientName, @Param("disease") Disease disease, @Param("status") int status, Pageable pageable);
//
//    @Query("select mr from MedicalRecord mr " +
//            "where (:patient is null or mr.patient.name like :patient) " +
//            "and (:disease is null or mr.disease = :disease) " +
//            "and (:status is null or mr.status = :status) " +
//            "and mr.practitioner = :practitioner")
//    Optional<List<MedicalRecord>> findByPractitionerAndPatientAndDisease(@Param("practitioner") Practitioner practitioner, @Param("patient") String patientName, @Param("disease") Disease disease, @Param("status") int status, Pageable pageable);
}
