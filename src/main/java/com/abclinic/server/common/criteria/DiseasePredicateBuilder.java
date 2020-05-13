package com.abclinic.server.common.criteria;

import com.abclinic.server.common.constant.FilterConstant;
import com.abclinic.server.model.entity.Disease;
import com.querydsl.core.types.dsl.PathBuilder;

/**
 * @author tmduc
 * @package com.abclinic.server.common.criteria
 * @created 5/13/2020 3:34 PM
 */
public class DiseasePredicateBuilder extends EntityPredicateBuilder<Disease> {

    @Override
    protected void config() {
        this.allow(FilterConstant.NAME)
                .allow(FilterConstant.DESCRIPTION)
                .setPredicate(new CustomPredicate<>())
                .setPathBuilder(new PathBuilder<Disease>(Disease.class, "disease"));
    }
}
