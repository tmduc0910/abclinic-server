package com.abclinic.server.common.criteria;

import com.abclinic.server.common.constant.Constant;
import com.abclinic.server.common.constant.FilterConstant;
import com.abclinic.server.common.utils.StringUtils;
import com.abclinic.server.model.entity.user.User;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author tmduc
 * @package com.abclinic.server.common
 * @created 4/22/2020 2:49 PM
 */
public abstract class UserPredicateBuilder<T extends User> {
    private List<SearchCriteria> params;
    private Set<String> allowedFilters = new HashSet<>();
    private UserPredicate<T> predicate;

    UserPredicateBuilder() {
        params = new ArrayList<>();
        allowedFilters.add(FilterConstant.PRACTITIONER.getValue());
        allowedFilters.add(FilterConstant.DIETITIAN.getValue());
        allowedFilters.add(FilterConstant.SPECIALIST.getValue());
        config();
    }

    protected abstract void config();

    public UserPredicateBuilder init(String search) {
        if (!StringUtils.endsWith(search, ","))
            search += ",";

        if (!StringUtils.isNull(search)) {
            Pattern pattern = Pattern.compile(Constant.FILTER_REGEX, Pattern.UNICODE_CHARACTER_CLASS);
            Matcher matcher = pattern.matcher(search + ",");
            while (matcher.find()) {
                this.with(matcher.group(1), matcher.group(2), matcher.group(3));
            }
        }
        return this;
    }

    UserPredicateBuilder allow(FilterConstant filter) {
        allowedFilters.add(filter.getValue());
        return this;
    }

    UserPredicateBuilder setPredicate(UserPredicate<T> predicate) {
        this.predicate = predicate;
        return this;
    }

    UserPredicateBuilder setPathBuilder(PathBuilder<T> pathBuilder) {
        this.predicate.setEntityPath(pathBuilder);
        return this;
    }

    public UserPredicateBuilder with(String key, String operation, Object value) {
        params.add(new SearchCriteria(key, operation, value));
        return this;
    }

    public BooleanExpression build() {
        if (params.size() == 0)
            return null;

        params = params.stream()
                .filter(p -> allowedFilters.contains(p.getKey()))
                .collect(Collectors.toList());

        List<BooleanExpression> predicates = params.stream().map(p -> {
            predicate.setCriteria(p);
            return predicate.getPredicate();
        }).filter(Objects::nonNull).collect(Collectors.toList());

        BooleanExpression result = Expressions.asBoolean(true).isTrue();
        for (BooleanExpression ex: predicates) {
            result = result.and(ex);
        }
        return result;
    }
}
