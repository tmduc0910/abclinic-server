package com.abclinic.server.common.criteria;

import com.abclinic.server.common.constant.Constant;
import com.abclinic.server.common.constant.FilterConstant;
import com.abclinic.server.common.utils.StringUtils;
import com.abclinic.server.model.entity.user.*;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.JPAExpressions;

/**
 * @author tmduc
 * @package com.abclinic.server.common
 * @created 4/22/2020 2:27 PM
 */
public class CustomPredicate<T> {
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
                    if (value != 0)
                        return path.divide(value).floor().mod(2).eq(1);
                    return path.eq(0);
            }
        } else if (criteria.getValue().equals("null")) {
            final NumberPath<Integer> path = entityPath.getNumber(criteria.getKey(), Integer.class);
            return path.isNull();
        } else if (criteria.getOperation().equalsIgnoreCase(Constant.CONTAIN_SBL)) {
            QPatient qPatient = QPatient.patient;
            return qPatient.subDoctors.contains((User) criteria.getValue());
        } else if (criteria.getKey().equalsIgnoreCase(FilterConstant.SPECIALTY.getValue())) {
            QPractitioner qPractitioner = QPractitioner.practitioner;
            QSpecialist qSpecialist = QSpecialist.specialist;
            QDietitian qDietitian = QDietitian.dietitian;

            NumberPath<Long> path = entityPath.getNumber("id", Long.class);
            return path.in(JPAExpressions.selectFrom(qPractitioner)
                    .where(qPractitioner.specialties.any().name.containsIgnoreCase(criteria.getValue().toString()))
                    .select(qPractitioner.id))
                    .or(path.in(JPAExpressions.selectFrom(qSpecialist)
                            .where(qSpecialist.specialty.name.containsIgnoreCase(criteria.getValue().toString()))
                            .select(qSpecialist.id)))
                    .or(path.in(JPAExpressions.selectFrom(qDietitian)
                            .where(qDietitian.specialty.name.containsIgnoreCase(criteria.getValue().toString()))
                            .select(qDietitian.id)));
        } else {
            final StringPath path = entityPath.getString(criteria.getKey());
            if (criteria.getOperation().equalsIgnoreCase(Constant.EQUAL_SBL)) {
                return path.containsIgnoreCase(criteria.getValue().toString());
            }
        }
        return null;
    }
}
