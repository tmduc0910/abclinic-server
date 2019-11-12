package com.abclinic.server.repository;

import com.abclinic.server.model.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Integer> {
    List<Doctor> findAll();
    Optional<Doctor> findById(int id);
}
