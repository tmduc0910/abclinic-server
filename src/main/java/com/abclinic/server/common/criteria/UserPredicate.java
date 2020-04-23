package com.abclinic.server.common.criteria;

import com.abclinic.server.common.utils.StringUtils;
import com.abclinic.server.model.entity.user.User;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.StringPath;

/**
 * @author tmduc
 * @package com.abclinic.server.common
 * @created 4/22/2020 2:27 PM
 */
public class UserPredicate<T extends User> {
    private SearchCriteria criteria;
    private PathBuilder<T> entityPath;

    public UserPredicate(SearchCriteria criteria) {
        this.criteria = criteria;
    }

    public UserPredicate() {

    }

    public void setEntityPath(PathBuilder<T> entityPath) {
        this.entityPath = entityPath;
    }

    public SearchCriteria getCriteria() {
        return criteria;
    }

    public void setCriteria(SearchCriteria criteria) {
        this.criteria = criteria;
    }

    public BooleanExpression getPredicate() {
        if (StringUtils.isNumeric(criteria.getValue().toString())) {
            final NumberPath<Integer> path = entityPath.getNumber(criteria.getKey(), Integer.class);
            final int value = Integer.parseInt(criteria.getValue().toString());
            switch (criteria.getOperation()) {
                case "=":
                    return path.eq(value);
                case ">":
                    return path.goe(value);
                case "<":
                    return path.loe(value);
                case "!":
                    return path.ne(value);
            }
        } else {
            final StringPath path = entityPath.getString(criteria.getKey());
            if (criteria.getOperation().equalsIgnoreCase("=")) {
                return path.containsIgnoreCase(criteria.getValue().toString());
            }
        }
        return null;
    }
}
