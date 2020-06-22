package com.abclinic.server.common.criteria;

import com.abclinic.server.common.constant.FilterConstant;
import com.abclinic.server.model.entity.user.Patient;
import com.querydsl.core.types.dsl.PathBuilder;

/**
 * @author tmduc
 * @package com.abclinic.server.common.criteria
 * @created 4/23/2020 8:14 PM
 */
public class PatientPredicateBuilder extends EntityPredicateBuilder<Patient> {

    @Override
    protected void config() {
        this.allow(FilterConstant.NAME)
                .allow(FilterConstant.AGE)
                .allow(FilterConstant.GENDER)
                .allow(FilterConstant.STATUS)
                .setPredicate(new CustomPredicate<>())
                .setPathBuilder(new PathBuilder<>(Patient.class, "patient"));
    }

    public boolean hasCriteria(String key) {
        return params.stream()
                .anyMatch(p -> p.getKey().equalsIgnoreCase(key));
    }
}
