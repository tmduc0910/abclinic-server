package com.abclinic.server.repository;

import com.abclinic.server.model.entity.user.*;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringPath;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.querydsl.binding.SingleValueBinding;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long>,
        QuerydslPredicateExecutor<Patient>, QuerydslBinderCustomizer<QPatient> {
    Patient findById(long id);
    Page<Patient> findAll(Pageable pageable);
    Optional<Page<Patient>> findByPractitioner(Practitioner practitioner, Pageable pageable);
    Optional<Page<Patient>> findByPractitionerAndStatus(Practitioner practitioner, int status, Pageable pageable);
    Optional<Page<Patient>> findByDietitians(Dietitian dietitian, Pageable pageable);
    Optional<Page<Patient>> findBySpecialists(Specialist specialist, Pageable pageable);
    Optional<Patient> findByUid(String uid);
    Page<Patient> findByName(String name, Pageable pageable);
    Optional<Patient> findByPhoneNumberAndPassword(String phoneNumber, String password);
    Optional<Patient> findByEmailAndPassword(String email, String password);

    @Override
    default void customize(QuerydslBindings querydslBindings, QPatient qPatient) {
        querydslBindings.bind(String.class)
                .first((SingleValueBinding<StringPath, String>) StringExpression::containsIgnoreCase);
        querydslBindings.excluding(qPatient.email);
    };
}