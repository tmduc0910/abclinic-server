package com.abclinic.server.repository;

import com.abclinic.server.model.entity.Dietitian;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DietitianRepository extends JpaRepository<Dietitian, Integer> {
    Optional<Dietitian> findById(int id);
}
