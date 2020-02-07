package com.abclinic.server.repository;

import com.abclinic.server.model.entity.Inquiry;
import com.abclinic.server.model.entity.user.Patient;
import com.abclinic.server.model.entity.user.Practitioner;
import io.swagger.models.auth.In;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    Optional<Inquiry> findById(long id);
    Optional<List<Inquiry>> findByPatient(Patient patient);
    Optional<List<Inquiry>> findByPatientPractitioner(Practitioner practitioner, Pageable pageable);
    Optional<List<Inquiry>> findByPatientIn(List<Patient> patients, Pageable pageable);
}
