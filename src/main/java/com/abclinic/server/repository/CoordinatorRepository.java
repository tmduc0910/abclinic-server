package com.abclinic.server.repository;

import com.abclinic.server.model.entity.user.Coordinator;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CoordinatorRepository extends JpaRepository<Coordinator, Integer> {
    Optional<Coordinator> findById(int id);
}
