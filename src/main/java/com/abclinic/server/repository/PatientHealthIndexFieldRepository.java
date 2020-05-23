package com.abclinic.server.repository;

import com.abclinic.server.model.entity.payload.health_index.HealthIndexField;
import com.abclinic.server.model.entity.payload.health_index.HealthIndexSchedule;
import com.abclinic.server.model.entity.payload.health_index.PatientHealthIndexField;
import com.abclinic.server.model.entity.payload.health_index.QPatientHealthIndexField;
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
 * @created 5/6/2020 2:26 PM
 */
public interface PatientHealthIndexFieldRepository extends JpaRepository<PatientHealthIndexField, Long>,
        QuerydslPredicateExecutor<PatientHealthIndexField>, QuerydslBinderCustomizer<QPatientHealthIndexField> {
    Optional<PatientHealthIndexField> findById(long id);

    Optional<Page<PatientHealthIndexField>> findByField(HealthIndexField field, Pageable pageable);

    Optional<Page<PatientHealthIndexField>> findBySchedule(HealthIndexSchedule schedule, Pageable pageable);

    Optional<Page<PatientHealthIndexField>> findBySchedule_Patient_Id(long id, Pageable pageable);

    Optional<Page<PatientHealthIndexField>> findBySchedule_Doctor_Id(long id, Pageable pageable);

    Optional<List<PatientHealthIndexField>> findByScheduleDoctorAndTagId(User doctor, long id);

    @Override
    default void customize(QuerydslBindings querydslBindings, QPatientHealthIndexField qPatientHealthIndexField) {
        querydslBindings.bind(String.class)
                .first((SingleValueBinding<StringPath, String>) StringExpression::containsIgnoreCase);
    };
}