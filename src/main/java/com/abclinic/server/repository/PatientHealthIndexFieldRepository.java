package com.abclinic.server.repository;

import com.abclinic.server.model.entity.payload.health_index.*;
import com.abclinic.server.model.entity.user.Doctor;
import com.abclinic.server.model.entity.user.Patient;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringPath;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.querydsl.binding.SingleValueBinding;

import java.util.Optional;

/**
 * @author tmduc
 * @package com.abclinic.server.repository
 * @created 5/6/2020 2:26 PM
 */
public interface PatientHealthIndexFieldRepository extends JpaRepository<PatientHealthIndexField, Long>,
        QuerydslPredicateExecutor<PatientHealthIndexField>, QuerydslBinderCustomizer<QPatientHealthIndexField> {
    Optional<PatientHealthIndexField> findById(long id);

    Optional<Page<PatientHealthIndexField>> findByField(HealthIndexField field, Pageable pageable);

    Optional<Page<PatientHealthIndexField>> findBySchedule(HealthIndexSchedule schedule, Pageable pageable);

    Optional<Page<PatientHealthIndexField>> findBySchedulePatient(Patient patient, Pageable pageable);

    Optional<Page<PatientHealthIndexField>> findByScheduleDoctor(Doctor doctor, Pageable pageable);

    @Override
    default void customize(QuerydslBindings querydslBindings, QPatientHealthIndexField qPatientHealthIndexField) {
        querydslBindings.bind(String.class)
                .first((SingleValueBinding<StringPath, String>) StringExpression::containsIgnoreCase);
    };
}