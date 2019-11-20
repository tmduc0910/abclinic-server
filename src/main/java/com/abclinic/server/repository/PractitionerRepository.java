package com.abclinic.server.repository;

import com.abclinic.server.model.entity.Practitioner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PractitionerRepository extends JpaRepository<Practitioner, Integer> {
    List<Practitioner> findAll();
    Optional<Practitioner> findById(int id);
    Optional<Practitioner> findByEmailAndPassword(String email, String password);
}
