package com.abclinic.server.repository;

import com.abclinic.server.model.entity.user.Specialist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpecialistRepository extends JpaRepository<Specialist, Integer> {
    Optional<Specialist> findById(int id);
}
