package com.abclinic.server.repository;

import com.abclinic.server.model.entity.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author tmduc
 * @package com.abclinic.server.repository
 * @created 12/27/2019 4:11 PM
 */
public interface SpecialtyRepository extends JpaRepository<Specialty, Long> {
    Optional<Specialty> findById(long id);
}