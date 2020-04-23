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
    PRACTITIONER("practitioner_id"),
    SPECIALIST("specialist_id"),
    DIETITIAN("dietitian_id"),
    SPECIALTY("specialty_id"),
    EXPERIENCE("experience"),
    ROLE("role");

    private final String value;

    FilterConstant(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
