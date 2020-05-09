package com.abclinic.server.repository;

import com.abclinic.server.model.entity.payload.health_index.HealthIndexSchedule;
import com.abclinic.server.model.entity.payload.health_index.QHealthIndexSchedule;
import com.abclinic.server.model.entity.user.Doctor;
import com.abclinic.server.model.entity.user.Patient;
import com.abclinic.server.model.entity.user.QUser;
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
 * @created 1/9/2020 2:32 PM
 */
public interface HealthIndexScheduleRepository extends JpaRepository<HealthIndexSchedule, Long>,
        QuerydslPredicateExecutor<HealthIndexSchedule>, QuerydslBinderCustomizer<QHealthIndexSchedule> {
    Optional<HealthIndexSchedule> findById(long id);

    Optional<Page<HealthIndexSchedule>> findByPatient(Patient patient, Pageable pageable);

    Optional<Page<HealthIndexSchedule>> findByDoctor(Doctor doctor, Pageable pageable);

    @Override
    default void customize(QuerydslBindings querydslBindings, QHealthIndexSchedule qHealthIndexSchedule) {
        querydslBindings.bind(String.class)
                .first((SingleValueBinding<StringPath, String>) StringExpression::containsIgnoreCase);
    };
}