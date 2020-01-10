package com.abclinic.server.repository;

import com.abclinic.server.model.entity.HealthIndexSchedule;
import com.abclinic.server.model.entity.Record;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author tmduc
 * @package com.abclinic.server.repository
 * @created 1/9/2020 2:32 PM
 */
public interface HealthIndexScheduleRepository extends JpaRepository<HealthIndexSchedule, Long> {

}