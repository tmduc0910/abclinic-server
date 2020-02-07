package com.abclinic.server.repository;

import com.abclinic.server.model.entity.Specialty;
import com.abclinic.server.model.entity.user.Practitioner;
import com.abclinic.server.model.entity.user.Specialist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpecialistRepository extends JpaRepository<Specialist, Long> {
    Specialist findById(long id);
    Optional<List<Specialist>> findBySpecialty(Specialty specialty);
}
