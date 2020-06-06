package com.abclinic.server.repository;

import com.abclinic.server.model.entity.Disease;
import com.abclinic.server.model.entity.payload.record.MedicalRecord;
import com.abclinic.server.model.entity.user.Patient;
import com.abclinic.server.model.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    Optional<MedicalRecord> findById(long id);
//    Optional<List<MedicalRecord>> findByPatient(Patient patient);
    Optional<Page<MedicalRecord>> findByInquiryPatientPractitionerId(long id, Pageable pageable);
    Optional<Page<MedicalRecord>> findByInquiryPatientIdAndStatus(long id, int status, Pageable pageable);
    Optional<Page<MedicalRecord>> findByDisease(Disease disease, Pageable pageable);
    Optional<Page<MedicalRecord>> findByInquiryPatientIn(List<Patient> patients, Pageable pageable);
}
