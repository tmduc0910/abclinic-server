package com.abclinic.server.repository;

import com.abclinic.server.model.entity.user.Dietitian;
import com.abclinic.server.model.entity.user.Patient;
import com.abclinic.server.model.entity.user.Practitioner;
import com.abclinic.server.model.entity.user.Specialist;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    Patient findById(long id);
    Optional<List<Patient>> findByPractitioner(Practitioner practitioner, Pageable pageable);
    Optional<List<Patient>> findByDietitians(Dietitian dietitian, Pageable pageable);
    Optional<List<Patient>> findBySpecialists(Specialist specialist, Pageable pageable);
    Optional<Patient> findByUid(String uid);
    List<Patient> findByName(String name);
    Optional<Patient> findByPhoneNumberAndPassword(String phoneNumber, String password);
    Optional<Patient> findByEmailAndPassword(String email, String password);
}