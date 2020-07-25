package com.abclinic.server.common.criteria;

import com.abclinic.server.common.constant.FilterConstant;
import com.abclinic.server.model.entity.payload.Chain;
import com.querydsl.core.types.dsl.PathBuilder;

/**
 * @author tmduc
 * @package com.abclinic.server.common.criteria
 * @created 7/24/2020 2:23 PM
 */
public class InquiryChainPredicateBuilder extends EntityPredicateBuilder<Chain> {
    @Override
    protected void config() {
        this.allow(FilterConstant.CHAIN_INQUIRY_ID)
                .allow(FilterConstant.CHAIN_PATIENT_ID)
                .setPredicate(new CustomPredicate<>())
                .setPathBuilder(new PathBuilder<>(Chain.class, "chain"));
    }
}
