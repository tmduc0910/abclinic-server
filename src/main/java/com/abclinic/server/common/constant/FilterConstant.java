package com.abclinic.server.common.constant;

/**
 * @author tmduc
 * @package com.abclinic.server.common.constant
 * @created 4/23/2020 8:06 PM
 */
public enum FilterConstant {
    NAME("name"),
    AGE("age"),
    GENDER("gender"),
    STATUS("status"),
    PRACTITIONER("practitioner.id"),
    SPECIALIST("subDoctors.id"),
    DIETITIAN("subDoctors"),
    SPECIALTY("specialty"),
    EXPERIENCE("experience"),
    ROLE("role"),
    SCHEDULE_PAT_ID("patient.id"),
    SCHEDULE_PAT_NAME("patient.name"),
    VAL_PAT_ID("schedule.patient.id"),
    VAL_PAT_NAME("schedule.patient.name"),
    VAL_INDEX_ID("schedule.index.id"),
    VAL_SCHEDULE_ID("schedule.id");

    private final String value;

    FilterConstant(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
