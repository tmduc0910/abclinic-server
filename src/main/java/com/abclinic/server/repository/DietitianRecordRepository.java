package com.abclinic.server.repository;

import com.abclinic.server.model.entity.payload.record.DietRecord;
import com.abclinic.server.model.entity.payload.record.MedicalRecord;
import com.abclinic.server.model.entity.payload.record.QDietRecord;
import com.abclinic.server.model.entity.payload.record.QMedicalRecord;
import com.abclinic.server.model.entity.user.Patient;
import com.abclinic.server.model.entity.user.User;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringPath;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.querydsl.binding.SingleValueBinding;

import java.util.List;
import java.util.Optional;

/**
 * @author tmduc
 * @package com.abclinic.server.repository
 * @created 1/5/2020 11:06 AM
 */
public interface DietitianRecordRepository extends JpaRepository<DietRecord, Long>,
        QuerydslPredicateExecutor<DietRecord>, QuerydslBinderCustomizer<QDietRecord> {
    Optional<DietRecord> findById(long id);

    Optional<Page<DietRecord>> findByInquiryPatientPractitionerIdAndInquiryId(long id, long inquiryId, Pageable pageable);

    Optional<Page<DietRecord>> findByInquiryPatientIdAndStatus(long id, int status, Pageable pageable);

    Optional<Page<DietRecord>> findByInquiryPatientInAndInquiryId(List<Patient> patients, long inquiryId, Pageable pageable);

    @Override
    default void customize(QuerydslBindings querydslBindings, QDietRecord qDietRecord) {
        querydslBindings.bind(String.class)
                .first((SingleValueBinding<StringPath, String>) StringExpression::containsIgnoreCase);
    };
}