package com.abclinic.server.repository;

import com.abclinic.server.model.entity.payload.record.DietRecord;
import com.abclinic.server.model.entity.user.Patient;
import com.abclinic.server.model.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author tmduc
 * @package com.abclinic.server.repository
 * @created 1/5/2020 11:06 AM
 */
public interface DietitianRecordRepository extends JpaRepository<DietRecord, Long> {
    Optional<DietRecord> findById(long id);
    Optional<Page<DietRecord>> findByInquiryPatientPractitionerId(long id, Pageable pageable);
    Optional<Page<DietRecord>> findByInquiryPatientIdAndStatus(long id, int status, Pageable pageable);
    Optional<Page<DietRecord>> findByInquiryPatientIn(List<Patient> patients, Pageable pageable);

}