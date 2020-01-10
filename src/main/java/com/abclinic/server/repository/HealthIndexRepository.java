package com.abclinic.server.repository;

import com.abclinic.server.model.entity.HealthIndex;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author tmduc
 * @package com.abclinic.server.repository
 * @created 1/9/2020 2:31 PM
 */
public interface HealthIndexRepository extends JpaRepository<HealthIndex, Long> {
    HealthIndex findById(long id);
}