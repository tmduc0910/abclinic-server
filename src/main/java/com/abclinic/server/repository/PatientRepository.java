package com.abclinic.server.repository;

import com.abclinic.server.model.entity.user.Patient;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    Patient findById(long id);
    Optional<List<Patient>> findAllByStatus(int status, Pageable pageable);
    Optional<Patient> findByUid(String uid);
    List<Patient> findByName(String name);
    Optional<Patient> findByPhoneNumberAndPassword(String phoneNumber, String password);
    Optional<Patient> findByEmailAndPassword(String email, String password);
}