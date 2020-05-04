package com.abclinic.server.common.criteria;

import com.abclinic.server.common.constant.FilterConstant;
import com.abclinic.server.model.entity.user.Doctor;
import com.querydsl.core.types.dsl.PathBuilder;

/**
 * @author tmduc
 * @package com.abclinic.server.common.criteria
 * @created 4/23/2020 8:17 PM
 */
public class DoctorPredicateBuilder extends EntityPredicateBuilder<Doctor> {

    @Override
    protected void config() {
        this.allow(FilterConstant.NAME)
                .allow(FilterConstant.ROLE)
                .allow(FilterConstant.SPECIALTY)
                .allow(FilterConstant.STATUS)
                .setPredicate(new CustomPredicate<Doctor>())
                .setPathBuilder(new PathBuilder<>(Doctor.class, "user"));
    }
}
