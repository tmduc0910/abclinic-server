package com.abclinic.server.repository;

import com.abclinic.server.model.entity.payload.Chain;
import com.abclinic.server.model.entity.payload.QChain;
import com.abclinic.server.model.entity.payload.record.MedicalRecord;
import com.abclinic.server.model.entity.payload.record.QMedicalRecord;
import com.abclinic.server.model.entity.user.Patient;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringPath;
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
 * @created 7/23/2020 2:26 PM
 */
public interface ChainRepository extends JpaRepository<Chain, Long>,
        QuerydslPredicateExecutor<Chain>, QuerydslBinderCustomizer<QChain> {
    Optional<Chain> findById(long id);
    Optional<List<Chain>> findByChainId(long chainId);
    Optional<Chain> findTopByPatientOrderByChainIdDesc(Patient patient);
    Optional<Chain> findByIdGreaterThan(long id);
    Optional<Chain> findByIdLessThan(long id);

    @Override
    default void customize(QuerydslBindings querydslBindings, QChain qChain) {
        querydslBindings.bind(String.class)
                .first((SingleValueBinding<StringPath, String>) StringExpression::containsIgnoreCase);
    };
}