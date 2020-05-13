package com.abclinic.server.repository;

import com.abclinic.server.model.entity.Disease;
import com.abclinic.server.model.entity.QDisease;
import com.abclinic.server.model.entity.payload.health_index.HealthIndexSchedule;
import com.abclinic.server.model.entity.payload.health_index.QHealthIndexSchedule;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringPath;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.querydsl.binding.SingleValueBinding;

import java.util.List;

/**
 * @author tmduc
 * @package com.abclinic.server.repository
 * @created 1/5/2020 11:04 AM
 */
public interface DiseaseRepository extends JpaRepository<Disease, Long>,
        QuerydslPredicateExecutor<Disease>, QuerydslBinderCustomizer<QDisease> {
    Disease findById(long id);
    boolean findByName(String name);

    @Override
    default void customize(QuerydslBindings querydslBindings, QDisease qDisease) {
        querydslBindings.bind(String.class)
                .first((SingleValueBinding<StringPath, String>) StringExpression::containsIgnoreCase);
    };
}