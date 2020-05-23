package com.abclinic.server.common.criteria;

import com.abclinic.server.common.constant.FilterConstant;
import com.abclinic.server.model.entity.payload.health_index.HealthIndexSchedule;
import com.querydsl.core.types.dsl.PathBuilder;

/**
 * @author tmduc
 * @package com.abclinic.server.common.criteria
 * @created 5/8/2020 3:47 PM
 */
public class HealthIndexSchedulePredicateBuilder extends EntityPredicateBuilder<HealthIndexSchedule> {
    @Override
    protected void config() {
        this.allow(FilterConstant.SCHEDULE_PAT_ID)
                .allow(FilterConstant.SCHEDULE_PAT_NAME)
                .allow(FilterConstant.STATUS)
                .setPredicate(new CustomPredicate<>())
                .setPathBuilder(new PathBuilder<>(HealthIndexSchedule.class, "healthIndexSchedule"));
    }
}
