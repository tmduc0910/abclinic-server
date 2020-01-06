package com.abclinic.server.repository;

import com.abclinic.server.model.entity.Disease;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author tmduc
 * @package com.abclinic.server.repository
 * @created 1/5/2020 11:04 AM
 */
public interface DiseaseRepository extends JpaRepository<Disease, Long> {
    Disease findById(long id);
}