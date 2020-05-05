package com.abclinic.server.repository;

import com.abclinic.server.model.entity.payload.Inquiry;
import com.abclinic.server.model.entity.user.Patient;
import com.abclinic.server.model.entity.user.Practitioner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    Optional<Inquiry> findById(long id);
    Optional<List<Inquiry>> findByPatient(Patient patient);
    Optional<Page<Inquiry>> findByPatient(Patient patient, Pageable pageable);
    Optional<Page<Inquiry>> findByPatientPractitioner(Practitioner practitioner, Pageable pageable);
    Optional<Page<Inquiry>> findByPatientIn(List<Patient> patients, Pageable pageable);
    Optional<Page<Inquiry>> findByPatientPractitionerAndPatientSpecialistsIsNullOrPatientPractitionerAndPatientDietitiansIsNull(Practitioner practitioner, Practitioner practitioner1, Pageable pageable);

}
