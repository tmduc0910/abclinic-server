package com.abclinic.server.repository;

import com.abclinic.server.model.entity.user.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Integer> {
    Optional<Patient> findByPhoneNumberAndPassword(String phoneNumber, String password);
    Optional<Patient> findByEmailAndPassword(String email, String password);
    List<Patient> findAll();
}