package com.abclinic.server.repository;

import com.abclinic.server.model.entity.payload.health_index.HealthIndex;
import com.abclinic.server.model.entity.payload.health_index.HealthIndexField;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author tmduc
 * @package com.abclinic.server.repository
 * @created 5/6/2020 2:26 PM
 */
public interface HealthIndexFieldRepository extends JpaRepository<HealthIndexField, Long> {
    Optional<HealthIndexField> findById(long id);
    Optional<List<HealthIndexField>> findByHealthIndex(HealthIndex healthIndex);
    List<HealthIndexField> findByHealthIndexAndNameIgnoreCase(HealthIndex index, String name);
}