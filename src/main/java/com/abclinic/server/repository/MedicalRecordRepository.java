package com.abclinic.server.repository;

import com.abclinic.server.model.entity.Disease;
import com.abclinic.server.model.entity.QDisease;
import com.abclinic.server.model.entity.payload.record.MedicalRecord;
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

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long>,
        QuerydslPredicateExecutor<MedicalRecord>, QuerydslBinderCustomizer<QMedicalRecord> {
    Optional<MedicalRecord> findById(long id);

    //    Optional<List<MedicalRecord>> findByPatient(Patient patient);
    Optional<Page<MedicalRecord>> findByInquiryPatientPractitionerIdAndInquiryId(long id, long inquiryId, Pageable pageable);

    Optional<Page<MedicalRecord>> findByInquiryPatientIdAndStatus(long id, int status, Pageable pageable);

    Optional<Page<MedicalRecord>> findByDisease(Disease disease, Pageable pageable);

    Optional<Page<MedicalRecord>> findByInquiryPatientInAndInquiryId(List<Patient> patients, long inquiryId, Pageable pageable);

    @Override
    default void customize(QuerydslBindings querydslBindings, QMedicalRecord qMedicalRecord) {
        querydslBindings.bind(String.class)
                .first((SingleValueBinding<StringPath, String>) StringExpression::containsIgnoreCase);
    };
}
