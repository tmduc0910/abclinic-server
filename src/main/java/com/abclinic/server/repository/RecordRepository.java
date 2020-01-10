package com.abclinic.server.repository;

import com.abclinic.server.model.entity.Record;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author tmduc
 * @package com.abclinic.server.repository
 * @created 1/9/2020 3:42 PM
 */
public interface RecordRepository extends JpaRepository<Record, Long> {
    Record findById(long id);
}