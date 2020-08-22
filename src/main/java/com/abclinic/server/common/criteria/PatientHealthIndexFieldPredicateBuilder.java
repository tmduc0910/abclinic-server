package com.abclinic.server.common.criteria;

import com.abclinic.server.common.constant.FilterConstant;
import com.abclinic.server.model.entity.payload.health_index.PatientHealthIndexField;
import com.querydsl.core.types.dsl.PathBuilder;

/**
 * @author tmduc
 * @package com.abclinic.server.common.criteria
 * @created 5/10/2020 2:46 PM
 */
public class PatientHealthIndexFieldPredicateBuilder extends EntityPredicateBuilder<PatientHealthIndexField> {
    @Override
    protected void config() {
        this.allow(FilterConstant.VAL_PAT_ID)
                .allow(FilterConstant.VAL_PAT_NAME)
                .allow(FilterConstant.VAL_INDEX_ID)
                .allow(FilterConstant.VAL_INDEX_NAME)
                .allow(FilterConstant.VAL_SCHEDULE_ID)
                .allow(FilterConstant.VAL_DOC_ID)
                .allow(FilterConstant.VAL_PRAC_ID)
                .setPredicate(new CustomPredicate<>())
                .setPathBuilder(new PathBuilder<>(PatientHealthIndexField.class, "patientHealthIndexField"));
    }
}
