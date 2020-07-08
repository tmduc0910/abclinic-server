package com.abclinic.server.common.criteria;

import com.abclinic.server.common.constant.FilterConstant;
import com.abclinic.server.model.entity.payload.record.DietRecord;
import com.abclinic.server.model.entity.payload.record.MedicalRecord;
import com.querydsl.core.types.dsl.PathBuilder;

/**
 * @author tmduc
 * @package com.abclinic.server.common.criteria
 * @created 7/8/2020 10:28 AM
 */
public class DietRecordPredicateBuilder extends EntityPredicateBuilder<DietRecord> {
    @Override
    protected void config() {
        this.allow(FilterConstant.TYPE)
                .allow(FilterConstant.INQUIRY_ID)
                .allow(FilterConstant.DIET_INQUIRY_PAT)
                .allow(FilterConstant.INQUIRY_PAT_PRAC_ID)
                .allow(FilterConstant.STATUS)
                .setPredicate(new CustomPredicate<>())
                .setPathBuilder(new PathBuilder<>(DietRecord.class, "dietRecord"));
    }
}
