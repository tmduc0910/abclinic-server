package com.abclinic.server.repository;

import com.abclinic.server.model.entity.user.Practitioner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PractitionerRepository extends JpaRepository<Practitioner, Long> {
    Optional<Practitioner> findById(long id);
    Optional<Practitioner> findByEmailAndPassword(String email, String password);
}
