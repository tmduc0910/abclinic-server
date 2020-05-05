package com.abclinic.server.common.criteria;

import com.abclinic.server.common.constant.Constant;
import com.abclinic.server.common.utils.StringUtils;
import com.abclinic.server.model.entity.user.User;
import com.querydsl.core.types.dsl.*;

/**
 * @author tmduc
 * @package com.abclinic.server.common
 * @created 4/22/2020 2:27 PM
 */
public class CustomPredicate<T extends User> {
    private SearchCriteria criteria;
    private PathBuilder<T> entityPath;

    public CustomPredicate(SearchCriteria criteria) {
        this.criteria = criteria;
    }

    public CustomPredicate() {

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
                case Constant.EQUAL_SBL:
                    return path.eq(value);
                case Constant.GTE_SBL:
                    return path.goe(value);
                case Constant.LTE_SBL:
                    return path.loe(value);
                case Constant.NOT_SBL:
                    return path.ne(value);
                case Constant.AND_SBL:
                    return Expressions.numberTemplate(Integer.class, "function('bitand', {0}, {1})", path, value)
                            .gt(0);
            }
        } else {
            final StringPath path = entityPath.getString(criteria.getKey());
            if (criteria.getOperation().equalsIgnoreCase(Constant.EQUAL_SBL)) {
                return path.containsIgnoreCase(criteria.getValue().toString());
            }
        }
        return null;
    }
}
