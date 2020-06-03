package com.abclinic.server.repository;

import com.abclinic.server.model.entity.payload.Inquiry;
import com.abclinic.server.model.entity.user.Patient;
import com.abclinic.server.model.entity.user.Practitioner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    Optional<Inquiry> findById(long id);
    Optional<List<Inquiry>> findByPatient(Patient patient);
    Optional<Page<Inquiry>> findByPatient(Patient patient, Pageable pageable);
    Optional<Page<Inquiry>> findByPatientAndStatus(Patient patient, int status, Pageable pageable);
    Optional<Page<Inquiry>> findByPatientPractitioner(Practitioner practitioner, Pageable pageable);
    Optional<Page<Inquiry>> findByPatientInAndType(List<Patient> patients, int type, Pageable pageable);
    Optional<Page<Inquiry>> findByPatientPractitionerAndPatientSubDoctorsIsNull(Practitioner practitioner, Pageable pageable);
    Optional<List<Inquiry>> findByPatientIdAndCreatedAtBetweenOrderByCreatedAtDesc(long id, LocalDateTime from, LocalDateTime to);
}
