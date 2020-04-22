package com.abclinic.server.common;

import com.abclinic.server.common.constant.Constant;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author tmduc
 * @package com.abclinic.server.common
 * @created 4/22/2020 2:49 PM
 */
public class PatientPredicatesBuilder {
    private List<SearchCriteria> params;

    public PatientPredicatesBuilder() {
        params = new ArrayList<>();
    }

    public PatientPredicatesBuilder with(String key, String operation, Object value) {
        params.add(new SearchCriteria(key, operation, value));
        return this;
    }

    public BooleanExpression build() {
        if (params.size() == 0)
            return null;

        params = params.stream()
                .filter(p -> Arrays.asList(Constant.ALLOWED_FILTERS).contains(p.getKey()))
                .collect(Collectors.toList());

        List<BooleanExpression> predicates = params.stream().map(p -> {
            PatientPredicate predicate = new PatientPredicate(p);
            return predicate.getPredicate();
        }).filter(Objects::nonNull).collect(Collectors.toList());

        BooleanExpression result = Expressions.asBoolean(true).isTrue();
        for (BooleanExpression ex: predicates) {
            result = result.and(ex);
        }
        return result;
    }
}
