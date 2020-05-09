package com.abclinic.server.repository;

import com.abclinic.server.model.entity.payload.health_index.HealthIndex;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author tmduc
 * @package com.abclinic.server.repository
 * @created 1/9/2020 2:31 PM
 */
public interface HealthIndexRepository extends JpaRepository<HealthIndex, Long> {
    Optional<HealthIndex> findById(long id);
    boolean findByNameIgnoreCase(String name);
}